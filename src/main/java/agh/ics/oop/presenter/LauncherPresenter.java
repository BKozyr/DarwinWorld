package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LauncherPresenter {

    @FXML private ComboBox<MapVariant> mapVariantComboBox;
    @FXML private TextField widthField;
    @FXML private TextField heightField;
    @FXML private TextField startAnimalsField;
    @FXML private TextField startEnergyField;
    @FXML private TextField plantEnergyField;
    @FXML private TextField plantsPerDayField;

    //wariant C
    @FXML private TextField minEnergyToCultivateField;
    @FXML private TextField fertilityDecayField;
    @FXML private TextField superPlantBitesField;

    @FXML private ComboBox<String> presetComboBox;
    private final ConfigManager configManager = new ConfigManager();

    @FXML
    public void initialize() {
        mapVariantComboBox.getItems().setAll(MapVariant.values());
        mapVariantComboBox.getSelectionModel().selectFirst();
        refreshPresets();
    }

    private void refreshPresets() {
        presetComboBox.getItems().setAll(configManager.getAvailablePresets());
    }

    @FXML
    private void onSavePresetClicked() {
        try {
            SimulationConfig config = getConfigFromForm();
            String name = "config_" + System.currentTimeMillis();
            configManager.saveConfig(config, name);
            refreshPresets();
            presetComboBox.getSelectionModel().select(name);
        } catch (Exception e) {
            System.err.println("Błąd zapisu: " + e.getMessage());
        }
    }

    @FXML
    private void onLoadPresetClicked() {
        String name = presetComboBox.getValue();
        if (name == null) return;
        try {
            SimulationConfig loaded = configManager.loadConfig(name);
            if (loaded != null) {
                widthField.setText(String.valueOf(loaded.width()));
                heightField.setText(String.valueOf(loaded.height()));
                startAnimalsField.setText(String.valueOf(loaded.startAnimals()));
                startEnergyField.setText(String.valueOf(loaded.startingEnergy()));
                plantEnergyField.setText(String.valueOf(loaded.plantEnergy()));
                plantsPerDayField.setText(String.valueOf(loaded.plantsPerDay()));

                // Nowe pola
                minEnergyToCultivateField.setText(String.valueOf(loaded.minEnergyToCultivate()));
                fertilityDecayField.setText(String.valueOf(loaded.fertilityDecayDays()));
                superPlantBitesField.setText(String.valueOf(loaded.superPlantBites()));

                mapVariantComboBox.setValue(loaded.mapVariant());
            }
        } catch (Exception e) {
            System.err.println("Błąd odczytu: " + e.getMessage());
        }
    }

    private SimulationConfig getConfigFromForm() {
        int width = Integer.parseInt(widthField.getText());
        int height = Integer.parseInt(heightField.getText());
        int startAnimals = Integer.parseInt(startAnimalsField.getText());
        int startEnergy = Integer.parseInt(startEnergyField.getText());
        int plantEnergy = Integer.parseInt(plantEnergyField.getText());
        int plantsPerDay = Integer.parseInt(plantsPerDayField.getText());

        // Pobieranie nowych pól
        int minCultivate = Integer.parseInt(minEnergyToCultivateField.getText());
        int fertilityDecay = Integer.parseInt(fertilityDecayField.getText());
        int superBites = Integer.parseInt(superPlantBitesField.getText());

        MapVariant selectedVariant = mapVariantComboBox.getValue();

        return new SimulationConfig(
                width, height, plantEnergy, plantsPerDay, startAnimals,
                minCultivate, fertilityDecay, superBites,
                startEnergy, 1, 10, 5, 5, 2, 4,
                selectedVariant
        );
    }

    @FXML
    private void onSimulationStartClicked() throws IOException {
        try {
            SimulationConfig config = getConfigFromForm();

            WorldMap map;
            if (config.mapVariant() == MapVariant.CULTIVATION) {
                map = new CultivationMap(config);
            } else {
                map = new RectangularMap(config);
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
            BorderPane viewRoot = loader.load();

            Simulation simulation = new Simulation(map, config);

            SimulationPresenter presenter = loader.getController();
            presenter.setWorldMap(map);
            presenter.setSimulation(simulation);

            if (map instanceof AbstractWorldMap abstractMap) {
                abstractMap.registerObserver(presenter);
            }

            Thread simulationThread = new Thread(simulation);
            simulationThread.start();

            Stage stage = new Stage();
            stage.setTitle("Symulacja: " + config.mapVariant());
            stage.setScene(new Scene(viewRoot));
            stage.setOnCloseRequest(event -> simulation.stop());
            stage.show();

        } catch (NumberFormatException e) {
            System.err.println("Błąd danych wejściowych! Wpisz liczby.");
        }
    }
}