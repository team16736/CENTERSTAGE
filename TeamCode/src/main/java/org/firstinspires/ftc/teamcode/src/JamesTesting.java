
package org.firstinspires.ftc.teamcode.src;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.src.attachments.DetectPropActions;
import org.firstinspires.ftc.teamcode.src.attachments.OpenCV;
import org.firstinspires.ftc.teamcode.src.driving.HelperActions;
import org.junit.Assert;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

@TeleOp(name = "James Test", group = "Linear Opmode")
public class JamesTesting extends HelperActions {

    private OpenCV openCV = null;

    @Override
    public void runOpMode() {

        openCV = new OpenCV();
        DetectPropActions detectPropActions = new DetectPropActions(hardwareMap);

        //Set Speed for teleOp. Mecannum wheel speed.
        //driveActions.setSpeed(1.0);

        // Wait for the game to start (driver presses PLAY)
        telemetry.addData("Waiting for start", "");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            detectPropActions.whereProp(10);
            while (detectPropActions.propPlace == "") {
                detectPropActions.whereProp(10);
//                RobotLog.dd("OpenCV", "Result" + detectPropActions.propPlace);
            }
            RobotLog.dd("OpenCV", "Result %s", detectPropActions.propPlace);
            telemetry.addData("Prop is on the", detectPropActions.propPlace);
            telemetry.update();
//            String pathTemplate = "/sdcard/FIRST/java/src/blackboxtemplatedownsized.jpg";
//
//            RobotLog.dd("OpenCV", "Reached 1");
//            File fileTemplate = new File(pathTemplate);
//            String absolutePathTemplate = fileTemplate.getAbsolutePath();
//            RobotLog.dd("OpenCV", "Reached 2");
//
//            String pathImg = "/sdcard/FIRST/java/src/blackboximagedownsized.jpg";
//
//            File fileImg = new File(pathImg);
//            String absolutePathImg = fileImg.getAbsolutePath();
//            RobotLog.dd("OpenCV", "file exists %b", fileTemplate.exists());
//
//            RobotLog.dd("OpenCV", "Reached 3");
//
////            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//            RobotLog.dd("OpenCV", "Reached 4");
//
//        /* In order for this to work, download OpenCV version 4.7.0 and extract the file.
//        In the OpenCV file, go into build then java then x64.
//        Copy the opencv_java470.dll and paste it into Program Files/Android/AndroidStudio/jre/bin
//        */
//            OpenCV openCv = new OpenCV();
//            RobotLog.dd("OpenCV", "Reached 5");
//
//            RobotLog.dd("OpenCV", absolutePathImg);
//            Mat img = Imgcodecs.imread(absolutePathImg, Imgcodecs.IMREAD_COLOR);
//            Mat templ = Imgcodecs.imread(absolutePathTemplate, Imgcodecs.IMREAD_COLOR);
//            RobotLog.dd("OpenCV", "Reached 6");
//
//
//            Point resultL = openCv.itemExists(img, templ);
//            RobotLog.dd("OpenCV", "Point X %f", resultL.x);
//            RobotLog.dd("OpenCV", "Point Y %f", resultL.y);

        }
    }
}