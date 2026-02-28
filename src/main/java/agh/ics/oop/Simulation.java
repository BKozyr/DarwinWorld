package agh.ics.oop;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.IncorrectPositionException;
import agh.ics.oop.model.util.Boundary;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Simulation implements Runnable {
    private final List<Animal> animals;
    private final SimulationConfig config;
    private final WorldMap map;
    private int day = 0;

    private volatile boolean running = true;
    private boolean paused = false;
    private final Object pauseLock = new Object();

    private double totalLifeSpanOfDead = 0;
    private int deadAnimalsCount = 0;

    private BufferedWriter csvWriter;

    public Simulation(WorldMap map, SimulationConfig config) {
        this.map = map;
        this.config = config;
        this.animals = new ArrayList<>();
        spawnStartingAnimals();
        initCsvFile();
    }

    private void initCsvFile() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String fileName = "stats_" + map.getId().toString().substring(0, 5) + "_" + timestamp + ".csv";
        try {
            csvWriter = new BufferedWriter(new FileWriter(fileName));
            csvWriter.write("Day;Animals;Plants;FreeFields;AvgEnergy;AvgLifeSpan;AvgChildren\n");
        } catch (IOException e) {
            System.err.println("Nie udało się utworzyć pliku CSV: " + e.getMessage());
        }
    }

    private void saveStatsToCsv(SimulationStats stats) {
        if (csvWriter == null) return;
        try {
            String line = String.format("%d;%d;%d;%d;%.2f;%.2f;%.2f\n",
                    stats.day(), stats.animalCount(), stats.plantCount(), stats.freeFields(),
                    stats.avgEnergy(), stats.avgLifeSpan(), stats.avgChildren());
            csvWriter.write(line);
            csvWriter.flush();
        } catch (IOException e) {
            System.err.println("Błąd zapisu do CSV: " + e.getMessage());
        }
    }

    private void spawnStartingAnimals() {
        for (int i = 0; i < config.startAnimals(); i++) {
            Vector2d pos = new Vector2d(
                    (int)(Math.random() * config.width()),
                    (int)(Math.random() * config.height())
            );
            Animal animal = new Animal(pos, config);
            try {
                map.place(animal);
                animals.add(animal);
            } catch (IncorrectPositionException e) { }
        }
    }

    @Override
    public void run() {
        while (running) {
            synchronized (pauseLock) {
                while (paused && running) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
            if (!running) break;

            day++;

            // 1. martwe
            map.removeDeadAnimals();
            Iterator<Animal> it = animals.iterator();
            while (it.hasNext()) {
                Animal animal = it.next();
                if (animal.isDead()) {
                    animal.markAsDead(day);
                    totalLifeSpanOfDead += animal.getAge();
                    deadAnimalsCount++;
                    it.remove();
                }
            }

            // 2. ruch
            for (Animal animal : animals) {
                Vector2d oldPos = animal.getPosition();
                animal.move(map);
                map.move(animal, oldPos);
            }

            // 3. konflikty
            Set<Vector2d> occupiedPositions = new HashSet<>();
            for (Animal a : animals) occupiedPositions.add(a.getPosition());

            for (Vector2d position : occupiedPositions) {
                List<Animal> animalsOnField = new ArrayList<>(map.getAnimalsAt(position));
                if (animalsOnField.isEmpty()) continue;

                animalsOnField.sort((a1, a2) -> {
                    if (a1.getEnergy() != a2.getEnergy()) return a2.getEnergy() - a1.getEnergy();
                    if (a1.getAge() != a2.getAge()) return a2.getAge() - a1.getAge();
                    return a2.getChildrenCount() - a1.getChildrenCount();
                });

                Plant plant = map.plantAt(position);
                if (plant != null) {
                    animalsOnField.get(0).eat(config.plantEnergy());
                    map.removePlant(position);
                }

                if (animalsOnField.size() >= 2) {
                    Animal p1 = animalsOnField.get(0);
                    Animal p2 = animalsOnField.get(1);
                    if (p1.canReproduce() && p2.canReproduce()) {
                        Animal child = p1.reproduce(p2);
                        try {
                            map.place(child);
                            animals.add(child);
                        } catch (IncorrectPositionException e) { }
                    }
                }
            }

            map.growPlants();
            saveStatsToCsv(getStats());

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) { break; }
        }

        try { if (csvWriter != null) csvWriter.close(); } catch (IOException e) {}
    }

    public void pause() { paused = true; }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public boolean isPaused() { return paused; }

    public void stop() {
        this.running = false;
        resume();
    }

    public List<Animal> getAnimals() { return Collections.unmodifiableList(animals); }
    public int getDay() { return day; }
    public SimulationConfig getConfig() { return config; }

    public SimulationStats getStats() {
        int animalsAlive = animals.size();
        int plantsCount = map.getElements().stream().filter(e -> e instanceof Plant).toList().size();

        Boundary bounds = map.getCurrentBounds();
        int totalCells = (bounds.upperRight().getX() + 1) * (bounds.upperRight().getY() + 1);
        long occupiedCount = map.getElements().stream().map(WorldElement::getPosition).distinct().count();
        int freeFields = Math.max(0, totalCells - (int)occupiedCount);

        double avgEnergy = 0;
        double avgChildren = 0;
        Map<List<Integer>, Integer> genotypeCounts = new HashMap<>();

        for (Animal a : animals) {
            avgEnergy += a.getEnergy();
            avgChildren += a.getChildrenCount();
            List<Integer> genes = a.getGenotype().getGenes();
            genotypeCounts.put(genes, genotypeCounts.getOrDefault(genes, 0) + 1);
        }

        if (animalsAlive > 0) {
            avgEnergy /= animalsAlive;
            avgChildren /= animalsAlive;
        }

        double avgLifeSpan = (deadAnimalsCount > 0) ? (totalLifeSpanOfDead / deadAnimalsCount) : 0;

        List<Integer> dominantGenotype = null;
        int maxCount = -1;
        for (Map.Entry<List<Integer>, Integer> entry : genotypeCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                dominantGenotype = entry.getKey();
            }
        }

        return new SimulationStats(
                day, animalsAlive, plantsCount, freeFields, avgEnergy, avgLifeSpan, avgChildren, dominantGenotype
        );
    }
}