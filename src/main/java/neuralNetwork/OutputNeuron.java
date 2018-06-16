package neuralNetwork;

public class OutputNeuron extends Neuron {

    private double idealRes;
    private double delta;

    public OutputNeuron() {
    }

    public void setIdealRes(double idealRes) {
        this.idealRes = idealRes;
    }

    public double getIdealRes() {
        return idealRes;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public double getDelta() {
        return delta;
    }
}
