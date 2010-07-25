package comparisons;

public class EmailDeliveryComparison {


	public static void main(String[] args){
		String[] a1 = Data1.a.split("[\n|\r\n]");
		String[] b1 = Data2.b.split("[\n|\r\n]");
		System.out.println("Start of 1:");
		for (int i = 0; i < a1.length; i ++){
			if (a1[i].startsWith("delivered email:")){
				System.out.println(a1[i]);
			}
		}
		System.out.println("\n\nStart of 2:");
		for (int i = 0; i < b1.length; i ++){
			if (b1[i].startsWith("delivered email:")){
				System.out.println(b1[i]);
			}
		}
	}
}
