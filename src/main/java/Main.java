import neuralNetwork.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static neuralNetwork.ActivationFunc.SIGMOID;

public class Main {
    static NeuralNetwork neuralNetwork;

    public static void main(String args[]) {

        ArrayList<ArrayList<Double>> in = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> out = new ArrayList<ArrayList<Double>>();

        in.add(getPixelArray("in.jpg"));
        out.add(getPixelArray("out.jpg"));


        updateNeuralNetwork(in, out);

       ArrayList<Double> res = neuralNetwork.predict(out.get(0));
        int[] newPixels = new int[res.size() / 4];
        for (int count = 0, count2 = 0; count < res.size(); ++count2) {


            Double alpaDouble = res.get(count) * 256;
            int alpha = alpaDouble.intValue();

            Double redDouble = res.get(count + 1) * 256;
            int red = redDouble.intValue();

            Double greenDouble = res.get(count + 2) * 256;
            int green = greenDouble.intValue();

            Double blueDouble = res.get(count + 3) * 256;
            int blue = blueDouble.intValue();

            int argb = (alpha << 24) | (red << 16 ) | (green << 8) | blue;

            newPixels[count2] = argb;

            count += 4;
        }


        BufferedImage imgSample = null;
        try {
            imgSample = ImageIO.read(new File("test.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        BufferedImage img = new BufferedImage(imgSample.getWidth(), imgSample.getHeight(), BufferedImage.TYPE_INT_ARGB);
        File f = null;
        //create random image pixel by pixel
        int counter = 0;
        for(int y = 0; y < imgSample.getHeight(); y++){
            for(int x = 0; x < imgSample.getWidth(); x++){

                img.setRGB(x, y, newPixels[counter]);
                ++counter;
            }
        }
        //write image
        try{
            f = new File("Output.png");
            ImageIO.write(img, "png", f);
        }catch(IOException e){
            System.out.println("Error: " + e);
        }

    }

    public static Image getImageFromArray(int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = (WritableRaster) image.getData();
        raster.setPixels(0,0,width,height,pixels);
        return image;
    }

    static ArrayList<Double> getPixelArray(String filename) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int[][] pixelsArray= new int[img.getWidth()][img.getHeight()];

        ArrayList<Double> pixel = new ArrayList<Double>();

        for (int i=0; i<img.getWidth(); i++) {


            for (int j=0; j < img.getHeight(); j++) {
                pixelsArray[i][j] = img.getRGB(i, j);

                int argb = img.getRGB(i, j);
                int alpha = (argb >> 24) & 0xff;
                int red = (argb >> 16) & 0xff;
                int green = (argb >>  8) & 0xff;
                int blue = (argb ) & 0xff;

                pixel.add(((double) alpha) * 0.00390625);
                pixel.add(((double) red) * 0.00390625);
                pixel.add(((double) green) * 0.00390625);
                pixel.add(((double) blue) * 0.00390625);

            }

        }

        return pixel;
    }


    public static void updateNeuralNetwork(ArrayList<ArrayList<Double>> input, ArrayList<ArrayList<Double>> output) {
        double min = -1;
        double max = 1;

        ArrayList<InputNeuron> inputNeurons = new ArrayList<InputNeuron>();
        for (int count = 0; count < input.get(0).size(); ++count) {
            ArrayList<Double> wi = new ArrayList<Double>();
            for (int count2 = 0; count2 < input.get(0).size(); ++count2) {
                double weight = min + Math.random() * (max - min);
                wi.add(weight);
            }
            InputNeuron i = new InputNeuron(wi, false);
            inputNeurons.add(i);
        }

        //displacement neuron test////////

        /*ArrayList<Double> wi = new ArrayList<>();
        for (int count2 = 0; count2 < 23; ++count2) { //!!22
            double weight = 1;
            wi.add(weight);
        }
        InputNeuron i = new InputNeuron(wi, true);
        inputNeurons.add(i);*/
        //displacement neuron test////////

        ArrayList<ArrayList<HiddenNeuron>> hiddenNeurons = new ArrayList<ArrayList<HiddenNeuron>>();

        //для нейронов скрытого слоя, кроме последнего
        for (int count = 0; count < 1; ++count) { //2
            ArrayList<HiddenNeuron> hiddenNeuronsList = new ArrayList<HiddenNeuron>();
            for (int count2 = 0; count2 < input.get(0).size(); ++count2) {
                ArrayList<Double> wh = new ArrayList<Double>();
                for (int count3 = 0; count3 < input.get(0).size(); ++count3) {
                    double weight = min + Math.random() * (max - min);
                    wh.add(weight);
                }
                HiddenNeuron h = new HiddenNeuron(wh, false);
                hiddenNeuronsList.add(h);
            }

            //deplacement neuron test////////
            /*  ArrayList<Double> wh = new ArrayList<>();
                for (int count3 = 0; count3 < 23; ++count3) {//!!22
                    double weight = 1;
                    wh.add(weight);
                }
                HiddenNeuron h = new HiddenNeuron(wh, true);
                hiddenNeuronsList.add(h);*/
            //deplacement neuron test////////

            hiddenNeurons.add(hiddenNeuronsList);
        }

        //для последнего слоя скрытых нейронов
        ArrayList<HiddenNeuron> hiddenNeuronsList = new ArrayList<HiddenNeuron>();
        for (int count2 = 0; count2 < input.get(0).size(); ++count2) {
            ArrayList<Double> wh = new ArrayList<Double>();
            for (int count3 = 0; count3 < input.get(0).size(); ++count3) {
                double weight = min + Math.random() * (max - min);
                wh.add(weight);
            }
            HiddenNeuron h = new HiddenNeuron(wh, false);
            hiddenNeuronsList.add(h);
        }

        //deplacement neuron test////////
            /*ArrayList<Double> wh = new ArrayList<>();
            for (int count3 = 0; count3 < 1; ++count3) {
                double weight = min + Math.random() * (max - min);
                wh.add(weight);
            }
            HiddenNeuron h = new HiddenNeuron(wh, true);
            hiddenNeuronsList.add(h);*/
        //deplacement neuron test////////

        hiddenNeurons.add(hiddenNeuronsList);



        ArrayList<OutputNeuron> outputNeurons = new ArrayList<OutputNeuron>();
        for (int count = 0; count < output.get(0).size(); ++count) {
            OutputNeuron o = new OutputNeuron();
            outputNeurons.add(o);
        }

        ErrorType errorType = ErrorType.MSE;

        neuralNetwork = new NeuralNetwork(0.5, 0.3, SIGMOID, 1, inputNeurons, hiddenNeurons, outputNeurons, errorType, input, output, 0.0);
        neuralNetwork.training();
    }


}
