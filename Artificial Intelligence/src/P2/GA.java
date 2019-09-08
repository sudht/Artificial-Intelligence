package P2;

import java.io.File;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class GA {
	private int bitStreamLength;	// ��Ʈ �迭 ���� (������ ����)
	private int population;			// ������ ���� 
	private int iteration;			// ���� ��(�ݺ� ȸ��)
	private int iterationCount = 0;	// ���� ī��Ʈ
	private Chromosome [][] chromosome_Array;

	GA(int bitStreamLength, int population, int iteration) {
		this.bitStreamLength = bitStreamLength;
		this.population = population;
		this.iteration = iteration;
		chromosome_Array = new Chromosome[this.iteration][this.population];
		init_Chromosome_Arr();
	}

	// �������� �̷���� ���ڿ� ���� (���� 1ȸ�� ����)
	public void init_Chromosome_Arr() {
		for(int i=0; i<population; i++) {
			String C = "";
			for(int j = 0; j < bitStreamLength; j++) {
				C += Integer.toString((int)(Math.random() + 0.5));
			}
			chromosome_Array[0][i] = new Chromosome(C);
		}
	}

	// �귿 ���� ����� ���� �����ڸ� 1�� ����ִ� �Լ�
	public int select(double fitnessArr[], double total) {
		double rand = Math.random() * total;
		double acc = 0;
		for(int i=0; i<fitnessArr.length; i++) {
			acc += fitnessArr[i];
			if(acc>rand) return i;
		}
		return 0;
	}

	public void sortChromosome(Chromosome [] chromosome_Array) {
		Chromosome temp;
		for(int i=0; i<chromosome_Array.length; i++) {
			for(int j=0; j < chromosome_Array.length-1; j++) {
				if(chromosome_Array[j].getFitness() > chromosome_Array[j+1].getFitness()) {
					temp = chromosome_Array[j];
					chromosome_Array[j] = chromosome_Array[j+1];
					chromosome_Array[j+1] = temp;
				}
			}
		}
	}

	// �� �����ڸ� �����Ͽ� �ڽ� ������ 2���� ����� �Լ�
	public void crossover(int point1, int point2, double crossoverRate) {
		int population = (int)(this.population*crossoverRate);
		double min = min_Fitness();
		double [] fitnessArr = new double[population];
		double total = .0;

		for(int i=0; i<population; i++) {
			fitnessArr[i] = chromosome_Array[iterationCount][i].getFitness() - min;	// ��� �������� ���յ��� ����ȭ�� ���� �ּҰ��� ���ؼ� ����
			total += fitnessArr[i];
		}

		// �ڽ� ���븦 ����� ���� �κ�
		int parent1, parent2;
		for(int i=0; i<population; i+= 2) {
			int count = 0;
			do {
				parent1 = select(fitnessArr, total);
				parent2 = select(fitnessArr, total);
				count++;
				if(count > 100) parent2 = parent1+1;		// ���ѷ��� ����
			} while(parent1 == parent2);	// �θ� ������ 2�� ���� �Ϸ�(��ø���� ���� �� ����, ������ ���� ���� ��� ���ѷ��� ����)

			String parentBianary1 = chromosome_Array[iterationCount][parent1].getBinary();
			String parentBianary2 = chromosome_Array[iterationCount][parent2].getBinary();
			String child1 = parentBianary1.substring(0, point1) + parentBianary2.substring(point1, point2) + parentBianary1.substring(point2);
			String child2 = parentBianary2.substring(0, point1) + parentBianary1.substring(point1, point2) + parentBianary2.substring(point2);
			chromosome_Array[iterationCount+1][i] = new Chromosome(child1);
			chromosome_Array[iterationCount+1][i+1] = new Chromosome(child2);
		}
		// crossoverRate �̿� ������ ����Ʈ ������ �״�� �ڼտ� ����
		sortChromosome(chromosome_Array[iterationCount]);
		for(int i=population; i<this.population; i++) {
			chromosome_Array[iterationCount+1][i] = chromosome_Array[iterationCount][i];
		}
		iterationCount++;	// �ڽ� ���븦 ��� ����� ���� ī��Ʈ ��
	}

	// �������� �߻�
	public void mutation(double mutationRate) {
		for(int i=0; i<population; i++) 
			for(int j=0; j<bitStreamLength; j++)
				if(Math.random() <= mutationRate) {
					String tmp = chromosome_Array[iterationCount][i].getBinary();
					if(tmp.charAt(j) == 1) chromosome_Array[iterationCount][i] = new Chromosome(tmp.substring(0, j) + "0" + tmp.substring(j+1));
					else chromosome_Array[iterationCount][i] = new Chromosome(tmp.substring(0, j) + "1" + tmp.substring(j+1));
				}
		printBestAndAvr();
	}
	
	public void printBestAndAvr() {
		double max = Double.MIN_VALUE;
		double sum = 0;
		for(int i = 0; i< chromosome_Array[iterationCount].length; i++)
		{
			sum += chromosome_Array[iterationCount][i].getFitness();
			if(max < chromosome_Array[iterationCount][i].getFitness())
				max = chromosome_Array[iterationCount][i].getFitness();
		}
		System.out.printf("��� �� : %.3f\t �ִ� �� :%.3f\n", sum/chromosome_Array[iterationCount].length, max);
	}

	// �ּ� fitness ��ȯ
	public double min_Fitness() {
		double min = Double.MAX_VALUE;
		for(int i=0; i<population; i++) {
			if(chromosome_Array[iterationCount][i].getFitness() < min)
				min = chromosome_Array[iterationCount][i].getFitness();
		}
		return min;
	}

	// �ִ� fitness ��ȯ
	public double max_Fitness(int iterationCount) {
		double max = Double.MIN_VALUE;
		for(int i=0; i<population; i++) {
			if(chromosome_Array[iterationCount][i].getFitness() > max)
				max = chromosome_Array[iterationCount][i].getFitness();
		}
		return max;
	}

	// ��� fitness ��ȯ
	public double avg_Fitness(int iterationCount) {
		double avg = .0;
		for(int i=0; i<population; i++) {
			avg += chromosome_Array[iterationCount][i].getFitness();
		}
		return avg / population;
	}

	public void createExcel() {
		String str = "BitStreamLength" + bitStreamLength + "_Population" + population + "_Iteration" + iteration + ".xls";
		WritableWorkbook workbook = null;

		// ��Ʈ ��ü ����
		WritableSheet sheet = null;

		// �� ��ü ����
		Label label = null;


		// ������ ���� ��ü ����
		File file = new File(str);
		try{
			// ���� ����
			workbook = Workbook.createWorkbook(file);

			// ��Ʈ ����
			workbook.createSheet("sheet1", 0);
			sheet = workbook.getSheet(0);

			// ������ ����
			for(int i=0; i < iteration; i++){
				label = new Label(0, i, Integer.toString(i));
				sheet.addCell(label);

				label = new Label(1, i, Double.toString(max_Fitness(i)));
				sheet.addCell(label);

				label = new Label(2, i, Double.toString(avg_Fitness(i)));
				sheet.addCell(label);
			}

			workbook.write();
			workbook.close();

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

// ������ ��ü
class Chromosome {
	private String binary;
	private double decimal;
	private double fitness;

	Chromosome(String binary) {
		this.binary = binary;
		decimal = String_To_Double(binary);
		fitness = Fitness(decimal);
	}

	// ���ڿ��� �� �������� double�� ��ȯ
	public double String_To_Double(String C) {
		double x = .0;
		for(int i = 1; i < binary.length(); i++) 
			if(C.charAt(i) == '1') x += Math.pow(2, 3 - i);

		// 4�� ��Ÿ���� ��Ʈ�� 3���� �ٲ�(������ �����ϱ� ���ؼ�) (3, 2, 1 ....)
		if(C.charAt(1) == '1') x -= 1;

		// �Ǽ� �κ� ������ ��Ʈ�� ������ �����ϰ� ����, �� �ڱ� �ڽ��� �ѹ��� �� �� (������ �����ϱ� ���ؼ�) (0.5, 0.25, 0.125, 0.125 ó��)
		if(C.charAt(binary.length()-1) == '1') x += Math.pow(2, 3 - (binary.length()-1));

		// ��ȣ ��Ʈ ó��
		if(C.charAt(0) == '1') x *= -1;
		return x;
	}

	// �Լ��� double ���� �־� ���յ� ���. �ִ밪�� ���ϹǷ� �������� ����.
	public double Fitness(double x) {
		return Math.pow(x, 5) - 4 * Math.pow(x, 4) - 12 * Math.pow(x, 3) + 34 * Math.pow(x, 2) + 11 * x - 30;
	}

	public String getBinary() {return binary;}
	public double getFitness() {return fitness;}
}