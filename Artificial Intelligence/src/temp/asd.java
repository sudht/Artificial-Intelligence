package temp;

public class asd 
{
	private int population_size;
	private int chromosomeLength;
	private int numGeneration;
	private double crossoverRate = 0.7;
	private String [][] chromosomeArr;
	private double [][] fitness;

	asd(int chromosomeLength, int population_size, int numGeneration) {
		this.chromosomeLength = chromosomeLength;
		this.population_size = population_size;
		this.numGeneration = numGeneration;
		chromosomeArr =  new String [this.numGeneration][this.population_size];
		fitness = new double [this.numGeneration][this.population_size];
		makeChromosome();
		makeFitness(0);
	}

	public void makeChromosome() {
		for(int j = 0; j < population_size; j++) {
			String s = "";
			for(int i = 0; i < chromosomeLength; i++)
				s += Integer.toString((int)(Math.random() + 0.5));
			chromosomeArr[0][j] = s;
		}
	}

	public void makeFitness(int gen) {
		for(int i = 0; i < population_size; i++) 
			fitness[gen][i] = function(String_To_Double(chromosomeArr[gen][i]));
	}

	public void printBestAndAvr(double[] arr) {
		double max = Double.MIN_VALUE;
		double sum = 0;
		for(int i = 0; i< arr.length; i++)
		{
			sum += arr[i];
			if(max < arr[i])
				max = arr[i];
		}
		System.out.printf("평균 값 : %.3f\t 최대 값 :%.3f\n", sum/population_size, max);
	}

	public int select(double total, double [] fitnessArr) {
		double num = Math.random()*total;
		double acc = 0;
		int i = 0;
		for(; i < fitnessArr.length; i++) {
			acc += fitnessArr[i];
			if(acc > num)
				break;
		}
		return i;
	}

	public void crossing(int p1, int p2, int gen) {
		int pc = (int)(population_size*crossoverRate);
		double [] fitnessArr = new double [pc];
		double min = findMin(fitness[gen]);
		double total = .0;

		for(int i = 0; i < pc; i++) {
			fitnessArr[i] = fitness[gen][i]-min;
			total += fitnessArr[i];
		}

		int m, f;
		for(int i = 0; i < pc; i+=2) {
			int count = 0;
			do {
				m = select(total, fitnessArr);
				f = select(total, fitnessArr);
				count++;
				if(count > 100) f = m-1;	
			} while(m == f);

			chromosomeArr[gen+1][i] = chromosomeArr[gen][m].substring(0, p1) + chromosomeArr[gen][f].substring(p1, p2) + chromosomeArr[gen][m].substring(p2, chromosomeLength);
			chromosomeArr[gen+1][i+1] = chromosomeArr[gen][f].substring(0, p1) + chromosomeArr[gen][m].substring(p1, p2) + chromosomeArr[gen][f].substring(p2, chromosomeLength);
		}

		sortFitness(gen);// 적합도의 오름차순에 맞춰 chromosomeArr도 같이 정렬
		for(int i = pc; i < population_size; i++) {
			chromosomeArr[gen+1][i] = chromosomeArr[gen][i];
		}
	}

	public void mutation(double mutationRate, int gen) {	
		StringBuilder sb;
		gen++;
		for(int i = 0; i < population_size; i++) {
			sb = new StringBuilder(chromosomeArr[gen][i]);
			for(int j = 0; j < chromosomeLength; j++) {
				if(Math.random() <= mutationRate) {
					if(chromosomeArr[gen][i].charAt(j) == '0') sb.replace(j, j+1, "1");
					else sb.replace(j, j+1, "0");
				}
				chromosomeArr[gen][i] = sb.toString();
			}
		}
		makeFitness(gen);
		printBestAndAvr(fitness[gen]);
	}

	public void sortFitness(int gen) {
		double temp_d;
		String temp_s;
		for(int i = 0; i < population_size; i++) {
			for(int j = 0; j < population_size-1; j++) {
				if(fitness[gen][j] > fitness[gen][j+1]) {
					temp_d = fitness[gen][j];
					fitness[gen][j] = fitness[gen][j+1];
					fitness[gen][j+1] = temp_d;

					temp_s = chromosomeArr[gen][j];
					chromosomeArr[gen][j] = chromosomeArr[gen][j+1];
					chromosomeArr[gen][j+1] = temp_s;
				}
			}
		}
	}

	public double findMin(double[] arr) {
		double min = Double.MAX_VALUE;
		for(int i = 0; i < arr.length; i++)
			if(min > arr[i])
				min = arr[i];
		return min;
	}

	// 문자열로 된 이진수를 double로 반환
	public double String_To_Double(String C) {
		double x = .0;
		for(int i = 1; i < chromosomeLength; i++) 
			if(C.charAt(i) == '1') x += Math.pow(2, 3 - i);

		// 4를 나타내는 비트를 3으로 바꿈(범위를 유지하기 위해서) (3, 2, 1 ....)
		if(C.charAt(1) == '1') x -= 1;

		// 실수 부분 마지막 비트를 이전과 동일하게 만듦, 즉 자기 자신을 한번더 더 함 (범위를 유지하기 위해서) (0.5, 0.25, 0.125, 0.125 처럼)
		if(C.charAt(chromosomeLength-1) == '1') x += Math.pow(2, 3 - (chromosomeLength-1));

		// 부호 비트 처리
		if(C.charAt(0) == '1') x *= -1;
		return x;
	}
	
	public double function(double ch) {
		return Math.pow(ch, 5) - 4 * Math.pow(ch, 4) - 12 * Math.pow(ch, 3) + 34 * Math.pow(ch, 2) + 11 * ch - 30;
	}
}