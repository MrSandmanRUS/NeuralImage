package neuralNetwork;

import static java.lang.Math.E;
import static java.lang.Math.pow;

public enum ActivationFunc {
    LINEAR(),
    SIGMOID(),
    HYPERBOLICTANGENT();

    ActivationFunc() {
    }

    public double getFuncResult(double arg) {
        double result = 0;
        switch (this) {
            case LINEAR:
                result = arg;
                break;
            case SIGMOID:
                result = 1/(1 + pow(E, (-1 * arg)));
                break;
            case HYPERBOLICTANGENT:
                result = (pow(E, 2 * arg) - 1)/(pow(E, 2 * arg) + 1);
                break;
        }

        return result;
    }

    public double getDerivedResult(double arg) {
        double result = 0;
        switch (this) {
            case LINEAR:
                result = 1;
                break;
            case SIGMOID:
                result = (1 - arg) * arg;
                break;
            case HYPERBOLICTANGENT:
                result = 1 - pow(arg, 2);
                break;
        }

        return result;
    }
}
