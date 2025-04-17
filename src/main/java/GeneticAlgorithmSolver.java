import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// 个体
class Individual {
    private List<Integer> genes;

    public Individual(int itemCount) {
        this.genes = new ArrayList<>(); // 创建一个空的基因列表
        for (int i = 0; i < itemCount; i++) {
            genes.add(i);   // 初始化基因列表为0到itemCount-1
        }
        Collections.shuffle(genes, new Random());   // 打乱基因顺序
    }

    public List<Integer> getGenes() {
        return genes;   // 返回基因列表
    }

    public void setGenes(List<Integer> genes) {
        this.genes = genes; // 设置基因列表
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
        return individuals; // 返回个体列表
    }

    public Individual getIndividual(int index) {
        return individuals.get(index);  // 从个体群中获取特定索引的个体
    }

    public void setIndividual(int index, Individual individual) {
        individuals.set(index, individual); // 更改个体群中指定索引的个体
    }
}

// 遗传算法求解器
public class GeneticAlgorithmSolver {
    private static final int POPULATION_SIZE = 100; // 种群大小
    private static final int GENERATIONS = 500; // 迭代次数
    private static final double MUTATION_RATE = 0.02;   // 变异率
    private static final double CROSSOVER_RATE = 0.8;   // 交叉率
    private static final Random random = new Random();  // 随机数生成器
    private static final List<Double> fitnessLog = new ArrayList<>();       // 适应度记录

    public static Shelf solve(int shelfLength, List<Item> items) {
        fitnessLog.clear();
        Population population = new Population(POPULATION_SIZE, items.size());  // 创建新的种群
        for (int generation = 0; generation < GENERATIONS; generation++) {  // 进行迭代
            double generationFitness = 0;   // 适应度置零
            List<Double> fitnessValues = calculateFitness(population, shelfLength, items);  // 通过传入种群、货架长度和物品列表计算适应度
            Population newPopulation = new Population(POPULATION_SIZE, items.size());   //  创建新的种群
            for (int i = 0; i < POPULATION_SIZE; i++) {
                double populationTotalFitness = 0;
                for (double fitness : fitnessValues) {  // 计算适应度总和
                    populationTotalFitness += fitness;
                }
                generationFitness += (populationTotalFitness / POPULATION_SIZE);    // 当代适应度
                Individual parent1 = selection(population, populationTotalFitness, fitnessValues);  // 选择父亲
                Individual parent2 = selection(population, populationTotalFitness, fitnessValues);  // 选择母亲
                Individual child = crossover(parent1, parent2); // 杂交
                mutate(child);  // 变异
                newPopulation.setIndividual(i, child);  // 将子代添加到新种群
            }
            population = newPopulation; // 种群更替
            fitnessLog.add(generationFitness);  // 记录适应度
        }
        List<Double> finalFitnessValues = calculateFitness(population, shelfLength, items); // 计算最终适应度
        int bestIndex = getBestIndex(finalFitnessValues);   // 获得最优适应度迭代索引
        Individual bestIndividual = population.getIndividual(bestIndex);    // 获取最优个体
        return SolutionDecoder.decode(bestIndividual.getGenes(), shelfLength, items);   // 返回最优解
    }

    private static List<Double> calculateFitness(Population population, int shelfLength, List<Item> items) {    // 计算适应度
        List<Double> fitnessValues = new ArrayList<>(); // 定义适应度列表
        for (Individual individual : population.getIndividuals()) { // 遍历种群中的每个个体
            Shelf solution = SolutionDecoder.decode(individual.getGenes(), shelfLength, items); // 解码个体基因
            double fitness = solution.getUsage()* ((double) 1 / solution.shelf.size()); // 适应度考虑使用率和货架层数
            fitnessValues.add(fitness);    // 添加适应度记录
        }
        return fitnessValues;
    }

