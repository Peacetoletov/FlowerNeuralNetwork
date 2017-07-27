/**
 * Created by lukas on 20.7.2017.
 */

import java.io.*;
import java.util.ArrayList;

public class FileDataReader {

    public ArrayList<Double[]> read(String fileName){
        BufferedReader br = null;
        ArrayList<Double[]> list = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(fileName));
            String line;

            int arrayElements = 0;
            if (fileName == "blueFlower.txt" || fileName == "redFlower.txt")
                arrayElements = 2;
            else if (fileName == "weights.txt")
                arrayElements = 4;

            while((line = br.readLine()) != null){
                list.add(convertDataToValues(line, arrayElements));
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        finally {     //Finally = executes no matter what (exception / no exception)
            try {
                br.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return list;
    }

    public double getWeight(ArrayList<Double[]> weightList, int layer, int firstNeuronPos, int secondNeuronPos){
        double weight = 0;
        for (int i = 0; i < weightList.size(); i++){
            Double[] x = weightList.get(i);
            if (x[0] == layer && x[1] == firstNeuronPos && x[2] == secondNeuronPos){
                weight = x[3];
            }
        }
        return weight;
    }

    private Double[] convertDataToValues(String line, int arrayElements) {
        Double[] valueArray = new Double[arrayElements];      //Training data values: [height][length];  Weight values: [firstLayer][firstPosition][secondPosition][weight]
        String tempString = "";
        for (int i = 0; i < line.length(); i++) {
            if (!String.valueOf(line.charAt(i)).equals(" ") && i != line.length() - 1) {
                tempString += String.valueOf(line.charAt(i));
            }
            else {
                try {
                    double value = Double.parseDouble(tempString);
                    tempString = "";

                    /*
                    if (valueArray[0] == null)
                        valueArray[0] = value;
                    else
                        valueArray[1] = value;
                        */
                    for (int j = 0; j < arrayElements; j++){
                        if (valueArray[j] == null){
                            valueArray[j] = value;
                            break;
                        }
                    }
                }
                catch(NumberFormatException e){
                    e.printStackTrace();
                }
            }
        }

        return valueArray;
    }
}
