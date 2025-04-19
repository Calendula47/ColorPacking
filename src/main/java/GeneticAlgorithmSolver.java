import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// 个体
class Individual {
    private List<Integer> genes;
    private double fitness;

    public Individual(int itemCount) {
        this.genes = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            genes.add(i);
        }
        Collections.shuffle(genes, new Random()); // 初始基因为打乱的物品索引列表
    }

    public List<Integer> getGenes() {
        return genes;
    }

    public void setGenes(List<Integer> genes) {
        this.genes = genes;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }
}

// 种群
class Population {
    private final List<Individual> individuals; // 创建个体群

    public Population(int populationSize, int itemCount) {
        this.individuals = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            individuals.add(new Individual(itemCount)); // 填充种群
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
    private static final int POPULATION_SIZE = 100; // 种群大小
    private static final int GENERATIONS = 300; // 迭代次数
    private static final double MUTATION_RATE = 0.03; // 变异率
    private static final double CROSSOVER_RATE = 0.75; // 交叉率
    private static final Random random = new Random();
    private static final List<Double> fitnessLog = new ArrayList<>();
    private static double generationFitness; // 用于记录每代平均适应度

    public static Shelf solve(int shelfLength, List<Item> items) {
        fitnessLog.clear(); // 初始化迭代日志
        Population population = new Population(POPULATION_SIZE, items.size()); // 初始化种群
        for (int generation = 0; generation < GENERATIONS; generation++) { // 迭代过程
            List<Double> fitnessValues = calculateFitness(population, shelfLength, items); // 计算种群所有个体适应度

            Population newPopulation = new Population(POPULATION_SIZE, items.size()); // 创建下一代
            // 找到亲代种群中适应度最高的个体
            Individual bestParent = population.getIndividual(getBestIndex(fitnessValues));
            // 将最佳亲代个体放入新种群
            newPopulation.setIndividual(0, new Individual(items.size()));
            newPopulation.getIndividual(0).setGenes(new ArrayList<>(bestParent.getGenes()));

            for (int i = 1; i < POPULATION_SIZE; i++) { // 从索引 1 开始填充新种群，因为索引 0 已经被最佳亲代个体占据
                double populationTotalFitness = 0;
                for (double fitness : fitnessValues) { // 计算适应度总和以便轮盘赌选择调用
                    populationTotalFitness += fitness;
                }
                Individual parent1 = selection(population, populationTotalFitness, fitnessValues);
                Individual parent2 = selection(population, populationTotalFitness, fitnessValues); // 选择亲代
                Individual child = crossover(parent1, parent2); // 杂交
                mutate(child); // 变异
                if (child.getFitness() >= parent1.getFitness()) {
                    if (child.getFitness() >= parent2.getFitness()) {
                        newPopulation.setIndividual(i, child); // 当子代大于亲代适应度时将子代添加到新种群
                    }
                    newPopulation.setIndividual(i,parent2); // 大于父 1 但小于父 2 时添加父 2
                }
                newPopulation.setIndividual(i,parent1); // 小于父 1 时直接添加父 1
            }
            population = newPopulation;
            fitnessLog.add(generationFitness); // 添加日志（群体平均）（最优在 getBestIndex 函数中）
        }
        List<Double> finalFitnessValues = calculateFitness(population, shelfLength, items); // 计算最终适应度
        int bestIndex = getBestIndex(finalFitnessValues);
        Individual bestIndividual = population.getIndividual(bestIndex); // 获取最优个体
        return SolutionDecoder.decode(bestIndividual.getGenes(), shelfLength, items); // 返回最优解
    }

    private static List<Double> calculateFitness(Population population, int shelfLength, List<Item> items) {
        List<Double> fitnessValues = new ArrayList<>(); // 用于存放种群中所有个体的适应度
        generationFitness = 0;
        for (Individual individual : population.getIndividuals()) { // 遍历种群中的每个个体
            Shelf solution = SolutionDecoder.decode(individual.getGenes(), shelfLength, items); // 创建当前解表示的货架
            double fitness = solution.getUsage() * ((double) 1 / solution.layers.size()); // 算法适应度考虑使用率和货架层数
            fitnessValues.add(fitness); // 添加适应度记录
            generationFitness += (solution.getUsage() / POPULATION_SIZE);
            individual.setFitness(fitness); // 设置个体适应度
        }
        return fitnessValues;
    }

    private static Individual selection(Population population, double fitnessSum, List<Double> fitnessValues) {
        double threshold = random.nextDouble() * fitnessSum; // 轮盘赌阈值
        double currentSum = 0; // 当前适应度和
        int individualSize = population.getIndividuals().size();
        for (int i = 0; i < individualSize; i++) { // 遍历种群中的每个个体
            currentSum += fitnessValues.get(i); // 计算当前适应度和
            if (currentSum >= threshold) { // 如果当前的适应度大于等于阈值则返回当前个体
                return population.getIndividual(i);
            }
        }
        return population.getIndividual(individualSize - 1); // 累加达不到阈值则返回最后一个个体
    }

    private static Individual crossover(Individual parent1, Individual parent2) {
        if (random.nextDouble() < CROSSOVER_RATE) { // 交叉判定
            int start = random.nextInt(parent1.getGenes().size());
            int end = random.nextInt(parent1.getGenes().size()); // 基因交叉的范围
            if (start > end) { // 确保范围起终点数值合法性
                int temp = start;
                start = end;
                end = temp;
            }

            List<Integer> childGenes = new ArrayList<>(parent1.getGenes().size());
            for (int i = 0; i < parent1.getGenes().size(); i++) { // 初始化子代基因列表，全置为 -1 表示未填充
                childGenes.add(-1);
            }
            // 为了保证解的完整性，这里使用部分交叉映射（PMX）算法
            boolean[] used = new boolean[parent1.getGenes().size()]; // 用于标记基因是否已使用
            for (int i = start; i <= end; i++) { // 填充子代基因列表
                childGenes.set(i, parent1.getGenes().get(i)); // 将父 1 的基因添加到子代基因列表
                used[parent1.getGenes().get(i)] = true; // 标记该基因已使用
            }

            int index = 0;
            for (int i = 0; i < parent2.getGenes().size(); i++) { // 遍历父 2 基因
                int gene = parent2.getGenes().get(i);
                if (!used[gene]) { // 如果本条基因未使用
                    while (childGenes.get(index) != -1) { // 查找子代下一个未使用的基因位置
                        index++;
                    }
                    childGenes.set(index, gene); // 将父 2 基因添加到子代基因
                    used[gene] = true; // 标记该基因已使用
                    index++;
                }
            }

            Individual child = new Individual(parent1.getGenes().size()); // 创建子代个体
            child.setGenes(childGenes); // 设置子代基因列表
            return child;
        }
        // 不交换则返回父1
        Individual child = new Individual(parent1.getGenes().size()); // 创建子代个体
        child.setGenes(new ArrayList<>(parent1.getGenes())); // 设置子代基因列表为父1的基因列表
        return child;
    }

    private static void mutate(Individual individual) { // 变异
        for (int i = 0; i < individual.getGenes().size(); i++) { // 遍历基因列表
            if (random.nextDouble() < MUTATION_RATE) { // 变异判定（为了保证解的完整性，变异为自我交换）
                int j = random.nextInt(individual.getGenes().size()); // 随机选择另一个基因
                int temp = individual.getGenes().get(i);
                individual.getGenes().set(i, individual.getGenes().get(j));
                individual.getGenes().set(j, temp); // 交换两个基因
            }
        }
    }

    private static int getBestIndex(List<Double> fitnessValues) { // 获取最优适应度索引
        int bestIndex = 0; // 初始化最优索引为0
        double bestFitness = fitnessValues.getFirst(); // 初始化最优适应度为第一个个体的适应度
        for (int i = 1; i < fitnessValues.size(); i++) { // 遍历适应度列表
            if (fitnessValues.get(i) > bestFitness) { // 如果当前适应度大于最优适应度
                bestFitness = fitnessValues.get(i); // 更新最优适应度
                bestIndex = i; // 更新最优索引
            }
        }
//        fitnessLog.add(bestFitness); // 添加日志（最优）（群体平均在 solve 函数中）
        return bestIndex;
    }

    public static List<Double> getFitnessLog() {
        return fitnessLog;
    }
}