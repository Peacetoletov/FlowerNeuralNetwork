/**
 * Created by lukas on 21.7.2017.
 */

import java.util.ArrayList;

public class Body {
    private Neuron[][] neuronArray;     //neuronArray[layer][position]
    private Synapse[][][] synapseArray;     //synapseArray[firstLayer][firstPosition][secondPosition]
    private int neuronsInLayer[];
    private ArrayList<Double[]> trainingData = new ArrayList<>();      //individual array: array[height, width, target]
    WeightSaver ws = new WeightSaver();
    FileDataReader fdr = new FileDataReader();

    Body(int layersAmount, int biggestLayerNeuronsAmount){
        neuronArray = new Neuron[layersAmount][biggestLayerNeuronsAmount];
        synapseArray = new Synapse[layersAmount-1][biggestLayerNeuronsAmount][biggestLayerNeuronsAmount];
        neuronsInLayer = new int[layersAmount];
    }

    public void createNeurons(int layer, int neuronsAmount){
        for(int i = 0; i < neuronsAmount; i++){
            neuronArray[layer][i] = new Neuron();
        }
        neuronsInLayer[layer] = neuronsAmount;
    }

    public void createSynapses(){   //must be called AFTER all neurons are created
        int layersAmount = neuronsInLayer.length;
        ArrayList<Double[]> weightList = fdr.read("weights.txt");
        for (int layer = 0; layer < layersAmount-1; layer++) {   //loop through each neuron layer except the output layer
            for (int firstNeuronPos = 0; firstNeuronPos < neuronsInLayer[layer]; firstNeuronPos++) {      //loop through each neuron in the layer
                for (int secondNeuronPos = 0; secondNeuronPos < neuronsInLayer[layer+1]; secondNeuronPos++) {      //loop through each neuron in the next layer
                    double synapseWeight = fdr.getWeight(weightList, layer, firstNeuronPos, secondNeuronPos);
                    synapseArray[layer][firstNeuronPos][secondNeuronPos] = new Synapse(synapseWeight);
                }
            }
        }
    }

    public void initializeTrainingData(Double[] data, Double target){
        Double[] scaledData = scaleData(data);
        Double[] example = new Double[3];
        example[0] = scaledData[0];
        example[1] = scaledData[1];
        example[2] = target;
        trainingData.add(example);
    }

    public void train(){
        int trainingIterations = 10000;     //10000
        double success = 0;
        for (int i = 0; i < trainingIterations; i++) {
            for (int example = 0; example < trainingData.size(); example++) {
                double target = getTarget(example);
                setInput(example);
                passForward();
                success += countSuccess(target);
                backpropagate(target);
            }
        }
        //saveWeights(ws);
        double successRate = success / (trainingData.size() * trainingIterations);
        System.out.println("Success rate = " + successRate);
    }

    private Double[] scaleData(Double[] originalData){
        //Scaling is hard coded
        Double[] scaledData = new Double[2];
        scaledData[0] = (originalData[0] - 5) / 15;
        scaledData[1] = (originalData[1] - 1) / 2;
        //System.out.println("New height = " + scaledData[0] + "; new width = " + scaledData[1]);
        return scaledData;
    }

    private void setInput(int example){
        Double[] inputData = trainingData.get(example);
        double inputHeight = inputData[0];
        double inputWidth = inputData[1];
        neuronArray[0][0].setValue(inputHeight);
        neuronArray[0][1].setValue(inputWidth);
    }

    private double countSuccess(double target){
        double success = 0;
        double output = neuronArray[2][0].getValue();
        if (target - output < 0.5){
            success = 1;
        }
        return success;
    }

    private double getTarget(int example){
        Double[] inputData = trainingData.get(example);
        return inputData[2];
    }

    private void passForward(){

        int layersAmount = neuronsInLayer.length;
        for (int layer = 1; layer < layersAmount; layer++){     //loop through each layer except the input layer
            for (int neuronNode = 0; neuronNode < neuronsInLayer[layer]; neuronNode++){     //loop through each neuron
                double newNeuronInput = 0;
                for (int synapse = 0; synapse < neuronsInLayer[layer-1]; synapse++){     //loop through each synapse
                    double previousNeuronValue = neuronArray[layer-1][synapse].getValue();
                    double synapseWeight = synapseArray[layer-1][synapse][neuronNode].getWeight();
                    newNeuronInput += previousNeuronValue * synapseWeight;
                }
                neuronArray[layer][neuronNode].sigmoid(newNeuronInput);
            }
        }
    }

