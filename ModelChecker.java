
/*
 * @name    Sander van der Leek (1564226), Linda Geraets (1565834), Kenna Janssens (1577271)
 * @date    13-12-2024
 */

import java.io.*;
import java.util.*;

public class ModelChecker {

    /**
     * Read the problem input from a file.
     * @throws IOException
     */
    void readInput(String filename) throws IOException {

        File file = new File(filename);
        Scanner scanner = new Scanner(file);
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            
        }

        scanner.close();
    }

    public static void main(String[] args) throws Exception {
        ModelChecker modelChecker = new ModelChecker();

        modelChecker.readInput("test.txt");
    }
}
