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


@Autonomous(name = "Just Park Near Blue")
/*
This the far side red, Mel
 */
public class AutoJustParkNearBlue extends HelperActions {
    //Create the actions as objects. This is so we can use the methods inside of them
    private GyroActions gyroActions = null;
    private DetectPropActions detectPropActions = null;
    private IntakeClass intake = null;
    private UpTake uptake = null;
    private LiftyUppyActions liftyUppyActions = null;
    private StateManager stateManager = null;

    //Initial variable declarations
    private double speed = 350;

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
            gyroActions.initEncoderGyroStrafeStateMachine(speed, 2, false);
            while (gyroActions.encoderGyroStrafeStateMachine(speed, 2, false)) ;

            // First, uses detectPropActions to find the prop. Assigns it to a variable so we can use it later.
            String propPlace = detectPropActions.whereProp(3);
            telemetry.addData("result", detectPropActions.getResult().x);
            telemetry.update();
            while (propPlace == "") {
                propPlace = detectPropActions.whereProp(3);
            }
            telemetry.addData("prop place", propPlace);

            //If statements, in case something could change in the program
            if (propPlace == "left") {
                //places pixel on the line
                placePixelLeft(placer);
            } else if (propPlace == "right") {
                placePixelRight(placer);
               } else {
                //Mid is the default position, if it is not on the left or the right, the only remaining option is the middle
                placePixelMid(placer);
            }
        }
    }

    /*
    This method is for placing pixel on the middle line
     */
    private void placePixelMid(PlacerActions placer) {
        //Move to the prop 30 inches
        double distance = 29;
        int angle = 90;
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

    }

    /*
    This method under is for placing pixels on the left line
    */
    private void placePixelLeft(PlacerActions placer) {
        double distance = 22;
        int angle = -90;

        // Strafe to the left center line
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 11, true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 11, true)) ;

        // move to the prop and push it forward
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0)) ;

        // place the pixel
        intake.outTake();
        sleep(1000);
        intake.intakeOff();

        // come back 15 inches to avoid the pixel/prop
        gyroActions.initEncoderGyroDriveStateMachine(speed, -15, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -15, 0)) ;

        // turn backward
        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        // Strafe to the original lane
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 5, false);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 5, false)) ;

    }
    /*
     method under is for placing pixels on the right line
     */
    private void placePixelRight(PlacerActions placer) {
        double distance = 30 ;
        int angle = -90;

        // Strafe away from the prop
        gyroActions.initEncoderGyroStrafeStateMachine(speed, 5, true);
        while (gyroActions.encoderGyroStrafeStateMachine(speed, 5, true)) ;

        // go forward 28  inches
        gyroActions.encoderGyroDriveStateMachine(speed, distance, 0);
        while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0)) ;


        gyroActions.initGyroSpin(angle);
        while (gyroActions.gyroSpin(speed)) ;

        gyroActions.initEncoderGyroDriveStateMachine(speed, 7.5, angle);
        while (gyroActions.encoderGyroDriveStateMachine(speed, 7.5, angle)) ;

        intake.outTake();
        sleep(1000);
        intake.intakeOff();

        gyroActions.initEncoderGyroDriveStateMachine(speed, -6, angle);
        while (gyroActions.encoderGyroDriveStateMachine(speed, -6, angle)) ;


      }

}
