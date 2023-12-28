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

@Autonomous(name = "Auto Audience Side Blue")
/*
This the far side blue, Mel
 */
public class AutoAudienceSideBlue extends HelperActions {
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
        detectPropActions = new DetectPropActions(hardwareMap, "BlueSphereTemplate", false);
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
            gyroActions.initEncoderGyroStrafeStateMachine(speed, 2, true);
            while (gyroActions.encoderGyroStrafeStateMachine(speed, 2, true)) ;


            // First, uses detectPropActions to find the prop. Assigns it to a variable so we can use it later.
            String propPlace = detectPropActions.whereProp(3);
            telemetry.addData("result", detectPropActions.getResult().x);
            telemetry.update();
            while (propPlace == "") {
                propPlace = detectPropActions.whereProp(3);
            }
            detectPropActions.stopStreaming();
            telemetry.addData("prop place", propPlace);


            //Start the robot moving forwards - 20 inches irrespective of the placer location
            //gyroActions.initEncoderGyroDriveStateMachine(speed, 20, 0);
            //Because the driving uses feedback from the gyroscope, we constantly have to update the driving
            //while (gyroActions.encoderGyroDriveStateMachine(speed, 20, 0)) ;

            ///// remove the hardcoded value /////
            //String propPlace = "left";
            //If statements, in case something could change in the program
            if (propPlace == "right") {
                //Prop is at the left side
                placePixelRight(placer);
                // drives to the board to place pixel
                //driveToBoard(placer, -60, 30,-37, 90);
                // places pixel and parks
                placeAndPark(placer);
            } else if (propPlace == "left") {
                placePixelLeft(placer);
                // drives to the board to place pixel
                //driveToBoard(placer, -52, 20,-37, 90);
                // places pixel and parks
                //placeAndPark(placer);
            } else {
                //Mid is the default position, if it is not on the left or the right, the only remaining option is the middle
                placePixelMid(placer);
                //driveToBoardMid(placer);
                // places pixel and parks
                //placeAndPark(placer);
            }
        }
    }

    private void placePixelMid(PlacerActions placer) {
        //Move to the prop 30 inches
        double distance = 30;
        int angle = -90;
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0)) ;

        // place pixel on the line
        intake.outTake();
        sleep(1000);
        intake.intakeOff();

        // move back 5 inches to avoid hitting the pixel/prop
        gyroActions.initEncoderGyroDriveStateMachine(speed, -5, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -5, 0)) ;

        // turn 45, prep to move backward
        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        // move 1 inch to center the robot, to avoid hitting the right bar
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 1, true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 1, true)) ;
    }

    private void driveToBoardMid(PlacerActions placer) {
        int angle = 90;
        // go past the middle bar
        gyroActions.initEncoderGyroDriveStateMachine(speed, -44, angle);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -44, angle)) ;

        // lift the pixel arm
        liftyUppyActions.flippyTurnyUp();

        // move to the board
        gyroActions.initEncoderGyroDriveStateMachine(speed, -44, angle);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -44, angle)) {
            if (liftyUppyActions.flippyTurny.getCurrentPosition() > 300) {
                liftyUppyActions.setLiftyUppyPosition(-800, 2500);
            }
        }
    }

    private void placePixelRight(PlacerActions placer) {
        double distance = 22;
        int angle = -90;

        // Strafe to the left center line
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 11.5, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 11.5, false)) ;

        // move to the prop and push it forward
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0)) ;

        // place the pixel
        intake.outTake();
        sleep(1000);
        intake.intakeOff();

        // come back 5 inches to avoid the pixel/prop
        gyroActions.initEncoderGyroDriveStateMachine(speed, -15, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -15, 0)) ;

        // turn backward
        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        // Strafe to the original lane
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 4, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 4, false)) ;

    }

    private void placePixelLeft(PlacerActions placer) {
        double distance = 28;
        int angle = 90;

        // Strafe away from the prop
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 5, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 5, false)) ;

        // go forward 28  inches
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0)) ;


        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        gyroActions.initEncoderGyroDriveStateMachine(speed, 6, angle);
        while (gyroActions.encoderGyroDriveStateMachine(speed, 6, angle)) ;

        intake.outTake();
        sleep(1000);
        intake.intakeOff();


        gyroActions.initEncoderGyroDriveStateMachine(speed, -3, angle);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -3, angle)) ;

        //gyroActions.initEncoderGyroStrafeStateMachine(speed, 26, true);
        //while (gyroActions.encoderGyroStrafeStateMachine(speed, 28 , true));
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
        gyroActions.initEncoderGyroStrafeStateMachine(speed, strafeDistance, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, strafeDistance, false)) ;
        //raise the arms
        liftyUppyActions.flippyTurnyUp();
        // keep going towards the back board
        gyroActions.encoderGyroDriveStateMachine(speed, distance2, angle);
        // raise the viper slides
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance2, angle)) {
            if (liftyUppyActions.flippyTurny.getCurrentPosition() > 300) {
                liftyUppyActions.setLiftyUppyPosition(-800, 2500);
//              liftyUppyActions.setLiftyUppyPosition(-1200, 2500);
            }
        }
    }

    private void placeAndPark(PlacerActions placer) {
        // release pixel
        placer.releasePixel();
        sleep(700);
        placer.closePlacer();

        //liftyUppyActions.goToPreset(false, true, false, false);
        liftyUppyActions.goToPreset(false, true, false, false);
        sleep(300);
        liftyUppyActions.flippyTurnyDown();
        sleep(200);
        liftyUppyActions.goToPreset(true, false, false, false);

        // go 3 inches away from the board
        gyroActions.encoderGyroDriveStateMachine(speed, 3);
        while (gyroActions.encoderGyroDriveStateMachine(speed, 3)) ;

        // move away from the board towards the wall
        gyroActions.encoderGyroStrafeStateMachine(speed, 21, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 21, false)) ;
    }

}
