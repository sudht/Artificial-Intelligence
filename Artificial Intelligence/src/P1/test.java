package P1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

public class test {
	private static final String file_path = "data/";
	private static final String data_file_name = "CTG.csv";
	private static final double training_data_rate = 0.8;
	private static int data_list_count = 2126, input_count = 21;

	private static void read_Data(double[][] input_data_list, int[] answer_data_list) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file_path + data_file_name));
			int data_list_index = 0;
			String line;
			while(reader.ready()) {
				line = reader.readLine();
				StringTokenizer tokens = new StringTokenizer(line, ",");
				for(int i=0; i<input_count; ++i)
					input_data_list[data_list_index][i] = Double.parseDouble(tokens.nextToken());
				tokens.nextToken();		// 필요없는 데이터는 건너뛴다.
				answer_data_list[data_list_index] = Integer.parseInt(tokens.nextToken())-1;
				++data_list_index;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void randomization(double [][] bigArrX, double [][] smallArrX, int[] bigArrY, int[] smallArrY) {
		int a, b, tempY;
		double [] tempX;
		for(int i=0; i<10000; i++) {
			a = (int) (bigArrY.length * Math.random());
			b = (int) (smallArrY.length * Math.random());
			tempX = bigArrX[a];
			bigArrX[a] = smallArrX[b];
			smallArrX[b] = tempX;
			
			tempY = bigArrY[a];
			bigArrY[a] = smallArrY[b];
			smallArrY[b] = tempY;
		}
	}

	public static void vectorize(int[] nonvector, int [][] vector) {
		for(int i = 0; i<nonvector.length; i++) {
			switch(nonvector[i]) {
			case 0:
				vector[i][0] = 1;
				vector[i][1] = 0;
				vector[i][2] = 0;
				break;
			case 1:
				vector[i][0] = 0;
				vector[i][1] = 1;
				vector[i][2] = 0;
				break;
			case 2:
				vector[i][0] = 0;
				vector[i][1] = 0;
				vector[i][2] = 1;
				break;
			}
		}
	}

	public static void main(String[] args) {	
		double[][] input_data_list = new double[data_list_count][input_count];
		int[] answer_data_list = new int[data_list_count];
		read_Data(input_data_list, answer_data_list);
		double[][] X_input = Arrays.copyOfRange(input_data_list, 0, (int)(data_list_count * training_data_rate));
		double[][] X_input_test = Arrays.copyOfRange(input_data_list, X_input.length, input_data_list.length);
		int[] Y_output_target_nonvector = Arrays.copyOfRange(answer_data_list, 0, (int)(data_list_count * training_data_rate));
		int[] Y_output_target_test_nonvector = Arrays.copyOfRange(answer_data_list, Y_output_target_nonvector.length, answer_data_list.length);

		int[][] Y_output_target = new int [Y_output_target_nonvector.length][3];
		int[][] Y_output_target_test = new int [Y_output_target_test_nonvector.length][3];
		randomization(X_input, X_input_test, Y_output_target_nonvector, Y_output_target_test_nonvector);
		vectorize(Y_output_target_nonvector, Y_output_target);
		vectorize(Y_output_target_test_nonvector, Y_output_target_test);

		int countH = 1000;
		int epochCount = 10000;
		double learningRate=0.00001;
		int countX = X_input[0].length;
		int countY = Y_output_target[0].length;

		FNN fnn= new FNN(countX, countH, countY, learningRate);

		for(int i=0; i<epochCount; i++) {
			System.out.println(i + "번째 실행");
			fnn.epoch(X_input, Y_output_target);
		}
		fnn.test(X_input_test, Y_output_target_test);
		fnn.createExcel(countH, epochCount, learningRate);
	}
}