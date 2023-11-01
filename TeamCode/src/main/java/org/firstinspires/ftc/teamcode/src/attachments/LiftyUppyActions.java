package org.firstinspires.ftc.teamcode.src.attachments;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;

public class LiftyUppyActions {

    private DcMotorEx flippyTurny = null;
    private HardwareMap hardwareMap;
    public enum FlippyTurnyState {
        up,
        upping,
        down,
        downing
    }
    public FlippyTurnyState flippyTurnyState = FlippyTurnyState.down;

    public LiftyUppyActions(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        flippyTurny = hardwareMap.get(DcMotorEx.class, ConfigConstants.FLIPPY_TURNY);
        flippyTurny.setDirection(DcMotorSimple.Direction.REVERSE);
        flippyTurny.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public int getTick() {
        return flippyTurny.getCurrentPosition();
    }

    public void update() {
        if (flippyTurnyState == FlippyTurnyState.upping && isDone()) {
            flippyTurnyState = FlippyTurnyState.up;
        } else if (flippyTurnyState == FlippyTurnyState.downing && isDone()) {
            flippyTurnyState = FlippyTurnyState.down;
        }
        if (flippyTurnyState == FlippyTurnyState.down) {
            flippyTurny.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            flippyTurny.setPower(0.0);
        }
    }

    public void goUp() {
        if (flippyTurnyState != FlippyTurnyState.up || flippyTurnyState != FlippyTurnyState.upping) {
            flippyTurny.setTargetPosition(800);
            flippyTurny.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            flippyTurny.setVelocity(300.0);
            flippyTurnyState = FlippyTurnyState.upping;
        }
    }

    public void goDown() {
        if (flippyTurnyState != FlippyTurnyState.down || flippyTurnyState != FlippyTurnyState.downing) {
            flippyTurny.setTargetPosition(0);
            flippyTurny.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            flippyTurny.setVelocity(300.0);
            flippyTurnyState = FlippyTurnyState.downing;
        }
    }

    public Boolean isDone() {
        return !(flippyTurny.isBusy());
    }

}
