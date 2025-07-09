package ch15.sec04;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

public class SeoulManager {
	List<CityPop> seoulList = new ArrayList<>();

	public SeoulManager() {
	
	}
	
	public void loadData() throws Exception{
		Reader reader = new FileReader("seoul.csv");
		BufferedReader inFile = new BufferedReader(reader);
		
		while (true) {
			String sLine = null;
		    if ( (sLine = inFile.readLine()) == null )
		    	break;
		    if ( sLine.charAt(0) != '#') {
		    	StringTokenizer st  = new StringTokenizer(sLine, ",");
		    	//System.out.println(st.countTokens());
		    	if ( st.countTokens() == 8 ) {
		    		String strTemp = null;
		    		CityPop newPop = new CityPop();
		    		newPop.setName(st.nextToken().trim());
		    		newPop.setCode(st.nextToken().trim());
		    		strTemp = st.nextToken();
		    		newPop.setTotalPopulation(Integer.parseInt(st.nextToken().trim()) );
		    		newPop.setHomeCount(Integer.parseInt(st.nextToken().trim()) );
		    		newPop.setHomePersonCount(Double.parseDouble(st.nextToken().trim()) );
		    		newPop.setManCount(Integer.parseInt(st.nextToken().trim()) );
		    		newPop.setWomenCount(Integer.parseInt(st.nextToken().trim()) );
		    		
		    		seoulList.add(newPop);
		    		
		    	}
		    }
		}
		
	}
	// 남자 인구가 가장 많은 곳
	public String getManMaxGu() {
		CityPop cityPop = seoulList.stream().max( Comparator.comparing(CityPop::getManCount)).get();
		return cityPop.getName();
	}
	// 남자 인구가 가장 적은 곳
	public String getManMinGu() {
		CityPop cityPop = seoulList.stream().min( Comparator.comparing(CityPop::getManCount)).get();
		return cityPop.getName();
	}
	// 여자 인구가 가장 많은 구
	public String getWomenMaxGu() {
		CityPop cityPop = seoulList.stream().max( Comparator.comparing(CityPop::getWomenCount)).get();
		return mcityPopax.getName();
	}
	// 여자 인구가 가장 적은 곳...
	public String getWomenMinGu() {
		CityPop cityPop = seoulList.stream().min( Comparator.comparing(CityPop::getWomenCount)).get();
		return cityPop.getName();
	}
	
	public int getCurrentListCount() {
		return seoulList.size();
	}
}
