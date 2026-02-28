package agh.ics.oop.model;

public class Plant implements WorldElement {
    private final Vector2d position;
    private int remainingBites;

    // Konstruktor dla "super rośliny"
    public Plant(Vector2d position, int remainingBites) {
        this.position = position;
        this.remainingBites = remainingBites;
    }

    // Konstruktor domyślny (zwykła trawa = 1 ugryzienie)
    public Plant(Vector2d position) {
        this(position, 1);
    }

    public void decreaseBite() {
        this.remainingBites--;
    }

    public boolean isConsumed() {
        return this.remainingBites <= 0;
    }

    public int getRemainingBites() {
        return remainingBites;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public String toString() {
        // Jeśli roślina ma więcej niż 1 życie, oznaczamy ją inaczej
        return remainingBites > 1 ? "**" : "*";
    }
}