package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CultivationMap extends AbstractWorldMap implements MoveValidator{
    private final SimulationConfig config;

    private final Map<Vector2d, Integer> fertilityMap = new ConcurrentHashMap<>();
    private final Map<Vector2d, Plant> plants = new ConcurrentHashMap<>();

    public CultivationMap(SimulationConfig config) {
        this.config = config;
    }

    @Override
    public Vector2d validatePosition(Vector2d position) {
        return step(position);
    }

    @Override
    public void move(Animal animal, Vector2d oldPosition) {
        super.move(animal, oldPosition);

        // Jeśli zwierzak jest silny to użyźnia glebę
        if (animal.getEnergy() >= config.minEnergyToCultivate()) {
            fertilityMap.put(oldPosition, config.fertilityDecayDays());
        }
    }

    private Vector2d step(Vector2d position) {
        int x = position.getX();
        int y = position.getY();
        int width = config.width();
        int height = config.height();
        if (x < 0) x = width - 1; else if (x >= width) x = 0;
        if (y < 0) y = 0; else if (y >= height) y = height - 1;
        return new Vector2d(x, y);
    }

    @Override
    public boolean isPreferred(Vector2d position) {
        // Pole jest preferowane, jeśli licznik dni jest większy od 0
        return fertilityMap.getOrDefault(position, 0) > 0;
    }

    public void growPlants() {
        // 1. zmniejszamy liczniki żyzności (GLEBA UMIERA, jeśli nikt po niej nie chodzi)
        fertilityMap.replaceAll((pos, days) -> days - 1);
        fertilityMap.values().removeIf(days -> days <= 0);

        // 2. sadzenie roślin
        int plantsToGrow = config.plantsPerDay();
        List<Vector2d> preferredFields = new ArrayList<>();
        List<Vector2d> otherFields = new ArrayList<>();

        for (int x = 0; x < config.width(); x++) {
            for (int y = 0; y < config.height(); y++) {
                Vector2d pos = new Vector2d(x, y);
                if (!plants.containsKey(pos)) {
                    if (isPreferred(pos)) preferredFields.add(pos);
                    else otherFields.add(pos);
                }
            }
        }
        Random random = new Random();
        for (int i = 0; i < plantsToGrow; i++) {
            if (preferredFields.isEmpty() && otherFields.isEmpty()) break;
            Vector2d selectedPos;
            boolean chosenPreferred = false;

            if (random.nextDouble() < 0.8 && !preferredFields.isEmpty()) {
                selectedPos = preferredFields.remove(random.nextInt(preferredFields.size()));
                chosenPreferred = true;
            } else if (!otherFields.isEmpty()) {
                selectedPos = otherFields.remove(random.nextInt(otherFields.size()));
            } else {
                if (!preferredFields.isEmpty()) {
                    selectedPos = preferredFields.remove(random.nextInt(preferredFields.size()));
                    chosenPreferred = true;
                } else break;
            }

            // Jeśli wyrosła na żyznym to ma więcej gryzów
            int bites = chosenPreferred ? config.superPlantBites() : 1;
            plants.put(selectedPos, new Plant(selectedPos, bites));
        }
    }

    public int getFertility(Vector2d position) { return fertilityMap.getOrDefault(position, 0); }
    public Plant plantAt(Vector2d position) { return plants.get(position); }
    public void removePlant(Vector2d position) { plants.remove(position); }

    @Override
    public WorldElement objectAt(Vector2d position) {
        WorldElement animal = super.objectAt(position);
        if (animal != null) return animal;
        return plants.get(position);
    }

    @Override
    public List<WorldElement> getElements() {
        List<WorldElement> elements = super.getElements();
        elements.addAll(plants.values());
        return elements;
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0, 0), new Vector2d(config.width() - 1, config.height() - 1));
    }
}