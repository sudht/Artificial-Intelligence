package P1;

import java.io.File;
import java.util.ArrayList;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

class FNN{	
	private double learningRate;
	private double [] H_hidden;
	private double [] Y_output;
	private double [][] WX;
	private double [][] WH;
	private ArrayList<String> Loss = new ArrayList<>();
	private String testAccuracy;

	public FNN(int countX, int countH, int countY, double learningRate) {
		this.learningRate = learningRate;
		this.H_hidden = new double[countH];
		this.Y_output = new double[countY];
		this.WX = new double[countX][countH];
		this.WH = new double[countH][countY];
		makeRandW(WX);
		makeRandW(WH);
	}

	void makeRandW(double [][] W) {
		for(int i=0; i<W.length; i++) {
			for(int j=0; j<W[0].length; j++) {
				W[i][j] = Math.random() - 0.5;
			}
		}
	}

	double sigmoid(double input) {
		return 1/(1+Math.exp(-input));
	}

	// 전방향 메소드 
	double feedForward(double[] X_input, double[][] W, int OutputNum) {
		double output = 0.;
		for(int i = 0;i < X_input.length; i++) {
			output += W[i][OutputNum] * X_input[i];	
		}
		output = sigmoid(output);
		return output;
	}

	// 입력층에서 은닉층으로 feedForward를 실행함.
	void inputToHidden(double[] X_input,double[][] WX, double [] H_hidden) {
		for(int i = 0; i < H_hidden.length; i++) {
			H_hidden[i] = feedForward(X_input, WX, i);
		}
	}

	// 은닉층에서 출력층으로 feedForward를 실행함.
	void HiddenToOutput(double[] H_hidden,double[][] WH, double [] Y_output) {
		for(int i = 0; i < Y_output.length; i++) {
			Y_output[i] = feedForward(H_hidden, WH, i);
		}
	}

	// 역방향 메소드
	void backward(double [] X_input, double [][] WX, double H_hidden, double error, int hiddenIndex) {
		for(int i=0; i<X_input.length; i++) { 
			WX[i][hiddenIndex] = WX[i][hiddenIndex] + (learningRate * X_input[i] *  H_hidden * (1 - H_hidden) * error);
		}
	}

	// 은닉층에서 입력층으로 backward를 실행함.
	void HiddenToInput(double [] X_input, double[][] WX, double[][] WH, double [] H_hidden, double [] Y_output, int [] Y_output_target) {
		double [] error = new double[H_hidden.length];
		for(int i=0; i < error.length; i++) {
			error[i] = 0.0;
			for(int j = 0; j < Y_output.length; j++) {
				error[i] += Y_output[j] * (1-Y_output[j]) * (Y_output_target[j] - Y_output[j]) * WH[i][j];
			}
		}
		for(int i =0; i < H_hidden.length; i++) {
			backward(X_input, WX, H_hidden[i], error[i], i);
		}
	}

	// 출력층에서 은닉층으로 backward를 실행함.
	void OutToHidden(double [] H_hidden, double [][] WH, double [] Y_output, int [] Y_output_target) {
		double [] error = new double[Y_output.length];
		for(int i=0; i<Y_output.length; i++) {
			error[i] = Y_output_target[i] - Y_output[i];
		}
		for(int i =0;i<Y_output.length;i++) {
			backward(H_hidden, WH, Y_output[i], error[i], i);
		}
	}

	int argMax(double [] arr) {
		int maxIndex = 0;
		double max =-999;

		for(int i=0; i<arr.length; i++) {
			if(arr[i]> max) {
				max = arr[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	void epoch(double [][] X_input, int [][] Y_output_target) {
		double loss_sum=0.0;
		double right_cnt=0;
		for(int i=0; i<X_input.length; i++) {
			inputToHidden(X_input[i], WX, H_hidden);
			HiddenToOutput(H_hidden, WH, Y_output);
			OutToHidden(H_hidden, WH, Y_output, Y_output_target[i]);
			HiddenToInput(X_input[i], WX, WH, H_hidden, Y_output, Y_output_target[i]);

			int maxIndex = argMax(Y_output);
			if(Y_output_target[i][maxIndex]==1)		// accuracy 계산을 위해 정답 카운트
				right_cnt++;

			for(int j=0; j<Y_output.length; j++)	// Loss Sum 계산
				loss_sum+= Math.pow(Y_output_target[i][j] - Y_output[j],2);
		}
		double accuracy=right_cnt/X_input.length*100;
		System.out.println("loss "+(loss_sum/2)+" accuracy : "+accuracy+"%");
		Loss.add(String.format("%.4f", loss_sum/2));
	}

	void test(double [][] X_input, int [][] Y_output_target) {
		double right_cnt=0;
		for(int i=0; i<X_input.length; i++) {
			inputToHidden(X_input[i], WX, H_hidden);
			HiddenToOutput(H_hidden, WH, Y_output);
			int maxIndex = argMax(Y_output);
			if(Y_output_target[i][maxIndex]==1)		// accuracy 계산을 위해 정답 카운트
				right_cnt++;
		}
		double accuracy = right_cnt/X_input.length*100;
		System.out.println("accuracy : "+accuracy+"%");
		testAccuracy = String.format("%.4f", accuracy);
	}

	void createExcel(int H, int EP, double LR) {
		String str = "H" + H + "_EP" + EP + "_LR" + LR + ".xls";
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
			for(int i=0; i < Loss.size(); i++){
				label = new Label(0, i, Integer.toString(i));
				sheet.addCell(label);
				
				label = new Label(1, i, Loss.get(i));
				sheet.addCell(label);

			}
			label = new Label(0, Loss.size(), "Accuracy");
			sheet.addCell(label);
			
			label = new Label(1, Loss.size(), testAccuracy);
			sheet.addCell(label);
			
			workbook.write();
			workbook.close();

		}catch(Exception e){
			e.printStackTrace();
		}

	}
}