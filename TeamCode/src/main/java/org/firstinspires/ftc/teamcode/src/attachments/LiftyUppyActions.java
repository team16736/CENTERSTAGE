package org.firstinspires.ftc.teamcode.src.attachments;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;

public class LiftyUppyActions {

    private DcMotorEx flippyTurny = null;
    private DcMotorEx liftyUppy = null;
    private HardwareMap hardwareMap;
    public StateManager stateManager;

    public LiftyUppyActions(HardwareMap hardwareMap, StateManager stateManager) {
        this.stateManager = stateManager;
        this.hardwareMap = hardwareMap;
        flippyTurny = hardwareMap.get(DcMotorEx.class, ConfigConstants.FLIPPY_TURNY);
        flippyTurny.setDirection(DcMotorSimple.Direction.REVERSE);
        liftyUppy = hardwareMap.get(DcMotorEx.class, ConfigConstants.LIFTY_UPPY);
        liftyUppy.setDirection(DcMotorSimple.Direction.REVERSE);
        flippyTurny.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public int getTick() {
        return flippyTurny.getCurrentPosition();
    }

    public void update() {
        if (stateManager.flippyTurnyState == stateManager.FLIPPYTURNY_UPPING && isDone()) {
            stateManager.flippyTurnyState = stateManager.FLIPPYTURNY_UP;
        } else if (stateManager.flippyTurnyState == stateManager.FLIPPYTURNY_DOWNING && isDone()) {
            stateManager.flippyTurnyState = stateManager.FLIPPYTURNY_DOWN;
        }
        if (stateManager.flippyTurnyState == stateManager.FLIPPYTURNY_DOWN) {
            flippyTurny.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            flippyTurny.setPower(0.0);
        }
    }

    public void flippyTurnyUp() {
        if (stateManager.flippyTurnyState != stateManager.FLIPPYTURNY_UP || stateManager.flippyTurnyState != stateManager.FLIPPYTURNY_UPPING) {
            flippyTurny.setTargetPosition(800);
            flippyTurny.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            flippyTurny.setVelocity(300.0);
            stateManager.flippyTurnyState = stateManager.FLIPPYTURNY_UPPING;
        }
    }

    public void flippyTurnyDown() {
        if (stateManager.flippyTurnyState != stateManager.FLIPPYTURNY_DOWN || stateManager.flippyTurnyState != stateManager.FLIPPYTURNY_DOWNING) {
            flippyTurny.setTargetPosition(0);
            flippyTurny.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            flippyTurny.setVelocity(300.0);
            stateManager.flippyTurnyState = stateManager.FLIPPYTURNY_DOWNING;
        }
    }

    public void setLiftyUppyPower(double power) {
        if (power < 0) {
            stateManager.liftyUppyState = stateManager.LIFTYUPPY_DOWNING;
        } else if (power > 0) {
            stateManager.liftyUppyState = stateManager.LIFTYUPPY_UPPING;
        } else {
            stateManager.liftyUppyState = stateManager.LIFTYUPPY_STOPPED;
        }
        liftyUppy.setPower(power);
    }

    public Boolean isDone() {
        return !(flippyTurny.isBusy());
    }

}
