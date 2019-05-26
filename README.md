This project was created as a university group assignment (done in pairs).
It fully implements DES encryption from scratch (as per the assignment specifications). We recieved full marks.

COMPILATION:
javac Application.java

EXECUTION:
java Application "inputfile.txt" "outputfile.txt"

Example: 
java Application myInputFile.txt myOutputFile.txt

Input file must be of the following format:
mode
plaintext
key

Where mode is a single bit with 1 denoting encryption and 0 for decryption, plaintext is a 64 bit string, and key is a 56 bit string.
Example:
1
0000000000000000000000000000000000000000000000000000000000000000
11111111111111111111111111111111111111111111111111111111


The program has been successfully compiled and run with java version "10.0.1" 2018-04-17 and with java version "1.9.0_171"


CLASS DETAILS:
Application: This class contains the main() method. It reads the input file, writes the output, and calculates the avalanche effect.

DES.java: This class accepts the plaintext and key and produces ciphertext (can also decrypt). It also stores the result of each round and can encrypt using any of the 4 DES implementation in the specs (DES0/1/2/3).

KeyGenerator.java: This class handles the key generation. It accepts a 56 bit key, pads every eight parity bit, then applies the key generation algorithm to create the subkeys which it stores. It can also generate them in reverse order for decryption.

Round.java: This class handles each individual round of the DES algorithm. It can run any of the 4 DES implementations specified in the assignment. It takes the left and right halves of the text along with the correct subkey and returns the generated text.

Bitwise.java: This class simply contains a static method to xor two binary strings. It is used throughout the program extensively.

Transposition.java: This class applies a permutation to a string based on an integer array. It is used by KeyGenerator, DES and Round classes.
