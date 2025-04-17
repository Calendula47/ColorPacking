import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// 个体
class Individual {
    private List<Integer> genes;

    public Individual(int itemCount) {
        this.genes = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            genes.add(i);
        }
        Collections.shuffle(genes, new Random());
    }

    public List<Integer> getGenes() {
        return genes;
    }

    public void setGenes(List<Integer> genes) {
        this.genes = genes;
    }
}

// 种群
class Population {
    private List<Individual> individuals;

    public Population(int populationSize, int itemCount) {
        this.individuals = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            individuals.add(new Individual(itemCount));
        }
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }

    public Individual getIndividual(int index) {
        return individuals.get(index);
    }

    public void setIndividual(int index, Individual individual) {
        individuals.set(index, individual);
    }
}

// 遗传算法求解器
public class GeneticAlgorithmSolver {
    private static final int POPULATION_SIZE = 100;
    private static final int GENERATIONS = 500;
    private static final double MUTATION_RATE = 0.02;
    private static final double CROSSOVER_RATE = 0.8;
    private static final Random random = new Random();
    private static final List<Double> fitnessLog = new ArrayList<>();


    public static Shelf solve(int shelfLength, List<Item> items) {
        Population population = new Population(POPULATION_SIZE, items.size());
        for (int generation = 0; generation < GENERATIONS; generation++) {
            double generationFitness = 0;
            List<Double> fitnessValues = calculateFitness(population, shelfLength, items);
            Population newPopulation = new Population(POPULATION_SIZE, items.size());
            for (int i = 0; i < POPULATION_SIZE; i++) {
                double populationTotalFitness = 0;
                for (double fitness : fitnessValues) {
                    populationTotalFitness += fitness;
                }
                generationFitness += (populationTotalFitness / POPULATION_SIZE);
                Individual parent1 = selection(population, populationTotalFitness, fitnessValues);
                Individual parent2 = selection(population, populationTotalFitness, fitnessValues);
                Individual child = crossover(parent1, parent2);
                mutate(child);
                newPopulation.setIndividual(i, child);
            }
            population = newPopulation;
            fitnessLog.add(generationFitness);
        }
        List<Double> finalFitnessValues = calculateFitness(population, shelfLength, items);
        int bestIndex = getBestIndex(finalFitnessValues);
        Individual bestIndividual = population.getIndividual(bestIndex);
        return SolutionDecoder.decode(bestIndividual.getGenes(), shelfLength, items);
    }

    private static List<Double> calculateFitness(Population population, int shelfLength, List<Item> items) {
        List<Double> fitnessValues = new ArrayList<>();
        for (Individual individual : population.getIndividuals()) {
            Shelf solution = SolutionDecoder.decode(individual.getGenes(), shelfLength, items);
            double cost = solution.getUsage();
            fitnessValues.add(cost);
        }
        return fitnessValues;
    }

    private static Individual selection(Population population, double fitnessSum, List<Double> fitnessValues) {
        double threshold = random.nextDouble() * fitnessSum; // 轮盘停留位置
        double currentSum = 0;
        int individualSize = population.getIndividuals().size();
        for (int i = 0; i < individualSize; i++) {
            currentSum += fitnessValues.get(i);
            if (currentSum >= threshold) {
                return population.getIndividual(i);
            }
        }
        return population.getIndividual(individualSize - 1);
    }

    private static Individual crossover(Individual parent1, Individual parent2) {
        if (random.nextDouble() < CROSSOVER_RATE) {
            int start = random.nextInt(parent1.getGenes().size());
            int end = random.nextInt(parent1.getGenes().size());
            if (start > end) {
                int temp = start;
                start = end;
                end = temp;
            }

            List<Integer> childGenes = new ArrayList<>(parent1.getGenes().size());
            for (int i = 0; i < parent1.getGenes().size(); i++) {
                childGenes.add(-1);
            } // 初始化空子代
            boolean[] used = new boolean[parent1.getGenes().size()];
            for (int i = start; i <= end; i++) {
                childGenes.set(i, parent1.getGenes().get(i));
                used[parent1.getGenes().get(i)] = true;
            } // 填充父1基因并标记

            int index = 0;
            for (int i = 0; i < parent2.getGenes().size(); i++) {
                int gene = parent2.getGenes().get(i);
                if (!used[gene]) {
                    while (childGenes.get(index) != -1) {
                        index++;
                    }
                    childGenes.set(index, gene);
                    used[gene] = true;
                    index++;
                }
            } // 填充父2基因

            Individual child = new Individual(parent1.getGenes().size());
            child.setGenes(childGenes);
            return child;
        }
        // 不交换则返回父1
        Individual child = new Individual(parent1.getGenes().size());
        child.setGenes(new ArrayList<>(parent1.getGenes()));
        return child;
    }

    private static void mutate(Individual individual) {
        for (int i = 0; i < individual.getGenes().size(); i++) {
            if (random.nextDouble() < MUTATION_RATE) {
                int j = random.nextInt(individual.getGenes().size());
                int temp = individual.getGenes().get(i);
                individual.getGenes().set(i, individual.getGenes().get(j));
                individual.getGenes().set(j, temp);
            }
        }
    }

    private static int getBestIndex(List<Double> fitnessValues) {
        int bestIndex = 0;
        double bestFitness = fitnessValues.get(0);
        for (int i = 1; i < fitnessValues.size(); i++) {
            if (fitnessValues.get(i) > bestFitness) {
                bestFitness = fitnessValues.get(i);
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    public static List<Double> getFitnessLog() {
        return fitnessLog;
    }
}