package ch11.sec01;

public class LottoExam {

	public static boolean haveNum(int[] array, int num) {
		for(int i=0; i < array.length; i++) {
			if ( array[i] == num)
				return true;
		}
		return false;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] lottoArray = {0,0,0,0,0,0};
		int nIndex = 0;

		while (true) {
			int num = (int)(Math.random() * 45) +1;
			if ( haveNum(lottoArray, num) == false) {
				lottoArray[nIndex] = num;
				nIndex++;
			}
			if ( lottoArray.length == nIndex)
				break;
		}
		for ( int item : lottoArray)
			System.out.print(item +" ");
		System.out.println();
 	}

}
