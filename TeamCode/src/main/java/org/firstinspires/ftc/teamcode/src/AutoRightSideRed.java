package org.firstinspires.ftc.teamcode.src;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

// Import Statements. Important if you want to use anything from a file
import org.firstinspires.ftc.teamcode.src.attachments.IntakeClass;
import org.firstinspires.ftc.teamcode.src.attachments.LiftyUppyActions;
import org.firstinspires.ftc.teamcode.src.attachments.DetectPropActions;
import org.firstinspires.ftc.teamcode.src.attachments.PlacerActions;
import org.firstinspires.ftc.teamcode.src.attachments.StateManager;
import org.firstinspires.ftc.teamcode.src.attachments.UpTake;
import org.firstinspires.ftc.teamcode.src.driving.GyroActions;
import org.firstinspires.ftc.teamcode.src.driving.HelperActions;

@Autonomous(name = "AutoRightSideRed")
public class AutoRightSideRed extends HelperActions {

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

            gyroActions.initEncoderGyroStrafeStateMachine(speed, 2, true);
            while (gyroActions.encoderGyroStrafeStateMachine(speed, 2, true));

//            First, uses detectPropActions to find the prop. Assigns it to a variable so we can use it later.
            String propPlace = detectPropActions.whereProp(3);
            while (detectPropActions.whereProp(3) == "");
            propPlace = detectPropActions.propPlace;
//            String propPlace = "left";
            telemetry.addData("result", detectPropActions.getResult().x);
            telemetry.update();
            detectPropActions.stopStreaming();

            gyroActions.initEncoderGyroStrafeStateMachine(speed, 2, false);
            while (gyroActions.encoderGyroStrafeStateMachine(speed, 2, false));

//            while (propPlace == "") {
//                propPlace = detectPropActions.whereProp(10);
//            }
//            String propPlace = "right";
            telemetry.addData("prop place", propPlace);

            //Start the robot moving forwards
            gyroActions.initEncoderGyroDriveStateMachine(speed, 21);
            //Because the driving uses feedback from the gyroscope, we constantly have to update the driving
            while (gyroActions.encoderGyroDriveStateMachine(speed, 21)) ;

            //If statements, in case something could change in the program
            if (propPlace == "left") {
                goToLeft(placer);

            } else if (propPlace == "right") {
                goToRight(placer);


            } else {
                //For when it is in the middle. Do not need to use an if statement to check if it is, because
                //if it is not on the left or the right, the only remaining option is the middle

                //Move to the prop.
                goToMid(placer);
            }
//            intake.outTake();
           // uptake.setUptakeDown();
            sleep(1000);



        }
    }

    private void goToLeft(PlacerActions placer) {
        //Turn towards the prop

        gyroActions.initGyroSpin(45);
        while (gyroActions.gyroSpin(speed)) ;

        //Move to the prop. Because moving at an angle, must pass that in
        gyroActions.initEncoderGyroDriveStateMachine(speed, 9);
        while (gyroActions.encoderGyroDriveStateMachine(speed, 9)) ;
        intake.outTake();
        sleep(1000);
        intake.intakeOff();
        int distance = -3;
        gyroActions.initEncoderGyroDriveStateMachine(speed,distance);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance));
        gyroActions.initGyroSpin(45);
        while (gyroActions.gyroSpin(speed));
        liftyUppyActions.flippyTurnyUp();

        gyroActions.initEncoderGyroDriveStateMachine(speed, -41);
        while (gyroActions.encoderGyroDriveStateMachine(speed,-41)){
            if(liftyUppyActions.flippyTurny.getCurrentPosition()>300){
                liftyUppyActions.setLiftyUppyPosition(-800, 2500);
            }
        }
        gyroActions.initEncoderGyroStrafeStateMachine(speed,5,false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed,5,false));
        placeAndPark(placer);
        gyroActions.initEncoderGyroStrafeStateMachine(speed,5,true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed,5,true));
    }

    private void goToMid(PlacerActions placer) {
        int distance = 8;
        gyroActions.initEncoderGyroDriveStateMachine(speed, distance);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance));
        intake.outTake();
        sleep(1000);
        intake.intakeOff();
        gyroActions.initEncoderGyroDriveStateMachine(speed, -5);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -5));
        gyroActions.initGyroSpin(90);
        while (gyroActions.gyroSpin(speed));
        gyroActions.initEncoderGyroDriveStateMachine(speed, -37);
        liftyUppyActions.flippyTurnyUp();
        while(gyroActions.encoderGyroDriveStateMachine(speed, -37)) {
            if (liftyUppyActions.flippyTurny.getCurrentPosition() > 300) {
                liftyUppyActions.setLiftyUppyPosition(-800, 2500);
            }
        }
        placeAndPark(placer);
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 4, true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 4, true));
    }

    private void goToRight(PlacerActions placer) {
        //Other situation, if the propPlace is on the right this triggers
        //Turn towards the prop
        int angle = -45;
        int distance = 3;
        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        //Move to the prop.

        gyroActions.initEncoderGyroDriveStateMachine(speed, distance);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance));
        intake.outTake();
        // drive backwards to get away from the pixel
        sleep(800);
        gyroActions.initEncoderGyroDriveStateMachine(speed, -4);
        while(gyroActions.encoderGyroDriveStateMachine(speed,-4));
        distance = 5;
        gyroActions.initEncoderGyroStrafeStateMachine(speed,distance, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, distance, false));
        intake.intakeOff();
        // spin right to straighten out the robot
        gyroActions.initGyroSpin(135);
        while (gyroActions.gyroSpin(speed)) ;
//                // drive forward to get to the backboard
        liftyUppyActions.flippyTurnyUp();

        gyroActions.initEncoderGyroDriveStateMachine(speed, -34.5);
        while(gyroActions.encoderGyroDriveStateMachine(speed,-34.5)) {
            if (liftyUppyActions.flippyTurny.getCurrentPosition() > 300) {
                liftyUppyActions.setLiftyUppyPosition(-800, 2500);
            }
        }

        gyroActions.initEncoderGyroStrafeStateMachine(speed, 3, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 3, false));

        placeAndPark(placer);
    }

    private void placeAndPark(PlacerActions placer) {
        placer.releasePixel();
        sleep(800);
        placer.closePlacer();

        liftyUppyActions.goToPreset(false, true, false, false);
        sleep(300);
        liftyUppyActions.flippyTurnyDown();
        sleep(200);
        liftyUppyActions.goToPreset(true, false, false, false);
        gyroActions.initEncoderGyroDriveStateMachine(speed, 2);
        while (gyroActions.encoderGyroDriveStateMachine(speed,2));
        gyroActions.initEncoderGyroStrafeStateMachine(speed*2,18,true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed*2,18,true));
    }
}