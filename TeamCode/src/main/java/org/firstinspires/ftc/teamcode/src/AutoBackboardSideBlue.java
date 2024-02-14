package org.firstinspires.ftc.teamcode.src;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.src.attachments.DetectPropActions;
import org.firstinspires.ftc.teamcode.src.attachments.IntakeClass;
import org.firstinspires.ftc.teamcode.src.attachments.LiftyUppyActions;
import org.firstinspires.ftc.teamcode.src.attachments.PlacerActions;
import org.firstinspires.ftc.teamcode.src.attachments.StateManager;
import org.firstinspires.ftc.teamcode.src.attachments.UpTake;
import org.firstinspires.ftc.teamcode.src.driving.GyroActions;
import org.firstinspires.ftc.teamcode.src.driving.HelperActions;


@Autonomous(name = "Auto Backboard Side Blue")
/*
This the Audience (far) side blue, Mel
 */
public class AutoBackboardSideBlue extends HelperActions {
    //Create the actions as objects. This is so we can use the methods inside of them
    private GyroActions gyroActions = null;
    private DetectPropActions detectPropActions = null;
    private IntakeClass intake = null;
    private UpTake uptake = null;
    private LiftyUppyActions liftyUppyActions = null;
    private StateManager stateManager = null;

    //Initial variable declarations
    private double speed = AutoParameters.BOARDSIDE_BLUE_SPEED;

