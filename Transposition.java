public class Transposition {	
	/* Rearranges the @param text in an order defined by the @param table. 
	 * @param text: The text to be rearranged. 
	 * @param table: The order in which the text should be rearranged. 
	 * @return: Returns the permutation of the text.*/
	public static String permute(String text, int[] table){
		String permutation = "";
		for(int i = 0; i < table.length; i++){ //loop through the table from start to end
			permutation += text.substring(table[i] - 1, table[i]); //form the permuted string character by character
		}
		return permutation;
	}
}
