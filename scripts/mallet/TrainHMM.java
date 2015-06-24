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

public class TrainHMM {

    public static void main(String[] args) throws Exception {

        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new SimpleTaggerSentence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        //pipes.add(new PrintInputAndTarget());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList trainingInstances = fileToInstanceList(args[0], pipe);
        InstanceList testingInstances = fileToInstanceList(args[1], pipe);
        HMM hmm = TrainHMM(trainingInstances, testingInstances, pipe);

        System.out.println("\nTagged test sequences:");
        for (int i = 0; i < testingInstances.size(); i++)
            {
                Sequence input = (Sequence)testingInstances.get(i).getData();
                Sequence outputs[] = apply(hmm, input, 1);
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

    public static HMM TrainHMM(InstanceList trainingInstances, InstanceList testingInstances, Pipe pipe) throws IOException {
		
        HMM hmm = new HMM(pipe, null);

        hmm.addStatesForLabelsConnectedAsIn(trainingInstances);
        //hmm.addStatesForBiLabelsConnectedAsIn(trainingInstances);

        HMMTrainerByLikelihood trainer = 
            new HMMTrainerByLikelihood(hmm);
        TransducerEvaluator trainingEvaluator = 
            new PerClassAccuracyEvaluator(trainingInstances, "training");
        TransducerEvaluator testingEvaluator = 
            new PerClassAccuracyEvaluator(testingInstances, "testing");

        trainer.train(trainingInstances, 10);
        trainingEvaluator.evaluate(trainer);
        testingEvaluator.evaluate(trainer);

        return hmm;
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
