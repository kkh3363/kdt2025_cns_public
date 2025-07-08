public class Student {
	private String hakbun;
	private String name;
	private int korean, eng, math;
	
	public Student(String hakbun, String name, int korean, int eng, int math) {
		this.hakbun = hakbun;
		this.name = name;
		this.korean = korean;
		this.eng = eng;
		this.math = math;
	}
	public int getKorean() { return korean; }
	public int getTotal(){
		return korean + eng +math;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("%s %s %d ",  hakbun, name, getTotal());
	}
	
}

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class StudentDemo2 {

	public static void main(String[] args) {
		Student[] students = { new Student("100", "홍길동", 90,77,88)
				,  new Student("101", "이순신", 88,94,90)
				,  new Student("102", "타이거", 78,88,99)
				,  new Student("103", "라이온", 85,90, 100) };
		
		// 총점으로 정렬....
		Arrays.sort(students , new Comparator<Student>() {
			public int compare(Student std1, Student std2) {
				return std1.getTotal() - std2.getTotal();
			}
		});
		
		for (Student r : students)
            System.out.println(r);
		
		Arrays.sort(students , new Comparator<Student>() {
			public int compare(Student std1, Student std2) {
				return std1.getKorean() - std2.getKorean()  ;
			}
		});

	}

}
