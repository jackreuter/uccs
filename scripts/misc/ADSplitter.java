// split dict into two files, words and defs
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ADSplitter {

    public static void main(String[] args) {

        try {

            String file = args[0];
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = br.readLine();
            int count = 0;
            while (line != null) {
                String[] split = line.split("\t");
                for (int i=0; i<split.length; i++){
                    System.out.println(split[i]);
                }
                line = br.readLine();
            }

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }
}
