package agh.ics.oop.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final String SEPARATOR = ";";
    private static final String PRESETS_DIR = "presets";

    public void saveConfig(SimulationConfig config, String name) throws IOException {
        Files.createDirectories(Path.of(PRESETS_DIR));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRESETS_DIR + "/" + name + ".csv"))) {
            writer.write("width" + SEPARATOR + config.width() + "\n");
            writer.write("height" + SEPARATOR + config.height() + "\n");
            writer.write("startAnimals" + SEPARATOR + config.startAnimals() + "\n");
            writer.write("plantEnergy" + SEPARATOR + config.plantEnergy() + "\n");
            writer.write("plantsPerDay" + SEPARATOR + config.plantsPerDay() + "\n");
            writer.write("startEnergy" + SEPARATOR + config.startingEnergy() + "\n");

            // Nowe pola
            writer.write("minEnergyToCultivate" + SEPARATOR + config.minEnergyToCultivate() + "\n");
            writer.write("fertilityDecayDays" + SEPARATOR + config.fertilityDecayDays() + "\n");
            writer.write("superPlantBites" + SEPARATOR + config.superPlantBites() + "\n");

            writer.write("mapVariant" + SEPARATOR + config.mapVariant().name() + "\n");
        }
    }

    public SimulationConfig loadConfig(String name) throws IOException {
        Path path = Path.of(PRESETS_DIR + "/" + name + ".csv");
        if (!Files.exists(path)) return null;

        int w=20, h=20, sa=10, pe=10, ppd=5, se=20;
        int metc=5, fdd=10, spb=3; // Domyślne dla nowych pól
        MapVariant mv = MapVariant.STANDARD;

        List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            String[] parts = line.split(SEPARATOR);
            if (parts.length < 2) continue;
            String key = parts[0];
            String val = parts[1];

            try {
                switch (key) {
                    case "width" -> w = Integer.parseInt(val);
                    case "height" -> h = Integer.parseInt(val);
                    case "startAnimals" -> sa = Integer.parseInt(val);
                    case "plantEnergy" -> pe = Integer.parseInt(val);
                    case "plantsPerDay" -> ppd = Integer.parseInt(val);
                    case "startEnergy" -> se = Integer.parseInt(val);
                    case "minEnergyToCultivate" -> metc = Integer.parseInt(val);
                    case "fertilityDecayDays" -> fdd = Integer.parseInt(val);
                    case "superPlantBites" -> spb = Integer.parseInt(val);
                    case "mapVariant" -> mv = MapVariant.valueOf(val);
                }
            } catch (Exception e) { /* ignoruj */ }
        }
        return new SimulationConfig(w, h, pe, ppd, sa, metc, fdd, spb, se, 1, 10, 5, 5, 2, 4, mv);
    }

    public List<String> getAvailablePresets() {
        List<String> results = new ArrayList<>();
        File folder = new File(PRESETS_DIR);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));
            if (files != null) {
                for (File file : files) {
                    results.add(file.getName().replace(".csv", ""));
                }
            }
        }
        return results;
    }
}