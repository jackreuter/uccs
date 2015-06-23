import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class BDSplitter {

    public static void main(String[] args) {

        try {

            String file = args[0];
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = br.readLine();
            while (line != null) {
		//process line
		if (line.length()<10) {}
		else {
		    String word = line.substring(0,line.indexOf(" "));
		    System.out.println(word);
		}

		
                line = br.readLine();
            }

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }
}
