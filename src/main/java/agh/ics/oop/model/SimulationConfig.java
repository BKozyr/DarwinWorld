package agh.ics.oop.model;

public record SimulationConfig(
        int width,
        int height,
        int plantEnergy,
        int plantsPerDay,
        int startAnimals,

        int minEnergyToCultivate,
        int fertilityDecayDays,
        int superPlantBites,

        int startingEnergy,
        int loseEnergy,
        int copulationEnergy,
        int minEnergyToReproduce,
        int genomeLength,
        int minMutations,
        int maxMutations,

        MapVariant mapVariant
) {}