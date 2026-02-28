package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {

    private final SimulationConfig config = new SimulationConfig(
            10, 10,
            10,
            2,
            5,
            20,
            5,
            100,
            100,
            20,
            20,
            5,
            8, 2,4,
            MapVariant.STANDARD
    );

    @Test
    void LoseEnergyOnMove() {
        // Given
        Animal animal = new Animal(new Vector2d(2, 2), config);
        int initialEnergy = animal.getEnergy();

        // akceptuejmy kazdy ruch
        MoveValidator validator = pos -> pos;

        animal.move(validator);


        // Miał 100, koszt ruchu to 10 -> powinien mieć 90
        assertEquals(initialEnergy - config.loseEnergy(), animal.getEnergy());
    }

    @Test
    void DeadWhenEnergyIsZeroOrLess() {
        Animal animal = new Animal(new Vector2d(2, 2), config);
        MoveValidator validator = pos -> pos;


        while (animal.getEnergy() > 0) {
            animal.move(validator);
        }

        assertTrue(animal.isDead());
    }

    @Test
    void shouldReproduceCorrectly() {
        Animal parent1 = new Animal(new Vector2d(2, 2), config); // Energy 100
        Animal parent2 = new Animal(new Vector2d(2, 2), config); // Energy 100


        // Rodzice tracą po 20 energii (copulationEnergy)
        Animal child = parent1.reproduce(parent2);

        // 1. Sprawdzamy rodziców: 100 - 20 = 80
        assertEquals(80, parent1.getEnergy());
        assertEquals(80, parent2.getEnergy());

        // 2. Sprawdzamy dziecko: Dostaje 2 * 20 = 40 energii
        assertEquals(40, child.getEnergy());

        // 3. Sprawdzamy czy rodzicom przybyło dziecko w statystykach
        assertEquals(1, parent1.getChildrenCount());
        assertEquals(1, parent2.getChildrenCount());

        // 4. Genotyp dziecka musi mieć poprawną długość
        assertEquals(config.genomeLength(), child.getGenotype().getGenes().size());
    }

    @Test
    void shouldCheckReproductionThreshold() {

        // Ustawiamy zwierzakowi mało energii (40), a próg to 50
        SimulationConfig strictConfig = new SimulationConfig(
                10, 10, 10, 2, 5, 30, 5,
                40,
                40,
                20,
                50,
                50, 8, 2,4,
                MapVariant.STANDARD
        );

        Animal weakAnimal = new Animal(new Vector2d(2,2), strictConfig);

        assertFalse(weakAnimal.canReproduce());
    }

    @Test
    void shouldRotateAndMoveAccordingToGenotype() {

        Animal animal = new Animal(new Vector2d(2, 2), config);

        // Pobieramy jego aktualny kierunek
        MapDirection startDirection = animal.getDirection();

        // Pobieramy genotyp, listę genów i aktualny indeks.
        Genotype genotype = animal.getGenotype();
        int currentIndex = genotype.getCurrentGeneIndex();
        int expectedRotation = genotype.getGenes().get(currentIndex);

        // Obliczamy ręcznie, jaki powinien być nowy kierunek
        MapDirection expectedDirection = startDirection;
        for (int i = 0; i < expectedRotation; i++) {
            expectedDirection = expectedDirection.next();
        }


        MoveValidator validator = new MoveValidator() {
            @Override
            public Vector2d validatePosition(Vector2d position) {
                return position;
            }
        };

        animal.move(validator);

        // Sprawdzamy czy obrócił się zgodnie z genem
        assertEquals(expectedDirection, animal.getDirection());

        // Sprawdzamy czy przesunął się w tym nowym kierunku kierunku
        Vector2d expectedPosition = new Vector2d(2, 2).add(expectedDirection.toUnitVector());
        assertEquals(expectedPosition, animal.getPosition());
    }

    @Test
    public void testHierarchyByEnergy(){
        SimulationConfig hierarchyConfig = new SimulationConfig(
                10, 10,
                10,
                2,
                5,
                0,
                5,
                0,
                10,
                20,
                50,
                5,
                8, 2,4,
                MapVariant.STANDARD
        );;
        Animal strongAnimal = new Animal(new Vector2d(2,2), hierarchyConfig);
        strongAnimal.eat(100);
        // weakAnimal ma 0 energii
        Animal weakAnimal = new Animal(new Vector2d(2,2), hierarchyConfig);
        Animal mediumAnimal = new Animal(new Vector2d(2,2), hierarchyConfig);
        mediumAnimal.eat(50);

        //kopiujemy czesc kodu z simulation
        List<Animal> animalsOnField = new ArrayList<>(List.of(mediumAnimal, weakAnimal, strongAnimal));

        // Sortowanie: najsilniejszy na poczatku listy
        // Kryteria: Energia (malejąco) -> Wiek (malejąco) -> Liczba dzieci (malejąco)
        animalsOnField.sort((a1, a2) -> {
            if (a1.getEnergy() != a2.getEnergy()) {
                return a2.getEnergy() - a1.getEnergy();
            }
            if (a1.getAge() != a2.getAge()) {
                return a2.getAge() - a1.getAge();
            }
            return a2.getChildrenCount() - a1.getChildrenCount();
        });

        assertEquals(animalsOnField.get(0), strongAnimal);
        assertEquals(animalsOnField.get(1), mediumAnimal);
        assertEquals(animalsOnField.get(2), weakAnimal);
    }


    @Test
    public void testHierarchyByAge(){
        SimulationConfig hierarchyConfig = new SimulationConfig(
                10, 10,
                10,
                2,
                5,
                0,
                5,
                0,
                10,
                20,
                50,
                5,
                8, 2,4,
                MapVariant.STANDARD
        );

        Animal strongAnimal = new Animal(new Vector2d(2,2), hierarchyConfig);
        strongAnimal.setAge(100);
        Animal weakAnimal = new Animal(new Vector2d(2,2), hierarchyConfig);
        Animal mediumAnimal = new Animal(new Vector2d(2,2), hierarchyConfig);
        mediumAnimal.setAge(50);


        List<Animal> animalsOnField = new ArrayList<>(List.of(mediumAnimal, weakAnimal, strongAnimal));


        animalsOnField.sort((a1, a2) -> {
            if (a1.getEnergy() != a2.getEnergy()) {
                return a2.getEnergy() - a1.getEnergy();
            }
            if (a1.getAge() != a2.getAge()) {
                return a2.getAge() - a1.getAge();
            }
            return a2.getChildrenCount() - a1.getChildrenCount();
        });

        assertEquals(animalsOnField.get(0), strongAnimal);
        assertEquals(animalsOnField.get(1), mediumAnimal);
        assertEquals(animalsOnField.get(2), weakAnimal);

        //drugi test


        strongAnimal.setAge(1);
        mediumAnimal.setAge(50);
        weakAnimal.setAge(100);

        animalsOnField.sort((a1, a2) -> {
            if (a1.getEnergy() != a2.getEnergy()) {
                return a2.getEnergy() - a1.getEnergy();
            }
            if (a1.getAge() != a2.getAge()) {
                return a2.getAge() - a1.getAge();
            }
            return a2.getChildrenCount() - a1.getChildrenCount();
        });

        assertEquals(animalsOnField.get(0), weakAnimal);
        assertEquals(animalsOnField.get(1), mediumAnimal);
        assertEquals(animalsOnField.get(2), strongAnimal);
    }

    @Test
    public void testHierarchyByChilderAmount(){
        SimulationConfig hierarchyConfig = new SimulationConfig(
                10, 10,
                10,
                2,
                5,
                0,
                5,
                0,
                10,
                20,
                50,
                5,
                8, 2,4,
                MapVariant.STANDARD
        );;
        Animal strongAnimal = new Animal(new Vector2d(2,2), hierarchyConfig);
        strongAnimal.setChildrenCount(10);
        Animal weakAnimal = new Animal(new Vector2d(2,2), hierarchyConfig);
        Animal mediumAnimal = new Animal(new Vector2d(2,2), hierarchyConfig);
        mediumAnimal.setChildrenCount(5);

        List<Animal> animalsOnField = new ArrayList<>(List.of(mediumAnimal, weakAnimal, strongAnimal));

        animalsOnField.sort((a1, a2) -> {
            if (a1.getEnergy() != a2.getEnergy()) {
                return a2.getEnergy() - a1.getEnergy();
            }
            if (a1.getAge() != a2.getAge()) {
                return a2.getAge() - a1.getAge();
            }
            return a2.getChildrenCount() - a1.getChildrenCount();
        });

        assertEquals(animalsOnField.get(0), strongAnimal);
        assertEquals(animalsOnField.get(1), mediumAnimal);
        assertEquals(animalsOnField.get(2), weakAnimal);
    }

    @Test
    void childInheritGenesFromParents() {
        // Bez mutacji
        SimulationConfig bioConfig = new SimulationConfig(
                10, 10, 10, 2, 5, 0, 5, 100, 10, 20, 50,
                10,
                8, 0,0, MapVariant.STANDARD
        );

        Animal animal1 = new Animal(new Vector2d(2,2), bioConfig);
        Animal animal2 = new Animal(new Vector2d(2,2), bioConfig);
        //zwierzeta maja te samą energie, wiec ratio = 0.5

        int size = bioConfig.genomeLength();
        IO.println(size);
        Animal child = animal1.reproduce(animal2);
        ArrayList genesA = new ArrayList(size);

        //Opcja A
        IO.println("OPCJA A:");
        genesA.addAll(animal1.getGenotype().getGenes().subList(0, size/2));
        IO.println("Geny rodzica 1:" + animal1.getGenotype().getGenes().subList(0, size/2));
        genesA.addAll(animal2.getGenotype().getGenes().subList(size/2, size));
        IO.println("Geny rodzica 2:" + animal2.getGenotype().getGenes().subList(size/2, size));

        //Opcja B
        IO.println("OPCJA B:");
        ArrayList genesB = new ArrayList(size);
        genesB.addAll(animal2.getGenotype().getGenes().subList(0, size/2));
        IO.println("Geny rodzica 1:" + animal2.getGenotype().getGenes().subList(0, size/2));
        genesB.addAll(animal1.getGenotype().getGenes().subList(size/2, size));
        IO.println("Geny rodzica 2:" + animal1.getGenotype().getGenes().subList(size/2, size));

        IO.println("Geny dziecka 2:" + child.getGenotype().getGenes());
        assertTrue(child.getGenotype().getGenes().equals(genesA) || child.getGenotype().getGenes().equals(genesB));
    }
}