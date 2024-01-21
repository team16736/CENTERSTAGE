package org.firstinspires.ftc.teamcode.src.driving;

import static android.os.SystemClock.sleep;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.src.constants.MotorConstants;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.opencv.core.Point;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GyroActions {
    public DcMotorEx motorFrontL;
    public DcMotorEx motorFrontR;
    public DcMotorEx motorBackL;
    public DcMotorEx motorBackR;
    private IMU imu = null;


    private double robotHeading = 0;
    public double headingOffset = 0;
    private double headingError = 0;
    private double targetHeading = 0;
    static final double HEADING_THRESHOLD = 1.0;    // How close must the heading get to the target before moving to next step.
    static final double P_TURN_GAIN = 0.02;     // Larger is more responsive, but also less stable
    static final double P_DRIVE_GAIN = 0.03;     // Larger is more responsive, but also less stable
    private double driveSpeed = 0;
    private double turnSpeed = 0;
    private double leftSpeed = 0;
    private double rightSpeed = 0;
    private double leftFrontSpeed = 0;
    private double rightFrontSpeed = 0;
    private double leftBackSpeed = 0;
    private double rightBackSpeed = 0;
    int leftFrontTarget = 0;
    int leftBackTarget = 0;
    int rightFrontTarget = 0;
    int rightBackTarget = 0;

    double ticksPerInch = 31;
    double ticksPerInchStrafe = 39;

    public int driveState = 0;
    int distanceError;
    double adjSpeed;
    int totalTicks;

    public int strafeState = 0;

    public double currentTargetAngle = 0;

    private static final int DESIRED_TAG_ID = 5;     // Choose the tag you want to approach or set to -1 for ANY tag.
    private VisionPortal visionPortal;               // Used to manage the video source.
    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.
    private AprilTagDetection desiredTag = null;     // Used to hold the data for a detected AprilTag

    private static LinearOpMode opModeObj;


    private Telemetry telemetry;
    private HardwareMap hardwareMap;
    private ElapsedTime runtime = new ElapsedTime();

    public GyroActions(LinearOpMode opMode, Telemetry opModeTelemetry, HardwareMap opModeHardware) {
        opModeObj = opMode;
        this.telemetry = opModeTelemetry;
        this.hardwareMap = opModeHardware;
        motorFrontL = hardwareMap.get(DcMotorEx.class, "leftFront");
        motorFrontR = hardwareMap.get(DcMotorEx.class, "rightFront");
        motorBackL = hardwareMap.get(DcMotorEx.class, "leftRear");
        motorBackR = hardwareMap.get(DcMotorEx.class, "rightRear");

        //Probably necessary
        motorFrontL.setDirection(MotorConstants.REVERSE);
        motorBackL.setDirection(MotorConstants.REVERSE);

        motorFrontR.setDirection(MotorConstants.FORWARD);
        motorBackR.setDirection(MotorConstants.FORWARD);

        // define initialization values for IMU, and then initialize it.

        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.LEFT;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        //Probably necessary
        motorFrontL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBackL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFrontR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBackR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        resetHeading();
//        initAprilTag();
//        setManualExposure(6, 250);  // Use low exposure time to reduce motion blur
    }


    public void initEncoderGyroDriveStateMachine(double speed, double distance) {
        initEncoderGyroDriveStateMachine(speed, distance, currentTargetAngle);
    }
    public void initEncoderGyroDriveStateMachine(double speed, double distance, double heading) { // For driving straight forwards/backwards
        // Set the direction to forwards/backwards
        motorFrontL.setDirection(MotorConstants.REVERSE);
        motorBackL.setDirection(MotorConstants.REVERSE);

        motorFrontR.setDirection(MotorConstants.FORWARD);
        motorBackR.setDirection(MotorConstants.FORWARD);

        adjSpeed = speed;

        // Reset the encoders
        motorFrontL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Set the distance we need to go
        totalTicks = (int) (ticksPerInch * distance);
        motorFrontL.setTargetPosition(totalTicks);
        motorFrontR.setTargetPosition(totalTicks);
        motorBackL.setTargetPosition(totalTicks);
        motorBackR.setTargetPosition(totalTicks);

        // Switch to RUN_TO_POSITION mode
        motorFrontL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFrontR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set the velocity to what we want it to be
        motorFrontL.setVelocity(speed);
        motorFrontR.setVelocity(speed);
        motorBackL.setVelocity(speed);
        motorBackR.setVelocity(speed);
        driveState = 1;
    }
    public boolean encoderGyroDriveStateMachine(double speed, double distance) {
        return encoderGyroDriveStateMachine(speed, distance, currentTargetAngle);
    }
    public boolean encoderGyroDriveStateMachine(double speed, double distance, double heading) { // For driving straight forwards/backwards
        if (driveState == 0) { // Only does this the first time
            initEncoderGyroDriveStateMachine(speed, distance, heading);
        }

        if (motorFrontL.isBusy()) { // If it's running
            if (Math.abs(motorFrontL.getCurrentPosition()) > Math.abs(totalTicks * 0.9)) { // For the last 10%, slow it down
                adjSpeed = speed * 0.5;
            }

            // Heading error is how far off we are from where we want to be, rotationally
            headingError = getSteeringCorrection(heading, adjSpeed * 0.05, adjSpeed);
            RobotLog.dd("Gyro", "Heading %f", headingError / (adjSpeed * 0.05));
            if (distance < 0) {
                headingError *= -1;
            }
            // Adjust the speed of the motors to correct the heading
            motorFrontL.setVelocity(adjSpeed - headingError);
            motorFrontR.setVelocity(adjSpeed + headingError);
            motorBackL.setVelocity(adjSpeed - headingError);
            motorBackR.setVelocity(adjSpeed + headingError);

            distanceError = motorFrontL.getTargetPosition() - motorFrontL.getCurrentPosition();
            motorFrontL.setTargetPosition(motorFrontL.getCurrentPosition() + distanceError);
            motorFrontR.setTargetPosition(motorFrontR.getCurrentPosition() + distanceError);
            motorBackL.setTargetPosition(motorBackL.getCurrentPosition() + distanceError);
            motorBackR.setTargetPosition(motorBackR.getCurrentPosition() + distanceError);
            return true;
        } else { // If it's done, reset it
            driveState = 0;
            return false;
        }
    }

    public void initEncoderGyroStrafeStateMachine (double speed, double distance, boolean strafeLeft) { // Inits the encoderGyroStrafe method
        motorFrontL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        double ticksPerInch = 33.6;
        int totalTicks = (int) (ticksPerInch * distance);
        // When going left, the target position needs to be inverted from when we go right
        if (strafeLeft) {
            motorFrontL.setTargetPosition(-totalTicks);
            motorFrontR.setTargetPosition(totalTicks);
            motorBackL.setTargetPosition(totalTicks);
            motorBackR.setTargetPosition(-totalTicks);
        } else {
            motorFrontL.setTargetPosition(totalTicks);
            motorFrontR.setTargetPosition(-totalTicks);
            motorBackL.setTargetPosition(-totalTicks);
            motorBackR.setTargetPosition(totalTicks);
        }

        // Switch to RUN_TO_POSITION mode
        motorFrontL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFrontR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motorFrontL.setVelocity(-speed);
        motorFrontR.setVelocity(-speed);
        motorBackL.setVelocity(-speed);
        motorBackR.setVelocity(-speed);
        strafeState = 1;
    }
    public boolean encoderGyroStrafeStateMachine (double speed, double distance, boolean strafeLeft) {
        return encoderGyroStrafeStateMachine(speed, distance, currentTargetAngle, strafeLeft);
    }
    public boolean encoderGyroStrafeStateMachine (double speed, double distance, double heading, boolean strafeLeft) { // Method for going right or left
        if (strafeState == 0) {
            initEncoderGyroStrafeStateMachine(speed, distance, strafeLeft);
        }

        if (motorFrontL.isBusy()) {
            headingError = getSteeringCorrection(heading, speed * 0.05, speed);
            RobotLog.dd("FindJunction", "Heading Error %f", headingError / (speed * 0.05));
//            motorFrontL.setVelocity(-speed + headingError);
//            motorFrontR.setVelocity(-speed - headingError);
//            motorBackL.setVelocity(-speed + headingError);
//            motorBackR.setVelocity(-speed - headingError);
            if (!strafeLeft) {
                headingError *= -1;
            }
            motorFrontL.setVelocity(-speed - headingError);
            motorFrontR.setVelocity(-speed - headingError);
            motorBackL.setVelocity(-speed + headingError);
            motorBackR.setVelocity(-speed + headingError);

            distanceError = Math.abs(motorFrontL.getTargetPosition() - motorFrontL.getCurrentPosition());
            if (strafeLeft){
                motorFrontL.setTargetPosition(motorFrontL.getCurrentPosition() - distanceError);
                motorFrontR.setTargetPosition(motorFrontR.getCurrentPosition() + distanceError);
                motorBackL.setTargetPosition(motorBackL.getCurrentPosition() + distanceError);
                motorBackR.setTargetPosition(motorBackR.getCurrentPosition() - distanceError);
            }else{
                motorFrontL.setTargetPosition(motorFrontL.getCurrentPosition() + distanceError);
                motorFrontR.setTargetPosition(motorFrontR.getCurrentPosition() - distanceError);
                motorBackL.setTargetPosition(motorBackL.getCurrentPosition() - distanceError);
                motorBackR.setTargetPosition(motorBackR.getCurrentPosition() + distanceError);
            }
            telemetry.addData("is busy", true);
            return true;
        } else {
            strafeState = 0;
            telemetry.addData("isn't busy", false);
            return false;
        }
    }

    boolean startBit = false;
    public void initGyroSpin(double angle) {
        // Set the direction to forwards/backwards
        motorFrontL.setDirection(MotorConstants.REVERSE);
        motorBackL.setDirection(MotorConstants.REVERSE);

        motorFrontR.setDirection(MotorConstants.FORWARD);
        motorBackR.setDirection(MotorConstants.FORWARD);

        currentTargetAngle += angle;
        while (currentTargetAngle > 180) currentTargetAngle -= 360;
        while (currentTargetAngle < -180) currentTargetAngle += 360;

        RobotLog.dd("Gyro", "Target Heading %f", currentTargetAngle);

        motorFrontL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        double ticksPerDegree = 9.7;
        totalTicks = (int) (ticksPerDegree * angle);
        motorFrontL.setTargetPosition(totalTicks);
        motorFrontR.setTargetPosition(-totalTicks);
        motorBackL.setTargetPosition(totalTicks);
        motorBackR.setTargetPosition(-totalTicks);

        motorFrontL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFrontR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        startBit = false;
    }

    double prevHeading = 0;
    public boolean gyroSpin(double speed) {
        double rampDown = 1 - Math.pow(0.97, Math.abs(headingError) + 3);
        setVelocity(speed * rampDown);
        double rawHeading = getRawHeading();
        headingError = rawHeading - currentTargetAngle;
        RobotLog.dd("Gyro", "Heading %f", rawHeading);
        if (prevHeading != rawHeading && Math.abs(prevHeading - rawHeading) < 10 && startBit) {
            double ticksPerDegree = 9.7;
            int errorInTicks = (int) (ticksPerDegree * headingError);
            motorFrontL.setTargetPosition(motorFrontL.getCurrentPosition() + errorInTicks);
            motorFrontR.setTargetPosition(motorFrontR.getCurrentPosition() - errorInTicks);
            motorBackR.setTargetPosition(motorBackR.getCurrentPosition() - errorInTicks);
            motorBackL.setTargetPosition(motorBackL.getCurrentPosition() + errorInTicks);
//        RobotLog.dd("Gyro Turn", "target pos ` %d", motorFrontL.getTargetPosition());
//        RobotLog.dd("Gyro Turn", "current pos ` %d", motorFrontL.getCurrentPosition());
//        RobotLog.dd("Gyro Turn", "current ang ` %f", getRawHeading());
//        RobotLog.dd("Gyro Turn", "target ang ` %f", currentTargetAngle);
//        RobotLog.dd("Gyro Turn", "heading err ` %f", headingError);
        }
        startBit = true;
        telemetry.update();
        prevHeading = rawHeading;
        return motorFrontL.isBusy();
    }

//    public void gyroAprilDrive(double xOffset, double yOffset, double speed) {
//
//        Point distance = getTag();
//
//        double yError = distance.y - yOffset;
//        double xError = distance.x - xOffset;
//        telemetry.addData("y", yError);
//        telemetry.update();
//
//        initEncoderGyroDriveStateMachine(speed, yError);
//
//        while (motorFrontL.isBusy()) { // If it's running
//            distance = getTag();
//
//             yError = distance.y - yOffset;
//             xError = distance.x - xOffset;
//            if (Math.abs(motorFrontL.getCurrentPosition()) > Math.abs(totalTicks * 0.9)) { // For the last 10%, slow it down
//                adjSpeed = speed * 0.5;
//            }
//
//            // Heading error is how far off we are from where we want to be, rotationally
//            headingError = getSteeringCorrection(currentTargetAngle, adjSpeed * 0.05, adjSpeed);
//            RobotLog.dd("Gyro", "Heading %f", headingError / (adjSpeed * 0.05));
//            if (yError < 0) {
//                headingError *= -1;
//            }
//            // Adjust the speed of the motors to correct the heading
//            motorFrontL.setVelocity(adjSpeed - headingError + xError);
//            motorFrontR.setVelocity(adjSpeed + headingError - xError);
//            motorBackL.setVelocity(adjSpeed - headingError - xError);
//            motorBackR.setVelocity(adjSpeed + headingError + xError);
//
//            distanceError = (int) (yError * ticksPerInch);
//            motorFrontL.setTargetPosition(motorFrontL.getCurrentPosition() + distanceError);
//            motorFrontR.setTargetPosition(motorFrontR.getCurrentPosition() + distanceError);
//            motorBackL.setTargetPosition(motorBackL.getCurrentPosition() + distanceError);
//            motorBackR.setTargetPosition(motorBackR.getCurrentPosition() + distanceError);
//
//        }
//        driveState = 0;
//    }

    public void gyroDiagonal(double speed, double inchesForward, double inchesLeft) {
        gyroDiagonal(speed, inchesForward, inchesLeft, currentTargetAngle);
    }
    public void gyroDiagonal(double speed, double inchesForward, double inchesLeft, double heading) {
        double ticksPerInchForward = 31;
        int forwardTicks = (int) (inchesForward * ticksPerInchForward);
        double ticksPerInchLeft = 33.6;
        int leftTicks = (int) (inchesLeft * ticksPerInchLeft);
        int totalTicksFL = forwardTicks - leftTicks;
        int totalTicksFR = forwardTicks + leftTicks;
        int totalTicksBL = forwardTicks + leftTicks;
        int totalTicksBR = forwardTicks - leftTicks;
        motorFrontL.setTargetPosition(totalTicksFL);
        motorFrontR.setTargetPosition(totalTicksFR);
        motorBackL.setTargetPosition(totalTicksBL);
        motorBackR.setTargetPosition(totalTicksBR);

    }
    public void setVelocity(double velocity) {
        motorFrontL.setVelocity(velocity);
        motorFrontR.setVelocity(velocity);
        motorBackR.setVelocity(velocity);
        motorBackL.setVelocity(velocity);
    }

    public double getSteeringCorrection(double desiredHeading, double proportionalGain) {
        return getSteeringCorrection(desiredHeading, proportionalGain, 1);
    }
    public double getSteeringCorrection(double desiredHeading, double proportionalGain, double range) {
        targetHeading = desiredHeading;  // Save for telemetry

        // Get the robot heading by applying an offset to the IMU heading
        robotHeading = getRawHeading();
        RobotLog.dd("Gyro", "Heading %f", robotHeading);
//        telemetry.addData("robotHeading", robotHeading);
//        telemetry.update();
//        opModeObj.sleep(3000);

        // Determine the heading current error
        headingError = targetHeading - robotHeading;

        // Normalize the error to be within +/- 180 degrees
        while (headingError > 180) headingError -= 360;
        while (headingError <= -180) headingError += 360;

        // Multiply the error by the gain to determine the required steering correction/  Limit the result to +/- 1.0
        return Range.clip(headingError * proportionalGain, -range, range);
    }

    public double getRawHeading() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        return orientation.getYaw(AngleUnit.DEGREES) - headingOffset;
    }

    public void resetHeading() {
        // Save a new heading offset equal to the current raw heading.
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        headingOffset = orientation.getYaw(AngleUnit.DEGREES);
        robotHeading = 0;
    }

    public void runUsingEncoders() {
        motorFrontL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void sendTelemetry(boolean straight) {
        if (straight) {
            telemetry.addData("Motion", "Drive Straight");
            telemetry.addData("Target Pos L:R",  "%7d:%7d",      leftFrontTarget,  rightFrontTarget);
            telemetry.addData("Actual Pos L:R",  "%7d:%7d",      motorFrontL.getCurrentPosition(),
                    motorFrontR.getCurrentPosition());
        } else {
            telemetry.addData("Motion", "Turning");
        }

        telemetry.addData("Angle Target:Current", "%5.2f:%5.0f", targetHeading, robotHeading);
        telemetry.addData("Error:Steer",  "%5.1f:%5.1f", headingError, turnSpeed);
        telemetry.addData("Wheel Speeds L:R.", "%5.2f : %5.2f", leftSpeed, rightSpeed);
        telemetry.update();
    }
//
//    private void initAprilTag() {
//        // Create the AprilTag processor by using a builder.
//        aprilTag = new AprilTagProcessor.Builder().build();
//
//        // Create the vision portal by using a builder.
//        visionPortal = new VisionPortal.Builder()
//                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
//                .addProcessor(aprilTag)
//                .build();
//
//    }
//
//    private void    setManualExposure(int exposureMS, int gain) {
//        // Wait for the camera to be open, then use the controls
//
//        if (visionPortal == null) {
//            return;
//        }
//
//        // Make sure camera is streaming before we try to set the exposure controls
//        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
//            telemetry.addData("Camera", "Waiting");
//            telemetry.update();
//            while (!opModeObj.isStopRequested() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
//                sleep(20);
//            }
//            telemetry.addData("Camera", "Ready");
//            telemetry.update();
//        }
//
//        // Set camera controls unless we are stopping.
//        if (!opModeObj.isStopRequested())
//        {
//            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
//            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
//                exposureControl.setMode(ExposureControl.Mode.Manual);
//                sleep(50);
//            }
//            exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
//            sleep(20);
//            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
//            gainControl.setGain(gain);
//            sleep(20);
//        }
//    }
//    public Point getTag() {
//        telemetry.addData("Checkpoint", 1);
//        telemetry.update();
//        boolean targetFound = false;
//        while (!targetFound) {
//            desiredTag = null;
//            telemetry.addData("Checkpoint", 2);
//            telemetry.update();
//            // Step through the list of detected tags and look for a matching tag
//            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//            for (AprilTagDetection detection : currentDetections) {
//                if ((detection.metadata != null)) {
//                    targetFound = true;
//                    desiredTag = detection;
//                    break;  // don't look any further.
//                }
//            }
//
//            telemetry.addData("Checkpoint", 3);
//            telemetry.update();
//            // Tell the driver what we see, and what to do.
//        }
//        double y = desiredTag.ftcPose.y;
//        double x = desiredTag.ftcPose.x;
//        telemetry.addData("Checkpoint", 4);
//        telemetry.update();
//        if (desiredTag.id == 1 || desiredTag.id == 4) {
//            x += 6;
//        } else if (desiredTag.id == 3 || desiredTag.id == 6) {
//            x -= 6;
//        }
//        telemetry.addData("Checkpoint", 5);
//        telemetry.update();
//        return new Point(x, y);
//    }
}
