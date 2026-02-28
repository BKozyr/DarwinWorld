package agh.ics.oop.model.util;

import agh.ics.oop.model.Vector2d;

import java.util.*;

public class RandomPositionGenerator implements Iterable<Vector2d>, Iterator<Vector2d> {
    private final int maxWidth;
    private final int maxHeight;
    private final int grassCount;

    // Licznik wygenerowanych już pozycji
    private int generatedCount = 0;

    // Mapa do śledzenia "zamienionych" indeksów (symulacja tablicy)
    private final Map<Integer, Integer> occupiedPositions = new HashMap<>();
    private final Random random = new Random();

    public RandomPositionGenerator(int maxWidth, int maxHeight, int grassCount) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.grassCount = grassCount;
    }

    @Override
    public Iterator<Vector2d> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return generatedCount < grassCount;
    }

    @Override
    public Vector2d next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // 1. Określamy zakres dostępnych indeksów.
        // Z każdym krokiem zakres "wirtualnej tablicy" się zmniejsza.
        int totalSlots = maxWidth * maxHeight;
        int range = totalSlots - generatedCount;

        // 2. Losujemy indeks w aktualnym zakresie
        int randomIndex = random.nextInt(range);

        // 3. Sprawdzamy, jaka wartość kryje się pod wylosowanym indeksem.
        // Jeśli indeks był już ruszany, bierzemy wartość z mapy.
        // Jeśli nie, wartością jest sam indeks.
        int selectedValue = occupiedPositions.getOrDefault(randomIndex, randomIndex);

        // 4. "Przenosimy" wartość z końca zakresu na miejsce wylosowanego indeksu.
        int endOfRangeIndex = range - 1;
        int valueAtEnd = occupiedPositions.getOrDefault(endOfRangeIndex, endOfRangeIndex);

        // Zapisujemy w mapie, że pod randomIndex znajduje się teraz wartość z końca zakresu
        occupiedPositions.put(randomIndex, valueAtEnd);

        // 5. Konwertujemy wylosowaną liczbę (selectedValue) na współrzędne 2D
        int x = selectedValue % maxWidth;
        int y = selectedValue / maxWidth;

        generatedCount++;

        return new Vector2d(x, y);
    }
}