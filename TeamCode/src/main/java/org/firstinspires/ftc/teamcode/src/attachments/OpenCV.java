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
        img = img.submat(img.rows() / 2, img.rows(), 0, img.cols());
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);

        Imgproc.threshold(img, img, 124, 206, Imgproc.THRESH_BINARY);
        Imgproc.medianBlur(img, img, 5);
        Imgcodecs.imwrite("src/main/java/org/firstinspires/ftc/teamcode/src/attachments/data/threshold.jpg", img);


        Mat circles = new Mat();

        Imgproc.HoughCircles(img, circles, Imgproc.HOUGH_GRADIENT_ALT, 1.5, (double) img.rows()/16,
                100.0, 0.3, 10, 40);

        double[] c = circles.get(0,0);
        Point center = new Point(Math.round(c[0]), Math.round(c[1]));

        return center;
    }
    public Point ROI(Mat img, boolean detectRed) {
        Mat right = new Mat();
        Mat mid = new Mat();
        Mat left = new Mat();
        right = img.submat(140, 215, 270, 319);
        mid = img.submat(135, 200, 140, 200);
        left = img.submat(140, 215, 0, 75);
        int colour;
        if (detectRed) {
            colour = 2;
        } else {
            colour = 0;
        }
        double rightColour = Core.mean(right).val[colour];
        double midColour = Core.mean(mid).val[colour];
        double leftColour = Core.mean(left).val[colour];

        Point result;
        if (rightColour < midColour && rightColour < leftColour) {
            result = new Point(310, 170);
        } else if (midColour < leftColour) {
            result = new Point(174, 154);
        } else {
            result = new Point(33, 176);
        }
        return result;
    }
}
