
package org.firstinspires.ftc.teamcode.src;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.src.attachments.IntakeClass;
import org.firstinspires.ftc.teamcode.src.attachments.PlacerActions;
import org.firstinspires.ftc.teamcode.src.attachments.StateManager;
import org.firstinspires.ftc.teamcode.src.attachments.UpTake;
import org.firstinspires.ftc.teamcode.src.driving.DriveActions;
import org.firstinspires.ftc.teamcode.src.driving.GyroActions;
import org.firstinspires.ftc.teamcode.src.driving.HelperActions;
import org.firstinspires.ftc.teamcode.src.attachments.LiftyUppyActions;

@TeleOp(name = "Tele Op", group = "Linear Opmode")
public class MainTeleOp extends HelperActions {

//    private GyroActions gyroActions = null;
    private DriveActions driveActions = null;
    private LiftyUppyActions liftyUppyActions = null;
    boolean correctRotation = false;
    double rotationPosition = 0;
    double rotation = 0;
    double liftSpdMult = 0.3;

    @Override
    public void runOpMode() {

        StateManager stateManager = new StateManager();
//        gyroActions = new GyroActions(this, telemetry, hardwareMap);
        driveActions = new DriveActions(telemetry, hardwareMap);
        liftyUppyActions = new LiftyUppyActions(hardwareMap, stateManager);
        IntakeClass intakeClass = new IntakeClass(stateManager, hardwareMap);
        UpTake upTake = new UpTake(stateManager, hardwareMap);
        PlacerActions placer = new PlacerActions(stateManager, hardwareMap);

        boolean placerBit = false;
        double prevTime = 0;
        double pixelReleaseTime = 800; //Millis

        //Set Speed for teleOp. Mecannum wheel speed.
        //driveActions.setSpeed(1.0);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        while (opModeIsActive()) {

            /** Gamepad 1 **/

            driveActions.drive(
                    (gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x)),      //joystick controlling strafe
                    (-gamepad1.left_stick_y * Math.abs(gamepad1.left_stick_y)),     //joystick controlling forward/backward
                    /*driveStraight(gamepad1.right_stick_x)*/ (gamepad1.right_stick_x));    //joystick controlling rotation
            telemetry.addData("Left stick x", gamepad1.left_stick_x);
            telemetry.addData("left stick y", gamepad1.left_stick_y);
            telemetry.addData("right stick x", gamepad1.right_stick_x);

            intakeClass.intakeButtons(gamepad1);
            upTake.uptakeButtons(gamepad1);

            telemetry.addData("Joystick", gamepad2.right_stick_y);

            changeSpeed(driveActions, gamepad1.dpad_up, gamepad1.dpad_down, false, false);

            liftyUppyActions.update();
            if(gamepad2.b) {
                liftyUppyActions.flippyTurnyDown();
            } else if (gamepad2.x){
                liftyUppyActions.flippyTurnyUp();
            }

            double intakePower = (gamepad1.left_trigger + gamepad2.left_trigger - gamepad1.right_trigger - gamepad2.right_trigger);
            intakeClass.setPower(intakePower);
            upTake.setPower(intakePower);

            liftyUppyActions.teleOpLiftyUppy(gamepad2.left_stick_y * Math.abs(gamepad2.left_stick_y), liftSpdMult);
            liftyUppyActions.goToPreset(gamepad2.dpad_down, gamepad2.dpad_left || gamepad2.dpad_right, gamepad2.dpad_up);

            if (gamepad2.y && stateManager.flippyTurnyState == stateManager.FLIPPYTURNY_UP) {
                placer.releasePixel();
                placerBit = true;
                prevTime = System.currentTimeMillis();
            }
            if (placerBit && System.currentTimeMillis() > prevTime + pixelReleaseTime) {
                placer.closePlacer();
                placerBit = false;
            }

            telemetry.update();
        }

        telemetry.addData("[ROBOTNAME] ", "Going");
        telemetry.update();

        idle();
    }

    // Code to make it drive straight
    private double driveStraight(double rightStickX) {
        if(Math.abs(rightStickX) > 0.01){ // Only correct position when not rotating
            rotation = rightStickX * Math.abs(rightStickX); // Rotating voluntarily
            correctRotation = false;
        } else if (!correctRotation){ // If not rotating, get the position rotationally once when the turn is done
            if (Math.abs(driveActions.leftFront.getVelocity()) < 3) {
                correctRotation = true;
//                rotationPosition = gyroActions.getRawHeading() - gyroActions.headingOffset;
            }
            rotation = 0;
        } else { // Correct rotation when not turning
//            rotation = -gyroActions.getSteeringCorrection(rotationPosition, 0.02);
        }
        return rotation;
    }
}
