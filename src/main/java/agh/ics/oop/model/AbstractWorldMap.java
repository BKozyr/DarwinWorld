package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractWorldMap implements WorldMap {
    protected final Map<Vector2d, List<Animal>> animals = new ConcurrentHashMap<>();
    protected final MapVisualizer vis = new MapVisualizer(this);

    private final UUID id = UUID.randomUUID();
    private final List<MapChangeListener> observers = new ArrayList<>();

    @Override
    public UUID getId() {
        return id;
    }

    public void registerObserver(MapChangeListener observer) {
        observers.add(observer);
    }

    public void deregisterObserver(MapChangeListener observer) {
        observers.remove(observer);
    }

    protected void mapChanged(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this, message);
        }
    }

    @Override
    public abstract Boundary getCurrentBounds();

    @Override
    public void place(Animal animal){
        Vector2d position = animal.getPosition();
        // Bezpieczne dodawanie listy
        animals.putIfAbsent(position, new CopyOnWriteArrayList<>());
        animals.get(position).add(animal);
        mapChanged("Umieszczono zwierzaka na " + position);
    }

    @Override
    public void move(Animal animal, Vector2d oldPosition) {
        List<Animal> oldList = animals.get(oldPosition);
        if (oldList != null) {
            oldList.remove(animal);
            if (oldList.isEmpty()) {
                animals.remove(oldPosition);
            }
        }
        placeWithoutException(animal);
        mapChanged("Przesunięto zwierzaka na " + animal.getPosition());
    }

    private void placeWithoutException(Animal animal) {
        Vector2d pos = animal.getPosition();
        animals.putIfAbsent(pos, new CopyOnWriteArrayList<>());
        animals.get(pos).add(animal);
    }

    public void removeDeadAnimals() {
        for (List<Animal> animalList : animals.values()) {
            List<Animal> dead = new ArrayList<>();
            for (Animal animal : animalList) {
                if (animal.isDead()) {
                    dead.add(animal);
                }
            }
            animalList.removeAll(dead);
        }
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        List<Animal> list = animals.get(position);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }


    @Override
    public List<WorldElement> getElements() {
        List<WorldElement> allElements = new ArrayList<>();
        for (List<Animal> list : animals.values()) {
            allElements.addAll(list);
        }
        return allElements;
    }

    @Override
    public String toString() {
        Boundary bound = getCurrentBounds();
        return vis.draw(bound.lowerLeft(), bound.upperRight());
    }

    public List<Animal> getAnimalsAt(Vector2d position){
        List<Animal> list = animals.get(position);
        if(list == null){
            return new ArrayList<>();
        }
        return list;
    }
}