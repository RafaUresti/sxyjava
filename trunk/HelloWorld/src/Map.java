import java.util.*;
import java.util.Map.Entry;

public class Map {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<String, Double> myMap1 = new HashMap<String, Double>();
		SortedMap<String, Double> myMap2 = new TreeMap<String, Double>();
		
		myMap1.put("how", 3.3);
		myMap1.put("are", 4.3);
		myMap2.put("ho"+"w", 3.3);
		myMap2.put("are", 4.3);
//		for (Entry<String, Double> entry: myMap.entrySet())
//			System.out.println(entry + " = " + myMap.get(entry));
		Set<Entry<String, Double>> entrySet1 = myMap1.entrySet();
		Set<Entry<String, Double>> entrySet2 = myMap2.entrySet();
		System.out.println(entrySet1.equals(entrySet2));
	}

}
