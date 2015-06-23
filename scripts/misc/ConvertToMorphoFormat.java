import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.util.Random;

public class ConvertToMorphoFormat {

    public static void main(String[] args) {

        try {

            String file = args[0];
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
	    int count = 0;
	    Random rand = new Random();
	    int randomNum = 0;

	    while (line != null) {
		//process lines
		String formatted = "";
		
		String[] words = line.split(" ");
		for (int i=0; i<words.length; i++) {
		    formatted += words[i];
		}
		formatted += '\t';
		for (int i=0; i<words.length; i++) {
		    formatted += words[i]+" ";
		}
		System.out.println(chomp(formatted));
		    
		line = br.readLine();
		count++;
            }

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    //removes " " from front and back of string if exists
    public static String chomp(String s) {
	int l = s.length();
	if (l>1 && s.charAt(0)==' ') {s = s.substring(1); l = l-1;}
	if (l>1 && s.charAt(l-1)==' ') {s = s.substring(0,l-1);}
	if (s.equals(" ")) {s = "";}
	return s;
    }

}
