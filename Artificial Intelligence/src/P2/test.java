package P2;

public class test {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int size = 20;
		int population = 100;
		int iteration = 100;
		double mutationRate = 0.001;
		double crossoverRate = 0.7;
		int point1 = 1, point2 = 5;
		GA ga = new GA(size, population, iteration);
		for(int i = 0; i < iteration - 1; i++) {
			ga.crossover(point1, point2, crossoverRate);
			ga.mutation(mutationRate);
		}
		ga.createExcel();
	}
}