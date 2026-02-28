package agh.ics.oop.model;

import java.util.*;

public class Animal implements WorldElement {
    private MapDirection direction = MapDirection.NORTH;
    private Vector2d position;
    private int energy;
    private int age;
    private int childrenCount;
    private int plantsEaten = 0;
    private final List<Animal> offspring = new ArrayList<>();
    private final Genotype genotype;
    private Integer deadDay = null; // null oznacza, że żyje
    private final SimulationConfig config;

    public Animal(Vector2d position, SimulationConfig config) {
        this.position = position;
        this.genotype = new Genotype(config.genomeLength());
        this.direction = MapDirection.values()[new Random().nextInt(8)];
        this.config = config;
        this.energy = config.startingEnergy();
        this.age = 0;
        this.childrenCount = 0;
    }

    // Konstruktor prywatny dla dzieci
    private Animal(Vector2d position, int energy, Genotype genotype, SimulationConfig config) {
        this.position = position;
        this.energy = energy;
        this.genotype = genotype;
        this.config = config;
        this.direction = MapDirection.values()[new Random().nextInt(8)];
        this.age = 0;
        this.childrenCount = 0;
    }

    public void move(MoveValidator validator) {
        // Aktywacja genu następuje wewnątrz nextGene()
        int rotation = genotype.nextGene();
        for (int i = 0; i < rotation; i++) {
            this.direction = this.direction.next();
        }
        Vector2d newPosition = position.add(direction.toUnitVector());
        this.position = validator.validatePosition(newPosition);

        this.age++;
        this.energy -= config.loseEnergy();
    }
  
      public void eat(int energyFromPlant) {
        this.energy += energyFromPlant;
        this.plantsEaten++; // NOWE
    }

    public Animal reproduce(Animal partner){
        this.energy = this.energy - config.copulationEnergy();
        partner.energy = partner.energy - config.copulationEnergy();

        this.childrenCount++;
        partner.childrenCount++;

        Animal stronger = (this.energy > partner.energy) ? this : partner;
        Animal weaker = (this.energy > partner.energy) ? partner : this;

        double ratio = (double) stronger.energy / (stronger.energy + weaker.energy);
        Genotype childGenotype = new Genotype(stronger.genotype, weaker.genotype, ratio, config);

        Animal child = new Animal(this.position, config.copulationEnergy() * 2, childGenotype, config);

        // Zapisujemy dziecko w rodzicach (do śledzenia potomków)
        this.offspring.add(child);
        partner.offspring.add(child);

        return child;
    }

    // Algorytm liczenia wszystkich potomków (unikalnych)
    public int getDescendantsCount() {
        Set<Animal> uniqueDescendants = new HashSet<>();
        Queue<Animal> queue = new LinkedList<>(this.offspring);

        while (!queue.isEmpty()) {
            Animal current = queue.poll();
            if (uniqueDescendants.add(current)) {
                // jeśli tego zwierzaka jeszcze nie liczyliśmy, dodaj jego dzieci do kolejki
                queue.addAll(current.offspring);
            }
        }
        return uniqueDescendants.size();
    }

    public MapDirection getDirection() {
        return direction;
    }

    public void markAsDead(int day) {
        this.deadDay = day;
    }

    public boolean isDead() { return energy <= 0; }
    public boolean canReproduce() { return energy >= config.minEnergyToReproduce(); }
    public boolean isAt(Vector2d pos) { return position.equals(pos); }

    public Vector2d getPosition() { return position; }
    public int getEnergy() { return energy; }
    public int getAge() { return age; }
    public int getChildrenCount() { return childrenCount; }
    public int getPlantsEaten() { return plantsEaten; }
    public Integer getDeadDay() { return deadDay; }
    public Genotype getGenotype() { return genotype; }

    public int getActiveGene() {
        return genotype.getGenes().get(genotype.getCurrentGeneIndex());
    }

    @Override
    public String toString() { return direction.getShortcut(); }

    //metody potrzebne do testow, nie dajemy na public by  inne klasy nie mialy do tego dostepu
    void setAge(int age) {
        this.age = age;
    }

    void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }


}