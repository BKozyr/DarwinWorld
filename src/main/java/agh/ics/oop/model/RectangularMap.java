package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RectangularMap extends AbstractWorldMap {
    private final SimulationConfig config;
    private final Random random = new Random();
    private final Map<Vector2d, Plant> plants = new ConcurrentHashMap<>();

    public RectangularMap(SimulationConfig config) {
        this.config = config;
    }

    @Override
    public Vector2d validatePosition(Vector2d position) {
        if (position.precedes(new Vector2d(config.width() - 1, config.height() - 1))
                && position.follows(new Vector2d(0, 0))) {
            return position;
        }
        int x = Math.max(0, Math.min(position.getX(), config.width() - 1));
        int y = Math.max(0, Math.min(position.getY(), config.height() - 1));
        return new Vector2d(x, y);
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0, 0), new Vector2d(config.width() - 1, config.height() - 1));
    }

    @Override
    public boolean isPreferred(Vector2d position) {
        Boundary bounds = getCurrentBounds();
        int height = bounds.upperRight().getY() - bounds.lowerLeft().getY() + 1;

        int jungleHeight = (int) (height * 0.2);
        int jungleLowerY = bounds.lowerLeft().getY() + (height - jungleHeight) / 2;
        int jungleUpperY = jungleLowerY + jungleHeight;

        return position.getY() >= jungleLowerY && position.getY() <= jungleUpperY;
    }

    @Override
    public void growPlants() {
        int plantsToGrow = config.plantsPerDay();
        int width = config.width();
        int height = config.height();

        for (int i = 0; i < plantsToGrow * 2; i++) {
            Vector2d pos = new Vector2d(random.nextInt(width), random.nextInt(height));
            if (!plants.containsKey(pos)) {
                boolean preferred = isPreferred(pos);
                if (preferred && random.nextDouble() < 0.8) {
                    plants.put(pos, new Plant(pos));
                } else if (!preferred && random.nextDouble() < 0.2) {
                    plants.put(pos, new Plant(pos));
                } else {
                    plants.put(pos, new Plant(pos));
                }
            }
        }
    }

    @Override
    public Plant plantAt(Vector2d position) { return plants.get(position); }

    @Override
    public void removePlant(Vector2d position) { plants.remove(position); }

    @Override
    public WorldElement objectAt(Vector2d position) {
        WorldElement animal = super.objectAt(position);
        if (animal != null) return animal;
        return plants.get(position);
    }

//    @Override
//    public boolean canMoveTo(Vector2d position) {
//        return position.follows(lowerLeft)
//                && position.precedes(upperRight)
//                && !super.isOccupied(position);
//    }

    @Override
    public List<WorldElement> getElements() {
        List<WorldElement> elements = super.getElements();
        elements.addAll(plants.values());
        return elements;
    }
}