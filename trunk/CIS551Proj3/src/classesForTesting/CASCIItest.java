package classesForTesting;

import cis551proj3.cascii.CASCII;

class CASIItest{
	public static void main(String[] args) throws Exception {
		byte[] barray = CASCII.Convert(args[0]);
		for(int i=0;i<barray.length;i++)
			System.out.print(barray[i]);
		//for(int i=0;i<barray.length;i+=5)
			//System.out.println(CASCII.Convert(barray));
	}
	
}