
package org.firstinspires.ftc.teamcode.src;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.src.attachments.HangerActions;
import org.firstinspires.ftc.teamcode.src.attachments.IntakeClass;
import org.firstinspires.ftc.teamcode.src.attachments.IntakeFinger;
import org.firstinspires.ftc.teamcode.src.attachments.LauncherActions;
import org.firstinspires.ftc.teamcode.src.attachments.PlacerActions;
import org.firstinspires.ftc.teamcode.src.attachments.StateManager;
import org.firstinspires.ftc.teamcode.src.attachments.UpTake;
import org.firstinspires.ftc.teamcode.src.driving.DriveActions;
import org.firstinspires.ftc.teamcode.src.driving.GyroActions;
import org.firstinspires.ftc.teamcode.src.driving.HelperActions;
import org.firstinspires.ftc.teamcode.src.attachments.LiftyUppyActions;

@TeleOp(name = "Tele Op", group = "Linear Opmode")
public class MainTeleOp extends HelperActions {

    private GyroActions gyroActions = null;
    private DriveActions driveActions = null;
    private LiftyUppyActions liftyUppyActions = null;
    private HangerActions hanger = null;
    private PlacerActions placer = null;
    private IntakeFinger finger = null;
    boolean correctRotation = false;
    double rotationPosition = 0;
    double rotation = 0;
    double liftSpdMult = 0.4 ;

    @Override
    public void runOpMode() {

        StateManager stateManager = new StateManager();
        gyroActions = new GyroActions(this, telemetry, hardwareMap);
        driveActions = new DriveActions(telemetry, hardwareMap);
        liftyUppyActions = new LiftyUppyActions(hardwareMap, stateManager, telemetry);
        hanger = new HangerActions(hardwareMap);
        IntakeClass intakeClass = new IntakeClass(stateManager, hardwareMap);
        UpTake upTake = new UpTake(stateManager, hardwareMap);
        placer = new PlacerActions(stateManager, hardwareMap);
        LauncherActions launcherActions = new LauncherActions(telemetry, hardwareMap);
        finger = new IntakeFinger(telemetry,hardwareMap);


        boolean placerBit = false;
        double prevTime = 0;

        //Set Speed for teleOp. Mecannum wheel speed.
        //driveActions.setSpeed(1.0);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        driveActions.setPowerMax();
        driveActions.drive(0,0,0);
        sleep(500);
        while (opModeIsActive()) {

            /** Gamepad 1 **/

            driveActions.drive(
                    (gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x)),      //joystick controlling strafe
                    (-gamepad1.left_stick_y * Math.abs(gamepad1.left_stick_y)),     //joystick controlling forward/backward
                    driveStraight(gamepad1.right_stick_x));    //joystick controlling rotation
            telemetry.addData("Left stick x", gamepad1.left_stick_x);
            telemetry.addData("left stick y", gamepad1.left_stick_y);
            telemetry.addData("right stick x", gamepad1.right_stick_x);

            telemetry.addData("Joystick", gamepad2.left_stick_y);

            changeSpeed(driveActions, gamepad1.dpad_up, gamepad1.dpad_down, false, false);
            toggleSpeed(gamepad1.a);
            if(gamepad1.x) {
                launcherActions.launch();
            }

            if (gamepad2.right_bumper) {
                hanger.releaseHanger();
                hanger.hangerUp();
            }
            hanger.hangerDown(gamepad2.left_bumper);

            hanger.hangerDirect(gamepad1.right_bumper, gamepad1.left_bumper);
            hanger.resetHanger(gamepad1.b);

//            automatedPlacing(liftyUppyActions, placer, gamepad2.right_bumper);

            liftyUppyActions.update();
            if(gamepad2.b) {
                liftyUppyActions.goToPreset(true, false, false, false);
                liftyUppyActions.flippyTurnyDown();
            } else if (gamepad2.x){
                liftyUppyActions.flippyTurnyUp();
            }

            double intakePower = (gamepad2.left_trigger - gamepad2.right_trigger);
            intakeClass.setPower(intakePower);
            upTake.setPower(intakePower * 0.8);

            liftyUppyActions.teleOpLiftyUppy(gamepad2.left_stick_y * Math.abs(gamepad2.left_stick_y), liftSpdMult);
            liftyUppyActions.goToPreset(gamepad2.dpad_down, gamepad2.dpad_left, gamepad2.dpad_right, gamepad2.dpad_up);
            if (gamepad2.a) {
                liftyUppyActions.resetLiftyUppy();
            }

//
//            if (gamepad2.y) {
//                placer.releasePixel();
//                placerBit = true;
//                prevTime = System.currentTimeMillis();
//            }
//            if (placerBit && System.currentTimeMillis() > prevTime + pixelReleaseTime) {
//                placer.closePlacer();
//                placerBit = false;
//            }
            if (gamepad2.y) {
                pixelReleaseTime = 168;
            }
//            if (gamepad2.dpad_up) {
//                pixelReleaseTime = 5000;
//            }
            releasePixel(gamepad2.y || gamepad2.dpad_up);

            telemetry.update();

//            if (gamepad1.left_trigger > 0.3) {
//
//            } else if (gamepad1.left_trigger == 0.0) {
//                finger.StopRotatingFinger();
//            }

            if (gamepad1.right_trigger > 0.3) {
                finger.TranslateFingerDown();
                finger.RotateFinger();
            } else if (gamepad1.right_trigger == 0.0) {
                finger.StopRotatingFinger();
                finger.TranslateFingerUp();
            }


        }

        telemetry.addData("[ROBOTNAME] ", "Going");
        telemetry.update();

        idle();
    }

    // Code to make it drive straight
    private double driveStraight(double rightStickX) {
//        if(Math.abs(rightStickX) > 0.01){ // Only correct position when not rotating
//            rotation = rightStickX * Math.abs(rightStickX); // Rotating voluntarily
//            correctRotation = false;
//        } else if (!correctRotation){ // If not rotating, get the position rotationally once when the turn is done
//            if (Math.abs(driveActions.leftFront.getVelocity()) < 3) {
//                correctRotation = true;
//                rotationPosition = gyroActions.getRawHeading();
//            }
//            rotation = 0;
//        } else { // Correct rotation when not turning
//            rotation = -gyroActions.getSteeringCorrection(rotationPosition, 0.02);
//        }
//        return rotation;
        return  rightStickX * Math.abs(rightStickX);
    }

    double pixelReleaseTime = 200; //Millis
    boolean prevInput = false;
    int releaseState = 0;
    double prevTime = System.currentTimeMillis();
    private void releasePixel(boolean input) {
        if (!prevInput && input) {
            releaseState = 1;
        }
        if (releaseState == 1) {
            placer.releasePixel();
            prevTime = System.currentTimeMillis();
            releaseState = 2;
        }
        if (releaseState == 2 && System.currentTimeMillis() > prevTime + pixelReleaseTime) {
            placer.closePlacer();
            releaseState = 0;
        }
        prevInput = input;
    }

}
