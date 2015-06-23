import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;

public class CompareFiles {

    //first arg correct file
    //second arg test file
    public static void main(String[] args) {

        try {

            String file1 = args[0];
            String file2 = args[1];
            BufferedReader br1 = new BufferedReader(new FileReader(file1));
            BufferedReader br2 = new BufferedReader(new FileReader(file2));
            String line1 = br1.readLine();
            String line2 = br2.readLine();
	    
            double total = 0;
            double precision = 0;
            double recall = 0;

            while (line1 != null && line2 != null) {
                //process lines
	    
                if (line2.equals("$X$X$X$")) {}
                else {
                    System.out.println(line1+" : "+line2);
                    precision += getPrecision(toBin(line1),toBin(line2));
                    recall += getRecall(toBin(line1),toBin(line2));
                    total++;
                }
                line1 = br1.readLine();
                line2 = br2.readLine();		
            }

            precision = precision/total*100;
            recall = recall/total*100;
            System.out.println("Precision: "+precision);
            System.out.println("Recall: "+recall);

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    //bin rep of seg
    public static String toBin(String s) {
        String bin = "";
        int i=0;
        while (i<s.length()-1) {
            if (s.charAt(i+1)==' ') {bin+="1"; i+=2;}
            else {bin+="0"; i++;}
        }
        return bin;
    }

    //a is answer
    //p is proposed
    //returns number of correct strings over number in proposed
    public static double getPrecision(String a, String p) {
        int count = countMatches(a, '1');
        return numberCorrect(a, p)/(count+1);
    }

    //returns #correct over number in answer
    public static double getRecall(String a, String p) {
        int count = countMatches(p, '1');
        return numberCorrect(a, p)/(count+1);
    }

    //count occ of char in a string
    public static int countMatches(String s, char c) {
        int count = 0;
        for (int i=0; i<s.length(); i++) {
            if (s.charAt(i)==c) {count++;}
        }
        return count;
    }
    
    public static String chop(String s) {
        int i=s.indexOf("1");
        if (i==-1) {return "";}
        else {return s.substring(i+1);}
    }

    //gets number of correct words segmented
    public static int numberCorrect(String a, String p) {
        int alen=a.length();
        int plen=p.length();
        if (alen==0 || plen==0) {return 0;}
        else if (alen>plen) {return numberCorrect(chop(a),p);}
        else if (alen<plen) {return numberCorrect(a,chop(p));}
        else {
	
            int aindex=a.indexOf("1");
            int pindex=p.indexOf("1");

            if (aindex==-1 && pindex==-1) {return 1;}
            else if (aindex==-1 && pindex!=-1) {return 0;}
            else if (aindex!=-1 && pindex==-1) {return 0;}
            else if (aindex==pindex) {return 1+numberCorrect(chop(a), chop(p));}
            else {return numberCorrect(chop(a),chop(p));}
        }
    }
}
