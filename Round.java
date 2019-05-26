public class Round{	
	// sInstructions contains the order of the steps of the round. This is used for DES1/2/3 and is not part of the original DES standard
	// it is used to test how insecure DES is when you modify any part of the algorithm
	private int[] sInstructions; 
	private int[] eTable = new int[]{32,1,2,3,4,5,4,5,6,7,8,9,8,9,10,11,12,13,12,13,14,15,16,17,16,17,18,19,20,21,20,21,22,23,24,25,24,25,26,27,28,29,28,29,30,31,32,1};
	private int[] iETable = new int[]{2,3,4,5,8,9,10,11,14,15,16,17,20,21,22,23,26,27,28,29,32,33,34,35,38,39,40,41,44,45,46,47};
	private int[] pTable = new int[]{16,7,20,21,29,12,28,17,1,15,23,26,5,18,31,10,2,8,24,14,32,27,3,9,19,13,30,6,22,11,4,25};
	//This array contains the 16 S-boxes
	private int[][][] sBoxes = new int[][][] //[sBoxID][column][row]
	{
		{
			{14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
			{0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
			{4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
			{15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
		},

		{
			{15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
			{3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
			{0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
			{13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
		},

		{
			{10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
			{13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
			{13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
			{1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
		},

		{
			{7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
			{13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
			{10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
			{3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
		},

		{
			{2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
			{14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
			{4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
			{11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
		},

		{
			{12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
			{10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
			{9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
			{4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
		},

		{
			{4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
			{13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
			{1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
			{6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
		},

		{
			{13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
			{1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
			{7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
			{2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
		},
	};

	/* Constructor: Round
	 * @param sInstructions: Sequence of individual ciphers to be performed. */
	public Round(int[] sInstructions){this.sInstructions = sInstructions;}
	
	/* @param lHalf: 32-bit integer (Left half of the text).
	 * @param rHalf: 32-bit integer (Right half of the text).
	 * @param subKey: 48-bit integer.
	 * @return: The processed text, after a single round.*/
	public String process(String lHalf, String rHalf, String subKey){
		String leftSideUsedForXor = lHalf;
		lHalf = rHalf;
		// Subject the right side of the text, through a series of 
		// permutations and substitutions. 
		rHalf = function(rHalf, subKey); //Apply our round function to the right half using the appropriate subkey
		// Bitwise operator exclusiveOR.
		rHalf = Bitwise.xor(leftSideUsedForXor, rHalf); //And XOR the left half over it
		return lHalf+rHalf;
	}

	/* @param rHalf: 32-bit integer (Right half initial text).
	 * @param subKey: 48-bit integer.
	 * @return Processed 32-bit rhalf. */
	private String function(String rHalf, String subKey){
		// Expand the 32-bit text to 48-bits.
		rHalf = expansion(rHalf);
		// Bitwise operator exclusiveOR.
		// XOR the 48-bits, with the 48-bit sub key. 
		rHalf = Bitwise.xor(rHalf, subKey);
		// Execute additional methods, in the order in which they
		// were expressed in the int array sInstructions. 
		for(int i = 0; i < sInstructions.length; i++){
			rHalf = executeAdditionalCiphers(sInstructions[i], rHalf);
		}
		return rHalf;
	}

	/* @param i: The value in the value corresponding to an additional cipher to be executed. 
	 * @param text: The text to be processed through the cipher.
	 * @return Processed 32-bit text*/
	private String executeAdditionalCiphers(int i, String text){
		switch(i){
		case 0:
			text = substitution(text);
			break;
		case 1:
			text = permutation(text);
			break;
		case 2:
			text = inverseExpansion(text);
			break;
		}
		return text;
	}

	/* @param text: Text to be permuted, using a table.
	 * @return Permutation of the text. */
	public String expansion(String text){return Transposition.permute(text, eTable);}
	
	/* @param text: Text to be permuted, using a table.
	 * @return Permutation of the text. */
	public String inverseExpansion(String text){return Transposition.permute(text, iETable);}
	
	/* @param text: Text to be permuted, using a table.
	 * @return Permutation of the text. */
	public String permutation(String text){ return Transposition.permute(text, pTable); }
	
	/* @param text: A 48-bit integer
	 * @return: A 48-bit integer that has the 16 S-boxes applied to it */
	private String substitution(String text){
		String value="";
		String textBlock="";

		//break the text into 8 different 6 bit blocks
		for(int i=0; i<8; i++){
			textBlock=text.substring((i*6),(i*6)+6);

			//use the middle 4 bits for the row and the outer 2 bits for the column
			int row=binaryToDec(textBlock.substring(1,5));
			int column=binaryToDec(textBlock.substring(0,1)+textBlock.substring(5,6));
			
			//use the value in the corresponding sbox
			String block=decimalToBin(sBoxes[i][column][row]);
			
			//make sure it is 4 bits long
			while(block.length()<4){
				block="0"+block;
			}

			value+=block;
		}
		return value;
	}

	/* @param text accepts a string containing binary digits and return the integer conversion of it
	 * @return value*/
	private int binaryToDec(String text){
		int multiplier=1;
		int value=0;

		for(int i=text.length(); i>0; i--){ //loop backwards through the string
			if(text.substring(i-1,i).equals("1")){
				value+=multiplier; //add our multiplier value for each 1, add nothing for 0's
			}
			multiplier*=2; //double multiplier value for each digit
		}

		return value;
	}

	/* @param num accepts and integer and return a binary string conversion of it
	 * @return Binary string conversion of the input*/
	private String decimalToBin(int num){
		int multiplier=1;
		String value="";

		//start off with the largest multiple of 2 that is smaller than the number
		while((multiplier*2)<=num){
			multiplier*=2;
		}

		while(multiplier>0){
			if(multiplier>num){
				value+="0";
			}else{
				value+="1";
				num-=multiplier;
			}
			multiplier/=2;
		}
		return value;
	}
}
