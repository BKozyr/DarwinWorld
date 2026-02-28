package agh.ics.oop;

import agh.ics.oop.model.ConsoleMapDisplay;
import agh.ics.oop.model.CultivationMap;
import agh.ics.oop.model.MapVariant;
import agh.ics.oop.model.SimulationConfig;
import javafx.application.Application;

public class World {
    public static void main(String[] args) {
        System.out.println("System wystartował...");
        Application.launch(SimulationApp.class, args);
    }

    public static void start_new_sim(){
        System.out.println("Start systemu Darwin World...");

        SimulationConfig config = new SimulationConfig(5,5,10,10,3,10,10,100,20,10,10,10,10,2,4, MapVariant.CULTIVATION);

        // 2. Tworzymy Mapę
        CultivationMap map = new CultivationMap(config);
        ConsoleMapDisplay display = new ConsoleMapDisplay();
        map.registerObserver(display);

        Simulation simulation = new Simulation(map, config);

        Thread simulationThread = new Thread(simulation);
        simulationThread.start();


    }
    }
