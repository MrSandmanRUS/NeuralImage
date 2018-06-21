package neuralNetwork;

import java.util.ArrayList;

public class NeuralNetwork {

    double epsilon; //speed of training
    double alpha; //moment
    ActivationFunc activationFunc;
    double dropOutСoef; // 0<=dropOutСoef<=1
    int epochNum;
    ArrayList<InputNeuron> inputNeurons;
    ArrayList<ArrayList<HiddenNeuron>> hiddenNeurons;
    ArrayList<OutputNeuron> outputNeurons;
    ErrorType errorType;
    ArrayList<ArrayList<Double>> inputValue;
    ArrayList<ArrayList<Double>> outputValue;


    public NeuralNetwork(double epsilon, //speed of training
                  double alpha, //moment
                  ActivationFunc activationFunc,
                  int epochNum,
                  ArrayList<InputNeuron> inputNeurons,
                  ArrayList<ArrayList<HiddenNeuron>> hiddenNeurons,
                  ArrayList<OutputNeuron> outputNeurons,
                  ErrorType errorType,
                  ArrayList<ArrayList<Double>> inputValue,
                  ArrayList<ArrayList<Double>> outputValue,
                  double dropOutСoef) {
        if (!(inputValue.size() == outputValue.size())) {
            throw new IllegalArgumentException();
        }
        this.epsilon = epsilon;
        this.alpha = alpha;
        this.activationFunc = activationFunc;
        this.epochNum = epochNum;
        this.inputNeurons = inputNeurons;
        this.hiddenNeurons = hiddenNeurons;
        this.outputNeurons = outputNeurons;
        this.errorType = errorType;
        this.inputValue = inputValue;
        this.outputValue = outputValue;
        this.dropOutСoef = dropOutСoef;
    }

    public ArrayList<Double> predict(ArrayList<Double> input) {
        if (input.size() != inputNeurons.size()) {
            throw new IllegalArgumentException();
        }
        ArrayList<Double> result = new ArrayList<Double>();
        for (int count = 0; count < input.size(); ++count) {
            inputNeurons.get(count).setValue(input.get(count));

        }
        result = predictCalculate();


        return result;
    }

    public void training() {
        for (int count = 0; count < epochNum; ++count) {
            double numerator = 0;

            //select random neurons for dropout
            int isDropNum = 0;
            for (int count2 = 0; count2 < hiddenNeurons.size(); ++count2) {
                for (int count3 = 0; count3 < hiddenNeurons.get(count2).size(); ++count3) {
                    double coef = Math.random();
                    if (coef < dropOutСoef) {
                        hiddenNeurons.get(count2).get(count3).setIsDrop(true);
                        isDropNum++;
                    } else {
                        hiddenNeurons.get(count2).get(count3).setIsDrop(false);
                    }
                }
            }
            //System.out.println("epochNum=" + epochNum + " IsDropNum=" + isDropNum);

            for (int count2 = 0; count2 < inputValue.size(); ++count2) {
                for (int count3 = 0; count3 < inputNeurons.size(); ++count3) {
                    inputNeurons.get(count3).setValue(inputValue.get(count2).get(count3));
                }
                for (int count3 = 0; count3 < outputNeurons.size(); ++count3) {
                    outputNeurons.get(count3).setIdealRes(outputValue.get(count2).get(count3));
                }

                calculate();
                recalculateOutput();
                recalculateHiddenLayer();
                recalculateInput();
                numerator += getErrorNumerator();
                System.out.println(count);
            }
            //if (inputValue.size() * outputNeurons.size() > 0) {
                double error = errorType.getError(numerator, inputValue.size());// * outputNeurons.size()); ???
                System.out.println(error);
            //}

        }
        System.out.println("");

    }

