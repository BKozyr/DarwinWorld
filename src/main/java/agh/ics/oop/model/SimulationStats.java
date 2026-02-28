package agh.ics.oop.model;

import java.util.List;

public record SimulationStats(
        int day,
        int animalCount,
        int plantCount,
        int freeFields,
        double avgEnergy,
        double avgLifeSpan,
        double avgChildren,
        List<Integer> dominantGenotype
) {}