    private static Individual selection(Population population, double fitnessSum, List<Double> fitnessValues) {
        // 轮盘赌选择父亲母亲，单次选择一个
        double threshold = random.nextDouble() * fitnessSum;    // 随机阈值
        double currentSum = 0;  // 当前适应度和
        int individualSize = population.getIndividuals().size();    // 获得种群中的个体数量
        for (int i = 0; i < individualSize; i++) {  // 遍历种群中的每个个体
            currentSum += fitnessValues.get(i); // 计算当前适应度和
            if (currentSum >= threshold) {  // 如果当前的适应度大于等于阈值
                return population.getIndividual(i); // 返回当前个体
            }
        }
        return population.getIndividual(individualSize - 1);    // 返回最后一个个体
    }

    private static Individual crossover(Individual parent1, Individual parent2) {
        // 杂交
        if (random.nextDouble() < CROSSOVER_RATE) { // 如果随机数小于交叉率
            // 随机选择交叉点
            int start = random.nextInt(parent1.getGenes().size());
            int end = random.nextInt(parent1.getGenes().size());
            if (start > end) {  // 确保 start 小于 end，如果不满足则交换
                int temp = start;
                start = end;
                end = temp;
            }

            List<Integer> childGenes = new ArrayList<>(parent1.getGenes().size());  // 创建子代基因列表
            for (int i = 0; i < parent1.getGenes().size(); i++) {   // 初始化子代基因列表，全置为 -1
                childGenes.add(-1);
            }
            boolean[] used = new boolean[parent1.getGenes().size()];    // 创建一个布尔数组，用于标记基因是否已使用
            for (int i = start; i <= end; i++) { // 填充子代基因列表
                childGenes.set(i, parent1.getGenes().get(i)); // 将父1的基因添加到子代基因列表
                used[parent1.getGenes().get(i)] = true; // 标记父1基因已使用
            }

            int index = 0;
            for (int i = 0; i < parent2.getGenes().size(); i++) {   // 遍历父2基因
                int gene = parent2.getGenes().get(i);   // 获取父2基因
                if (!used[gene]) {  // 如果父2基因未使用
                    while (childGenes.get(index) != -1) {   // 查找下一个未使用的基因位置
                        index++;
                    }
                    childGenes.set(index, gene);    // 将父2基因添加到子代基因列表
                    used[gene] = true;  // 标记父2基因已使用
                    index++;
                }
            }

            Individual child = new Individual(parent1.getGenes().size());   // 创建子代个体
            child.setGenes(childGenes); // 设置子代基因列表
            return child;
        }
        // 不交换则返回父1
        Individual child = new Individual(parent1.getGenes().size());   // 创建子代个体
        child.setGenes(new ArrayList<>(parent1.getGenes()));    // 设置子代基因列表为父亲的基因列表
        return child;
    }

    private static void mutate(Individual individual) { // 变异
        // 遍历个体基因列表，随机选择两个基因进行交换
        for (int i = 0; i < individual.getGenes().size(); i++) {    // 遍历基因列表
            if (random.nextDouble() < MUTATION_RATE) {  // 如果随机数小于变异率
                // 随机选择两个基因进行交换
                int j = random.nextInt(individual.getGenes().size());   // 随机选择另一个基因
                int temp = individual.getGenes().get(i);    // 获取当前基因
                individual.getGenes().set(i, individual.getGenes().get(j)); // 将当前基因设置为另一个基因
                individual.getGenes().set(j, temp);     // 将另一个基因设置为当前基因
            }
        }
    }

    private static int getBestIndex(List<Double> fitnessValues) { // 获取最优适应度索引
        int bestIndex = 0;  // 初始化最优索引为0
        double bestFitness = fitnessValues.get(0);  // 初始化最优适应度为第一个个体的适应度
        for (int i = 1; i < fitnessValues.size(); i++) {    // 遍历适应度列表
            if (fitnessValues.get(i) > bestFitness) {   // 如果当前适应度大于最优适应度
                bestFitness = fitnessValues.get(i); // 更新最优适应度
                bestIndex = i;  // 更新最优索引
            }
        }
        return bestIndex;
    }

    public static List<Double> getFitnessLog() {    // 获取适应度记录
        return fitnessLog;  // 返回适应度记录
    }
}