    private void backpropagate(double target) {
        //double cost = calculateCost(target);        //might not be needed
        double stepSize = 0.1;

        //Define deltaK
        double outputK = neuronArray[neuronsInLayer.length - 1][0].getValue();      //makes the variable name shorter
        double deltaK = outputK * (1 - outputK) * (outputK - target);

        //Define deltaJ
        double[] deltaJ = new double[neuronsInLayer[1]];
        for (int i = 0; i < deltaJ.length; i++) {
            double outputJ = neuronArray[1][i].getValue();
            deltaJ[i] = outputJ * (1 - outputJ) * deltaK * synapseArray[1][i][0].getWeight();
        }

        //Loop through each synapse to calculate the derivative
        int layersAmount = neuronsInLayer.length;
        for (int layer = 0; layer < layersAmount - 1; layer++) {   //loop through each neuron layer except the output layer
            for (int firstNeuronPos = 0; firstNeuronPos < neuronsInLayer[layer]; firstNeuronPos++) {      //loop through each neuron in the layer
                for (int secondNeuronPos = 0; secondNeuronPos < neuronsInLayer[layer + 1]; secondNeuronPos++) {      //loop through each neuron in the previous layer

                    //Hidden layer
                    if (layer == 0) {
                        double outputI = neuronArray[layer][firstNeuronPos].getValue();
                        double derivative = stepSize * deltaJ[secondNeuronPos] * outputI;
                        synapseArray[layer][firstNeuronPos][secondNeuronPos].setDerivative(derivative);
                    }

                    //Output layer
                    if (layer == 1) {
                        double outputJ = neuronArray[layer][firstNeuronPos].getValue();
                        double derivative = stepSize * deltaK * outputJ;
                        synapseArray[layer][firstNeuronPos][secondNeuronPos].setDerivative(derivative);
                        //System.out.println("costDerivativeToWjk = " + costDerivativeToWjk);
                    }
                }
            }
        }

        //Loop through each synapse to update weights
        for (int layer = 0; layer < layersAmount - 1; layer++) {   //loop through each neuron layer except the output layer
            for (int firstNeuronPos = 0; firstNeuronPos < neuronsInLayer[layer]; firstNeuronPos++) {      //loop through each neuron in the layer
                for (int secondNeuronPos = 0; secondNeuronPos < neuronsInLayer[layer + 1]; secondNeuronPos++) {      //loop through each neuron in the next layer
                    synapseArray[layer][firstNeuronPos][secondNeuronPos].updateWeight();
                }
            }
        }
    }

    private void saveWeights(WeightSaver ws){
        ws.createNewFile();

        int layersAmount = neuronsInLayer.length;
        for (int layer = 0; layer < layersAmount-1; layer++) {   //loop through each neuron layer except the output layer
            for (int firstNeuronPos = 0; firstNeuronPos < neuronsInLayer[layer]; firstNeuronPos++) {      //loop through each neuron in the layer
                for (int secondNeuronPos = 0; secondNeuronPos < neuronsInLayer[layer+1]; secondNeuronPos++) {      //loop through each neuron in the next layer
                    double weight = synapseArray[layer][firstNeuronPos][secondNeuronPos].getWeight();
                    ws.write(layer, firstNeuronPos, secondNeuronPos, weight);
                }
            }
        }
    }

    public void guessColor(double inputHeight, double inputWidth){
        Double[] input = new Double[2];
        input[0] = inputHeight;
        input[1] = inputWidth;
        Double[] scaledInput = scaleData(input);
        neuronArray[0][0].setValue(scaledInput[0]);
        neuronArray[0][1].setValue(scaledInput[1]);
        passForward();
        callbackMessage();
    }

    private void callbackMessage(){
        double output = neuronArray[neuronsInLayer.length - 1][0].getValue();
        double certainty;
        String color;
        if (output > 0.5){
            certainty = Math.round((output * 1000)) / 10;
            color = "red";
        }
        else {
            certainty = Math.round(((1 - output) * 1000)) / 10;
            color = "blue";
        }

        System.out.println("My guess is " + color + "(" + certainty + " % certain)");
    }
}
