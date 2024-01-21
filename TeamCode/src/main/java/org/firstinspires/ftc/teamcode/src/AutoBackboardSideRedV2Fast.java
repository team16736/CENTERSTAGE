package org.firstinspires.ftc.teamcode.src;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.src.attachments.DetectPropActions;
import org.firstinspires.ftc.teamcode.src.attachments.IntakeClass;
import org.firstinspires.ftc.teamcode.src.attachments.IntakeFinger;
import org.firstinspires.ftc.teamcode.src.attachments.LiftyUppyActions;
import org.firstinspires.ftc.teamcode.src.attachments.PlacerActions;
import org.firstinspires.ftc.teamcode.src.attachments.StateManager;
import org.firstinspires.ftc.teamcode.src.attachments.UpTake;
import org.firstinspires.ftc.teamcode.src.driving.GyroActions;
import org.firstinspires.ftc.teamcode.src.driving.HelperActions;


@Autonomous(name = "Auto Backboard Side Fast Red V2")
/*
This the Audience (far) side red, Mel
 */
public class AutoBackboardSideRedV2Fast extends HelperActions {
    //Create the actions as objects. This is so we can use the methods inside of them
    private GyroActions gyroActions = null;
    private DetectPropActions detectPropActions = null;
    private IntakeClass intake = null;
    private UpTake uptake = null;
    private LiftyUppyActions liftyUppyActions = null;
    private StateManager stateManager = null;
    private IntakeFinger intakeFinger = null;

    //Initial variable declarations
    private double speed = 600;

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
//            gyroActions.initEncoderGyroDriveStateMachine(speed, 8);
//            while (gyroActions.encoderGyroDriveStateMachine(speed,8, 0));

            // First, uses detectPropActions to find the prop. Assigns it to a variable so we can use it later.
            String propPlace = detectPropActions.whereProp(3);
            telemetry.addData("result", detectPropActions.getResult().x);
            telemetry.update();
            while (propPlace == "") {
                propPlace = detectPropActions.whereProp(3);
            }
            telemetry.addData("prop place", propPlace);

            ///// remove the hardcoded value /////
            //String propPlace = "right";

            //If statements, in case something could change in the program
            if (propPlace == "right") {
                //Prop is at the left side
                //places pixel on the line
                placePixelRight(placer);
                // drives to the board to place pixel
                driveToBoard(placer, -0, 0,-28, 90);
                // places pixel and parks
                placeAndPark(placer, 17);

            } else if (propPlace == "left") {
                // NEED TO WORK HERE
                placePixelLeft(placer);
                // drives to the board to place pixel
                driveToBoard(placer, -0, 3,-31, 90);
                // places pixel and parks
                placeAndPark(placer, 28);

            } else {
                //Mid is the default position, if it is not on the left or the right, the only remaining option is the middle
                placePixelMid(placer);
                // drives to the board to place pixel
                driveToBoard(placer, 0, 0, -36, 90);
                // places pixel and parks
               placeAndPark(placer, 22);
            }
        }
    }

    /*
    This method is for placing pixel on the middle line
     */
    private void placePixelMid(PlacerActions placer) {
        //Move to the prop 27.5 inches
        double distance = 28;
        int angle = 90;
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
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 3, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 3, false)) ;
    }

    /*
    This method under is for placing pixels on the left line
    */
    private void placePixelRight(PlacerActions placer) {
        double distance = 22;
        int angle = 90;

        // move 2 inches to avoid the catch
        gyroActions.encoderGyroDriveStateMachine(speed, 2, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, 2, 0)) ;

        // Strafe to the left center line
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 9, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 9, false)) ;

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
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 3, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 3, false)) ;

    }

    /*
     method under is for placing pixels on the right line
     */
    private void placePixelLeft(PlacerActions placer) {
        double distance = 27;
        int angle = 90;

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
            gyroActions.initEncoderGyroStrafeStateMachine(speed, strafeDistance, false);
            while (gyroActions.encoderGyroStrafeStateMachine(speed, strafeDistance, false)) ;
        }
        //raise the arms
        liftyUppyActions.flippyTurnyUp();
        // keep going towards the back board in slow speed
        gyroActions.encoderGyroDriveStateMachine(400, distance2, angle);
        // raise the viper slidesfs
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance2, angle)) {
            if (liftyUppyActions.flippyTurny.getCurrentPosition() > 300) {
                liftyUppyActions.setLiftyUppyPosition(-800, 2500);
            }
        }
    }

   /*
       This method is for placing the first pixel
   */
    private void placeAndPark(PlacerActions placer, int strafeDistance) {


        // Currently we're messing with code to make lifty Uppy go fully down
        // The code below is taken from TeleOp where Lifty Uppy works correctly

        placer.releasePixel();
        sleep(800);
        placer.closePlacer();

        liftyUppyActions.goToPreset(false, false, true, false);
        sleep(500);
        liftyUppyActions.goToPreset(true, false, false, false);
        liftyUppyActions.flippyTurnyDown();

        while (liftyUppyActions.liftyUppy.getCurrentPosition() > -1000);
        gyroActions.initEncoderGyroDriveStateMachine(speed, 2);
        while (gyroActions.encoderGyroDriveStateMachine(speed,2));
        //speed *= 2;
        //strafe left, towards the truss closest to the wall
        gyroActions.initEncoderGyroStrafeStateMachine(speed,27,true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed,27,true));
        sleep(200);
        liftyUppyActions.turnFlippyTurnyOff();
        gyroActions.initEncoderGyroDriveStateMachine(speed, 85);
        while (gyroActions.encoderGyroDriveStateMachine(speed,85));
        sleep(200);
        gyroActions.initEncoderGyroStrafeStateMachine(speed,23,false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed,23,false));
        sleep(200);
        gyroActions.initEncoderGyroDriveStateMachine(speed, 15);
        while (gyroActions.encoderGyroDriveStateMachine(speed,15));
        sleep(200);
        //Pick up the two white pixels. There is a momentary pause while uptaking to avoid jamming.

        sleep(500);
        intake.setPower(-1.0);
        uptake.setPower(-0.6);
        intakeFinger.RotateFinger();
        gyroActions.initEncoderGyroDriveStateMachine(speed, 2);
        while (gyroActions.encoderGyroDriveStateMachine(speed,2));
        sleep(2000);
        intake.setPower(0.0);
        uptake.setPower(0.0);
        sleep(400);
        intake.setPower(-1.0);
        uptake.setPower(-0.6);
        sleep(800);
        intake.setPower(0.0);
        uptake.setPower(0.0);
        intakeFinger.StopRotatingFinger();
        //journey back to parking spot
        //17 back
        //23 left
        //85 back
    }
}
