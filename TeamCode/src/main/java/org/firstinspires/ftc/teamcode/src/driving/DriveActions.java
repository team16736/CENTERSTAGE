package org.firstinspires.ftc.teamcode.src.driving;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;
import org.firstinspires.ftc.teamcode.src.constants.MotorConstants;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Make sure to have the following:
 *
 * 1. Hardware config
 * 2. Setup direction of motors
 * 3. Action method to do something (hookUpDown, drive, etc.,)
 * 4. Helper methods (stop, brake, leftTurn, rightTurn, etc.,)
 *
 * Purpose: Drive the 4 wheels
 */
public class DriveActions {

    public DcMotorEx leftFront;
    public DcMotorEx leftRear;

    public DcMotorEx rightFront;
    public DcMotorEx rightRear;

    //the amount to throttle the power of the motors
    public double THROTTLE = 1.0;

    private Telemetry telemetry;
    private HardwareMap hardwareMap;
    private ElapsedTime runtime = new ElapsedTime();

    public boolean applySensorSpeed = false;

    /**
     * Creates a mecanum motor using the 4 individual motors passed in as the arguments
     * @param opModeTelemetry : Telemetry to send messages to the Driver Control
     * @param opModeHardware : Hardware Mappings
     */
    // Constructor
    public DriveActions(Telemetry opModeTelemetry, HardwareMap opModeHardware ) {

        this.telemetry = opModeTelemetry;
        this.hardwareMap = opModeHardware;

        // 1. Hardware config
        leftFront = hardwareMap.get(DcMotorEx.class, ConfigConstants.FRONT_LEFT);
        leftRear = hardwareMap.get(DcMotorEx.class, ConfigConstants.BACK_LEFT);

        rightFront = hardwareMap.get(DcMotorEx.class, ConfigConstants.FRONT_RIGHT);
        rightRear = hardwareMap.get(DcMotorEx.class, ConfigConstants.BACK_RIGHT);

        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // 2. Set direction
        setMotorDirection_Forward();
    }

    public void setSpeed(double mySpeed){
        THROTTLE = mySpeed;
    }


    /**
     * Drive method to throttle the power
     * @param speedX - the x value of the joystick controlling strafe
     * @param speedY - the y value of the joystick controlling the forward/backward motion
     * @param rotation - the x value of the joystick controlling the rotation
     */
    public void drive(double speedX, double speedY, double rotation){

        double throttledX = speedX * THROTTLE;
        double throttledY = speedY * THROTTLE;
        double throttledRotation = rotation * THROTTLE;

        driveUsingJoyStick(throttledX, throttledY, throttledRotation);
    }

    /**
     * This function makes the mecanum motor drive using the joystick
     * @param speedX - the x value of the joystick controlling strafe
     * @param speedY - the y value of the joystick controlling the forward/backwards motion
     * @param rotation - the x value of the joystick controlling the rotation
     */
    public void driveUsingJoyStick(double speedX, double speedY, double rotation) {

        double frontLeft = speedX + speedY + rotation;
        double frontRight = -speedX + speedY - rotation;

        double backLeft = -speedX + speedY + rotation;
        double backRight = speedX + speedY - rotation;

//        double fl = speedX + speedY + rotation;
//        double fr = -speedX + speedY - rotation;
//        double bl= -speedX + speedY + rotation;
//        double br = speedX + speedY - rotation;

        double max = getMaxPower(frontLeft, frontRight, backLeft, backRight);
        if (max > 1) {
            frontLeft = frontLeft / max;
            frontRight = frontRight / max;
            backLeft = backLeft / max;
            backRight = backRight / max;
        }

        rightFront.setPower(frontRight);
        leftFront.setPower(frontLeft);
        rightRear.setPower(backRight);
        leftRear.setPower(backLeft);
    }

    private double getMaxPower(double frontLeftValue, double frontRightValue, double backLeftValue, double backRightValue) {
        List<Double> valueList = new LinkedList<>();
        valueList.add(frontLeftValue);
        valueList.add(frontRightValue);
        valueList.add(backLeftValue);
        valueList.add(backRightValue);

        return Collections.max(valueList);
    }

    //This methods is meant for AUTONOMOUS
    public void setMotorDirection_Forward() {
        leftFront.setDirection(MotorConstants.REVERSE);
        leftRear.setDirection(MotorConstants.REVERSE);

        rightFront.setDirection(MotorConstants.FORWARD);
        rightRear.setDirection(MotorConstants.FORWARD);
    }
}