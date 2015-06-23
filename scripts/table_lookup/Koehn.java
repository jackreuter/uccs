import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

//first arg list to be segmented
public class Koehn {

    private static final int INTFREQ=1000;
    private static final int UNKNOWNFREQ=1000;
    
    public static void main(String[] args) {

        try {
            String file = args[0];
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            int count = 0;

            while (line != null) {
                //process lines
                System.out.println(chooseBest(getAllSegs(line)));
                line = br.readLine();
                count++;
            }

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    //segment by capitals
    public static String segmentByCaps(String s) {
        String output = "";
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {output += " "+c;}
            else {output += c;}
        }
        return chomp(output);
    }

    // //gets frequency score as described by Koehn for a segmentation
    // public static double getFreqScore(String s) {
    // 	String[] split = s.split(" ");
    // 	double score = 1;
    // 	int len = split.length;
    // 	for (int i=0; i<len; i++) {
    // 	    score = score * Math.pow(getFreq(split[i]),(double)1/len);
    // 	}
    // 	return score;
    // }
    
    //chooses best based on Koehn paper
    public static String chooseBest(Object[] sf) {
        ArrayList<String> s = (ArrayList<String>) sf[0];
        ArrayList<Integer> f = (ArrayList<Integer>) sf[1];

        double highScore = 0;
        int index = 0;
        for (int i=0; i<s.size(); i++) {
            double freqScore = Math.pow(f.get(i), (double)1/s.get(i).length());
            if (freqScore>highScore) {
                highScore = freqScore;
                index = i;
            }
        }
        return s.get(index);
    }

    //recursively finds all poss segs with at least one matched word
    //also gets freq of word from dict
    public static Object[] getAllSegs(String s) {

        ArrayList<String> segs = new ArrayList<String>();
        ArrayList<Integer> freqs = new ArrayList<Integer>();
        if (s.length()==0) {return new Object[] {segs, freqs};}
        else {
            segs.add(s);
            freqs.add(UNKNOWNFREQ);
	    
            int len = s.length();
            boolean unknown = true;

            for (int i=0; i<len && unknown; i++) {
                String front = s.substring(0, len-i);
                String back = s.substring(len-i);

                Object[] frontMF = hasMatchWithFreq(front);
                Object[] backMF = hasMatchWithFreq(back);
                boolean frontIs = (boolean) frontMF[0];
                boolean backIs = (boolean) backMF[0];

                if (frontIs) {
                    Object[] backARR = getAllSegs(back);
                    ArrayList<String> backSegs = (ArrayList<String>) backARR[0];
                    ArrayList<Integer> backFreqs = (ArrayList<Integer>) backARR[1];
                    for (int j=0; j<backSegs.size(); j++) {
                        String seg = front+" "+backSegs.get(j);
                        int freq = (int)frontMF[1]*backFreqs.get(j);
                        segs.add(seg);
                        freqs.add(freq);
                    }
                }
                if (backIs) {
                    Object[] frontARR = getAllSegs(front);
                    ArrayList<String> frontSegs = (ArrayList<String>) frontARR[0];
                    ArrayList<Integer> frontFreqs = (ArrayList<Integer>) frontARR[1];		    
                    for (int j=0; j<frontSegs.size(); j++) {
                        String seg = frontSegs.get(j)+" "+back;
                        int freq = frontFreqs.get(j)*(int)backMF[1];
                        segs.add(seg);
                        freqs.add(freq);
                    }
                }
            }
            return new Object[] {segs, freqs};
        }
    }

    //table lookup
    //total dict entries should be of the form
    //<length> \t <word> \t <freq>
    public static Object[] hasMatchWithFreq(String s) {
        if (s.length()==0) {return new Object[] {false, 1};}
        else if (isInteger(s)) {return new Object[] {true, INTFREQ};}
        //else if (s.length()==1) {return true;}
        else {
            int len = s.length();
            boolean has = false;
            int freq = UNKNOWNFREQ;

            try {
                String file = "../dicts/total/try4/total.txt";
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                int count = 0;
                boolean done = false;
                while (line != null && !done) {
                    //process lines
                    String[] split = line.split("\t");
                    if (s.length()>Integer.parseInt(split[0])) {
                        done = true;
                        has = false;
                    } else if (s.toLowerCase().equals(split[1].toLowerCase())) {
                        done = true;
                        has = true;
                        freq = Integer.parseInt(split[2]);
                    }
                    line = br.readLine();
                    count++;
                }
            } catch (FileNotFoundException ex) {
                has = false;
            } catch (IOException ex) {
                has = false;
            }
	    
            return new Object[] {has, freq};
        }
    }    

    //HELPER FUNCTIONS//
    //------------------------------------------------------------------------------//

    //checks if string is integer
    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    //removes " " from front and back of string if exists
    public static String chomp(String s) {
        int l = s.length();
        if (l>1 && s.charAt(0)==' ') {s = s.substring(1); l = l-1;}
        if (l>1 && s.charAt(l-1)==' ') {s = s.substring(0,l-1);}
        if (s.equals(" ")) {s = "";}
        return s;
    }
    
    //converts segmentation to bin representation
    public static String toBin(String s) {
        String bin = "";
        int i=0;
        while (i<s.length()-1) {
            if (s.charAt(i+1)==' ') {bin+="1"; i+=2;}
            else {bin+="0"; i++;}
        }
        return bin;
    }

    //gets avg word length in segmentation
    public static double averageWordLength(String a) {
        String[] words = a.split(" ");
        double sum = 0.0;
        for (int i=0; i<words.length; i++) {
            sum += words[i].length();
        }

        return sum / words.length;
    }
}
