public class Bitwise{
	/* @param text1: n-bit text.
	 * @param text2: n-bit text.
	 * @return n-bit XOR text of text1 and text2.*/
	public static String xor(String text1, String text2){
		String xored = "";
		for(int i = 0; i < text1.length() && i < text2.length(); i++){
			if(text1.charAt(i) == text2.charAt(i)){ 
				xored += "0";	
			}else{
				xored += "1";
			}
		}
		return xored;
	}
}