    private ArrayList<Double> predictCalculate() {
        ArrayList<Double> result = new ArrayList<Double>();

        //if hasn't hidden layer
        if (hiddenNeurons.size() == 0) {
            throw new IllegalArgumentException();
        } else {
            //if has hidden layer
            for (int count = 0; count <= hiddenNeurons.size(); ++count) {
                if (count == 0) {
                    //first step
                    for (int count2 = 0; count2 < hiddenNeurons.get(count).size(); ++count2) {
                        if (!hiddenNeurons.get(0).get(count2).getIsDisplacement()) {
                            double inputValue = 0;
                            for (int count3 = 0; count3 < inputNeurons.size(); ++count3) {
                                inputValue += inputNeurons.get(count3).value * inputNeurons.get(count3).getWeight().get(count2);
                            }
                            hiddenNeurons.get(0).get(count2).setValue((1 - dropOutСoef) * activationFunc.getFuncResult(inputValue));
                        } else {
                            hiddenNeurons.get(0).get(count2).setValue((1 - dropOutСoef) * 1); //if IsDisplacement => output=1
                        }
                    }
                } else {
                    if (count == hiddenNeurons.size()) {
                        //last step, don't have displacement neurons
                        for (int count2 = 0; count2 < outputNeurons.size(); ++count2) {
                            double inputValue = 0;
                            for (int count3 = 0; count3 < hiddenNeurons.get(count - 1).size(); ++count3) {
                                inputValue += hiddenNeurons.get(count - 1).get(count3).value * hiddenNeurons.get(count - 1).get(count3).getWeight().get(count2);
                            }
                            outputNeurons.get(count2).setValue(activationFunc.getFuncResult(inputValue));
                        }
                    } else {
                        for (int count2 = 0; count2 < hiddenNeurons.get(count).size(); ++count2) {
                            if (!hiddenNeurons.get(count).get(count2).getIsDisplacement()) {
                                double inputValue = 0;
                                for (int count3 = 0; count3 < hiddenNeurons.get(count - 1).size(); ++count3) {
                                    inputValue += hiddenNeurons.get(count - 1).get(count3).value * hiddenNeurons.get(count - 1).get(count3).getWeight().get(count2);
                                }
                                hiddenNeurons.get(count).get(count2).setValue((1 - dropOutСoef) * activationFunc.getFuncResult(inputValue));
                            } else {
                                hiddenNeurons.get(count).get(count2).setValue((1 - dropOutСoef) * 1);
                            }
                        }
                    }
                }
            }
        }

        for (int count = 0; count < outputNeurons.size(); ++count) {
            result.add(outputNeurons.get(count).getValue());
        }

        return result;
    }

