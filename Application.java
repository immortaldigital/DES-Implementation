/* 
 * This program will encrypt and decrypt a 64 bit message using a 56 bit key using a hand-coded version of the DES algorithm.
 * It contains the original DES algorithm, as well as three slightly modified versions to demonstrate the importance of each part.
 * It will also encrypt each message P under every possible Ki where Ki differs from key K by 1 bit.
 * As well as encrypt each message Pi under key K, where Pi is every possible message that differs from P by 1 bit.
 * Finally it will check the average difference between the text generated at the end of each of the 16 rounds for Pi
 * and that of message P (and the same for Ki and K). It does this for each algorithm DES0/1/2/3 and demonstrates how much a small
 * change to the algorithm affects the security of the cipher.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Application {
	
	/* @param args: Contains the arguments used for the input and output files */
	public static void main(String[] args) {	
		// Name of the file containing the text, and key. 
		try{
			Scanner scanner = new Scanner(new File(args[0]));
				
			// Name of the file the data will be saved to. 
			String output = args[1];	

			String bit = scanner.nextLine();
			String text = scanner.nextLine();
			String key = scanner.nextLine();
			
			if(key.length() < 56 || key.length() > 56){
				throw new Exception("Number of bits in the key is not 56.");
			}
			
			if(!output.contains(".txt")){
				output += ".txt";
			}
			
			if(bit.equals("0")){
				encryption(text, key, output);
			}else{
				decryption(text, key, output);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/* Encrypts and saves the appropriate data to the file called 'outputFilename' 
	 * @param plaintext: 64-bit binary integer.
	 * @param key: 56-bit binary integer. 
	 * @param outputFilename: the name of the output file.  
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException */
	private static void encryption(String plaintext, String key, String outputFilename) throws FileNotFoundException, UnsupportedEncodingException{
		DES[][] desKi, desPi; //DES P under each Ki and K under each Pi
		DES[] desPK;
		String[] p, k;
		PrintWriter eWriter;
		// No difference in either plaintext P or key K. 
		desPK = new DES[DES.Version.values().length]; //4 DES versions, the original and a few different modified versions
		
		for(int i = 0; i < DES.Version.values().length; i++){
			desPK[i] = new DES(DES.Version.values()[i]); //Create a DES object for each version
			
			// Encrypt P under K;
			desPK[i].initializeCipher(DES.DESMode.ENCRYPT, key);
			desPK[i].begin(plaintext);
		}
				
		// Arrays of plaintexts and keys differing by 1-bit.
		p = bitDifferenceArray(plaintext);
		k = bitDifferenceArray(key);
					
		// DES Plain texts with a difference of 1-bit. Pi under K and plaintext P under different keys (by 1-bit).
		desKi = differentPlaintextsUnderKeyK(p.length, key, p); 
		desPi = plaintextUnderDifferentKeys(k.length, plaintext, k);
		
		// Output the desired information of encryption. 	
		eWriter = new PrintWriter(outputFilename, "UTF-8");
	    eWriter.write("ENCRYPTION"+System.lineSeparator());
	    eWriter.write("Plaintext P:" + plaintext +System.lineSeparator());
	    eWriter.write("Key K:" + key+System.lineSeparator());
	    eWriter.write("Ciphertext C:"+ desPK[0].ciphertext()+System.lineSeparator());
	    eWriter.write("Avalanche:"+System.lineSeparator());
		eWriter.write("P and Pi under K"+System.lineSeparator());
		eWriter.write("Round:\tDES0\tDES1\tDES2\tDES3"+System.lineSeparator());
	    avalanche(plaintext.length(), desPK, desKi, eWriter);
		eWriter.write("P under K and Ki"+System.lineSeparator());
		eWriter.write("Round:\tDES0\tDES1\tDES2\tDES3"+System.lineSeparator());
	    avalanche(key.length(), desPK, desPi, eWriter);
		eWriter.close();

	}

	/* @param ciphertext: 64-bit binary integer.
	 * @param key: 56-bit binary integer. 
	 * @param outputFilename: the name of the output file.  
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException*/
	private static void decryption(String ciphertext, String key, String outputFilename) throws FileNotFoundException, UnsupportedEncodingException{
		DES des = new DES(DES.Version.DES0); //DES0 is the original DES algorithm
		des.initializeCipher(DES.DESMode.DECRYPT, key); //initialise to decrypt and use the supplied 56bit key
		des.begin(ciphertext);
				
		// Output the desired information for decryption. 
	    PrintWriter dWriter = new PrintWriter(outputFilename, "UTF-8");
		dWriter.write("DECRYPTION"+System.lineSeparator());
		dWriter.write("Ciphertext C:" + des.ciphertext()+System.lineSeparator());
		dWriter.write("Key K:" + key+System.lineSeparator());
		dWriter.write("Plaintext P:"+ des.plaintext()+System.lineSeparator());	
		dWriter.close();
	}

	/* @param copies: The number of different iPlaintexts, each differing by 1 bit.
	 * @param key: 56-bit binary integer,
	 * @param iPlaintexts: Plaintexts are differing by 1-bit.
	 * @return An array of DES objects, each with different plaintexts. */
	private static DES[][] differentPlaintextsUnderKeyK(int copies, String key, String[] iPlaintexts){
		return desArray(false, copies, key, iPlaintexts);
	}
	
	/* @param copies: The number of different iPlaintexts, each differing by 1 bit.
	 * @param plaintext: Plaintext to be encrypted by DES.
	 * @param iKeys: an array of 56-bit binary integers, each differing with 1 bit. 
	 * @return An array of DES objects, each with different keys. */
	private static DES[][] plaintextUnderDifferentKeys(int copies, String plaintext, String[] iKeys){
		return desArray(true, copies, plaintext, iKeys);
	}
	
	/* Encrypts either the differing the 64 differing bit plaintexts using a single key, or
	 * encrypts a single plaintext, using 56 differing bit keys. 
	 * @param isDifferentKeys: Boolean variable to determine, with we are using different keys, or different
	 * plaintexts. 
	 * @param copies: The number of different diffText, each differing by 1 bit.
	 * @param text: Will either be the key or the plaintext. 
	 * @param diffText: An array of text, all differing by 1 bit.
	 * @return An array of DES objects, each with different either different plaintexts (encrypted) or keys. */
	private static DES[][] desArray(boolean isDifferentKeys, int copies, String text, String[] diffText){
		DES[][] iDES = new DES[DES.Version.values().length][copies];
		for(int version = 0; version < DES.Version.values().length; version++){
			for(int i = 0; i < copies; i++){
				if(isDifferentKeys){
					iDES[version][i] = new DES(DES.Version.values()[version]);
					
					// 56 (56-bit) keys, all differing by 1 bit. 
					iDES[version][i].initializeCipher(DES.DESMode.ENCRYPT, diffText[i]);
					iDES[version][i].begin(text);
				}else{
					iDES[version][i] = new DES(DES.Version.values()[version]);
					iDES[version][i].initializeCipher(DES.DESMode.ENCRYPT, text);
					
					// 64 (64-bit) plaintexts, all differing by 1 bit. 
					iDES[version][i].begin(diffText[i]);
				}
			}
		}
		return iDES;
	}
	
	/* @param noOfBits: Number of bits in the text (either plaintext (64), or key (56)).
	 * @param desPK: An array of DES (With different versions).
	 * @param desDiff: An array of DES, with either 64 differing plaintexts, or 56 differing keys.
	 * @param writer: PrinterWriter object, used to write the appropriate information to the
	 * outputFilename given.  */
	private static void avalanche(int noOfBits, DES[] desPK, DES[][] desDiff, PrintWriter writer){
		int noOfRounds = desPK[0].NUMBEROFROUNDS + 1; 
		// 16 rounds, however round 0, is based against the plaintext, that hasn't gone through the rounds. 
		double[][] avgAval = new double[DES.Version.values().length][noOfRounds];
		
		// r = ROUNDS
		// v = VERSIONS
		// i = ith Differing (plaintext or key DES used to encrypt). 
		for(int r = 0; r < noOfRounds; r++){
			for(int v = 0; v < DES.Version.values().length; v++){
				for(int i = 0; i < noOfBits; i++){
					avgAval[v][r] += diffCount(desPK[v].roundText(r), desDiff[v][i].roundText(r));
				}
				avgAval[v][r] = Math.round(avgAval[v][r] / noOfBits);
			}
		}

		writeToFile(noOfRounds, avgAval, writer);
	}
	
	/* @param rounds: Number of rounds (16) DES used to encrypt the plaintext. However,
	 * an additional round (Round 0), illustrates the original plaintext used for encrypting. 
	 * @param data: Average avalanche data corresponding with all rounds of DES.
	 * @param writer: PrinterWriter object, used to write the appropriate information to the
	 * outputFilename given.  */
	private static void writeToFile(int rounds, double [][] data, PrintWriter writer){
		for(int i = 0; i < rounds; i++){
			writer.write(""+i);
			
			for(int y = 0; y < DES.Version.values().length; y++){
				writer.write("\t\t"+(int)data[y][i]);
			}
			writer.write(System.lineSeparator());
		}
	}
	
	/* Creates an array of Strings differing by 1 bit, this is dependent upon @param text (plaintext
	 * or key). 
	 * @param text: n-bit binary integer (plaintext or key). 
	 * @return: An array, with each String instance differing in 1 bit. */
	private static String[] bitDifferenceArray(String text){
		String[] bitDiff = new String[text.length()];
		for(int i=0; i < text.length(); i++){ //for each character in String text
			bitDiff[i] = text; //create a string that matches it

			//then invert a single bit at index i
			if(bitDiff[i].charAt(i)=='0'){
				bitDiff[i] = bitDiff[i].substring(0,i)+"1"+bitDiff[i].substring(i+1);
			}else{
				bitDiff[i] = bitDiff[i].substring(0,i)+"0"+bitDiff[i].substring(i+1);
			}
		}
		return bitDiff;
	}

	/* Counts the number of differing bits of both @param a and @param b (at each index in the Strings). 
	 * @param a: n-bit binary integer.
	 * @param b: n-bit binary integer. 
	 * @return The number of differing bits between both arguments. */
	private static int diffCount(String a, String b){
		int diffCount = 0;
		for(int i=0; i<a.length(); i++){
			if(i<b.length()){
				if(a.charAt(i)!=b.charAt(i)){
					diffCount++;
				}
			}
		}
		return diffCount;
	}
}
