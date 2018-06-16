package neuralNetwork;

import static java.lang.Math.atan;
import static java.lang.Math.pow;

public enum ErrorType {
    MSE(),
    ROOTMSE(),
    ARCTAN();

    ErrorType() {
    }

    public double getNumerator(double[] idealRes, double[] obtainedRes) {
        if (!(idealRes.length == obtainedRes.length)) {
            throw new IllegalArgumentException();
        }

        int size = idealRes.length;
        double numerator = 0;
        switch (this) {
            case MSE:
                for (int count = 0; count < size; ++count) {
                    numerator += pow((idealRes[count] - obtainedRes[count]), 2);
                }
                break;
            case ROOTMSE:
                for (int count = 0; count < size; ++count) {
                    numerator += pow((idealRes[count] - obtainedRes[count]), 2);
                }
                break;
            case ARCTAN:
                for (int count = 0; count < size; ++count) {
                    numerator += pow(atan(idealRes[count] - obtainedRes[count]), 2);
                }
                break;
        }

        return numerator;
    }

    public double getError(double numerator, int setNum) {
        if (setNum <= 0) {
            throw new IllegalArgumentException();
        }

        double error = -1;
        switch (this) {
            case MSE:
                error = numerator / setNum;
                break;
            case ROOTMSE:
                error = pow(numerator / setNum, 0.5);
                break;
            case ARCTAN:
                error = numerator / setNum;
                break;
        }

        return error;
    }
}
