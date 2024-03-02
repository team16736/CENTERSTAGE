package org.firstinspires.ftc.teamcode.src.tests.attachments;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.util.RobotLog;

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
    public void testTemplateMatching(){
//        OpenCVLoader.initDebug();

        int i = CvType.CV_16UC4;
        String pathTemplate = "src/main/java/org/firstinspires/ftc/teamcode/src/attachments/data/BlueThingTempl.png";
        File fileTemplate = new File(pathTemplate);
        String absolutePathTemplate = fileTemplate.getAbsolutePath();
        OpenCV openCV = new OpenCV();

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat templ = Imgcodecs.imread(absolutePathTemplate, Imgcodecs.IMREAD_COLOR);
        Mat templR = templ.submat(0, templ.rows(), templ.cols() / 2, templ.cols());
        Mat templL = templ.submat(0, templ.rows(), 0, templ.cols() / 2);
//        RobotLog.dd("OpenCV", "type %d", templ.type());

        String pathImg = "src/main/java/org/firstinspires/ftc/teamcode/src/attachments/data/RedThing2.png";

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
        img = img.submat(100, img.rows(), 0, img.cols());


        Point resultL = openCv.templateMatchingHalfImg(img, templL, templR);
        Assert.assertEquals(516, resultL.x, 0);
        Assert.assertEquals(1000, Core.mean(img).val[0], 0);
//        img = loadImage("imgR");
//        Point resultR = openCv.templateMatching(img, templ);
//        img = loadImage("imgM");
//        Point resultM = openCv.templateMatching(img, templ);
//        Assert.assertEquals(250, resultL.y, 10);
    }

    @Test
    public void testHoughCircles() {
        for (int i = 0; i < 3; i++) {
            String pathImg = "src/main/java/org/firstinspires/ftc/teamcode/src/attachments/data/BlueSphere2Inches" + i + ".png";

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


            if (i == 0) {
                Assert.assertEquals(33, result.x, 20);
            } else if (i == 1) {
                Assert.assertEquals(174*2, result.x, 20);
            } else {
                Assert.assertEquals(249*2, result.x, 50);
            }
        }
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
