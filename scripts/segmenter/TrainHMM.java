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

        FileOutputStream fos = new FileOutputStream("hmm.model");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(hmm);
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
    
}
