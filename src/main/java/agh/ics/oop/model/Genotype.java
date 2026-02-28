package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Genotype {
    private final ArrayList<Integer> genes;
    private final Random random = new Random();
    private int currentGeneIndex;

    public Genotype(int size){
        this.genes = new ArrayList<>(size);
        for(int i=0; i< size; i++){
            genes.add(random.nextInt(8));
        }
        this.currentGeneIndex = random.nextInt(size);
    }

    public Genotype(Genotype strongerAnimal, Genotype weakerAnimal, double ratio, SimulationConfig config){
        int size = config.genomeLength();
        this.genes = new ArrayList<>(size);

        boolean strongIsLeft = random.nextBoolean();

        int genesFromStrong = (int) (ratio * size);

        if (genesFromStrong > size) genesFromStrong = size;
        if (genesFromStrong < 0) genesFromStrong = 0;

        int genesFromWeak = size - genesFromStrong;

        if (strongIsLeft){
            genes.addAll(strongerAnimal.genes.subList(0, genesFromStrong));
            genes.addAll(weakerAnimal.genes.subList(size - genesFromWeak, size));
        }
        else{
            genes.addAll(weakerAnimal.genes.subList(0, genesFromWeak));
            genes.addAll(strongerAnimal.genes.subList(size - genesFromStrong, size));
        }

        mutations(config.minMutations(), config.maxMutations());
        this.currentGeneIndex = random.nextInt(size);
    }

    public void mutations(int min, int max){
        int amountOfMutations = random.nextInt(max-min+1) + min;
        int size = genes.size();

        List<Integer> allIndexes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            allIndexes.add(i);
        }

        for (int i=0; i<amountOfMutations; i++){
            if(allIndexes.isEmpty()) break;
            int hitIndex = random.nextInt(allIndexes.size());
            int geneIndexToMutate = allIndexes.get(hitIndex);

            allIndexes.set(hitIndex, allIndexes.get(allIndexes.size() - 1));
            allIndexes.remove(allIndexes.size() - 1);

            genes.set(geneIndexToMutate, random.nextInt(8));
        }
    }

    public int nextGene(){
        if (genes.isEmpty()) return 0;
        int currentGene = genes.get(currentGeneIndex);
        currentGeneIndex = (currentGeneIndex + 1) % genes.size();
        return currentGene;
    }

    public ArrayList<Integer> getGenes() {
        return genes;
    }

    public int getCurrentGeneIndex() {
        return currentGeneIndex;
    }
}