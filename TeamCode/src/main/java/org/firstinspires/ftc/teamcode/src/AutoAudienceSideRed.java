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


@Autonomous(name = "Auto Audience Side Red")
/*
This the Audience (far) side red, Mel
 */
public class AutoAudienceSideRed extends HelperActions {
    //Create the actions as objects. This is so we can use the methods inside of them
    private GyroActions gyroActions = null;
    private DetectPropActions detectPropActions = null;
    private IntakeClass intake = null;
    private UpTake uptake = null;
    private LiftyUppyActions liftyUppyActions = null;
    private StateManager stateManager = null;

    //Initial variable declarations
    private double speed = AutoParameters.AUDIENCE_RED_SPEED;

    public void runOpMode() {
        //Done during initialization
        //Before this, the actions we created are empty. Assigns the actions to stop being nothing
        stateManager = new StateManager();
        gyroActions = new GyroActions(this, telemetry, hardwareMap);
        detectPropActions = new DetectPropActions(hardwareMap, "RedSphereTemplate", true);
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
            String propPlace = detectPropActions.whereProp(3);
            telemetry.addData("result", detectPropActions.getResult().x);
            telemetry.update();
            while (propPlace == "") {
                propPlace = detectPropActions.whereProp(3);
            }
            telemetry.addData("prop place", propPlace);
            detectPropActions.stopStreaming();

            ///// remove the hardcoded value /////
            //String propPlace = "left";

            // Initial delay
            sleep(AutoParameters.AUDIENCE_RED_INITIAL_DELAY);

            //If statements, in case something could change in the program
            if (propPlace == "left") {
                placePixelLeft(placer);
                sleep(AutoParameters.AUDIENCE_RED_INTERMEDIATE_DELAY);
                // drives to the board to place pixel
                driveToBoard(placer, -55, true, 18, -32, 90);
                // places pixel and parks
                placeAndPark(placer, -8);

            } else if (propPlace == "right") {
                placePixelRight(placer);
                sleep(AutoParameters.AUDIENCE_RED_INTERMEDIATE_DELAY);
                // drives to the board to place pixel
                driveToBoard(placer, -65, false, 17, -27, 90);
                 // places pixel and parks
                placeAndPark(placer, 6);

            } else {
                //Mid is the default position, if it is not on the left or the right, the only remaining option is the middle
                placePixelMid(placer);
                sleep(AutoParameters.AUDIENCE_RED_INTERMEDIATE_DELAY);
                // drives to the board to place pixel
                driveToBoard(placer, -55, false, 24, -34, 90);
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
        int angle = 90;
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0)) ;

        // place pixel on the line
        intake.outTake();
        sleep(1000);
        intake.intakeOff();

        // move back 6 inches to avoid hitting the pixel/prop
        gyroActions.initEncoderGyroDriveStateMachine(speed, -6, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -6, 0)) ;


        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        // move 3 inch to center the robot, to avoid hitting the right bar
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 19, true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 19, true)) ;
    }

    private void placePixelLeft(PlacerActions placer) {
        double distance = 23;
        int angle = 90;

        // Strafe to the left center line
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 2, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 2, false)) ;


        // drive 22 inches
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0)) ;

        // spin and position the robot
        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        // position to the middle of the line
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 6, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 6, false)) ;

        // move an inch close to the line
//        gyroActions.encoderGyroDriveStateMachine(speed, 2, 90);
//        while (gyroActions.encoderGyroDriveStateMachine(speed, 2, 90)) ;

            // place the pixel
        intake.outTake();
        sleep(1000);
        intake.intakeOff();

        // Strafe to the original lane
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 22, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 22, false)) ;
    }

   

    /*
     method under is for placing pixels on the right line
     */
    private void placePixelRight(PlacerActions placer) {
        double distance = 27;
        int angle = -90;
        // go faster for right
        speed = 500;

        // go forward 28  inches
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0)) ;


        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        // go slow
        gyroActions.initEncoderGyroDriveStateMachine(400, 4, angle);
        while (gyroActions.encoderGyroDriveStateMachine(400, 4 , angle)) ;

        intake.outTake();
        sleep(1000);
        intake.intakeOff();

        gyroActions.initEncoderGyroDriveStateMachine(speed, -6, angle);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -6, angle)) ;

        gyroActions.initGyroSpin(-180);
        while (gyroActions.gyroSpin(speed)) ;

        gyroActions.initEncoderGyroStrafeStateMachine(speed, 25, true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 25, true)) ;
    }

    /*
    This method is for original lane - both left and right
    distance1 = distance before Strafing
    strafeDistance = distance to line up with the board on any side
    distance2 = distance to board after strafing
     */
    private void driveToBoard(PlacerActions placer, int distance1, boolean strafeLeft, int strafeDistance, int distance2, int angle) {
        // go towards the back board
        if (distance1 != 0) {
            gyroActions.encoderGyroDriveStateMachine(speed, distance1, angle);
            while (gyroActions.encoderGyroDriveStateMachine(speed, distance1, angle)) ;
        }
        // strafe to align with board
        if (strafeDistance != 0) {
            gyroActions.initEncoderGyroStrafeStateMachine(speed, strafeDistance, strafeLeft);
            while (gyroActions.encoderGyroStrafeStateMachine(speed, strafeDistance, strafeLeft));
        }
        //raise the arms
        liftyUppyActions.flippyTurnyUp();
        // keep going towards the back board in slow speed
        gyroActions.encoderGyroDriveStateMachine(400, distance2, angle);

        int position = -1000;
        if (AutoParameters.AUDIENCE_RED_HAS_PIXEL) {
            position = AutoParameters.PIXEL_DROP_HEIGHT_HIGH;
        } else {
            position = AutoParameters.PIXEL_DROP_HEIGHT_LOW;
        }

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

        if (AutoParameters.AUDIENCE_RED_HAS_PIXEL) {
            liftyUppyActions.goToPreset(false, false, true, false);
        } else {
            liftyUppyActions.goToPreset(false, true, false, false);
        }

        sleep(300);
//        liftyUppyActions.flippyTurnyDown();
//        sleep(200);

        placer.closePlacer();

//        liftyUppyActions.goToPreset(true, false, false, false);
//        while (liftyUppyActions.liftyUppy.getCurrentPosition() > -1000) ;
        gyroActions.initEncoderGyroDriveStateMachine(speed, 3);
        while (gyroActions.encoderGyroDriveStateMachine(speed, 3)) ;

        liftyUppyActions.goToPreset(true, false, false, false);
        while (liftyUppyActions.liftyUppy.getCurrentPosition() < -700);

        liftyUppyActions.flippyTurnyDown();
        sleep(200);

        boolean strafeLeft = true;
        int strafeDistance = 0;

        if(AutoParameters.AUDIENCE_RED_PARK_MIDDLE){
            strafeLeft = false;
            strafeDistance = AutoParameters.AUDIENCE_RED_PARK_STRAFE_DISTANCE + strafeOffset;
        }else {
            strafeDistance = AutoParameters.AUDIENCE_RED_PARK_STRAFE_DISTANCE - strafeOffset;
        }
        gyroActions.initEncoderGyroStrafeStateMachine(2*speed, strafeDistance, strafeLeft);
        while (gyroActions.encoderGyroStrafeStateMachine(2*speed, strafeDistance, strafeLeft)) ;
        while(liftyUppyActions.flippyTurny.isBusy());
    }
}
