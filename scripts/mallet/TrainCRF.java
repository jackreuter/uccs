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

public class TrainCRF {

    public static void main (String[] args) throws Exception {

        // ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        // int[][] conjunctions = new int[2][];
        // conjunctions[0] = new int[] { -1 };
        // conjunctions[1] = new int[] { 1 };
        // pipes.add(new SimpleTaggerSentence2TokenSequence());
        // pipes.add(new OffsetConjunctions(conjunctions));
        // //pipes.add(new FeaturesInWindow("PREV-", -1, 1));
        // pipes.add(new TokenTextCharSuffix("C1=", 1));
        // pipes.add(new TokenTextCharSuffix("C2=", 2));
        // pipes.add(new TokenTextCharSuffix("C3=", 3));
        // pipes.add(new RegexMatches("CAPITALIZED", Pattern.compile("^\\p{Lu}.*")));
        // pipes.add(new RegexMatches("STARTSNUMBER", Pattern.compile("^[0-9].*")));
        // pipes.add(new RegexMatches("HYPHENATED", Pattern.compile(".*\\-.*")));
        // pipes.add(new RegexMatches("DOLLARSIGN", Pattern.compile(".*\\$.*")));
        // pipes.add(new TokenFirstPosition("FIRSTTOKEN"));
        // pipes.add(new TokenSequence2FeatureVectorSequence());
        // pipes.add(new PrintInputAndTarget());
        // Pipe pipe = new SerialPipes(pipes);

        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new SimpleTaggerSentence2TokenSequence());
        pipes.add(new TokenSequence2FeatureVectorSequence());
        //pipes.add(new PrintInputAndTarget());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList trainingInstances = fileToInstanceList(args[0], pipe);
        InstanceList testingInstances = fileToInstanceList(args[1], pipe);
        CRF crf = TrainCRF(trainingInstances, testingInstances, pipe);

        System.out.println("\nTagged test sequences:");
        for (int i = 0; i < testingInstances.size(); i++)
            {
                Sequence input = (Sequence)testingInstances.get(i).getData();
                Sequence outputs[] = apply(crf, input, 1);
                int k = outputs.length;
                boolean error = false;
                for (int a = 0; a < k; a++) {
                    if (outputs[a].size() != input.size()) {
                        System.out.println("Failed to decode input sequence " + i + ", answer " + a);
                        error = true;
                    }
                }
                if (!error) {
                    for (int j = 0; j < input.size(); j++)
                        {
                            StringBuffer buf = new StringBuffer();
                            for (int a = 0; a < k; a++)
                                buf.append(outputs[a].get(j).toString()).append(" ");
                            buf.append(input.get(j));                
                            System.out.println(buf.toString());
                        }
                    System.out.println();
                }
            }
    }
	
    public static InstanceList fileToInstanceList(String filename, Pipe pipe) throws IOException {

        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(new FileInputStream(filename))), Pattern.compile("^\\s*$"), true));

        return instances;
    }

    public static CRF TrainCRF(InstanceList trainingInstances, InstanceList testingInstances, Pipe pipe) throws IOException {
		
        CRF crf = new CRF(pipe, null);
        //crf.addStatesForLabelsConnectedAsIn(trainingInstances);
        crf.addStatesForThreeQuarterLabelsConnectedAsIn(trainingInstances);
        crf.addStartState();

        CRFTrainerByLabelLikelihood trainer = 
            new CRFTrainerByLabelLikelihood(crf);
        trainer.setGaussianPriorVariance(10.0);

        //CRFTrainerByStochasticGradient trainer = 
        //new CRFTrainerByStochasticGradient(crf, 1.0);

        //CRFTrainerByL1LabelLikelihood trainer = 
        //	new CRFTrainerByL1LabelLikelihood(crf, 0.75);

        //trainer.addEvaluator(new PerClassAccuracyEvaluator(trainingInstances, "training"));
        trainer.addEvaluator(new PerClassAccuracyEvaluator(testingInstances, "testing"));
        trainer.addEvaluator(new TokenAccuracyEvaluator(testingInstances, "testing"));
        trainer.train(trainingInstances);

        return crf;
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
