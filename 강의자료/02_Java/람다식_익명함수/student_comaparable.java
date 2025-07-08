//public class Student implements Comparable<Student>{
class Student implements Comparable<Student>{
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
@Override
	public int compareTo(Student o) {
		// TODO Auto-generated method stub
		return getTotal() - o.getTotal();
	}
	
}

public class StudentDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Student[] students = { new Student("100", "홍길동", 90,77,88)
				,  new Student("101", "이순신", 88,94,90)
				,  new Student("102", "타이거", 78,88,99)
				,  new Student("103", "라이온", 85,90, 100) };
		
		// 오름 차순
		Arrays.sort(students);
		// 내림 차순
		Arrays.sort(students, Collections.reverseOrder());
		
		for (Student r : students)
            System.out.println(r);
		
	}

}
