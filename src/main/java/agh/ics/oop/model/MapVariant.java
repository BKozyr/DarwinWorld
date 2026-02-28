package agh.ics.oop.model;

public enum MapVariant {
    STANDARD("Wariant Podstawowy (Prostokąt)"),
    CULTIVATION("Wariant C (Żyzne Ścieżki)");

    private final String label;

    MapVariant(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}