    private ArrayList<Double> calculate() {
        ArrayList<Double> result = new ArrayList<Double>();

        //if hasn't hidden layer
        if (hiddenNeurons.size() == 0) {
            throw new IllegalArgumentException();
        } else {
            //if has hidden layer
            for (int count = 0; count <= hiddenNeurons.size(); ++count) {
                if (count == 0) {
                    //first step
                    for (int count2 = 0; count2 < hiddenNeurons.get(count).size(); ++count2) {
                        if (!hiddenNeurons.get(0).get(count2).getIsDrop()) {
                            if (!hiddenNeurons.get(0).get(count2).getIsDisplacement()) {
                                double inputValue = 0;
                                for (int count3 = 0; count3 < inputNeurons.size(); ++count3) {
                                    inputValue += inputNeurons.get(count3).value * inputNeurons.get(count3).getWeight().get(count2);
                                }
                                hiddenNeurons.get(0).get(count2).setValue(activationFunc.getFuncResult(inputValue));
                            } else {
                                hiddenNeurons.get(0).get(count2).setValue(1); //if IsDisplacement => output=1
                            }
                        }
                    }
                } else {
                    if (count == hiddenNeurons.size()) {
                        //last step, don't have displacement neurons
                        for (int count2 = 0; count2 < outputNeurons.size(); ++count2) {
                            double inputValue = 0;
                            for (int count3 = 0; count3 < hiddenNeurons.get(count - 1).size(); ++count3) {
                                if (!hiddenNeurons.get(count - 1).get(count3).getIsDrop()) { //if not dropout
                                    inputValue += hiddenNeurons.get(count - 1).get(count3).value * hiddenNeurons.get(count - 1).get(count3).getWeight().get(count2);
                                }
                            }
                            outputNeurons.get(count2).setValue(activationFunc.getFuncResult(inputValue));
                        }
                    } else {
                        for (int count2 = 0; count2 < hiddenNeurons.get(count).size(); ++count2) {
                            if (!hiddenNeurons.get(count).get(count2).getIsDrop()) {
                                if (!hiddenNeurons.get(count).get(count2).getIsDisplacement()) {
                                    double inputValue = 0;
                                    for (int count3 = 0; count3 < hiddenNeurons.get(count - 1).size(); ++count3) {
                                        if (!hiddenNeurons.get(count - 1).get(count3).getIsDrop()) { //if not dropout
                                            inputValue += hiddenNeurons.get(count - 1).get(count3).value * hiddenNeurons.get(count - 1).get(count3).getWeight().get(count2);
                                        }
                                    }
                                    hiddenNeurons.get(count).get(count2).setValue(activationFunc.getFuncResult(inputValue));
                                } else {
                                    hiddenNeurons.get(count).get(count2).setValue(1);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int count = 0; count < outputNeurons.size(); ++count) {
            result.add(outputNeurons.get(count).getValue());
        }

        return result;
    }


    private void recalculateInput() {
        for (int count = 0; count < inputNeurons.size(); ++count) {
            ArrayList<Double> grad = new ArrayList<Double>();
            for (int count2 = 0; count2 < hiddenNeurons.get(0).size(); ++count2) {
                if (!hiddenNeurons.get(0).get(count2).getIsDrop()) {
                    if (!hiddenNeurons.get(0).get(count2).getIsDisplacement()) { //displacement neurons don't have input
                        double tempGrad = inputNeurons.get(count).getValue() * hiddenNeurons.get(0).get(count2).getDelta();
                        grad.add(tempGrad);

                        double deltaWeight = epsilon * tempGrad + alpha * inputNeurons.get(count).getPrevDeltaWeight().get(count2);
                        ArrayList<Double> weight = inputNeurons.get(count).getWeight();
                        ArrayList<Double> prevWeight = inputNeurons.get(count).getPrevDeltaWeight();
                        prevWeight.set(count2, deltaWeight);
                        weight.set(count2, inputNeurons.get(count).getWeight().get(count2) + deltaWeight);

                        inputNeurons.get(count).setWeight(weight);
                        inputNeurons.get(count).setPrevDeltaWeight(prevWeight);
                    }
                }
            }
            inputNeurons.get(count).setGrad(grad);
        }
    }

    private void recalculateHiddenLayer() {
        for (int count = hiddenNeurons.size() - 1; count >= 0; --count) {
            for (int count2 = 0; count2 < hiddenNeurons.get(count).size(); ++count2) {
                if (!hiddenNeurons.get(count).get(count2).getIsDrop()) {

                    //calculate delta
                    double sum = 0;
                    if (count == hiddenNeurons.size() - 1) {
                        for (int count3 = 0; count3 < outputNeurons.size(); ++count3) {
                            sum += outputNeurons.get(count3).getDelta() * hiddenNeurons.get(count).get(count2).getWeight().get(count3);
                        }
                    } else {
                        for (int count3 = 0; count3 < hiddenNeurons.get(count + 1).size(); ++count3) {
                            if (!hiddenNeurons.get(count + 1).get(count3).getIsDrop()) {
                                if (!hiddenNeurons.get(count + 1).get(count3).getIsDisplacement()) {
                                    sum += hiddenNeurons.get(count + 1).get(count3).getDelta() * hiddenNeurons.get(count).get(count2).getWeight().get(count3);
                                }
                            }
                        }
                    }
                    hiddenNeurons.get(count).get(count2).setDelta(activationFunc.getDerivedResult(hiddenNeurons.get(count).get(count2).getValue()) * sum);

                    //calculate weight
                    ArrayList<Double> grad = new ArrayList<Double>();
                    if (count == hiddenNeurons.size() - 1) {
                        for (int count3 = 0; count3 < outputNeurons.size(); ++count3) {
                            double tempGrad = hiddenNeurons.get(count).get(count2).getValue() * outputNeurons.get(count3).getDelta();
                            grad.add(tempGrad);

                            double deltaWeight = epsilon * tempGrad + alpha * hiddenNeurons.get(count).get(count2).getPrevDeltaWeight().get(count3);
                            ArrayList<Double> weight = hiddenNeurons.get(count).get(count2).getWeight();
                            ArrayList<Double> prevWeight = hiddenNeurons.get(count).get(count2).getPrevDeltaWeight();
                            prevWeight.set(count3, deltaWeight);
                            weight.set(count3, hiddenNeurons.get(count).get(count2).getWeight().get(count3) + deltaWeight);

                            hiddenNeurons.get(count).get(count2).setWeight(weight);
                            hiddenNeurons.get(count).get(count2).setPrevDeltaWeight(prevWeight);
                        }
                    } else {
                        for (int count3 = 0; count3 < hiddenNeurons.get(count + 1).size(); ++count3) {
                            if (!hiddenNeurons.get(count + 1).get(count3).getIsDrop()) {
                                if (!hiddenNeurons.get(count + 1).get(count3).getIsDisplacement()) {
                                    double tempGrad = hiddenNeurons.get(count).get(count2).getValue() * hiddenNeurons.get(count + 1).get(count3).getDelta();
                                    grad.add(tempGrad);

                                    double deltaWeight = epsilon * tempGrad + alpha * hiddenNeurons.get(count).get(count2).getPrevDeltaWeight().get(count3);
                                    ArrayList<Double> weight = hiddenNeurons.get(count).get(count2).getWeight();
                                    ArrayList<Double> prevWeight = hiddenNeurons.get(count).get(count2).getPrevDeltaWeight();
                                    prevWeight.set(count3, deltaWeight);
                                    weight.set(count3, hiddenNeurons.get(count).get(count2).getWeight().get(count3) + deltaWeight);

                                    hiddenNeurons.get(count).get(count2).setWeight(weight);
                                    hiddenNeurons.get(count).get(count2).setPrevDeltaWeight(prevWeight);
                                }
                            }
                        }
                    }
                    hiddenNeurons.get(count).get(count2).setGrad(grad);

                }
            }
        }
    }

    private void recalculateOutput() {
        for (int count = 0; count < outputNeurons.size(); ++count) {
            outputNeurons.get(count).setDelta(
                    (outputNeurons.get(count).getIdealRes() - outputNeurons.get(count).getValue()) *
                            activationFunc.getDerivedResult(outputNeurons.get(count).getValue()));
        }
    }

    private double getErrorNumerator() {
        double[] idealRes = new double[outputNeurons.size()];
        double[] obtainedRes = new double[outputNeurons.size()];
        for (int count = 0; count < outputNeurons.size(); ++count) {
            idealRes[count] = outputNeurons.get(count).getIdealRes();
            obtainedRes[count] = outputNeurons.get(count).getValue();
        }

        return errorType.getNumerator(idealRes, obtainedRes);
    }
}

/*EXAMPLE №1:
             ArrayList<Double> wi1 = new ArrayList<Double>();
        wi1.add(0.45);
        wi1.add(0.78);
        ArrayList<Double> wi2 = new ArrayList<Double>();
        wi2.add(-0.12);
        wi2.add(0.13);
        ArrayList<Double> wh1 = new ArrayList<Double>();
        wh1.add(1.5);
        ArrayList<Double> wh2 = new ArrayList<Double>();
        wh2.add(-2.3);


        ArrayList<InputNeuron> inputNeurons = new ArrayList<InputNeuron>();
        InputNeuron i1= new InputNeuron(wi1, false);
        InputNeuron i2 = new InputNeuron(wi2, false);
        inputNeurons.add(i1);
        inputNeurons.add(i2);

        HiddenNeuron h1 = new HiddenNeuron(wh1, false);
        HiddenNeuron h2 = new HiddenNeuron(wh2, false);
        ArrayList<HiddenNeuron> hiddenNeuronsList = new ArrayList<HiddenNeuron>();
        hiddenNeuronsList.add(h1);
        hiddenNeuronsList.add(h2);

        ArrayList<ArrayList<HiddenNeuron>> hiddenNeurons = new ArrayList<ArrayList<HiddenNeuron>>();
        hiddenNeurons.add(hiddenNeuronsList);

        OutputNeuron o1 = new OutputNeuron();
        ArrayList<OutputNeuron> outputNeurons = new ArrayList<OutputNeuron>();
        outputNeurons.add(o1);

        ArrayList<Double> inputList = new ArrayList<Double>();
        inputList.add(1.0);
        inputList.add(0.0);
        ArrayList<Double> inputList2 = new ArrayList<Double>();
        inputList2.add(0.0);
        inputList2.add(1.0);
        ArrayList<Double> inputList3 = new ArrayList<Double>();
        inputList3.add(1.0);
        inputList3.add(1.0);
        ArrayList<Double> inputList4 = new ArrayList<Double>();
        inputList4.add(0.0);
        inputList4.add(0.0);
        ArrayList<ArrayList<Double>> input = new ArrayList<ArrayList<Double>>();
        input.add(inputList);
        input.add(inputList2);
        input.add(inputList3);
        input.add(inputList4);

        ArrayList<Double> outputList = new ArrayList<Double>();
        outputList.add(0.0);
        ArrayList<Double> outputList2 = new ArrayList<Double>();
        outputList2.add(0.0);
        ArrayList<Double> outputList3 = new ArrayList<Double>();
        outputList3.add(1.0);
        ArrayList<Double> outputList4 = new ArrayList<Double>();
        outputList4.add(1.0);

        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();
        output.add(outputList);
        output.add(outputList2);
        output.add(outputList3);
        output.add(outputList4);

        ErrorType errorType = ErrorType.MSE;

        NeuralNetwork neuralNetwork = new NeuralNetwork(0.7, 0.3, SIGMOID, 1000, inputNeurons, hiddenNeurons, outputNeurons, errorType, input, output, 0);
        neuralNetwork.training();
 */