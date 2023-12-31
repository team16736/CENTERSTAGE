package org.firstinspires.ftc.teamcode.src.attachments;
import com.qualcomm.robotcore.util.RobotLog;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class OpenCV {

    public Point templateMatchingHalfImg(Mat img, Mat templ) {
        Mat templRight = templ.submat(0, templ.rows(), templ.cols() / 2, templ.cols());
        Mat templLeft = templ.submat(0, templ.rows(), 0, templ.cols() / 2);
        return templateMatchingHalfImg(img, templLeft, templRight);
    }
    public Point templateMatchingHalfImg(Mat img, Mat templL, Mat templR) {
        Point left = templateMatching(img, templL);
        Point right = templateMatching(img, templR);
        if (left.y > right.y) {
            return left;
        } else {
            return right;
        }
    }
    public Point templateMatching(Mat img, Mat templ){
        Imgcodecs.imwrite("/sdcard/FIRST/java/src/RedThing0.png", img);
        //These should be able to vary
        int match_method = Imgproc.TM_SQDIFF;
        Boolean use_mask = false;

        Mat result = new Mat();
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        result.create(result_rows, result_cols, CvType.CV_32FC1);
        Boolean method_accepts_mask = (Imgproc.TM_SQDIFF == match_method || match_method == Imgproc.TM_CCORR_NORMED);
//        RobotLog.dd("OpenCV", "img type %d", img.type());
        Imgproc.matchTemplate(img, templ, result, match_method);

        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Point matchLoc;
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        result.release();
        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }
//        Point output = new Point(matchLoc.x, result.get((int) matchLoc.x, (int) matchLoc.y)[0]);
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

        Imgproc.HoughCircles(img, circles, Imgproc.HOUGH_GRADIENT_ALT, 1.5, (double) img.rows()/16,
                100.0, 1.0, 40, 70);

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
        int rowStart = 100;
        int rowEnd = 300;
        Mat left = img.submat(rowStart, rowEnd, 30, 175);
        Mat mid = img.submat(rowStart, 250, 300, 400);
        Mat right = img.submat(rowStart, rowEnd, 500, 639);

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
        Imgcodecs.imwrite("/sdcard/FIRST/java/src/RedThing0.png", img);

        double rightColour = Core.mean(right).val[colour] / Core.mean(rightGray).val[0];
        double midColour = Core.mean(mid).val[colour] / Core.mean(midGray).val[0];
        double leftColour = Core.mean(left).val[colour] / Core.mean(leftGray).val[0];
        Imgproc.rectangle(img, new Point(30, rowStart), new Point(175, rowEnd), new Scalar(200, 0, 0));
        Imgproc.rectangle(img, new Point(300, rowStart), new Point(400, 250), new Scalar(0, 200, 0));
        Imgproc.rectangle(img, new Point(500, rowStart), new Point(639, rowEnd), new Scalar(0, 0, 200));
        Imgcodecs.imwrite("src/main/java/org/firstinspires/ftc/teamcode/src/attachments/data/rects.jpg", img);

        Point result = null;
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
