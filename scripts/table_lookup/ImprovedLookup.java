import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;

//first arg list to be segmented
public class ImprovedLookup {

    public static final int FULLDICT = 5;
    public static final int SAFEDICT = 4;

    public static void main(String[] args) {

        try {
            String raw = args[0];
            BufferedReader br = new BufferedReader(new FileReader(raw));
            String line = br.readLine();
            
            while (line!=null) {
                //process lines
                HashSet<String> segs = getAllSegs(line, false);
                if (segs.size()==0) {System.out.println(line);}
                //System.out.println(getSegWithLongestWords(segs));
                line = br.readLine();                
            }

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }

    }

    //name says it all
    public static String getSegWithLongestWords(HashSet<String> segs) {
        Iterator<String> itr = segs.iterator();
        String longestOverall = "";
        while(itr.hasNext()) {
            String s = itr.next();
            if (greaterThan(s,longestOverall)) {
                longestOverall=s;
            }
        }
        return longestOverall;
    }

    //true if a has longer words than b
    public static boolean greaterThan(String a, String b) {
        if (a=="" && b=="") {return false;}
        if (a=="") {return false;}
        if (b=="") {return true;}
        String[] as = a.split(" ");
        String[] bs = b.split(" ");
        int alen = as.length;
        int blen = bs.length;    
        int[] als = new int[alen];
        int[] bls = new int[blen];
        for (int i=0; i<alen; i++) {als[i]=as[i].length();}
        for (int i=0; i<blen; i++) {bls[i]=bs[i].length();}
        Arrays.sort(als);
        Arrays.sort(bls);
        reverse(als);
        reverse(bls);
        boolean is=false;
        boolean done=false;
        for (int i=0; i<alen && i<blen && !done; i++) {
            //System.out.println(als[i]+" "+bls[i]+" || "+a+" : "+b);
            if (als[i]>bls[i]) {is=true; done=true;}
            if (als[i]<bls[i]) {is=false; done=true;}
        }
        return is;
    }

    //reverse an array
    public static void reverse(int[] arr) {
        int len = arr.length;
        for (int i=0; i<len/2; i++) {
            int t = arr[i];
            arr[i] = arr[len-i-1];
            arr[len-i-1] = t;
        }
    }
    
    //get all possible segs of string s
    //first input is string
    //second is whether or not it allows for unknown words
    public static HashSet<String> getAllSegs(String s, boolean allowUnknowns) {
        int len = s.length();
        //handle strings that are too long for full analysis
        if (len>25) {
            boolean found = false;
            String beg="";
            String mid="";
            String end="";
            for (int i=len; i>=1 && !found; i--) {
                for (int j=0; i+j<=len && !found; j++) {
                    beg = s.substring(0,j);
                    mid = s.substring(j,j+i);
                    end = s.substring(j+i,len);
                    if (hasMatch(mid,SAFEDICT)) {
                        System.out.println(mid);
                        found = true;
                    }
                }
            }
            HashSet<String> begSegs = getAllSegs(beg, allowUnknowns);
            HashSet<String> endSegs = getAllSegs(end, allowUnknowns);
            HashSet<String> segs = new HashSet<String>();
            segs.addAll(concat(begSegs,mid,endSegs,beg.length(),end.length()));
            return segs;
        } else {
            return getAllSegsDyn(s, new int[len+1][len+1], 0, len, allowUnknowns);
        }
    }
    
    public static HashSet<String> getAllSegsDyn(String s, int[][] matches, int start, int len, boolean au) {
        //System.out.println("\n"+s.substring(start, start+len));
        HashSet<String> segs = new HashSet<String>();
        if (len==0) {return segs;}
        else {
            //i is length of mid
            //j is index of mid in ref to whole string
            boolean done = false;
            for (int i=len; i>=1 && !done; i--) {
                for (int j=start; i+j<=start+len && !done; j++) {
                    //String beg = s.substring(start,j);
                    //String end = s.substring(j+i,start+len);
                    String mid = s.substring(j,j+i);
                    //System.out.println(beg+","+mid+","+end);
                    
                    if (matches[i-1][j]==1) {
                        HashSet<String> begSegs = getAllSegsDyn(s, matches, start, j-start, au);
                        HashSet<String> endSegs = getAllSegsDyn(s, matches, j+i, len-(j-start+i), au);
                        segs.addAll(concat(begSegs,mid,endSegs,j-start,len-(j-start+i)));
                        //System.out.println(segs);
                        //System.out.println(mid+"\t: savings baby");
                        
                    } else if (matches[i-1][j]==0) {
                        if (hasMatch(mid, FULLDICT)) {
                            HashSet<String> begSegs = getAllSegsDyn(s, matches, start, j-start, au);
                            HashSet<String> endSegs = getAllSegsDyn(s, matches, j+i, len-(j-start+i), au);
                            segs.addAll(concat(begSegs,mid,endSegs,j-start,len-(j-start+i)));
                            matches[i-1][j]=1;
                            //System.out.println(segs);
                            //System.out.println(mid+"\t: lookup");
                        } else {
                            matches[i-1][j]=-1;
                            if (au && i==len) {segs.add(s.substring(start,start+len));}
                        }
                    } else if (au && i==len) {
                        segs.add(s.substring(start, start+len));
                    }
                }
            }
        }
        return segs;
    }

    //schwoop together two arraylists with string in the middle
    public static HashSet<String> concat(HashSet<String> begs, String mid, HashSet<String> ends, int bLen, int eLen) {
        HashSet<String> c = new HashSet<String>();
        Iterator<String> bitr;
        Iterator<String> eitr;        
        int bs = begs.size();
        int es = ends.size();
        if (bs==0 && es==0) {
            if (bLen==0 && eLen==0) {c.add(mid);}
        } else if (bs==0) {
            if (bLen==0) {
                eitr = ends.iterator();
                while(eitr.hasNext()) {
                    c.add(mid+" "+eitr.next());
                }
            }
        } else if (es==0) {
            if (eLen==0) {
                bitr = begs.iterator();
                while(bitr.hasNext()) {
                    c.add(bitr.next()+" "+mid);
                }
            }
        } else {
            bitr = begs.iterator();
            eitr = ends.iterator();            
            while(bitr.hasNext()) {
                String beg = bitr.next();
                while(eitr.hasNext()) {
                    String end = eitr.next();
                    c.add(beg+" "+mid+" "+end);
                }
            }
        }
        return c;
    }
    
    //table lookup
    //total dict entries should be of the form
    //<length> \t <word> \t <freq>
    public static boolean hasMatch(String s, int dict) {
        if (s.length()==0) {return false;}
        if (isInteger(s)) {return true;}
        //else if (s.length()==1) {return true;}
        else {
            int len = s.length();
            boolean has = false;

            try {
                String file = "../dicts/total/try"+dict+"/total.txt";
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
                    }
                    line = br.readLine();
                    count++;
                }
            } catch (FileNotFoundException ex) {
                has = false;
            } catch (IOException ex) {
                has = false;
            }
	
            return has;
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
}
