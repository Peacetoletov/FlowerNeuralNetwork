/**
 * Created by lukas on 22.7.2017.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WeightSaver {
    public void createNewFile() {
        try {
            File file = new File("weights.txt");
            file.createNewFile();
            PrintWriter pw = new PrintWriter(file);     //overwrites

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void write(int layer, int firstNeuronPos, int secondNeuronPos, double weight) {
        try {
            File file = new File("weights.txt");

            PrintWriter pw = new PrintWriter(new FileWriter(file, true));         //doesn't overwrite

            pw.println(layer + " " + firstNeuronPos + " " + secondNeuronPos + " " + weight);

            pw.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

