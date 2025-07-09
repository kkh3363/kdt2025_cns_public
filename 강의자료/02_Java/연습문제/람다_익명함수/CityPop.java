package ch15.sec04;

public class CityPop {
	private String name;
	private String code;
	private int totalPopulation;
	private int homeCount;
	private double homePersonCount;
	private int manCount;
	private int womenCount;
	
	public CityPop() { }
	
	public CityPop(String name, String code, int totalPopulation, int homeCount, int homePersonCount, int manCount,
			int womenCount) {
		super();
		this.name = name;
		this.code = code;
		this.totalPopulation = totalPopulation;
		this.homeCount = homeCount;
		this.homePersonCount = homePersonCount;
		this.manCount = manCount;
		this.womenCount = womenCount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getTotalPopulation() {
		return totalPopulation;
	}
	public void setTotalPopulation(int totalPopulation) {
		this.totalPopulation = totalPopulation;
	}
	public int getHomeCount() {
		return homeCount;
	}
	public void setHomeCount(int homeCount) {
		this.homeCount = homeCount;
	}
	public double getHomePersonCount() {
		return homePersonCount;
	}
	public void setHomePersonCount(double homePersonCount) {
		this.homePersonCount = homePersonCount;
	}
	public int getManCount() {
		return manCount;
	}
	public void setManCount(int manCount) {
		this.manCount = manCount;
	}
	public int getWomenCount() {
		return womenCount;
	}
	public void setWomenCount(int womenCount) {
		this.womenCount = womenCount;
	}
	
	
}
