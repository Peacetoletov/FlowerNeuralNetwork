/**
 * Created by lukas on 20.7.2017.
 */

import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args){

        //Read data
        FileDataReader rff = new FileDataReader();
        ArrayList<Double[]> blueList = rff.read("blueFlower.txt");
        ArrayList<Double[]> redList = rff.read("redFlower.txt");

        //Create the body of the NN
        Body body = new Body(3, 3);
        body.createNeurons(0, 2);
        body.createNeurons(1, 3);
        body.createNeurons(2, 1);
        body.createSynapses();

        //Pass the training data to the body
        for (Double[] x: blueList){
            body.initializeTrainingData(x, 0.0);
        }
        for (Double[] y: redList){
            body.initializeTrainingData(y, 1.0);
        }

        //Train
        body.train();


        body.guessColor(16, 1.5);
        body.guessColor(7, 2);
        body.guessColor(8, 1.6);


        body.guessColor(10, 2);
    }
}
