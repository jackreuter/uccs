import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.util.Random;

public class CompileTotalDict {

    public static void main(String[] args) {

	int numberOfDicts = Integer.parseInt(args[0]);

	for (int i=1; i<=numberOfDicts; i++) {

	    try {
	    
		String file = args[i];
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		int count = 0;
		Random rand = new Random();
		int randomNum = 0;

		while (line != null) {
		    //process lines

		    System.out.println(line.toLowerCase());
		    
		    line = br.readLine();
		    count++;
		}

	    } catch (FileNotFoundException ex) {
	    } catch (IOException ex) {
	    }
	}
    }
}
