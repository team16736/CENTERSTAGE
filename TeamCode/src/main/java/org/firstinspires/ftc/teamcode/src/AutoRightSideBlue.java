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
@Autonomous(name = "AutoRightSideBlue")
/*
This the far side blue, Mel
 */
public class AutoRightSideBlue extends HelperActions {
    //Create the actions as objects. This is so we can use the methods inside of them
    private GyroActions gyroActions = null;
    private DetectPropActions detectPropActions = null;
    private IntakeClass intake = null;
    private UpTake uptake = null;
    private LiftyUppyActions liftyUppyActions = null;
    private StateManager stateManager = null;

    //Initial variable declarations
    private double speed = 300;

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
            while (gyroActions.encoderGyroStrafeStateMachine(speed, 2, true));
            // First, uses detectPropActions to find the prop. Assigns it to a variable so we can use it later.
            String propPlace = detectPropActions.whereProp(3);
            telemetry.addData("result", detectPropActions.getResult().x);
            telemetry.update();
            while (propPlace == "") {
                propPlace = detectPropActions.whereProp(3);
            }
            telemetry.addData("prop place", propPlace);

            //Start the robot moving forwards - 20 inches irrespective of the placer location
            //gyroActions.initEncoderGyroDriveStateMachine(speed, 20, 0);
            //Because the driving uses feedback from the gyroscope, we constantly have to update the driving
            //while (gyroActions.encoderGyroDriveStateMachine(speed, 20, 0)) ;

            ///// remove the hardcoded value /////
            propPlace = "left";
            //If statements, in case something could change in the program
            if (propPlace == "right") {
                //Prop is at the left side
                placePixelRight(placer);
                //placeAndPark(placer);
            } else if (propPlace == "left") {
                placePixelLeft(placer);
                //placeAndPark(placer);
            } else {
                //Mid is the default position, if it is not on the left or the right, the only remaining option is the middle
                placePixelMid(placer);
                //driveToBoardMid(placer);
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
                liftyUppyActions.goToPreset(false, true, false, false);
            }
        }
    }

    private void placePixelRight(PlacerActions placer) {
        double distance = 22;
        int angle = -90;

        // Strafe to the left center line
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 11.5, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 11.5, false));

        // move to the prop and push it forward
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0));

        // place the pixel
        intake.outTake();
        sleep(1000);
        intake.intakeOff();

        // come back 5 inches to avoid the pixel/prop
        gyroActions.initEncoderGyroDriveStateMachine(speed, -15, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -15, 0)) ;

        // turn backward
        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed));

        // Strafe to the original lane
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 4, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 4, false));

    }

    private void placePixelLeft(PlacerActions placer) {
        double distance = 28;
        int angle = 90;

        // Strafe away from the prop
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 5, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 5, false));

        // go forward 28  inches
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0));


        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        gyroActions.initEncoderGyroDriveStateMachine(speed, 5, angle);
        while (gyroActions.encoderGyroDriveStateMachine(speed, 5, angle));

        intake.outTake();
        sleep(1000);
        intake.intakeOff();
        

        gyroActions.initEncoderGyroDriveStateMachine(speed, -2, angle);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -2, angle));

        gyroActions.initEncoderGyroStrafeStateMachine(speed, 26, true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 26 , true));
    }

    private void placeAndPark(PlacerActions placer) {
        // release pixel
        placer.releasePixel();
        sleep(700);
        if (liftyUppyActions.flippyTurny.getCurrentPosition() > 300) {
            liftyUppyActions.goToPreset(true, false, false, false);

        }
        liftyUppyActions.flippyTurnyDown();

        // go 3 inches away from the board
        gyroActions.encoderGyroDriveStateMachine(speed, 3);
        while (gyroActions.encoderGyroDriveStateMachine(speed, 3)) ;

        // move away from the board towards the wall
        gyroActions.encoderGyroStrafeStateMachine(speed, 24, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 24, false)) ;
        placer.closePlacer();
    }
}
