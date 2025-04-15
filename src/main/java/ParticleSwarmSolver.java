import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
        Collections.shuffle(position, random);
        velocity = new ArrayList<>(Collections.nCopies(itemCount, 0));
        personalBestPosition = new ArrayList<>(position);
        personalBestFitness = Double.MAX_VALUE;
    }
}

// 粒子群算法求解器
public class ParticleSwarmSolver {
    private static final int PARTICLE_COUNT = 50;
    private static final int ITERATIONS = 300;
    private static final double W = 0.7; // 惯性
    private static final double C1 = 1.4; // 个体加速度
    private static final double C2 = 1.4; // 群体加速度
    private static List<Double> fitnessLog = new ArrayList<>();

    public static Shelf solve(int shelfLength, List<Item> items) {
        List<Particle> particles = initializeParticles(items.size());
        List<Integer> globalBestPosition = null;
        double globalBestFitness = 0;

        for (int iteration = 0; iteration < ITERATIONS; iteration++) {
            double totalFitness = 0;
            for (Particle particle : particles) {
                double fitness = calculateFitness(particle.position, shelfLength, items);
                totalFitness += fitness;
                if (fitness > particle.personalBestFitness) {
                    particle.personalBestFitness = fitness;
                    particle.personalBestPosition = new ArrayList<>(particle.position);
                    if (fitness > globalBestFitness) {
                        globalBestFitness = fitness;
                        globalBestPosition = new ArrayList<>(particle.position);
                    }
                }
            } // 检查是否为个体和群体更优解
            for (Particle particle : particles) {
                updateVelocity(particle, globalBestPosition);
                updatePosition(particle);
            } // 向最优解更新速度
            fitnessLog.add(totalFitness / PARTICLE_COUNT);
        }
        return SolutionDecoder.decode(globalBestPosition, shelfLength, items);
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
        return solution.getUsage();
    }

    private static void updateVelocity(Particle particle, List<Integer> globalBestPosition) {
        Random random = new Random();
        for (int i = 0; i < particle.position.size(); i++) {
            double r1 = random.nextDouble(); // 个体加速率
            double r2 = random.nextDouble(); // 群体加速率
            int personalAcceleration = (int) (C1 * r1 * (particle.personalBestPosition.get(i) - particle.position.get(i)));
            int socialAcceleration = (int) (C2 * r2 * (globalBestPosition.get(i) - particle.position.get(i)));
            particle.velocity.set(i, (int) (W * particle.velocity.get(i) + personalAcceleration + socialAcceleration));
        }
    }

    private static void updatePosition(Particle particle) {
        for (int i = 0; i < particle.position.size(); i++) {
            int newPosition = particle.position.get(i) + particle.velocity.get(i);
            if (newPosition < 0) {
                newPosition = 0;
            } else if (newPosition >= particle.position.size()) {
                newPosition = particle.position.size() - 1;
            }
            particle.position.set(i, newPosition);
        }

        // 处理重复数据
        boolean[] used = new boolean[particle.position.size()];
        // 删去重复数据
        List<Integer> newPosition = new ArrayList<>(Collections.nCopies(particle.position.size(), -1));
        for (int i = 0; i < particle.position.size(); i++) {
            int index = particle.position.get(i);
            if (!used[index]) {
                newPosition.set(i, index);
                used[index] = true;
            }
        }
        // 收集空位并打乱
        List<Integer> unusedIndexes = new ArrayList<>();
        for (int index = 0; index < used.length; index++) {
            if (!used[index]) {
                unusedIndexes.add(index);
            }
        }
        Collections.shuffle(unusedIndexes);
        // 填充空位
        int fillIndex = 0;
        for (int i = 0; i < newPosition.size(); i++) {
            if (newPosition.get(i) == -1 && fillIndex < unusedIndexes.size()) {
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