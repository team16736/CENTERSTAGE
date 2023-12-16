package org.firstinspires.ftc.teamcode.src;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

// Import Statements. Important if you want to use anything from a file
import org.firstinspires.ftc.teamcode.src.attachments.IntakeClass;
import org.firstinspires.ftc.teamcode.src.attachments.LiftyUppyActions;
import org.firstinspires.ftc.teamcode.src.attachments.DetectPropActions;
import org.firstinspires.ftc.teamcode.src.attachments.PlacerActions;
import org.firstinspires.ftc.teamcode.src.attachments.StateManager;
import org.firstinspires.ftc.teamcode.src.attachments.UpTake;
import org.firstinspires.ftc.teamcode.src.driving.GyroActions;
import org.firstinspires.ftc.teamcode.src.driving.HelperActions;
import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;

@Disabled
@Autonomous(name = "Just Park Blue")
public class JustParkBlue extends HelperActions {

    //Create the actions as objects. This is so we can use the methods inside of them
    private GyroActions gyroActions = null;
    private LiftyUppyActions liftyUppyActions = null;
    private PlacerActions placerActions = null;
    private StateManager stateManager = null;

    //Initial variable declarations
    private double speed = 200;

    boolean isBlue = true;
    public void runOpMode() {

        //Done during initialization
        //Before this, the actions we created are empty. Assigns the actions to stop being nothing
        stateManager = new StateManager();
        gyroActions = new GyroActions(this, telemetry, hardwareMap);
        liftyUppyActions = new LiftyUppyActions(hardwareMap, stateManager, telemetry);
        placerActions = new PlacerActions(stateManager, hardwareMap);

        //Ends initialization, waits for the player to hit the start button
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            liftyUppyActions.flippyTurnyUp();
            //Start the robot moving forwards
            gyroActions.initEncoderGyroDriveStateMachine(speed, -20, 0);
            //Because the driving uses feedback from the gyroscope, we constantly have to update the driving
            while (gyroActions.encoderGyroDriveStateMachine(speed, -20, 0)){
                liftyUppyActions.update();
                if (liftyUppyActions.flippyTurny.getCurrentPosition() > 300) {
                    liftyUppyActions.goToPreset(false, true, false, false);
                }
            }

            gyroActions.initEncoderGyroStrafeStateMachine(speed, 25, isBlue);
            while (gyroActions.encoderGyroStrafeStateMachine(speed, 25, 0, isBlue)){
                liftyUppyActions.update();
                if (liftyUppyActions.flippyTurny.getCurrentPosition() > 300) {
                    liftyUppyActions.goToPreset(false, true, false, false);
                }
            }

            gyroActions.initEncoderGyroDriveStateMachine(speed, -16, 0);
            //Because the driving uses feedback from the gyroscope, we constantly have to update the driving
            while (gyroActions.encoderGyroDriveStateMachine(speed, -16, 0)){
                liftyUppyActions.update();
                if (liftyUppyActions.flippyTurny.getCurrentPosition() > 300) {
                    liftyUppyActions.goToPreset(false, true, false, false);
                }
            }
            placerActions.releasePixel();
            sleep(400);
            placerActions.closePlacer();
            gyroActions.initEncoderGyroStrafeStateMachine(speed, 20, isBlue);
            while (gyroActions.encoderGyroStrafeStateMachine(speed, 20, 0, isBlue)){
                liftyUppyActions.update();
            }
            placerActions.releasePixel();
            sleep(800);
            placerActions.closePlacer();
            liftyUppyActions.flippyTurnyDown();
            while (liftyUppyActions.flippyTurny.getCurrentPosition() > 600);
            liftyUppyActions.goToPreset(true, false, false, false);
            while (!liftyUppyActions.isDone());
        }
    }
}