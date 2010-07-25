package treeSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class SortTreeSet {
	
	public static void main(String[] args){
		TreeSet<Obj> t = new TreeSet<Obj>();
		Obj a = new Obj(1);
		Obj b = new Obj(2);
		t.add(a);
		t.add(b);
		System.out.println("top =" + t.first().a);
		a.a = 3;
		System.out.println("top =" + t.first().a);
		t.remove(a);
		t.add(a);
		System.out.println("top =" + t.first().a);
//
//		ArrayList<Obj> l = new ArrayList<Obj>(t);
//		System.out.println(l.get(0).a);
//		Collections.sort(l);
//		System.out.println(l.get(0).a);
	}
}
