package org.firstinspires.ftc.teamcode.src.tests.attachments;

import android.media.Image;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.src.attachments.GrabberActions;
import org.firstinspires.ftc.teamcode.src.attachments.OpenCV;
import org.firstinspires.ftc.teamcode.src.attachments.DetectPropActions;
import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;
import org.firstinspires.ftc.teamcode.src.fakes.FakeTelemetry;
import org.firstinspires.ftc.teamcode.src.fakes.drive.FakeServo;
import org.firstinspires.ftc.teamcode.src.fakes.util.FakeHardwareMapFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
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


        Point resultL = openCv.itemExists(img, templ);
        img = loadImage("imgR");
        Point resultR = openCv.itemExists(img, templ);
        img = loadImage("imgM");
        Point resultM = openCv.itemExists(img, templ);
        Assert.assertEquals(250, resultL.y, 10);
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
}
