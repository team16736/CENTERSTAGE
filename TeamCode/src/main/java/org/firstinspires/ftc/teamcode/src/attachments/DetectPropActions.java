package org.firstinspires.ftc.teamcode.src.attachments;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.io.File;
import java.util.Objects;

public class DetectPropActions {
    OpenCvWebcam webcam;
    Point result = new Point(0,0);
    Mat templ;
    Mat templL;
    Mat templR;
    OpenCV openCV;
    boolean isRed;
    public DetectPropActions(HardwareMap hardwareMap, String imageName, boolean isRed) {
        this.isRed = isRed;
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        webcam.setPipeline(new ROIPipeline());

        webcam.setMillisecondsPermissionTimeout(5000); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                webcam.startStreaming(640, 360, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {
                /*
                 * This will be called if the camera could not be opened
                 */
            }
        });
        //Image MUST be png, to have same type as input
        //Use UtilityFrameCapture to grab frame, USB-C to robot, grab VisionPortal-CameraFrameCapture- latest one
        //Crop down to size

    }

    public void setToHoughCircles() {
        webcam.setPipeline(new HoughCirclesPipeline());
    }
    public void setToTemplateMatching() {
        webcam.setPipeline(new TemplateMatchingPipeline());
        String imageName;
        if (isRed) {
            imageName = "RedThingTempl";
        } else {
            imageName = "BlueThingTempl";
        }
        String pathTemplate = "/sdcard/FIRST/java/src/" + imageName + ".png";
        File fileTemplate = new File(pathTemplate);
        String absolutePathTemplate = fileTemplate.getAbsolutePath();

            /* In order for this to work, download OpenCV version 4.7.0 and extract the file.
            In the OpenCV file, go into build then java then x64.
            Copy the opencv_java470.dll and paste it into Program Files/Android/AndroidStudio/jre/bin
             */

        this.openCV = new OpenCV();

        templ = Imgcodecs.imread(absolutePathTemplate, Imgcodecs.IMREAD_COLOR);
        templR = templ.submat(0, templ.rows(), templ.cols() / 2, templ.cols());
        templL = templ.submat(0, templ.rows(), 0, templ.cols() / 2);
        RobotLog.dd("OpenCV", "type %d", templ.type());
    }
    public void setToROI() { webcam.setPipeline(new ROIPipeline()); }

    public void startStreaming() {
        webcam.startStreaming(320, 240);
    }
    public void stopStreaming() {
        webcam.stopStreaming();
    }

    public Point getResult() {
        return result;
    }

    int wherePropState = 0;
    int priorDetectionNumber = 0;
    int rightCount = 0;
    int midCount = 0;
    int leftCount = 0;
//    double sumOfXValues = 0;
//    int numOfZeroes = 0;
    public String propPlace = "";
    public String whereProp(int iterations) {
        if (!Objects.equals(propPlace, "")) {
            resetPropPlace();
        }
        if (wherePropState < iterations) {
            if (detectionNumber != priorDetectionNumber) {
                priorDetectionNumber = detectionNumber;
                wherePropState ++;
                if (result.x < 157) {
                    leftCount++;
                } else if (result.x < 400) {
                    midCount++;
                } else {
                    rightCount++;
                }
            }
            propPlace = "";
        }
        if (!(wherePropState < iterations)){
            if (rightCount > leftCount && rightCount > midCount) {
                propPlace = "right";
            } else if (leftCount > midCount) {
                propPlace = "left";
            } else {
                propPlace = "middle";
            }
        }
        return propPlace;
    }
    public void resetPropPlace() {
        wherePropState = 0;
        priorDetectionNumber = 0;
        rightCount = 0;
        leftCount = 0;
        midCount = 0;
        propPlace = "";
        RobotLog.dd("OpenCV", "Reset");
    }

    public void setDetectionNumber(int num) {
        detectionNumber = num;
    }
    public void setResult(Point point) {
        result = point;
    }

    int detectionNumber = 0;
    class TemplateMatchingPipeline extends OpenCvPipeline {
        boolean viewportPaused;

        @Override
        public Mat processFrame(Mat input) {

            Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2XYZ);
            Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2RGB);

            Core.rotate(input, input, Core.ROTATE_180);

            input = input.submat(100, input.rows(), 0, input.cols());

            if (Core.mean(input).val[0] > 100) {
                result = openCV.templateMatchingHalfImg(input, templL, templR);

                RobotLog.dd("OpenCV", "Point X %f", result.x);
                RobotLog.dd("OpenCV", "Point Y %f", result.y);

                detectionNumber++;
            }

            /**
             * NOTE: to see how to get data from your pipeline to your OpMode as well as how
             * to change which stage of the pipeline is rendered to the viewport when it is
             * tapped, please see {@link PipelineStageSwitchingExample}
             */

            return input;
        }

        @Override
        public void onViewportTapped() {
            viewportPaused = !viewportPaused;

            if (viewportPaused) {
                webcam.pauseViewport();
            } else {
                webcam.resumeViewport();
            }
        }
    }
    class HoughCirclesPipeline extends OpenCvPipeline {
        boolean viewportPaused;

        @Override
        public Mat processFrame(Mat input) {

            Imgproc.cvtColor(input, input, 32);
            Core.rotate(input, input, Core.ROTATE_180);

            result = openCV.houghCircles(input);

            RobotLog.dd("OpenCV", "Point X %f", result.x);
            RobotLog.dd("OpenCV", "Point Y %f", result.y);

            detectionNumber ++;

            /**
             * NOTE: to see how to get data from your pipeline to your OpMode as well as how
             * to change which stage of the pipeline is rendered to the viewport when it is
             * tapped, please see {@link PipelineStageSwitchingExample}
             */

            return input;
        }

        @Override
        public void onViewportTapped() {
            viewportPaused = !viewportPaused;

            if (viewportPaused) {
                webcam.pauseViewport();
            } else {
                webcam.resumeViewport();
            }
        }
    }
    class ROIPipeline extends OpenCvPipeline {
        boolean viewportPaused;

        @Override
        public Mat processFrame(Mat input) {

            Imgproc.cvtColor(input, input, 32);
            Core.rotate(input, input, Core.ROTATE_180);

            result = openCV.ROI(input, isRed);

            RobotLog.dd("OpenCV", "Point X %f", result.x);
            RobotLog.dd("OpenCV", "Point Y %f", result.y);

            detectionNumber ++;

            /**
             * NOTE: to see how to get data from your pipeline to your OpMode as well as how
             * to change which stage of the pipeline is rendered to the viewport when it is
             * tapped, please see {@link PipelineStageSwitchingExample}
             */

            return input;
        }

        @Override
        public void onViewportTapped() {
            viewportPaused = !viewportPaused;

            if (viewportPaused) {
                webcam.pauseViewport();
            } else {
                webcam.resumeViewport();
            }
        }
    }
}
