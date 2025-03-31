import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// 粒子类
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
    private static final double W = 0.7;
    private static final double C1 = 1.4;
    private static final double C2 = 1.4;

    public static List<List<Item>> solve(int shelfLength, List<Item> items) {
        List<Particle> particles = initializeParticles(items.size());
        List<Integer> globalBestPosition = null;
        double globalBestFitness = Double.MAX_VALUE;

        for (int iteration = 0; iteration < ITERATIONS; iteration++) {
            for (Particle particle : particles) {
                double fitness = calculateFitness(particle.position, shelfLength, items);
                if (fitness < particle.personalBestFitness) {
                    particle.personalBestFitness = fitness;
                    particle.personalBestPosition = new ArrayList<>(particle.position);
                    if (fitness < globalBestFitness) {
                        globalBestFitness = fitness;
                        globalBestPosition = new ArrayList<>(particle.position);
                    }
                }
            }

            for (Particle particle : particles) {
                updateVelocity(particle, globalBestPosition);
                updatePosition(particle);
            }
        }

        return decodeSolution(globalBestPosition, shelfLength, items);
    }

    private static List<Particle> initializeParticles(int itemCount) {
        List<Particle> particles = new ArrayList<>();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(new Particle(itemCount));
        }
        return particles;
    }

    private static double calculateFitness(List<Integer> position, int shelfLength, List<Item> items) {
        List<List<Item>> solution = decodeSolution(position, shelfLength, items);
        return solution.size();
    }

    private static void updateVelocity(Particle particle, List<Integer> globalBestPosition) {
        Random random = new Random();
        for (int i = 0; i < particle.position.size(); i++) {
            double r1 = random.nextDouble();
            double r2 = random.nextDouble();
            int cognitiveComponent = (int) (C1 * r1 * (particle.personalBestPosition.get(i) - particle.position.get(i)));
            int socialComponent = (int) (C2 * r2 * (globalBestPosition.get(i) - particle.position.get(i)));
            particle.velocity.set(i, (int) (W * particle.velocity.get(i) + cognitiveComponent + socialComponent));
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
        // 处理重复元素
        boolean[] used = new boolean[particle.position.size()];
        List<Integer> newPosition = new ArrayList<>(Collections.nCopies(particle.position.size(), -1));
        for (int i = 0; i < particle.position.size(); i++) {
            int index = particle.position.get(i);
            if (!used[index]) {
                newPosition.set(i, index);
                used[index] = true;
            }
        }
        for (int i = 0; i < newPosition.size(); i++) {
            if (newPosition.get(i) == -1) {
                for (int j = 0; j < used.length; j++) {
                    if (!used[j]) {
                        newPosition.set(i, j);
                        used[j] = true;
                        break;
                    }
                }
            }
        }
        particle.position = newPosition;
    }

    private static List<List<Item>> decodeSolution(List<Integer> position, int shelfLength, List<Item> items) {
        List<List<Item>> solution = new ArrayList<>();
        for (int index : position) {
            Item item = items.get(index);
            boolean placed = false;
            for (List<Item> layer : solution) {
                ShelfLayer currentLayer = new ShelfLayer(shelfLength);
                currentLayer.getItems().addAll(layer);
                currentLayer.remainingLength = shelfLength - layer.stream().mapToInt(Item::getLength).sum();
                if (currentLayer.addItem(item)) {
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                ShelfLayer newLayer = new ShelfLayer(shelfLength);
                newLayer.addItem(item);
                solution.add(newLayer.getItems());
            }
        }
        return solution;
    }
}    