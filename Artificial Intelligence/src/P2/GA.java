package P2;

import java.io.File;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class GA {
	private int bitStreamLength;	// 비트 배열 길이 (유전자 길이)
	private int population;			// 유전자 개수 
	private int iteration;			// 세대 수(반복 회수)
	private int iterationCount = 0;	// 세대 카운트
	private Chromosome [][] chromosome_Array;

	GA(int bitStreamLength, int population, int iteration) {
		this.bitStreamLength = bitStreamLength;
		this.population = population;
		this.iteration = iteration;
		chromosome_Array = new Chromosome[this.iteration][this.population];
		init_Chromosome_Arr();
	}

	// 이진수로 이루어진 문자열 생성 (최초 1회만 실행)
	public void init_Chromosome_Arr() {
		for(int i=0; i<population; i++) {
			String C = "";
			for(int j = 0; j < bitStreamLength; j++) {
				C += Integer.toString((int)(Math.random() + 0.5));
			}
			chromosome_Array[0][i] = new Chromosome(C);
		}
	}

	// 룰렛 선택 방법에 따라 유전자를 1개 골라주는 함수
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

	// 두 유전자를 결합하여 자식 유전자 2개를 만드는 함수
	public void crossover(int point1, int point2, double crossoverRate) {
		int population = (int)(this.population*crossoverRate);
		double min = min_Fitness();
		double [] fitnessArr = new double[population];
		double total = .0;

		for(int i=0; i<population; i++) {
			fitnessArr[i] = chromosome_Array[iterationCount][i].getFitness() - min;	// 모든 유전자의 적합도의 평준화를 위해 최소값을 구해서 빼줌
			total += fitnessArr[i];
		}

		// 자식 세대를 만들어 가는 부분
		int parent1, parent2;
		for(int i=0; i<population; i+= 2) {
			int count = 0;
			do {
				parent1 = select(fitnessArr, total);
				parent2 = select(fitnessArr, total);
				count++;
				if(count > 100) parent2 = parent1+1;		// 무한루프 방지
			} while(parent1 == parent2);	// 부모 유전자 2개 선발 완료(중첩되지 않을 때 까지, 유전자 수가 적을 경우 무한루프 가능)

			String parentBianary1 = chromosome_Array[iterationCount][parent1].getBinary();
			String parentBianary2 = chromosome_Array[iterationCount][parent2].getBinary();
			String child1 = parentBianary1.substring(0, point1) + parentBianary2.substring(point1, point2) + parentBianary1.substring(point2);
			String child2 = parentBianary2.substring(0, point1) + parentBianary1.substring(point1, point2) + parentBianary2.substring(point2);
			chromosome_Array[iterationCount+1][i] = new Chromosome(child1);
			chromosome_Array[iterationCount+1][i+1] = new Chromosome(child2);
		}
		// crossoverRate 이외 범위의 엘리트 집단은 그대로 자손에 전달
		sortChromosome(chromosome_Array[iterationCount]);
		for(int i=population; i<this.population; i++) {
			chromosome_Array[iterationCount+1][i] = chromosome_Array[iterationCount][i];
		}
		iterationCount++;	// 자식 세대를 모두 만들고 세대 카운트 업
	}

	// 돌연변이 발생
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
		System.out.printf("평균 값 : %.3f\t 최대 값 :%.3f\n", sum/chromosome_Array[iterationCount].length, max);
	}

	// 최소 fitness 반환
	public double min_Fitness() {
		double min = Double.MAX_VALUE;
		for(int i=0; i<population; i++) {
			if(chromosome_Array[iterationCount][i].getFitness() < min)
				min = chromosome_Array[iterationCount][i].getFitness();
		}
		return min;
	}

	// 최대 fitness 반환
	public double max_Fitness(int iterationCount) {
		double max = Double.MIN_VALUE;
		for(int i=0; i<population; i++) {
			if(chromosome_Array[iterationCount][i].getFitness() > max)
				max = chromosome_Array[iterationCount][i].getFitness();
		}
		return max;
	}

	// 평균 fitness 반환
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

		// 시트 객체 생성
		WritableSheet sheet = null;

		// 셀 객체 생성
		Label label = null;


		// 저장할 파일 객체 생성
		File file = new File(str);
		try{
			// 파일 생성
			workbook = Workbook.createWorkbook(file);

			// 시트 생성
			workbook.createSheet("sheet1", 0);
			sheet = workbook.getSheet(0);

			// 데이터 삽입
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

// 유전자 객체
class Chromosome {
	private String binary;
	private double decimal;
	private double fitness;

	Chromosome(String binary) {
		this.binary = binary;
		decimal = String_To_Double(binary);
		fitness = Fitness(decimal);
	}

	// 문자열로 된 이진수를 double로 반환
	public double String_To_Double(String C) {
		double x = .0;
		for(int i = 1; i < binary.length(); i++) 
			if(C.charAt(i) == '1') x += Math.pow(2, 3 - i);

		// 4를 나타내는 비트를 3으로 바꿈(범위를 유지하기 위해서) (3, 2, 1 ....)
		if(C.charAt(1) == '1') x -= 1;

		// 실수 부분 마지막 비트를 이전과 동일하게 만듦, 즉 자기 자신을 한번더 더 함 (범위를 유지하기 위해서) (0.5, 0.25, 0.125, 0.125 처럼)
		if(C.charAt(binary.length()-1) == '1') x += Math.pow(2, 3 - (binary.length()-1));

		// 부호 비트 처리
		if(C.charAt(0) == '1') x *= -1;
		return x;
	}

	// 함수에 double 값을 넣어 적합도 계산. 최대값을 구하므로 높을수록 좋음.
	public double Fitness(double x) {
		return Math.pow(x, 5) - 4 * Math.pow(x, 4) - 12 * Math.pow(x, 3) + 34 * Math.pow(x, 2) + 11 * x - 30;
	}

	public String getBinary() {return binary;}
	public double getFitness() {return fitness;}
}