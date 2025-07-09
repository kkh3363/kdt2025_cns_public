package ch15.sec04;


public class Exam04 {
	
	
	public static void main(String[] args) {
		SeoulManager sman = new SeoulManager();
		
		try {
			sman.loadData();
		}catch(Exception e) {
			System.out.println(e);
		}
		
		System.out.println( sman.getCurrentListCount());
		if ( sman.getCurrentListCount() < 1) 
			System.exit(0);
		
		System.out.println( "남자 인구가 가장 많은 곳은 : " + sman.getManMaxGu());
		System.out.println( "여자 인구가 가장 많은 곳은 : " + sman.getWomenMaxGu());
	}

}
