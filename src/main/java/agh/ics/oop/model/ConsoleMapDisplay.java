package agh.ics.oop.model;

public class ConsoleMapDisplay implements MapChangeListener {
    private int updatesCount = 0;

    @Override
    public synchronized void mapChanged(WorldMap worldMap, String message) {
        updatesCount++;
        System.out.println("Map ID: " + worldMap.getId());
        System.out.println("Update #" + updatesCount + ": " + message);
        System.out.println(worldMap);
    }
}