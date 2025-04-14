import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// 个体类
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

// 种群类
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

    public void setIndividuals(List<Individual> individuals) {
        this.individuals = individuals;
    }

    public Individual getIndividual(int index) {
        return individuals.get(index);
    }

    public void setIndividual(int index, Individual individual) {
        individuals.set(index, individual);
    }

    public int size() {
        return individuals.size();
    }
}

// 遗传算法求解器
public class GeneticAlgorithmSolver {
    private static final int POPULATION_SIZE = 100;
    private static final int GENERATIONS = 500;
    private static final double MUTATION_RATE = 0.02;
    private static final double CROSSOVER_RATE = 0.8;

    public static List<List<Item>> solve(int shelfLength, List<Item> items) {
        Population population = new Population(POPULATION_SIZE, items.size());
        for (int generation = 0; generation < GENERATIONS; generation++) {
            List<Double> fitnessValues = calculateFitness(population, shelfLength, items);
            Population newPopulation = new Population(POPULATION_SIZE, items.size());
            for (int i = 0; i < POPULATION_SIZE; i++) {
                Individual parent1 = selection(population, fitnessValues);
                Individual parent2 = selection(population, fitnessValues);
                Individual child = crossover(parent1, parent2);
                mutate(child);
                newPopulation.setIndividual(i, child);
            }
            population = newPopulation;
        }
        List<Double> finalFitnessValues = calculateFitness(population, shelfLength, items);
        int bestIndex = getBestIndex(finalFitnessValues);
        Individual bestIndividual = population.getIndividual(bestIndex);
        return decodeSolution(bestIndividual.getGenes(), shelfLength, items);
    }

    private static List<Double> calculateFitness(Population population, int shelfLength, List<Item> items) {
        List<Double> fitnessValues = new ArrayList<>();
        for (Individual individual : population.getIndividuals()) {
            List<List<Item>> solution = decodeSolution(individual.getGenes(), shelfLength, items);
            int cost = solution.size();
            fitnessValues.add(1.0 / cost);
        }
        return fitnessValues;
    }

    private static Individual selection(Population population, List<Double> fitnessValues) {
        double totalFitness = 0;
        for (double fitness : fitnessValues) {
            totalFitness += fitness;
        }
        Random random = new Random();
        double r = random.nextDouble() * totalFitness;
        double sum = 0;
        for (int i = 0; i < population.size(); i++) {
            sum += fitnessValues.get(i);
            if (sum >= r) {
                return population.getIndividual(i);
            }
        }
        return population.getIndividual(population.size() - 1);
    }

    private static Individual crossover(Individual parent1, Individual parent2) {
        Random random = new Random();
        if (random.nextDouble() < CROSSOVER_RATE) {
            int start = random.nextInt(parent1.getGenes().size());
            int end = random.nextInt(parent1.getGenes().size());
            if (start > end) {
                int temp = start;
                start = end;
                end = temp;
            }
            List<Integer> childGenes = new ArrayList<>(Collections.nCopies(parent1.getGenes().size(), -1));
            for (int i = start; i <= end; i++) {
                childGenes.set(i, parent1.getGenes().get(i));
            }
            int index = 0;
            for (int i = 0; i < parent2.getGenes().size(); i++) {
                if (!childGenes.contains(parent2.getGenes().get(i))) {
                    while (childGenes.get(index) != -1) {
                        index++;
                    }
                    childGenes.set(index, parent2.getGenes().get(i));
                    index++;
                }
            }
            Individual child = new Individual(parent1.getGenes().size());
            child.setGenes(childGenes);
            return child;
        }
        return new Individual(parent1.getGenes().size()) {{
            setGenes(new ArrayList<>(parent1.getGenes()));
        }};
    }

    private static void mutate(Individual individual) {
        Random random = new Random();
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

    private static Shelf decodeSolution(List<Integer> solution, int shelfLength, List<Item> items) {
        Shelf result = new Shelf();
        List<Layer> shelf = result.getShelf();
        for (int index : solution) {
            Item item = items.get(index);
            boolean placed = false;
            for (Layer layer : shelf) {
                if (layer.addItem(item)) {
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                result.addLayer(shelfLength, new Layer(shelfLength));
                shelf.get(shelf.size() - 1).addItem(item);
            }
        }
        return result;
    }
}