import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;


public class Hi implements Iterable{
	
	static SortedSet<String> set1 = new TreeSet<String>();
	static Set<String> set2 = new HashSet<String>();
	static Set<String> set3 = new HashSet<String>();
	
	public static void main(String[] args){
//		testList();
//		testStack();
//		set1.add("a");set1.add("b");set1.add("c");set1.add("d");set1.add("e");
//		set2.add("a");set2.add("e");set2.add("i");set2.add("o");set2.add("u");
//		set3.add("a");set3.add("b");
//		testArrayList();
//		for (Object it: set1){
//			System.out.println(it);
//		}
//		testMethods();
		int[] array = new int[8];
		Object obj = set1;
		System.out.println(obj.getClass());
	}

	private static void testArrayList() {
		ArrayList<Double> numbers = new ArrayList<Double>();
		numbers.add(3.33);
		
	}

	private static void testMethods() {
		if (set1.retainAll(set2)){
			System.out.println("True");
		}
		else System.out.println("false");
		System.out.println("set1 size = "+set1.size());
		System.out.println("set2 size = "+set2.size());
	}

	private static void testStack() {
		Stack<String> stack = new Stack<String>();
		stack.add("how");
		stack.add("are");
		stack.add("you");
		Iterator<String> x = stack.iterator();

		
		while (x.hasNext()){
			System.out.println(x.next());
			x.remove();
		}
		String[] strings2 = stack.toArray(new String[0]);
		Object[] objects = stack.toArray();
		for (int i = 0; i< strings2.length; i++){
			System.out.println(objects[i]);
		}
		
	}

	private static void testList() {
		LinkedList<String> list = new LinkedList<String>();
		list.add("how");
		list.add("are");
		list.add("you");
		
		String[] strings = list.toArray(new String[0]);
//		for (int i = 0; i< strings.length; i++){
//			System.out.println(strings[i]);
//		}
		System.out.println("list element = " + list.get(list.size()-1));
	}

	@Override
	public Iterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
