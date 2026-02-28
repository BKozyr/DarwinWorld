package agh.ics.oop.model;
import agh.ics.oop.model.util.Boundary;
import agh.ics.oop.model.util.IncorrectPositionException;
import java.util.UUID;
import java.util.List;

public interface WorldMap extends MoveValidator {
    void place(Animal animal) throws IncorrectPositionException;
    void move(Animal animal, Vector2d oldPosition);
    boolean isOccupied(Vector2d position);
    WorldElement objectAt(Vector2d position);
    List<WorldElement> getElements();
    Boundary getCurrentBounds();
    UUID getId();
    void growPlants();
    void removeDeadAnimals();
    List<Animal> getAnimalsAt(Vector2d position);
    Plant plantAt(Vector2d position);
    void removePlant(Vector2d position);
    boolean isPreferred(Vector2d position);
}