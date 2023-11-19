package org.firstinspires.ftc.teamcode.src.tests.attachments;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.teamcode.src.attachments.OpenCV;
import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
//import java.awt.image.BufferedImage;

public class TestOpenCV {

    @Test
    public void testItemExists(){
//        OpenCVLoader.initDebug();

        int i = CvType.CV_16UC4;
        String pathTemplate = "src/main/java/org/firstinspires/ftc/teamcode/src/attachments/data/template.jpg";

        File fileTemplate = new File(pathTemplate);
        String absolutePathTemplate = fileTemplate.getAbsolutePath();

        String pathImg = "src/main/java/org/firstinspires/ftc/teamcode/src/attachments/data/imgL.jpg";

        File fileImg = new File(pathImg);
        String absolutePathImg = fileImg.getAbsolutePath();


//        try {
//            Image img = ImageIO.read(new File("strawberry.jpg"));
//        } catch (IOException e) {
//        }
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        /* In order for this to work, download OpenCV version 4.7.0 and extract the file.
        In the OpenCV file, go into build then java then x64.
        Copy the opencv_java470.dll and paste it into Program Files/Android/AndroidStudio/jre/bin
        */
        OpenCV openCv = new OpenCV();

        Mat img = Imgcodecs.imread(absolutePathImg, Imgcodecs.IMREAD_COLOR);
        Mat templ = Imgcodecs.imread(absolutePathTemplate, Imgcodecs.IMREAD_COLOR);


        Point resultL = openCv.templateMatching(img, templ);
        img = loadImage("imgR");
        Point resultR = openCv.templateMatching(img, templ);
        img = loadImage("imgM");
        Point resultM = openCv.templateMatching(img, templ);
        Assert.assertEquals(250, resultL.y, 10);
    }

    @Test
    public void testHoughCircles() {
        String pathImg = "src/main/java/org/firstinspires/ftc/teamcode/src/attachments/data/redBoxImage.jpg";

        File fileImg = new File(pathImg);
        String absolutePathImg = fileImg.getAbsolutePath();


//        try {
//            Image img = ImageIO.read(new File("strawberry.jpg"));
//        } catch (IOException e) {
//        }
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        /* In order for this to work, download OpenCV version 4.7.0 and extract the file.
        In the OpenCV file, go into build then java then x64.
        Copy the opencv_java470.dll and paste it into Program Files/Android/AndroidStudio/jre/bin
        */
        OpenCV openCv = new OpenCV();

        Mat img = Imgcodecs.imread(absolutePathImg, Imgcodecs.IMREAD_COLOR);

        Point result = openCv.houghCircles(img);
        Assert.assertEquals(150, result.x, 20);
    }

    @NonNull
    private Mat loadImage(String imageName) {
        String absolutePathImg;
        Mat img;
        String pathImg;
        File fileImg;
        pathImg = "src/main/java/org/firstinspires/ftc/teamcode/src/attachments/data/" + imageName +".jpg";

        fileImg = new File(pathImg);
        absolutePathImg = fileImg.getAbsolutePath();
        img = Imgcodecs.imread(absolutePathImg, Imgcodecs.IMREAD_COLOR);
        return img;
    }

    @Test
    public void testWhereProp() {
        int wherePropState = 0;
        int priorDetectionNumber = 0;
        int rightCount = 0;
        int midCount = 0;
        int leftCount = 0;
        Point result = new Point(100,0);
//    double sumOfXValues = 0;
//    int numOfZeroes = 0;
        String propPlace = "";
        if (wherePropState < 1) {
            if (1 != priorDetectionNumber) {
                priorDetectionNumber = 1;
                wherePropState ++;
                if (result.x < 20 || result.x > 250) {
                    rightCount++;
                } else if (result.x < 150) {
                    leftCount++;
                } else {
                    midCount++;
                }
            }
            propPlace = "";
        }
        if (!(wherePropState < 1)){
            if (rightCount > leftCount && rightCount > midCount) {
                propPlace = "right";
            } else if (leftCount > midCount) {
                propPlace = "left";
            } else {
                propPlace = "middle";
            }
        }
        Assert.assertEquals("right", propPlace);
    }
}
