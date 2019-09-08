package temp;

public class test {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int size = 20;
		int population = 100;
		int iteration = 100;
		double mutationRate = 0.001;
		int point1 = 1, point2 = 5;
		asd ga = new asd(size, population, iteration);
		for(int i = 0; i < iteration - 1; i++) {
			ga.crossing(point1, point2, i);
			ga.mutation(mutationRate, i);
		}
	}
}