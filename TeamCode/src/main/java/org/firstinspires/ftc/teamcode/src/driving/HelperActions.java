package org.firstinspires.ftc.teamcode.src.driving;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.src.attachments.LiftyUppyActions;
import org.firstinspires.ftc.teamcode.src.attachments.PlacerActions;

public abstract class HelperActions extends LinearOpMode {
    public final double SPEED = 0.5;

    public static int LEFT = 1;
    public static int RIGHT = 2;
    public static int FORWARDS = 3;
    public static int BACKWARDS = 4;
    public static int LOW = 5;
    public static int MEDIUM = 6;
    public static int HIGH = 7;

    private int speeding = 0;
    private double speed = 0.6;

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public void changeSpeed(DriveActions driveActions, boolean upOne, boolean downOne, boolean upTwo, boolean downTwo) {
        if (upOne) {
            speeding++;
            if (speeding == 1) {
                speed = speed + 0.1;
            }
        }
        if (downOne) {
            speeding++;
            if (speeding == 1) {
                speed = speed - 0.1;
            }
        }
        if (upTwo) {
            speeding++;
            if (speeding == 1) {
                speed = speed + 0.2;
            }
        }
        if (downTwo) {
            speeding++;
            if (speeding == 1) {
                speed = speed - 0.2;
            }
        }
        if (!upOne && !downOne && !upTwo && !downTwo) {
            speeding = 0;
        }
        if (speed < 0) {
            speed = 0;
        }
        if (speed > 1.0) {
            speed = 1.0;
        }
        driveActions.setSpeed(speed);
        telemetry.addData("speed: ", speed);
    }

    double prevSpeed;
    boolean low = false;
    boolean prevToggle = false;
    double lowSpeed = 0.35;
    public void toggleSpeed(boolean toggle) {
        if (toggle && !prevToggle) {
            low = !low;
            if (low) {
                prevSpeed = speed;
                speed = lowSpeed;
            } else {
                lowSpeed = speed;
                speed = prevSpeed;
            }
        }
         prevToggle = toggle;
    }

    boolean prevAdvance = false;
    boolean running = false;
    boolean canAdvance = true;
    boolean releaseBit = false;
    double releaseStart = 0;
    int placingState = 0;
    public void automatedPlacing(LiftyUppyActions liftyUppyActions, PlacerActions placer, boolean advance) {
        if (advance && !prevAdvance && canAdvance == true) {
            running = true;
            placingState ++;
        }
        if (running) {
            if (placingState == 1) {
                liftyUppyActions.flippyTurnyUp();
                int flippyTurnyPosition = liftyUppyActions.flippyTurny.getCurrentPosition();
                if (flippyTurnyPosition > 300) {
                    liftyUppyActions.goToPreset(false, true, false, false);
                }
                if (liftyUppyActions.isDone()) {
                    canAdvance = true;
                } else {
                    canAdvance = false;
                }
            } else if (placingState == 2) {
                if (!releaseBit) {
                    placer.releasePixel();
                    releaseStart = System.currentTimeMillis();
                    releaseBit = true;
                    canAdvance = false;
                }
                if (System.currentTimeMillis() > releaseStart + 800) {
                    placer.closePlacer();
                    liftyUppyActions.flippyTurnyDown();
                    liftyUppyActions.goToPreset(true, false, false, false);
                    running = false;
                    placingState = 0;
                    releaseBit = false;
                    canAdvance = true;
                }
            }
        }
        prevAdvance = advance;
    }
}