import java.lang.*;
import java.util.Scanner;
import java.util.ArrayList;

public class encrypt {

    // vigenere cipher encryption
    // input and output are both in upper case
    public static String encrypt(String input, String passkey) {
        String result = "";
        for (int index = 0, j = 0; index < input.length(); index++) {
            char letter = input.charAt(index);
            if (letter == ' ') {
                result += " ";
            } else {
                result += (char) (((letter - 65) + (passkey.charAt(j) - 65)) % 26 + 65);
                j = ++j % passkey.length();
            }
        }
        return result;
    }

    // decryption
    // input and output are both in upper case
    public static String decrypt(String input, String passkey) {
        String result = "";
        for (int index = 0, j = 0; index < input.length(); index++) {
            char letter = input.charAt(index);
            if (letter == ' ') {
                result += " ";
            } else {
                result += (char) ((letter - passkey.charAt(j) + 26) % 26 + 65);
                j = ++j % passkey.length();
            }   
        }
        return result;
    }

    public static void main(String[] args) {
        // usage input validation
        if (args.length != 0) {
            System.out.println("Usage: java encrypt");
            System.exit(1);
        }
        // create scanner to read user input
        String input, passkey = null;
        ArrayList<String> history = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);

        // continue until quit
        // used a do while loop for fun
        do {
            // read input
            input = scanner.nextLine();
            // clean input
            input = input.toUpperCase(); // set to upper case
            String[] splitInput = input.split(" ");
            String finalInput = ""; // santized, final input
            for (int i = 1; i < splitInput.length; i++) {
                finalInput += splitInput[i] + " ";
            }
            finalInput = finalInput.trim();
            
            // exit condition
            switch (splitInput[0]) {
                // passkey can only be 1 word long
                case "PASSKEY":
                    // set password
                    passkey = splitInput[1];
                    passkey = passkey.toUpperCase(); // set to upper case
                    System.out.println("RESULT");
                    break;
                case "ENCRYPT":
                    // encrypt
                    if (passkey == null) {
                        System.out.println("ERROR Password not set");
                    } else {
                        System.out.println("RESULT " + encrypt(finalInput, passkey));
                    }
                    break;
                case "DECRYPT":
                    // decrypt
                    if (passkey == null) {
                        System.out.println("ERROR Password not set");
                    } else {
                        System.out.println("RESULT " + decrypt(finalInput, passkey));
                    }
                    break;
                case "QUIT":
                    // quit
                    System.exit(1);
                    break;
            }
        } while (true);
        
    }
}