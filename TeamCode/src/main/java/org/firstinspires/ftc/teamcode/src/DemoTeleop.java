
package org.firstinspires.ftc.teamcode.src;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.State;
import org.firstinspires.ftc.teamcode.src.attachments.IntakeClass;
import org.firstinspires.ftc.teamcode.src.attachments.StateManager;
import org.firstinspires.ftc.teamcode.src.attachments.UpTake;
import org.firstinspires.ftc.teamcode.src.driving.DriveActions;
import org.firstinspires.ftc.teamcode.src.driving.GyroActions;
import org.firstinspires.ftc.teamcode.src.driving.HelperActions;


@TeleOp(name = "Debug TeleOp", group = "Linear Opmode")
public class DemoTeleop extends HelperActions {



    boolean correctRotation = false;
    double rotationPosition = 0;
    double rotation = 0;

    @Override
    public void runOpMode() {


        //Set Speed for teleOp. Mecannum wheel speed.
        //driveActions.setSpeed(1.0);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        StateManager stateManager = new StateManager();
        IntakeClass intakeClass = new IntakeClass(stateManager, hardwareMap);
        UpTake upTake = new UpTake(stateManager, hardwareMap);
        while (opModeIsActive()) {

            /** Gamepad 1 **/


            telemetry.addData("Left stick x", gamepad1.left_stick_x);
            telemetry.addData("left stick y", gamepad1.left_stick_y);
            telemetry.addData("right stick x", gamepad1.right_stick_x);
            intakeClass.intakeButtons(gamepad1);
            upTake.uptakeButtons(gamepad1);


            telemetry.update();

            telemetry.addData("[ROBOTNAME] ", "Going");
            telemetry.update();

            idle();
        }

    }
    }

