package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CultivationMapTest {

    @Test
    public void checkBounds(){
        SimulationConfig config = new SimulationConfig(10,10,0,0,0,0,0,0,0,0,0,0,0,0,0,MapVariant.STANDARD);
        CultivationMap map = new CultivationMap(config);

        //probujemy wyjsc za lewa granice mapy, ladujemy po prawej stronie
        for(int i =0; i<config.height();i++){
            assertEquals(map.validatePosition(new Vector2d(-1,i)), new Vector2d(config.width()-1,i)) ;
        }
        //probujemy wyjsc za prawa granice mapy, ladujemy po lewej stronie
        for(int i =0; i<config.height();i++){
            assertEquals(map.validatePosition(new Vector2d(config.width(),i)), new Vector2d(0,i)) ;
        }

        // probujemy wyjsc poza gorna granice mapy, blokuje nas
        for(int i =0; i<config.width();i++){
            assertEquals(map.validatePosition(new Vector2d(i,config.height())), new Vector2d(i,config.height()-1)) ;
        }

        // probujemy wyjsc poza dolna granice mapy, blokuje nas
        for(int i =0; i<config.width();i++){
            assertEquals(map.validatePosition(new Vector2d(i,-1)), new Vector2d(i,0)) ;
        }
    }

    @Test
    public void checkVertilityPositive(){
        SimulationConfig config = new SimulationConfig(10,10,0,0,0,40,4,100,100,0,0,4,5,0,4,MapVariant.CULTIVATION);
        CultivationMap map = new CultivationMap(config);

        Animal animal = new Animal(new Vector2d(2,2), config);
        map.place(animal);
        Vector2d oldPostition = animal.getPosition();
        animal.move(map);
        map.move(animal, oldPostition);
        IO.println(map.getFertility(animal.getPosition()));
        assertTrue(map.getFertility(oldPostition) > 0);

    }

    @Test
    public void checkVertilityWithWeakAnimal(){
        SimulationConfig config = new SimulationConfig(10,10,0,0,0,40,0,20,10,0,0,4,5,0,4,MapVariant.CULTIVATION);
        CultivationMap map = new CultivationMap(config);

        Animal weakAnimal = new Animal(new Vector2d(2,2), config);
        map.place(weakAnimal);
        Vector2d oldPostition = weakAnimal.getPosition();
        weakAnimal.move(map);
        map.move(weakAnimal, oldPostition);
        IO.println(map.getFertility(weakAnimal.getPosition()));
        assertEquals(0, map.getFertility(oldPostition));

    }

    @Test
    public void checkGrowPlants(){
        SimulationConfig config = new SimulationConfig(10,10,10,4,0,40,0,100,10,0,0,4,0,0,4,MapVariant.CULTIVATION);
        CultivationMap map = new CultivationMap(config);

        List<WorldElement> elements = map.getElements();
        map.growPlants();
        List<WorldElement> elements2 = map.getElements();

        assertEquals(4,elements2.size());
    }
}
