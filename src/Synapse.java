/**
 * Created by lukas on 21.7.2017.
 */

public class Synapse {
    private double weight;
    private double costDerivative;      //cost derivative with respect to this weight

    Synapse(double weight){
        this.weight = weight;
    }

    public double getWeight(){
        return weight;
    }

    public void setDerivative(double derivative){
        this.costDerivative = derivative;
    }

    public void updateWeight(){
        weight -= costDerivative;
    }
}
