public class DES {
	private DESMode mode;
	private Version version;
	private KeyGenerator keyGenerator;
	private Round round;
	private String plaintext;
	private String ciphertext;
	private String[] roundText; //contains the text at the end of each round. Used to calculate Avalanche effect
	public final int NUMBEROFROUNDS = 16;
	private final int BLOCKLENGTH = 64;
	
	private final int[] initialPermutationTable = new int[]{58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7};
	private final int[] finalPermutationTable = new int[]{40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25};
	
	public enum DESMode{
		/* The boolean argument determines if we need to reverse the subkeys.
		 * false: no need to reverse, otherwise reverse subkeys.*/
		ENCRYPT(false), 
		DECRYPT(true);
		private boolean value;

		/* @param value: The DES mode either encrypting or decrypting the text*/
		DESMode(boolean value){ this.value = value;}

		/* @return boolean value. */
		public boolean valueOf(){return this.value;}
	}

	/* This enum contains the specifications for the 4 DES versions used in the assignment */
	public enum Version{
		DES0("DES0", new int[]{0,1}),
		DES1("DES1", new int[]{0}),
		DES2("DES2", new int[]{2,1}),
		DES3("DES3", new int[]{2});
				
		private String version;	// E.g. DES0, DES1, DES2, or DES3
		
		/* Sequence of instructions for which the round will execute and in what order. */
		private int[] sequence;

		/* @return the current version of the DES algorithm */
		public String getVersion(){ return version;	}
		
		/* @return The version of instructions and their order to be executed. */
		public int[] getSequence(){ return sequence;}

		/* @param version
		 * @param sequence */
		Version(String version, int[] sequence){
			this.version = version;
			this.sequence = sequence;
		}
	}  
	
	/* @param version: The version of DES used to encrypt or decrypt the text.
	 * @param noOfRounds: The number of rounds the text is subjected to. */
	public DES(Version version){
		this.version = version;
		this.round = new Round(version.getSequence()); //use the specification found in Version to get the function sequence
		this.roundText = new String[NUMBEROFROUNDS];
	}

	/* @param mode: The mode of DES either encrypting or decrypting the text. 
	 * @param key: 56-bit key. */
	public void initializeCipher(DESMode mode, String key){
		this.mode = mode;
		// .valueOf will either be true (decrypting) or false (encrypting). 
		// It defines if the generated subkeys are applied in reverse order. 
		this.keyGenerator = new KeyGenerator(mode.valueOf(), key, NUMBEROFROUNDS);
	}

	/* @param text: The text DES is going to transform into either plaintext or ciphertext. */
	public void begin(String text){
		if(text.length() < BLOCKLENGTH){
			// pad the text with zeros until it has 64-bits.
			text = padded(text);
		}
		if(mode.valueOf()){
			// Decrypting the ciphertext to obtains its plaintext
			ciphertext = text;
			plaintext = transform(ciphertext);
		}else{
			// Encrypting the plaintext to obtains its ciphertext
			plaintext = text;
			ciphertext = transform(plaintext);
		}
	}

	/* If the text we are either encrypting or decrypting is less than a 64-bit block.
	 * Pad to the end of the text, but adding additional zeros.
	 * @param text: n-bit text less than 64 bits.
	 * @return padded 64-bit text */
	private String padded(String text) {
		for(int i = text.length(); i < BLOCKLENGTH; i++){
			text += "0";
		}
		return text;
	}

	/* Subjects the @param text through a combination of permutations and rounds,
	 * to return a newly text that is either the plaintext or ciphertext.
	 * Depending on the mode of DES. 
	 * @param text: The n-bit text needed to be transformed to either plaintext or ciphertext.
	 * @return transformed n-bit text*/
	private String transform(String text){
		// Initial permutation;
		String permutatedInput = Transposition.permute(text, initialPermutationTable);
		// Iteration through the rounds;
		String roundInput = rounds(permutatedInput);
		// Final permutation through the use of swapping left and right halves.
		String fpermutatedInput = swap(roundInput);
		// Final (Inverse) permutation; 
		return Transposition.permute(fpermutatedInput, finalPermutationTable);
	}

	/* @param text: Text that containing the n-bits of data. 
	 * @return the permutation of the text */
	private String swap(String text){ return right(text)+left(text); }
	
	/* @param text: n-bit text processed by the 'noOfRounds' rounds.
	 * @return the processed text transformed by the rounds. */
	private String rounds(String text){
		for(int i = 0; i < NUMBEROFROUNDS; i++){
			text = round.process(left(text), right(text), keyGenerator.subkey()); //call our Round on the text with the correct subkey
			roundText[i] = text;
		}

		return text;
	}
	
	/* @param text: the n-bit text used to extract the left half from.
	 * @return The left half of the text */
	private String left(String text){ return text.substring(0, (text.length() / 2)); }
	
	/* @param text: the n-bit text used to extract the right half from.
	 * @return The right half of the text */
	private String right(String text){ return text.substring((text.length() / 2), text.length());}
	
	/*@return the current version of DES.*/
	public String version(){ return version.getVersion();}
	
	/* @return the result after a block of 64-bit has been gone through DES Rounds. */
	public String ciphertext(){ return ciphertext;}

	/* @return the original 64-bit plaintext. */
	public String plaintext(){ return plaintext;}
	
	/* @param index: The index used to extract the processed text for a particular round.  
	 * @return The text processed at 'index'th round*/
	public String roundText(int index){ 
		if(index == 0){
			return plaintext;
		}
		return roundText[index - 1];
	}
}
