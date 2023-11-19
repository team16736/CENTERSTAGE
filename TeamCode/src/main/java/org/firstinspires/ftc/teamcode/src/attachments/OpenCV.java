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
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);

        Imgproc.threshold(img, img, 124, 206, Imgproc.THRESH_BINARY);
        Imgproc.medianBlur(img, img, 5);
        Imgcodecs.imwrite("src/main/java/org/firstinspires/ftc/teamcode/src/attachments/data/threshold.jpg", img);


        Mat circles = new Mat();

        Imgproc.HoughCircles(img, circles, Imgproc.HOUGH_GRADIENT_ALT, 1.5, (double) img.rows()/16,
                100.0, 0.1, 10, 40);
        double[] c = new double[100];
        int cols = circles.cols();
        for (int i = 0; i < circles.cols(); i++) {
            c[3*i] = circles.get(0, i)[0];
            c[3*i+1] = circles.get(0, i)[1];
            c[3*i+2] = circles.get(0, i)[2];
        }
        Point center = new Point(Math.round(c[0]), Math.round(c[1]));
        return center;
    }
}
