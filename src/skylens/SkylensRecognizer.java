/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skylens;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.imgrec.ImageRecognitionPlugin;
import org.neuroph.imgrec.ImageUtilities;
import org.neuroph.imgrec.filter.impl.SobelEdgeDetection;
import org.neuroph.imgrec.filter.impl.GrayscaleFilter;

/**
 *
 * @author mzdv
 */
public class SkylensRecognizer {

    private static final double THRESHOLD = 0.5;
    private static final int CROP_SIZE = 100;

    public static String recognize(BufferedImage bufferedImage, NeuralNetwork coreNN, NeuralNetwork wingNN, NeuralNetwork noseNN) {

        ImageRecognitionPlugin coreImageRecognition = (ImageRecognitionPlugin) coreNN.getPlugin(ImageRecognitionPlugin.class);
        ImageRecognitionPlugin wingImageRecognition = (ImageRecognitionPlugin) wingNN.getPlugin(ImageRecognitionPlugin.class);
        ImageRecognitionPlugin noseImageRecognition = (ImageRecognitionPlugin) noseNN.getPlugin(ImageRecognitionPlugin.class);

        SobelEdgeDetection sobel = new SobelEdgeDetection();
        GrayscaleFilter grayscale = new GrayscaleFilter();

        BufferedImage filteredImage = grayscale.processImage(bufferedImage);
        filteredImage = sobel.processImage(filteredImage);

        ImageUtilities.resizeImage(filteredImage, 1000, filteredImage.getHeight());

        ArrayList<BufferedImage> croppedImages = new ArrayList<>();
        ArrayList<BufferedImage> pipelineImages = new ArrayList<>();

        long heightCount = filteredImage.getHeight() / CROP_SIZE;

        for (int i = 0; i < heightCount; i++) {
            for (int j = 0; j < 9; j++) {
                croppedImages.add(ImageUtilities.cropImage(filteredImage, j * CROP_SIZE, i * CROP_SIZE, (j + 1) * CROP_SIZE, i * CROP_SIZE));
            }
        }

        for (int i = 0; i < croppedImages.size(); i++) {
            HashMap<String, Double> output = coreImageRecognition.recognizeImage(croppedImages.get(i));
            double sum = 0;

            for (Double value : output.values()) {
                sum += sum + value;
            }

            if (sum / output.size() > THRESHOLD) {
                pipelineImages.add(croppedImages.get(i));
            }
        }

        double finalScore = 0;

        for (int i = 0; i < pipelineImages.size(); i++) {
            HashMap<String, Double> outputForWings = wingImageRecognition.recognizeImage(pipelineImages.get(i));
            HashMap<String, Double> outputForNose = noseImageRecognition.recognizeImage(pipelineImages.get(i));

            double sumForWings = 0;
            double sumForNose = 0;

            for (Double value : outputForWings.values()) {
                sumForWings += sumForWings + value;
            }

            for (Double value : outputForNose.values()) {
                sumForNose += sumForNose + value;
            }

            if (sumForNose > sumForWings) {
                finalScore += sumForNose;
            } else {
                finalScore += sumForWings;
            }
        }

        if (finalScore / pipelineImages.size() > THRESHOLD) {
            return "Boeing";
        } else {
            return "Airbus";
        }
    }
;
}
