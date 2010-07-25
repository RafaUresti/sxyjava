package superClass;

public class ChildClass extends BaseClass {

	void print(){
		System.out.println("child");

	}
	
	public static void main(String[] args){
		BaseClass b = new ChildClass();
		b.print();
	}
}
