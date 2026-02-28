import agh.ics.oop.Simulation;
import agh.ics.oop.model.Animal;
import agh.ics.oop.model.CultivationMap;
import agh.ics.oop.model.MapVariant;
import agh.ics.oop.model.SimulationConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class SimulationTest{

    @Test
    void checkStartingAmountOfAnimals(){
        SimulationConfig standardConfig = new SimulationConfig(
                5,
                5,
                20,
                4,
                2,
                40,
                3,
                100,
                10,
                40,
                40,
                7,
                2,
                4,
                4,
                MapVariant.STANDARD
        );

        CultivationMap map = new CultivationMap(standardConfig);
        Simulation simulation = new Simulation(map, standardConfig);

        List<Animal> animals = simulation.getAnimals();
        assertEquals(animals.size(), standardConfig.startAnimals());
    }

    @Test
    void animalsDie() throws InterruptedException {
        SimulationConfig deadlyConfig = new SimulationConfig(
                5,
                5,
                20,
                4,
                2,
                40,
                3,
                10,
                10,
                40,
                40,
                7,
                2,
                4,
                4,
                MapVariant.STANDARD
        );

        CultivationMap map = new CultivationMap(deadlyConfig);
        Simulation simulation = new Simulation( map, deadlyConfig);

        Thread thread = new Thread(simulation);
        thread.start();
        Thread.sleep(500);
        simulation.stop();
        thread.join();

        List<Animal> animals = simulation.getAnimals();
        assertEquals(0, animals.size());
    }

    @Test
    void animalsReproduce() throws InterruptedException {
        SimulationConfig reproduceConfig = new SimulationConfig(
                3,
                3,
                40,
                4,
                10,
                40,
                3,
                100,
                100,
                20,
                0,
                7,
                2,
                4,
                5,
                MapVariant.STANDARD
        );

        CultivationMap map = new CultivationMap(reproduceConfig);
        Simulation simulation = new Simulation(map, reproduceConfig);
        Thread thread = new Thread(simulation);


        thread.start();
        Thread.sleep(1000);
        simulation.stop();
        thread.join();

        List<Animal> animals = simulation.getAnimals();
        // Sprawdzamy czy populacja zwiększyła sie
        assertTrue(animals.size() > 10);
    }
    }
