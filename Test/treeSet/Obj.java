package treeSet;

public class Obj implements Comparable<Obj>{

	int a;
	public Obj(int a){
		this.a = a;
	}
	@Override
	public int compareTo(Obj o) {
		return a - o.a;
	}
	
	@Override
	public boolean equals(Object w){
		return (a - ((Obj)w).a == 0);
	}
	
	
}
