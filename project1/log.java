import java.lang.*;
import java.io.*;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class log {
    public static void main(String[] args) {
        // usage input validation
        if (args.length != 1) {
            System.out.println("Usage: java log <log_file>");
            System.exit(1);
        }    
        // set filename
        String filename = args[0];

        // create scanner to read user input
        String input;
        Scanner scanner = new Scanner(System.in);

        try {
            // create file if it doesn't exist
            if (!new File(filename).isFile()) {
                // create file
                new File(filename).createNewFile();
            }

            // open writer
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));

            // continue until quit\
            // used a do while loop for fun
            do {
                // read input
                input = scanner.nextLine();
                String[] splitInput = input.split(" ");
                // exit condition
                if (splitInput[0].equals("QUIT")) {
                    break;
                }
                // write to file
                writer.append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now()) + " ");
                writer.append("[" + splitInput[0] + "]");
                for (int i = 1; i < splitInput.length; i++) {
                    writer.append(" " + splitInput[i]);
                }
                writer.newLine();
            } while (true);

            // close writer
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}