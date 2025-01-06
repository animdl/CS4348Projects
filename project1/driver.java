import java.lang.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class driver {

    private static void printMenu() {
        System.out.println("\n--------------------------");
        System.out.println("\tMenu:");
        System.out.println("--------------------------");
        System.out.println("PASSWORD - Set the passkey for encryption/decryption");
        System.out.println("ENCRYPT - Encrypt a string");
        System.out.println("DECRYPT - Decrypt a string");
        System.out.println("HISTORY - Show history");
        System.out.println("QUIT \t- Quit program");
        System.out.println("\n--------------------------");
        System.out.print("Enter Command: ");
    }

    private static void printHistory(ArrayList<String> history, boolean flag) {
        System.out.println("\n--------------------------");
        System.out.println("\tHistory:");
        System.out.println("--------------------------");
        for (int i = 0; i < history.size(); i++) {
            System.out.println(i + ". " + history.get(i));
        }
        if (flag) {
            System.out.println(history.size() + ". " + "Go back");
        }
        System.out.println("\n--------------------------");
    }

    public static void main(String[] args) {
        // usage input validation
        if (args.length != 1) {
            System.out.println("Usage: java driver <filename>");
            System.exit(1);
        } 

        ArrayList<String> history = new ArrayList<String>();
        // create scanner to read user input
        Scanner scanner = new Scanner(System.in);

        try {
            // create encrypt process
            Process encrypt = Runtime.getRuntime().exec("java encrypt");
            // received from encrypt process 
            InputStream inStreamEncrypt = encrypt.getInputStream();
            Scanner fromEncrypt = new Scanner(inStreamEncrypt);
            // sent to encrypt process
            OutputStream outStreamEncrypt = encrypt.getOutputStream();
            PrintStream toEncrypt = new PrintStream(outStreamEncrypt);

            // create log process
            Process log = Runtime.getRuntime().exec("java log " + args[0]);
            // sent to log process
            OutputStream outStreamLog = log.getOutputStream();
            PrintStream toLog = new PrintStream(outStreamLog);

            // begin logging
            toLog.println("START Logging Started");
            toLog.flush();

            // while loop to read user commands
            while (true) {
                String command, input = null, formattedResult;
                String[] splitResult;
                boolean flag;
                printMenu();

                // read user input
                command = scanner.nextLine();
                // clean input
                command = command.toUpperCase(); // set to upper case
                command = command.trim();

                // input validation
                switch (command) {
                    case "PASSWORD":
                        /* DETERMINE ARGUMENT INPUT STRING */
                        flag = false; // reset flag
                        if (!history.isEmpty()) {
                            System.out.print("Would you like to use the history? (Y/N): ");
                            char choice = scanner.nextLine().toUpperCase().charAt(0);

                            // input validation
                            if (choice == 'Y') {
                                printHistory(history, true);
                                System.out.print("Select Index of String to use as " + command + ": ");
                                // set input string
                                int index = scanner.nextInt();

                                // check if index is valid
                                if (index == history.size()) {
                                    flag = true;
                                } else if (index < 0 || index > history.size()) {
                                    System.out.println("Invalid Index");
                                    flag = true;
                                } else {
                                    input = history.get(index);
                                }

                            } else if (choice == 'N') {
                                flag = true;

                            } else {
                                System.out.println("Invalid Input");
                                flag = true;
                            }
                        } 
                        // if history is not used
                        if (flag || history.isEmpty()) {
                            // set password
                            System.out.print("Enter " + command + ": ");
                            input = scanner.nextLine();
                        }

                        /* SEND ARGUMENT TO PROCESS AS INPUT AND RECEIVE OUTPUT */
                        // send to encrypt process
                        toEncrypt.println("PASSKEY " + input);
                        toEncrypt.flush();
                        // send to log process
                        toLog.println("SET_PASSWORD Setting passkey");
                        // receive from encrypt process
                        if (fromEncrypt.nextLine().equals("RESULT")) {
                            // success
                            toLog.println("SET_PASSWORD Success");
                        }
                        toLog.flush();
                        break;

                    case "ENCRYPT":
                        // same as decrypt
                    case "DECRYPT":
                        /* DETERMINE ARGUMENT INPUT STRING */
                        // check if history is empty
                        flag = false; // reset flag
                        if (!history.isEmpty()) {
                            System.out.print("Would you like to use the history? (Y/N): ");
                            char choice = scanner.nextLine().toUpperCase().charAt(0);

                            // input validation
                            if (choice == 'Y') {
                                printHistory(history, true);
                                System.out.print("Select Index of String to " + command + ": ");
                                // set input string
                                int index = scanner.nextInt();

                                // check if index is valid
                                if (index == history.size()) {
                                    flag = true;
                                } else if (index < 0 || index > history.size()) {
                                    System.out.println("Invalid Index");
                                    flag = true;
                                } else {
                                    input = history.get(index);
                                }

                            } else if (choice == 'N') {
                                flag = true;
                            } else {
                                System.out.println("Invalid Choice");
                                flag = true;
                            }

                        } 
                        // if history is not used
                        if (flag || history.isEmpty()) {
                            System.out.print("Enter String to " + command + ": ");
                            input = scanner.nextLine();
                            // add input to history
                            history.add(input.toUpperCase());
                        }

                        /* SEND ARGUMENT TO PROCESS AS INPUT AND RECEIVE OUTPUT */
                        // send to encrypt process
                        toEncrypt.println(command + " " + input);
                        toEncrypt.flush();
                        // send to log process
                        toLog.println(command + " " + input);

                        // receive from encrypt process
                        splitResult = null;
                        splitResult = fromEncrypt.nextLine().split(" ");

                        /* TAKE THE RETURNED OUTPUT FROM THE PROCESS: FORMAT, LOG, AND OUTPUT IT */
                        // format result
                        formattedResult = "";
                        for (int i = 1; i < splitResult.length; i++) {
                            formattedResult += splitResult[i] + " ";
                        }
                        formattedResult = formattedResult.trim();

                        // handle result
                        if (splitResult[0].equals("RESULT")) {
                            // success
                            toLog.println(command + " Success: " + formattedResult);
                            System.out.println("\nResult: " + formattedResult);
                            // add result to history
                            history.add(formattedResult);
                        } else {
                            // error
                            toLog.println(command + " Error: " + formattedResult);
                            System.out.println("\nError: " + formattedResult);
                        }
                        toLog.flush();
                        break;

                    case "HISTORY":
                        if (history.isEmpty()) {
                            System.out.println("History is empty");
                        } else {
                            printHistory(history, false);
                        }

                        // send to log process
                        toLog.println("HISTORY History Checked");
                        toLog.flush();
                        break;

                    case "QUIT":
                        // stop encrypt process
                        toEncrypt.println("QUIT");
                        toEncrypt.flush();
                        // stop log process
                        toLog.println("STOPPED Logging Stopped");
                        toLog.println("QUIT");
                        toLog.flush();
                        
                        // end processes
                        encrypt.waitFor();
                        log.waitFor();
                        return;
                    default:
                        System.out.println("Invalid Command: " + command);
                        break;
                }

                // pause loop
                System.out.print("\nPress Enter to continue...");
                new Scanner(System.in).nextLine();
            }

        } catch (IOException ex) {
            System.out.println("Unable to run encrypt or log");
        } catch (InterruptedException ex) {
            System.out.println("Unexpected Termination");
        }
    }
}