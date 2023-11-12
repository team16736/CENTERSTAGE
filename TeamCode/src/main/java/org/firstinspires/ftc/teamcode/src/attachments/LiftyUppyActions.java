package org.firstinspires.ftc.teamcode.src.attachments;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

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
        liftyUppy.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftyUppy.setTargetPosition(0);
        liftyUppy.setMode(DcMotor.RunMode.RUN_TO_POSITION);
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
        if (stateManager.flippyTurnyState == stateManager.FLIPPYTURNY_DOWN || stateManager.flippyTurnyState == stateManager.FLIPPYTURNY_UP) {
            flippyTurny.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            flippyTurny.setPower(0.0);
        }
    }

    public void flippyTurnyUp() {
        if (stateManager.flippyTurnyState != stateManager.FLIPPYTURNY_UP || stateManager.flippyTurnyState != stateManager.FLIPPYTURNY_UPPING) {
            flippyTurny.setTargetPosition(1250); //originally 800 ticks
            flippyTurny.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            flippyTurny.setVelocity(600.0);
            stateManager.flippyTurnyState = stateManager.FLIPPYTURNY_UPPING;
        }
    }

    static int ARM_DOWN_TICKS = -120;
    public void flippyTurnyDown() {
        if ((stateManager.flippyTurnyState != stateManager.FLIPPYTURNY_DOWN || stateManager.flippyTurnyState != stateManager.FLIPPYTURNY_DOWNING) && liftyUppyPosition > ARM_DOWN_TICKS) {
            flippyTurny.setTargetPosition(0);
            flippyTurny.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            flippyTurny.setVelocity(600.0);
            stateManager.flippyTurnyState = stateManager.FLIPPYTURNY_DOWNING;
        }
    }

    public void setLiftyUppyPower(double power) {
        if (liftyUppy.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) {
            liftyUppy.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        if (power < 0) {
            stateManager.liftyUppyState = stateManager.LIFTYUPPY_DOWNING;
        } else if (power > 0) {
            stateManager.liftyUppyState = stateManager.LIFTYUPPY_UPPING;
        } else {
            stateManager.liftyUppyState = stateManager.LIFTYUPPY_STOPPED;
        }
        liftyUppy.setPower(power);
    }


    double prevTime = System.currentTimeMillis();
    public void teleOpLiftyUppy(double power, double liftSpeedMultiplier) {
        if (power != 0 && stateManager.flippyTurnyState == stateManager.FLIPPYTURNY_UP) {
            double time = System.currentTimeMillis();
            liftyUppyPosition = Range.clip(liftyUppyPosition + power * (time - prevTime) * liftSpeedMultiplier, -3000, 0);
            setLiftyUppyPosition((int) liftyUppyPosition, 3000 * liftSpeedMultiplier);
            prevTime = time;
            RobotLog.dd("LiftyUppy", "Target Position %f, time %f", liftyUppyPosition, time);
        }
    }

    int preset1 = 0;
    int preset2 = -1900;
    int preset3 = -2900;
    public void goToPreset(boolean goTo1, boolean goTo2, boolean goTo3) {
        if (goTo1) {
            setLiftyUppyPosition(preset1, 1200);
        } else if (stateManager.flippyTurnyState == stateManager.FLIPPYTURNY_UP) {
            if (goTo2) {
                setLiftyUppyPosition(preset2, 2500);
            } else if (goTo3) {
                setLiftyUppyPosition(preset3, 2500);
            }
        }
    }

    double liftyUppyPosition = 0;
    public void setLiftyUppyPosition(int position, double velocity) {
        liftyUppy.setTargetPosition(position);
        liftyUppy.setVelocity(velocity);
        liftyUppyPosition = position;
    }

    public Boolean isDone() {
        return !(flippyTurny.isBusy());
    }

}
