import java.util.*;

class Particle {
    List<Integer> position;
    List<Integer> velocity;
    List<Integer> personalBestPosition;
    double personalBestFitness;

    public Particle(int itemCount) {
        Random random = new Random();
        position = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            position.add(i);
        }
        Collections.shuffle(position, random); // 初始粒子为打乱的物品索引列表
        velocity = new ArrayList<>(Collections.nCopies(itemCount, 0)); // 初始速度为零
        personalBestPosition = new ArrayList<>(position); // 初始化个体最优解位置
        personalBestFitness = 0; // 初始化个体最优解适应度
    }
}

// 粒子群算法求解器
public class ParticleSwarmSolver {
    private static final int PARTICLE_COUNT = 100;
    private static final int ITERATIONS = 300;
    private static final double W = 0.7; // 惯性
    private static final double C1 = 1.5; // 个体加速度
    private static final double C2 = 1.8; // 群体加速度
    private static final List<Double> fitnessLog = new ArrayList<>();

    public static Shelf solve(int shelfLength, List<Item> items) {
        fitnessLog.clear(); // 初始化迭代日志
        List<Particle> particles = initializeParticles(items.size()); // 初始化粒子群
        List<Integer> globalBestPosition = null; // 初始化群体最优解位置
        double globalBestFitness = 0; // 初始化群体最优解适应度

        for (int iteration = 0; iteration < ITERATIONS; iteration++) { // 迭代过程
            double totalFitness = 0; // 初始化适应度统计
            for (Particle particle : particles) {
                double fitness = calculateFitness(particle.position, shelfLength, items); // 计算每个粒子适应度
                totalFitness += fitness;
                if (fitness > particle.personalBestFitness) { // 当解为个体最优解时
                    particle.personalBestFitness = fitness;
                    particle.personalBestPosition = new ArrayList<>(particle.position); // 更新个体最优解适应度和位置
                    if (fitness > globalBestFitness) { // 当解还是群体最优解时
                        globalBestFitness = fitness;
                        globalBestPosition = new ArrayList<>(particle.position); // 更新群体最优解适应度和位置
                    }
                }
            }
            for (Particle particle : particles) { // 每个粒子向最优解更新速度
                updateVelocity(particle, globalBestPosition);
                updatePosition(particle);
            }
            fitnessLog.add(totalFitness / PARTICLE_COUNT); // 添加日志（群体平均）
//            fitnessLog.add(globalBestFitness); // 添加日志（最优）
        }
        return SolutionDecoder.decode(Objects.requireNonNull(globalBestPosition), shelfLength, items);
    }

    private static List<Particle> initializeParticles(int itemCount) {
        List<Particle> particles = new ArrayList<>();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(new Particle(itemCount));
        }
        return particles;
    }

    private static double calculateFitness(List<Integer> position, int shelfLength, List<Item> items) {
        Shelf solution = SolutionDecoder.decode(position, shelfLength, items);
        return solution.getUsage(); // 适应度为货架填充度
    }

    private static void updateVelocity(Particle particle, List<Integer> globalBestPosition) {
        Random random = new Random();
        for (int i = 0; i < particle.position.size(); i++) {
            double r1 = random.nextDouble(); // 个体加速率
            double r2 = random.nextDouble(); // 群体加速率
            int personalAcceleration = (int) (C1 * r1 * (particle.personalBestPosition.get(i) - particle.position.get(i))); // 更新个体加速度
            int socialAcceleration;
            if (globalBestPosition == null) {
                globalBestPosition = particle.position; // 第一轮的群体最优解位置在第一个粒子处
            }
            socialAcceleration = (int) (C2 * r2 * (globalBestPosition.get(i) - particle.position.get(i))); // 更新群体加速度
            particle.velocity.set(i, (int) (W * particle.velocity.get(i) + personalAcceleration + socialAcceleration)); // 更新粒子速度
        }
    }

    private static void updatePosition(Particle particle) {
        for (int i = 0; i < particle.position.size(); i++) {
            int newPosition = particle.position.get(i) + particle.velocity.get(i); // 新位置等于当前位置加上速度
            if (newPosition < 0) { // 防止位置越界
                newPosition = 0; // 超过下界置零
            } else if (newPosition >= particle.position.size()) {
                newPosition = particle.position.size() - 1; // 超过上界设为最后
            }
            particle.position.set(i, newPosition);
        }

        // 为了保证解的完整性，需要处理重复索引
        boolean[] used = new boolean[particle.position.size()];
        List<Integer> newPosition = new ArrayList<>(Collections.nCopies(particle.position.size(), -1)); // 初始化新位置，用 -1 标记未被填充的地方
        for (int i = 0; i < particle.position.size(); i++) {
            int index = particle.position.get(i);
            if (!used[index]) {
                newPosition.set(i, index); // 当某个索引值未被使用时才会被填充
                used[index] = true; // 标记已被使用，后续再遇到时不会填充
            }
        }
        List<Integer> unusedIndexes = new ArrayList<>();
        for (int index = 0; index < used.length; index++) {
            if (!used[index]) {
                unusedIndexes.add(index); // 收集未被用到的索引
            }
        }
        Collections.shuffle(unusedIndexes); // 打乱准备填充
        int fillIndex = 0;
        for (int i = 0; i < newPosition.size(); i++) {
            if (newPosition.get(i) == -1 && fillIndex < unusedIndexes.size()) { // 搜索未被填充的位置
                newPosition.set(i, unusedIndexes.get(fillIndex));
                fillIndex++;
            }
        }
        particle.position = newPosition;
    }

    public static List<Double> getFitnessLog() {
        return fitnessLog;
    }
}    