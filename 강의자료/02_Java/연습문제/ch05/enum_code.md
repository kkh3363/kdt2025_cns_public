```
package ch05;
import java.util.Calendar;
public class code0507 {
    public static void main(String[] args) {
        Week today = null;
        Calendar cal = Calendar.getInstance();
        int week = cal.get( Calendar.DAY_OF_WEEK);
        Season  seasonMonth = getSeason(cal.get( Calendar.MONTH) +1 );
        System.out.println("Season: "+ seasonMonth);
        // 1: sunday
        switch(week) {
			case 1:
				today = Week.SUNDAY; break;
			case 2:
				today = Week.MONDAY; break;
			case 3:
				today = Week.TUESDAY; break;
			case 4:
				today = Week.WEDNESDAY; break;
			case 5:
				today = Week.THURSDAY; break;
			case 6:
				today = Week.FRIDAY; break;				
			case 7:
				today = Week.SATURDAY; break;		
		}
		
		System.out.println("오늘 요일: "+ today);
		
		if(today == Week.SUNDAY) {
			System.out.println("일요일에는 축구를 합니다.");
		} else {
			System.out.println("열심히 자바 공부합니다.");
		}
    }
    public static Season getSeason(int month){
        Season season;
            switch (month) {
            case 12:
            case 1 :
            case 2 :
                season = Season.WINTER; break;
            case 3:
            case 4: 
            case 5 :
                season = Season.SPRING; break;
            case 6:
            case 7:
            case 8:
                season =Season.SUMMER;break;
            default:
                season =Season.FALL;
                break;
        }
        return season;
    }
}
```
