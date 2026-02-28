package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.*;
import agh.ics.oop.model.util.Boundary;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class SimulationPresenter implements MapChangeListener {

    @FXML private Label moveInfoLabel;
    @FXML private Canvas mapCanvas;

    @FXML private Label dayLabel;
    @FXML private Label animalCountLabel;
    @FXML private Label plantCountLabel;
    @FXML private Label freeFieldsLabel;
    @FXML private Label avgEnergyLabel;
    @FXML private Label avgLifeSpanLabel;
    @FXML private Label avgChildrenLabel;
    @FXML private Label dominantGenotypeLabel;
    @FXML private Label selectedAnimalInfoLabel;

    @FXML private Button pauseButton;
    @FXML private ComboBox<String> chartStatsSelector;
    @FXML private LineChart<Number, Number> statsChart;

    private WorldMap worldMap;
    private Simulation simulation;
    private Animal trackedAnimal;
    private SimulationStats lastStats;

    private XYChart.Series<Number, Number> currentSeries;
    private String currentChartMode = "Liczba Zwierząt";

    private double cellWidth;
    private double cellHeight;

    public void setWorldMap(WorldMap map) { this.worldMap = map; }
    public void setSimulation(Simulation simulation) { this.simulation = simulation; }

    @FXML
    public void initialize() {
        chartStatsSelector.getItems().addAll(
                "Liczba Zwierząt",
                "Liczba Roślin",
                "Średnia Energia",
                "Średnia Długość Życia",
                "Średnia Liczba Dzieci",
                "Wolne Pola"
        );
        chartStatsSelector.setValue("Liczba Zwierząt");

        chartStatsSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentChartMode = newVal;
            statsChart.getData().clear();
            currentSeries = new XYChart.Series<>();
            currentSeries.setName(newVal);
            statsChart.getData().add(currentSeries);
        });

        currentSeries = new XYChart.Series<>();
        currentSeries.setName(currentChartMode);
        statsChart.getData().add(currentSeries);
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            moveInfoLabel.setText(message);
            if (simulation != null) {
                lastStats = simulation.getStats();
                updateStatsUI(lastStats);
                updateChart(lastStats);
                drawMap(lastStats.dominantGenotype());
            }
        });
    }

    private void updateStatsUI(SimulationStats stats) {
        dayLabel.setText("Dzień: " + stats.day());
        animalCountLabel.setText("Zwierzaki: " + stats.animalCount());
        plantCountLabel.setText("Rośliny: " + stats.plantCount());
        freeFieldsLabel.setText("Wolne pola: " + stats.freeFields());
        avgEnergyLabel.setText(String.format("Śr. Energia: %.2f", stats.avgEnergy()));
        avgLifeSpanLabel.setText(String.format("Śr. życie (martwe): %.2f", stats.avgLifeSpan()));
        avgChildrenLabel.setText(String.format("Śr. dzieci (żywe): %.2f", stats.avgChildren()));
        dominantGenotypeLabel.setText(stats.dominantGenotype() != null ? stats.dominantGenotype().toString() : "-");

        updateTrackedAnimalInfo();
    }

    private void updateChart(SimulationStats stats) {
        double value = 0;
        switch (currentChartMode) {
            case "Liczba Zwierząt" -> value = stats.animalCount();
            case "Liczba Roślin" -> value = stats.plantCount();
            case "Średnia Energia" -> value = stats.avgEnergy();
            case "Średnia Długość Życia" -> value = stats.avgLifeSpan();
            case "Średnia Liczba Dzieci" -> value = stats.avgChildren();
            case "Wolne Pola" -> value = stats.freeFields();
        }
        currentSeries.getData().add(new XYChart.Data<>(stats.day(), value));
    }

    private void updateTrackedAnimalInfo() {
        if (trackedAnimal != null) {
            String status = trackedAnimal.getEnergy() > 0 ? "ŻYJE" : "MARTWY (Dzień " + trackedAnimal.getDeadDay() + ")";
            int descendants = trackedAnimal.getDescendantsCount();

            selectedAnimalInfoLabel.setText(
                    "Status: " + status + "\n" +
                            "Genom: " + trackedAnimal.getGenotype().getGenes() + "\n" +
                            "Aktywny Gen: " + trackedAnimal.getActiveGene() + "\n" +
                            "Energia: " + trackedAnimal.getEnergy() + "\n" +
                            "Zjedzone rośliny: " + trackedAnimal.getPlantsEaten() + "\n" +
                            "Dzieci: " + trackedAnimal.getChildrenCount() + "\n" +
                            "Potomkowie: " + descendants + "\n" +
                            "Wiek: " + trackedAnimal.getAge()
            );
        } else {
            selectedAnimalInfoLabel.setText("Kliknij zwierzaka, aby śledzić.");
        }
    }

    @FXML
    private void onPauseClicked() {
        if (simulation.isPaused()) {
            simulation.resume();
            pauseButton.setText("Pauza");
        } else {
            simulation.pause();
            pauseButton.setText("Wznów");
        }
    }

    @FXML
    private void onQuitClicked() {
        if (simulation != null) simulation.stop();
        ((Stage) mapCanvas.getScene().getWindow()).close();
    }

    @FXML
    private void onMapClicked(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        int gridX = (int) (x / cellWidth);
        Boundary bounds = worldMap.getCurrentBounds();
        int gridY = bounds.upperRight().getY() - (int) (y / cellHeight);

        List<Animal> animalsHere = worldMap.getAnimalsAt(new Vector2d(gridX, gridY));
        if (!animalsHere.isEmpty()) {
            trackedAnimal = animalsHere.get(0);
            updateTrackedAnimalInfo();
        } else {
            trackedAnimal = null;
            selectedAnimalInfoLabel.setText("Puste pole.");
        }
    }

    private void drawMap(List<Integer> dominantGenotype) {
        if (worldMap == null) return;
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();

        double canvasW = mapCanvas.getWidth();
        double canvasH = mapCanvas.getHeight();
        if (canvasW <= 0 || canvasH <= 0) { canvasW = 500; canvasH = 500; mapCanvas.setWidth(canvasW); mapCanvas.setHeight(canvasH); }

        gc.clearRect(0, 0, canvasW, canvasH);

        Boundary bounds = worldMap.getCurrentBounds();
        Vector2d lowerLeft = bounds.lowerLeft();
        Vector2d upperRight = bounds.upperRight();

        int gridWidth = Math.max(1, upperRight.getX() - lowerLeft.getX() + 1);
        int gridHeight = Math.max(1, upperRight.getY() - lowerLeft.getY() + 1);

        cellWidth = canvasW / gridWidth;
        cellHeight = canvasH / gridHeight;

        for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
            for (int y = lowerLeft.getY(); y <= upperRight.getY(); y++) {
                Vector2d pos = new Vector2d(x, y);
                Color bg = Color.BEIGE;
                try {
                    if (worldMap.isPreferred(pos)) bg = Color.DARKSEAGREEN;
                } catch (Exception e) { /* ignoruj */ }

                gc.setFill(bg);
                gc.fillRect((x - lowerLeft.getX()) * cellWidth, (upperRight.getY() - y) * cellHeight, cellWidth, cellHeight);
            }
        }

        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        for (int i = 0; i <= gridWidth; i++) gc.strokeLine(i * cellWidth, 0, i * cellWidth, canvasH);
        for (int i = 0; i <= gridHeight; i++) gc.strokeLine(0, i * cellHeight, canvasW, i * cellHeight);

        for (WorldElement element : worldMap.getElements()) {
            Vector2d pos = element.getPosition();
            double x = (pos.getX() - lowerLeft.getX()) * cellWidth;
            double y = (upperRight.getY() - pos.getY()) * cellHeight;

            if (element instanceof Plant) {
                gc.setFill(Color.FORESTGREEN);
                gc.fillRoundRect(x + cellWidth * 0.25, y + cellHeight * 0.25, cellWidth * 0.5, cellHeight * 0.5, 5, 5);
            }
            else if (element instanceof Animal animal) {
                if (dominantGenotype != null && animal.getGenotype().getGenes().equals(dominantGenotype)) {
                    gc.setFill(Color.MAGENTA);
                    gc.fillOval(x, y, cellWidth, cellHeight);
                }
                gc.setFill(getEnergyColor(animal.getEnergy()));
                gc.fillOval(x + cellWidth * 0.1, y + cellHeight * 0.1, cellWidth * 0.8, cellHeight * 0.8);
            }
        }
    }

    private Color getEnergyColor(int energy) {
        if (energy <= 0) return Color.BLACK;
        if (energy < 20) return Color.RED;
        if (energy < 50) return Color.ORANGE;
        if (energy < 100) return Color.YELLOW;
        return Color.CORNFLOWERBLUE;
    }
}