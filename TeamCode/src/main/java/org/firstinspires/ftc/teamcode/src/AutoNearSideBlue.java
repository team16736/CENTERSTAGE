package org.firstinspires.ftc.teamcode.src;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.src.attachments.DetectPropActions;
import org.firstinspires.ftc.teamcode.src.attachments.IntakeClass;
import org.firstinspires.ftc.teamcode.src.attachments.LiftyUppyActions;
import org.firstinspires.ftc.teamcode.src.attachments.PlacerActions;
import org.firstinspires.ftc.teamcode.src.attachments.StateManager;
import org.firstinspires.ftc.teamcode.src.attachments.UpTake;
import org.firstinspires.ftc.teamcode.src.driving.GyroActions;
import org.firstinspires.ftc.teamcode.src.driving.HelperActions;

@Disabled
@Autonomous(name = "AutoNearSideBlue")
/*
This the far side red, Mel
 */
public class AutoNearSideBlue extends HelperActions {
    //Create the actions as objects. This is so we can use the methods inside of them
    private GyroActions gyroActions = null;
    private DetectPropActions detectPropActions = null;
    private IntakeClass intake = null;
    private UpTake uptake = null;
    private LiftyUppyActions liftyUppyActions = null;
    private StateManager stateManager = null;

    //Initial variable declarations
    private double speed = 400;

    public void runOpMode() {
        //Done during initialization
        //Before this, the actions we created are empty. Assigns the actions to stop being nothing
        stateManager = new StateManager();
        gyroActions = new GyroActions(this, telemetry, hardwareMap);
        detectPropActions = new DetectPropActions(hardwareMap, "BlueSphereTemplate", true);
        detectPropActions.setToTemplateMatching();
        intake = new IntakeClass(stateManager, hardwareMap);
        uptake = new UpTake(stateManager, hardwareMap);
        liftyUppyActions = new LiftyUppyActions(hardwareMap, stateManager, telemetry);
        PlacerActions placer = new PlacerActions(stateManager, hardwareMap);

        //Ends initialization, waits for the player to hit the start button
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
             // First, uses detectPropActions to find the prop. Assigns it to a variable so we can use it later.
            while (detectPropActions.getResult().x == 0);
            telemetry.addData(">", "thingamajig captured");
            telemetry.update();
            detectPropActions.stopStreaming();
            if (detectPropActions.getResult().x < 157) {
                //left
                placePixelLeft(placer);
                driveToBoard(placer, -10, 5,-17, -90);
                placeAndPark(placer, 20);
            } else if (detectPropActions.getResult().x < 400) {
                //middle
                placePixelMid(placer);
                //driveToBoardMid(placer );
                // drives to the board to place pixel
                driveToBoard(placer, -10, 3,-30, -90);
                placeAndPark(placer, 25);
            } else {
                //right
                placePixelRight(placer);
                driveToBoard(placer, -10, 3,-30, -90);
                placeAndPark(placer, 32);
            }

            ///sleep(10000);

            //If statements, in case something could change in the program // returning middle for left
            //propPlace = "left";
        }
    }

    /*
    This method is for placing pixel on the middle line
     */
    private void placePixelMid(PlacerActions placer) {
        // move to the middle
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 2, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 2, false)) ;

        //Move to the prop 30 inches
        double distance = 29;
        int angle = -90;
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0)) ;

        // place pixel on the line
        intake.outTake();
        sleep(1000);
        intake.intakeOff();

        // move back 5 inches to avoid hitting the pixel/prop
        gyroActions.initEncoderGyroDriveStateMachine(speed, -4, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -4, 0)) ;

        // turn 45, prep to move backward
        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

    }

    /*
    This method under is for placing pixels on the left line
    */
    private void placePixelLeft(PlacerActions placer) {
        double distance = 22;
        int angle = -90;

        // Strafe to the left center line
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 8, true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 8, true)) ;

        // move to the prop and push it forward
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0)) ;

        // place the pixel
        intake.outTake();
        sleep(1000);
        intake.intakeOff();

        // come back 9 inches to avoid the pixel/prop
        gyroActions.initEncoderGyroDriveStateMachine(speed, -5, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -5, 0)) ;

        // turn backward
        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        // Strafe to the original lane
        //gyroActions.initEncoderGyroStrafeStateMachine(speed, 5, true);
        //while (gyroActions.encoderGyroStrafeStateMachine(speed, 5, true)) ;

    }
    /*
     method under is for placing pixels on the right line
     */
    private void placePixelRight(PlacerActions placer) {
        double distance = 30 ;
        int angle = -90;

        // Strafe away from the prop
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 7, true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 7, true)) ;

        // go forward 30  inches
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0)) ;


        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        gyroActions.initEncoderGyroDriveStateMachine(speed, 11, angle);
        while (gyroActions.encoderGyroDriveStateMachine(speed, 11, angle)) ;

        intake.outTake();
        sleep(1000);
        intake.intakeOff();

        //gyroActions.initEncoderGyroDriveStateMachine(speed, -6, angle);
        //while (gyroActions.encoderGyroDriveStateMachine(speed, -6, angle)) ;


    }

    /*
   This method is for original lane - both left and right
   distance1 = distance before Strafing
   strafeDistance = distance to line up with the board on any side
   distance2 = distance to board after strafing
    */
    private void driveToBoard(PlacerActions placer, int distance1, int strafeDistance, int distance2, int angle) {
        // go towards the back board
        gyroActions.encoderGyroDriveStateMachine(speed, distance1, angle);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance1, angle)) ;
        // strafe to align with board
        gyroActions.initEncoderGyroStrafeStateMachine(speed, strafeDistance, true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, strafeDistance, true)) ;
        //raise the arms
        liftyUppyActions.flippyTurnyUp();
        // keep going towards the back board
        gyroActions.encoderGyroDriveStateMachine(speed, distance2, angle);
        // raise the viper slides
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance2, angle)) {
            if (liftyUppyActions.flippyTurny.getCurrentPosition() > 300) {
                liftyUppyActions.setLiftyUppyPosition(-800, 2500);
            }
        }
    }

    /*
        This method is for middle lane
    */
    private void placeAndPark(PlacerActions placer, int strafeDistance) {
        placer.releasePixel();
        sleep(800);
        placer.closePlacer();

        liftyUppyActions.goToPreset(false, true, false, false);
        sleep(300);
        liftyUppyActions.flippyTurnyDown();
        sleep(200);
        liftyUppyActions.goToPreset(true, false, false, false);
        while (liftyUppyActions.liftyUppy.getCurrentPosition() > -1000);
        gyroActions.initEncoderGyroDriveStateMachine(speed, 2);
        while (gyroActions.encoderGyroDriveStateMachine(speed,2));
        //speed *= 2;
        gyroActions.initEncoderGyroStrafeStateMachine(speed,strafeDistance,false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed,strafeDistance,false));
    }
}
