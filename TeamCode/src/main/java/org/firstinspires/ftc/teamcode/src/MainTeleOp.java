
package org.firstinspires.ftc.teamcode.src;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.src.attachments.IntakeClass;
import org.firstinspires.ftc.teamcode.src.attachments.StateManager;
import org.firstinspires.ftc.teamcode.src.attachments.UpTake;
import org.firstinspires.ftc.teamcode.src.driving.DriveActions;
import org.firstinspires.ftc.teamcode.src.driving.GyroActions;
import org.firstinspires.ftc.teamcode.src.driving.HelperActions;

@Disabled
@TeleOp(name = "Tele Op", group = "Linear Opmode")
public class MainTeleOp extends HelperActions {

    private GyroActions gyroActions = null;
    private DriveActions driveActions = null;
    boolean correctRotation = false;
    double rotationPosition = 0;
    double rotation = 0;

    @Override
    public void runOpMode() {

        gyroActions = new GyroActions(this, telemetry, hardwareMap);
        driveActions = new DriveActions(telemetry, hardwareMap);

        //Set Speed for teleOp. Mecannum wheel speed.
        //driveActions.setSpeed(1.0);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        StateManager stateManager = new StateManager();
        IntakeClass intakeClass = new IntakeClass(stateManager, hardwareMap);
        UpTake upTake = new UpTake(stateManager, hardwareMap);
        while (opModeIsActive()) {

            /** Gamepad 1 **/

            driveActions.drive(
                    (gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x)),      //joystick controlling strafe
                    (-gamepad1.left_stick_y * Math.abs(gamepad1.left_stick_y)),     //joystick controlling forward/backward
                    driveStraight(gamepad1.right_stick_x));    //joystick controlling rotation
            telemetry.addData("Left stick x", gamepad1.left_stick_x);
            telemetry.addData("left stick y", gamepad1.left_stick_y);
            telemetry.addData("right stick x", gamepad1.right_stick_x);

            intakeClass.intakeButtons(gamepad1);
            upTake.uptakeButtons(gamepad1);

            telemetry.addData("Joystick", gamepad2.right_stick_y);

            changeSpeed(driveActions, gamepad1.dpad_up, gamepad1.dpad_down, false, false);

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
                rotationPosition = gyroActions.getRawHeading() - gyroActions.headingOffset;
            }
            rotation = 0;
        } else { // Correct rotation when not turning
            rotation = -gyroActions.getSteeringCorrection(rotationPosition, 0.02);
        }
        return rotation;
    }
}
