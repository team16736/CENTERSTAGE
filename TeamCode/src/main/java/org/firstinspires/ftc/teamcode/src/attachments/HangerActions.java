package org.firstinspires.ftc.teamcode.src.attachments;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;

public class HangerActions {
    public Servo leftRelease;
    public Servo rightRelease;
    public DcMotorEx leftPuller;
    public DcMotorEx rightPuller;

    public HangerActions(HardwareMap hardwareMap) {
        leftRelease = hardwareMap.get(Servo.class, ConfigConstants.LEFT_RELEASE);
        rightRelease = hardwareMap.get(Servo.class, ConfigConstants.RIGHT_RELEASE);
        leftPuller = hardwareMap.get(DcMotorEx.class, ConfigConstants.LEFT_PULLER);
        rightPuller = hardwareMap.get(DcMotorEx.class, ConfigConstants.RIGHT_PULLER);
        leftPuller.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightPuller.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void releaseHanger() {
        leftRelease.setPosition(0.8);
        rightRelease.setPosition(0.8);
    }
    public void hangerUp() {
        leftPuller.setTargetPosition(8000);
        rightPuller.setTargetPosition(8000);
        leftPuller.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightPuller.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftPuller.setVelocity(3000);
        rightPuller.setVelocity(3000);
    }
    boolean downing = false;
    public void hangerDown(boolean down) {
        if (down) {
            leftPuller.setTargetPosition(0);
            rightPuller.setTargetPosition(0);
            leftPuller.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightPuller.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftPuller.setVelocity(3000);
            rightPuller.setVelocity(3000);
            downing = true;
        }
        if (downing && !leftPuller.isBusy()) {
            leftPuller.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightPuller.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftPuller.setPower(0.0);
            rightPuller.setPower(0.0);
        }
    }

    public void hangerDirect(boolean up, boolean down) {
        if(up) {
            leftPuller.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightPuller.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            leftPuller.setPower(0.5);
            rightPuller.setPower(0.5);
        }
        if(down) {
            leftPuller.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightPuller.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            leftPuller.setPower(-0.5);
            rightPuller.setPower(-0.5);
        }
        leftPuller.setPower(0.0);
        rightPuller.setPower(0.0);
    }

    public void resetHanger(boolean reset) {
        if(reset) {
            leftPuller.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rightPuller.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }

}
