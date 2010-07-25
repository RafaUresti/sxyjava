
public class StringInplaceSpaceRemoval {
	public static void main(String[] args){
		removeSpace("how are you ? ");
	}
	public static void removeSpace(String s){
		StringBuilder sb = new StringBuilder(s);
		int i = 0;
		while (i < sb.length() && sb.charAt(i) != ' '){
			i++;
		}
		if (i < sb.length()){
			for (int j = i + 1; j < sb.length(); j ++){
				if (sb.charAt(j)!=' '){
					sb.setCharAt(i, sb.charAt(j));
					sb.setCharAt(j, ' ');
					i++;
				}
			}
		}
		System.out.println(sb);
		try{
			System.out.println("print");
			return;
		} catch(Exception e){
			
		} finally {
			System.out.println("executed?");
		}
	}
}