    public void runOpMode() {
        //Done during initialization
        //Before this, the actions we created are empty. Assigns the actions to stop being nothing
        stateManager = new StateManager();
        gyroActions = new GyroActions(this, telemetry, hardwareMap);
        detectPropActions = new DetectPropActions(hardwareMap, "RedSphereTemplate", false);
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
            String propPlace = detectPropActions.whereProp(3);
            while (detectPropActions.getResult().x == 0);
            detectPropActions.stopStreaming();
            if (detectPropActions.getResult().x < 240) {
                propPlace = "left";
            } else if (detectPropActions.getResult().x < 483) {
                propPlace = "middle";
            } else {
                propPlace = "right";
            }
            detectPropActions.stopStreaming();
            telemetry.addData("prop place", propPlace);

            ///// remove the hardcoded value /////
           //propPlace = "middle";

            // Initial delay
            sleep(AutoParameters.BOARDSIDE_BLUE_INITIAL_DELAY);

            //If statements, in case something could change in the program
            if (propPlace == "left") {
                placePixelLeft(placer);
                // drives to the board to place pixel
                driveToBoard(placer, -0, 0,-29, -90);
                // places pixel and parks
                placeAndPark(placer, -6);

            } else if (propPlace == "right") {
                placePixelRight(placer);
                // drives to the board to place pixel
                driveToBoard(placer, -0, 4,-32, -90);
                // places pixel and parks
                placeAndPark(placer, 6);

            } else {
                //Mid is the default position, if it is not on the left or the right, the only remaining option is the middle
                placePixelMid(placer);
                // drives to the board to place pixel
                driveToBoard(placer, 0, 0, -36, -90);
                // places pixel and parks
                placeAndPark(placer, 0);
            }
        }
    }

    /*
    This method is for placing pixel on the middle line
     */
    private void placePixelMid(PlacerActions placer) {
        //Move to the prop 28 inches
        double distance = 28;
        int angle = -90;
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0));

        // place pixel on the line
        intake.outTake();
        sleep(1000);
        intake.intakeOff();

        // move back 5 inches to avoid hitting the pixel/prop
        gyroActions.initEncoderGyroDriveStateMachine(speed, -6, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -6, 0)) ;

        // turn 45, prep to move backward
        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        // move 3 inch to center the robot, to avoid hitting the right bar
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 4, true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 4, true)) ;
    }

    /*
    This method under is for placing pixels on the left line
    */
    private void placePixelLeft(PlacerActions placer) {
        double distance = 22;
        int angle = -90;

        // move 2 inches to avoid the catch
        gyroActions.encoderGyroDriveStateMachine(speed, 2, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, 2, 0)) ;

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

        // come back 6 inches to avoid the pixel/prop
        gyroActions.initEncoderGyroDriveStateMachine(speed, -7, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -7, 0)) ;

        // turn backward
        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        // Strafe to the original lane
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 2, true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 2, true)) ;

    }

    /*
     method under is for placing pixels on the right line
     */
    private void placePixelRight(PlacerActions placer) {
        double distance = 29;
        int angle = -90;

        // go forward 28  inches
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0)) ;


        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        // go slow
        gyroActions.initEncoderGyroDriveStateMachine(400, 3, angle);
        while (gyroActions.encoderGyroDriveStateMachine(400, 3, angle)) ;

        intake.outTake();
        sleep(1000);
        intake.intakeOff();

        gyroActions.initEncoderGyroDriveStateMachine(speed, -6, angle);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -6, angle)) ;
    }

    /*
    This method is for original lane - both left and right
    distance1 = distance before Strafing
    strafeDistance = distance to line up with the board on any side
    distance2 = distance to board after strafing
     */
    private void driveToBoard(PlacerActions placer, int distance1, int strafeDistance, int distance2, int angle) {
        // go towards the back board
        if(distance1 != 0) {
            gyroActions.encoderGyroDriveStateMachine(speed, distance1, angle);
            while (gyroActions.encoderGyroDriveStateMachine(speed, distance1, angle)) ;
        }
        // strafe to align with board
        if (strafeDistance != 0) {
            gyroActions.initEncoderGyroStrafeStateMachine(speed, strafeDistance, true);
            while (gyroActions.encoderGyroStrafeStateMachine(speed, strafeDistance, true)) ;
        }

        int position = -1000;
        if(AutoParameters.BOARDSIDE_BLUE_HAS_PIXEL){
            position = AutoParameters.PIXEL_DROP_HEIGHT_HIGH;
        }else{
            position = AutoParameters.PIXEL_DROP_HEIGHT_LOW;
        }

        //raise the arms
        liftyUppyActions.flippyTurnyUp();
        // keep going towards the back board in slow speed
        gyroActions.encoderGyroDriveStateMachine(400, distance2, angle);
        // raise the viper slides
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance2, angle)) {
            if (liftyUppyActions.flippyTurny.getCurrentPosition() > 300) {
                liftyUppyActions.setLiftyUppyPosition(position, 2500);
            }
        }
    }

   /*
       This method is for placing the first pixel
   */
    private void placeAndPark(PlacerActions placer, int strafeOffset) {
        placer.releasePixel();
        sleep(1000);
        placer.closePlacer();

        if (AutoParameters.BOARDSIDE_BLUE_HAS_PIXEL) {
            liftyUppyActions.goToPreset(false, false, true, false);
        } else {
            liftyUppyActions.goToPreset(false, true, false, false);
        }

        liftyUppyActions.goToPreset(false, false, true, false);
        sleep(300);
        liftyUppyActions.flippyTurnyDown();
        sleep(200);
        liftyUppyActions.goToPreset(true, false, false, false);
        while (liftyUppyActions.liftyUppy.getCurrentPosition() > -1000);
        gyroActions.initEncoderGyroDriveStateMachine(speed, 2);
        while (gyroActions.encoderGyroDriveStateMachine(speed,2));

        boolean strafeLeft = false;
        int strafeDistance = 0;

        if(AutoParameters.BOARDSIDE_BLUE_PARK_MIDDLE){
            strafeDistance = AutoParameters.BOARDSIDE_BLUE_PARK_STRAFE_DISTANCE - strafeOffset;
            strafeLeft = true;
        }else {
            strafeDistance = AutoParameters.BOARDSIDE_BLUE_PARK_STRAFE_DISTANCE + strafeOffset;
        }

        gyroActions.initEncoderGyroStrafeStateMachine(2*speed,strafeDistance,strafeLeft);
        while (gyroActions.encoderGyroStrafeStateMachine(2*speed,strafeDistance,strafeLeft));
        while(liftyUppyActions.flippyTurny.isBusy());
    }
}
