package neuralNetwork;

public abstract class Neuron {
    protected double value;

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
