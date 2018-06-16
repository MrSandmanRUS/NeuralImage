package neuralNetwork;

import java.util.ArrayList;

public class InputNeuron extends Neuron {

    private ArrayList<Double> weight = new ArrayList<Double>();
    private ArrayList<Double> prevDeltaWeight = new ArrayList<Double>();
    private ArrayList<Double> grad = new ArrayList<Double>();
    private boolean isDisplacement;

    public InputNeuron(ArrayList<Double> weight, boolean isDisplacement ) {
        this.weight = weight;
        this.isDisplacement = isDisplacement;
        for (int count = 0; count < weight.size(); ++count) {
            prevDeltaWeight.add(0.0);
        }
    }

    public ArrayList<Double> getWeight() {
        return weight;
    }

    public void setWeight(ArrayList<Double> weight) {
        this.weight = weight;
    }

    public ArrayList<Double> getGrad() {
        return grad;
    }

    public void setGrad(ArrayList<Double> grad) {
        this.grad = grad;
    }

    public ArrayList<Double> getPrevDeltaWeight() {
        return prevDeltaWeight;
    }

    public void setPrevDeltaWeight(ArrayList<Double> prevDeltaWeight) {
        this.prevDeltaWeight = prevDeltaWeight;
    }

    public void setIsDisplacement(boolean isDisplacement) {
        this.isDisplacement = isDisplacement;
    }

    public boolean getIsDisplacement() {
        return isDisplacement;
    }

}
