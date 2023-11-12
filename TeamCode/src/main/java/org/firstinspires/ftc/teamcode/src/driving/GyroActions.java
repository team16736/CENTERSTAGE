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
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.src.constants.MotorConstants;

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

    public void initGyroSpin(double angle) {
        // Set the direction to forwards/backwards
        motorFrontL.setDirection(MotorConstants.REVERSE);
        motorBackL.setDirection(MotorConstants.REVERSE);

        motorFrontR.setDirection(MotorConstants.FORWARD);
        motorBackR.setDirection(MotorConstants.FORWARD);

        currentTargetAngle += angle;
        while (currentTargetAngle > 180) currentTargetAngle -= 360;
        while (currentTargetAngle < -180) currentTargetAngle += 360;
        motorFrontL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        double ticksPerDegree = 5.393;
        totalTicks = (int) (ticksPerDegree * angle);
        motorFrontL.setTargetPosition(totalTicks);
        motorFrontR.setTargetPosition(-totalTicks);
        motorBackL.setTargetPosition(totalTicks);
        motorBackR.setTargetPosition(-totalTicks);

        motorFrontL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFrontR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }


    public boolean gyroSpin(double speed) {
        double rampDown = 1 - Math.pow(0.97, Math.abs(headingError) + 3);
        setVelocity(speed * rampDown);
        headingError = getRawHeading() - currentTargetAngle;
        double ticksPerDegree = 5.393;
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
        telemetry.update();
        return motorFrontL.isBusy();
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
        robotHeading = getRawHeading() - headingOffset;
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
        return orientation.getYaw(AngleUnit.DEGREES);
    }

    public void resetHeading() {
        // Save a new heading offset equal to the current raw heading.
        headingOffset = getRawHeading();
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
}
