//package cc.mallet.examples;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

import cc.mallet.fst.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.pipe.tsf.*;
import cc.mallet.types.*;
import cc.mallet.util.*;

public class GetHMMScore {

    public static void main(String[] args) throws Exception {

        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new SimpleTaggerSentence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        //pipes.add(new PrintInputAndTarget());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList seg = new InstanceList(pipe);
        seg.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(new FileInputStream("currentseg"))), Pattern.compile("^\\s*$"), true));

        ObjectInputStream s = new ObjectInputStream(new FileInputStream("hmm.model"));
        HMM hmm = (HMM) s.readObject();
        s.close();
        Pipe p = hmm.getInputPipe();

        Sequence input = (Sequence)seg.get(0).getData();
        Sequence outputs[] = apply(hmm, input, 1);
        int k = outputs.length;
        boolean error = false;
        for (int a = 0; a < k; a++) {
            if (outputs[a].size() != input.size()) {
                System.out.println("Failed to decode input sequence, answer " + a);
                error = true;
            }
        }
        if (!error) {
            for (int j = 0; j < outputs[0].size(); j++)
                {
                    HMM.State state = hmm.getState(outputs[0].get(j).toString());
                    // StringBuffer buf = new StringBuffer();
                    // for (int a = 0; a < k; a++)
                    //     buf.append(outputs[a].get(j).toString()).append(" ");
                    // buf.append(input.get(j));                
                    // System.out.println(buf.toString());
                }
        }
        System.out.println(hmm.getOutputAlphabet());
        for (int i=0; i<hmm.numStates(); i++){
            System.out.println(hmm.getState(i));
        }
        // SumLatticeDefault lattice = new SumLatticeDefault(hmm,input,true);
        // double prob = lattice.getXiWeight(0,hmm.getState(0),hmm.getState(1));
        // System.out.println(prob);
    }

    /**
       FROM SIMPLETAGGER
       * Apply a transducer to an input sequence to produce the k highest-scoring
       * output sequences.
       *
       * @param model the <code>Transducer</code>
       * @param input the input sequence
       * @param k the number of answers to return
       * @return array of the k highest-scoring output sequences
       */
    public static Sequence[] apply(Transducer model, Sequence input, int k)
    {
        Sequence[] answers;
        if (k == 1) {
            answers = new Sequence[1];
            answers[0] = model.transduce (input);
        }
        else {
            MaxLatticeDefault lattice =
                new MaxLatticeDefault (model, input, null, 100000);

            answers = lattice.bestOutputSequences(k).toArray(new Sequence[0]);
        }
        return answers;
    }
    
}
