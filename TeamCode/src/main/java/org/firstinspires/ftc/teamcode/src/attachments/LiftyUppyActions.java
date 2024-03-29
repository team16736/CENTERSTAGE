package org.firstinspires.ftc.teamcode.src.attachments;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;

public class LiftyUppyActions {

    public DcMotorEx flippyTurny = null;
    public DcMotorEx liftyUppy = null;
    private Telemetry telemetry;
    public StateManager stateManager;

    public LiftyUppyActions(HardwareMap hardwareMap, StateManager stateManager, Telemetry telemetry) {
        this.stateManager = stateManager;
        this.telemetry = telemetry;
        flippyTurny = hardwareMap.get(DcMotorEx.class, ConfigConstants.FLIPPY_TURNY);
        flippyTurny.setDirection(DcMotorSimple.Direction.REVERSE);
        liftyUppy = hardwareMap.get(DcMotorEx.class, ConfigConstants.LIFTY_UPPY);
        liftyUppy.setDirection(DcMotorSimple.Direction.REVERSE);
        liftyUppy.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftyUppy.setTargetPosition(0);
        liftyUppy.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        flippyTurny.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void update() {
        if (stateManager.flippyTurnyState == stateManager.FLIPPYTURNY_UPPING && isDone()) {
            stateManager.flippyTurnyState = stateManager.FLIPPYTURNY_UP;
        } else if (stateManager.flippyTurnyState == stateManager.FLIPPYTURNY_DOWNING && isDone() && liftyUppy.getCurrentPosition() > -100) {
            stateManager.flippyTurnyState = stateManager.FLIPPYTURNY_DOWN;
        }
        if (stateManager.flippyTurnyState == stateManager.FLIPPYTURNY_DOWN || stateManager.flippyTurnyState == stateManager.FLIPPYTURNY_UP) {
            flippyTurny.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            flippyTurny.setPower(0.0);
        }
        if (downTo0 && !liftyUppy.isBusy()) {
            downTo0 = false;
            liftyUppy.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            liftyUppy.setPower(0.0);
        }
        telemetry.addData("Lifty Uppy Position", liftyUppy.getCurrentPosition());
        telemetry.addData("Flippy Turny Position", flippyTurny.getCurrentPosition());
    }

    public void flippyTurnyUp() {
        if (stateManager.flippyTurnyState != stateManager.FLIPPYTURNY_UP && stateManager.flippyTurnyState != stateManager.FLIPPYTURNY_UPPING) {
            flippyTurny.setTargetPosition(1250); //originally 800 ticks
            flippyTurny.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            flippyTurny.setPower(1.0);
            flippyTurny.setVelocity(1000.0);
            stateManager.flippyTurnyState = stateManager.FLIPPYTURNY_UPPING;
        }
    }

    public void resetLiftyUppy() {
        liftyUppy.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftyUppy.setTargetPosition(0);
        liftyUppy.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    boolean flippyTurnyDown = false;
    static int ARM_DOWN_TICKS = -120;

    public void flippyTurnyDown() {
        if ((stateManager.flippyTurnyState != stateManager.FLIPPYTURNY_DOWN && stateManager.flippyTurnyState != stateManager.FLIPPYTURNY_DOWNING) || flippyTurnyDown) {
            if (liftyUppy.getCurrentPosition() < ARM_DOWN_TICKS) {
                flippyTurny.setTargetPosition(300);
                flippyTurny.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                flippyTurny.setPower(1.0);
                //Change the speed so that it's slow at
                double velocity = 900 * (1.4 - ((liftyUppy.getCurrentPosition() + 300) / 3000));
                flippyTurny.setVelocity(velocity);
                flippyTurnyDown = true;
            } else {
                flippyTurny.setTargetPosition(0);
                flippyTurny.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                flippyTurny.setPower(1.0);
                flippyTurny.setVelocity(1000.0);
                flippyTurnyDown = false;
            }
            stateManager.flippyTurnyState = stateManager.FLIPPYTURNY_DOWNING;
        }
    }

    boolean wasResetFlippyTurnyBit = false;
    public void unmessFlippyTurny(boolean override, double power) {
        boolean resetFlippyTurnyBit = (power < 0 && override);
        if (resetFlippyTurnyBit) {
            if (!wasResetFlippyTurnyBit) {
                flippyTurny.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
            flippyTurny.setPower(power);
        }  else if (!resetFlippyTurnyBit && wasResetFlippyTurnyBit) {
            flippyTurny.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            flippyTurny.setTargetPosition(0);
            flippyTurny.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            flippyTurny.setPower(1.0);
            flippyTurny.setVelocity(1000.0);
            stateManager.flippyTurnyState = stateManager.FLIPPYTURNY_DOWN;
        }
        wasResetFlippyTurnyBit = resetFlippyTurnyBit;
    }
//       public void resetFlippyTurny(double power, boolean buttonPress) {
//            flippyTurny.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            flippyTurny.setPower(power*0.25);
//            telemetry.addData("Position", flippyTurny.getCurrentPosition());
//            //flippyTurny.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        if (buttonPress) {
 //            flippyTurny.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        }
//    }

//    public void newResetLiftyUp(double power) {
//        flippyTurny.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        flippyTurny.setPower(power);
//        flippyTurny.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//    }

    double prevTime = System.currentTimeMillis();

    public void teleOpLiftyUppy(double power, double liftSpeedMultiplier, boolean cancel) { //  controls the lifty uppy (viper slides) which is being extended and retracted
        double time = System.currentTimeMillis();
        if (power != 0 && (flippyTurny.getCurrentPosition() > 300 || cancel)) {
            if (liftyUppy.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
                liftyUppy.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                liftyUppy.setPower(1.0);
            }
//            double time = System.currentTimeMillis();

            double total = liftyUppyPosition + power * (time - prevTime) * liftSpeedMultiplier;
            if (!cancel) {
                total = Range.clip(total, -3000, 100);
            }
            setLiftyUppyPosition((int) total, 3000 * liftSpeedMultiplier);
//            prevTime = time;
            RobotLog.dd("LiftyUppy", "Target Position %f, time %f", liftyUppyPosition, time);
        }
        prevTime = time;
        telemetry.addData("target position", liftyUppyPosition);
        telemetry.addData("liftyPower", liftyUppy.getPower());
        telemetry.addData("liftyCurrent mA", liftyUppy.getCurrent(CurrentUnit.MILLIAMPS));

        double maxCurrent = 0;

        if (liftyUppy.getCurrent(CurrentUnit.MILLIAMPS) > maxCurrent) {
            maxCurrent = liftyUppy.getCurrent(CurrentUnit.MILLIAMPS);
        }

        telemetry.addData("liftyMax mA", maxCurrent);
        telemetry.addData("current position", liftyUppy.getCurrentPosition());
    }

    boolean downTo0 = false;
    int preset1 = 0;
    int preset2 = -1100;
    int preset3 = -1500;
    int preset4 = -2500;

    public void goToPreset(boolean goTo1, boolean goTo2, boolean goTo3, boolean goTo4) {
        if ((goTo1 || goTo2 || goTo3) && liftyUppy.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
            liftyUppy.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftyUppy.setPower(1.0);
        }
        if (goTo1) {
            setLiftyUppyPosition(preset1, 1800);
            downTo0 = true;
        } else if (flippyTurny.getCurrentPosition() > 300) {
            if (goTo2) {
                setLiftyUppyPosition(preset2, 2500);
            } else if (goTo3) {
                setLiftyUppyPosition(preset3, 2500);
            } else if (goTo4) {
                setLiftyUppyPosition(preset4, 2500);
            }
        }
    }

    public void turnFlippyTurnyOff() {
        flippyTurny.setPower(0);
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
