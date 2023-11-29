package org.firstinspires.ftc.teamcode.src.attachments;
import com.qualcomm.robotcore.util.RobotLog;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class OpenCV {
    public Point templateMatching(Mat img, Mat templ){
        //These should be able to vary
        int match_method = 1;
        Boolean use_mask = false;

        Mat mask = new Mat();
        Mat result = new Mat();
        Mat img_display = new Mat();
        img.copyTo(img_display);
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        result.create(result_rows, result_cols, CvType.CV_32FC1);
        Boolean method_accepts_mask = (Imgproc.TM_SQDIFF == match_method || match_method == Imgproc.TM_CCORR_NORMED);
        RobotLog.dd("OpenCV", "img type %d", img.type());
        if (use_mask && method_accepts_mask) {
            Imgproc.matchTemplate(img, templ, result, match_method, mask);
        } else {
            Imgproc.matchTemplate(img, templ, result, match_method);
        }
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Point matchLoc;
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }

//        Imgcodecs.imwrite(fileName, result);
        return matchLoc;
    }
    public Point houghCircles(Mat img) {
        img = img.submat((int) (img.rows() * 0.5), img.rows(), 0, img.cols());
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);

        Imgcodecs.imwrite("src/main/java/org/firstinspires/ftc/teamcode/src/attachments/data/gray.jpg", img);

        Imgproc.threshold(img, img, 142, 255, Imgproc.THRESH_BINARY);
//        Imgproc.medianBlur(img, img, 5);
        Imgcodecs.imwrite("src/main/java/org/firstinspires/ftc/teamcode/src/attachments/data/threshold.jpg", img);


        Mat circles = new Mat();

        Imgproc.HoughCircles(img, circles, Imgproc.HOUGH_GRADIENT, 1.0, (double) img.rows()/16,
                100.0, 1.0, 20, 50);

        double[] d = new double[circles.cols()];
        for (int i = 0; i < circles.cols(); i++) {
            d[i] = circles.get(0, i)[0];
        }
        double[] c = circles.get(0,0);
        Point center = new Point(0, 0);
        if (c != null) {
            center = new Point(Math.round(c[0]), Math.round(c[1]));
        }

        return center;
    }
    public Point ROI(Mat img, boolean detectRed) {
        double adjRows = (double) img.rows() / 240;
        double adjCols = (double) img.cols() / 320;
        int rowStart = 50;
        int rowEnd = 100;
        if (detectRed) {
            rowStart = 70;
            rowEnd = 130;
        }
        Mat left = img.submat(rowStart, rowEnd, 0, 55);
        Mat mid = img.submat(70, 121, 130, 181);
        Mat right = img.submat(rowStart, rowEnd, 270, 319);
        
        int colour;
        if (detectRed) {
            colour = 2;
        } else {
            colour = 0;
        }

        Mat rightGray = new Mat();
        Mat midGray = new Mat();
        Mat leftGray = new Mat();
        Imgproc.cvtColor(right, rightGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(mid, midGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(left, leftGray, Imgproc.COLOR_BGR2GRAY);
//        Imgcodecs.imwrite("/sdcard/FIRST/java/src/img.png", img);

        double rightColour = Core.mean(right).val[colour] / Core.mean(rightGray).val[0];
        double midColour = Core.mean(mid).val[colour] / Core.mean(midGray).val[0];
        double leftColour = Core.mean(left).val[colour] / Core.mean(leftGray).val[0];

        Point result;
        if (rightColour > midColour && rightColour > leftColour) {
            result = new Point(249 * adjCols, 170);
        } else if (midColour > leftColour) {
            result = new Point(174 * adjCols, 154);
        } else {
            result = new Point(33 * adjCols, 176);
        }
        return result;
    }
}
