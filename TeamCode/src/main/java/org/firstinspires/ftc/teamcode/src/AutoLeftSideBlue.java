package org.firstinspires.ftc.teamcode.src;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.src.attachments.DetectPropActions;
import org.firstinspires.ftc.teamcode.src.attachments.IntakeClass;
import org.firstinspires.ftc.teamcode.src.attachments.LiftyUppyActions;
import org.firstinspires.ftc.teamcode.src.attachments.StateManager;
import org.firstinspires.ftc.teamcode.src.attachments.UpTake;
import org.firstinspires.ftc.teamcode.src.driving.GyroActions;
import org.firstinspires.ftc.teamcode.src.driving.HelperActions;
// THIS HAS NOT BEEN DEBUGGED YET.
@Autonomous(name = "AutoLeftSideBlue")
public class AutoLeftSideBlue extends HelperActions {

    //Create the actions as objects. This is so we can use the methods inside of them
    private GyroActions gyroActions = null;
    private DetectPropActions detectPropActions = null;
    private IntakeClass intake = null;
    private UpTake uptake = null;
    private LiftyUppyActions liftyUppyActions = null;
    private StateManager stateManager = null;

    //Initial variable declarations
    private double speed = 200;

    public void runOpMode() {

        //Done during initialization
        //Before this, the actions we created are empty. Assigns the actions to stop being nothing
        stateManager = new StateManager();
        gyroActions = new GyroActions(this, telemetry, hardwareMap);
        detectPropActions = new DetectPropActions(hardwareMap, "RedBoxTemplate", false);
        intake = new IntakeClass(stateManager, hardwareMap);
        uptake = new UpTake(stateManager, hardwareMap);
        liftyUppyActions = new LiftyUppyActions(hardwareMap, stateManager, telemetry);

        //Ends initialization, waits for the player to hit the start button
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            //First, uses detectPropActions to find the prop. Assigns it to a variable so we can use it later.
//            String propPlace = detectPropActions.whereProp(10);
//            while (propPlace == "") {
//                propPlace = detectPropActions.whereProp(10);
//            }
            String propPlace = "right ";
            telemetry.addData("prop place", propPlace);

            //Start the robot moving forwards
            gyroActions.initEncoderGyroDriveStateMachine(speed, 20, 0);
            //Because the driving uses feedback from the gyroscope, we constantly have to update the driving
            while (gyroActions.encoderGyroDriveStateMachine(speed, 20, 0)) ;

            //If statements, in case something could change in the program
            if (propPlace == "left") {
                //Turn towards the prop
                gyroActions.initGyroSpin(45);
                while (gyroActions.gyroSpin(speed)) ;

                //Move to the prop. Because moving at an angle, must pass that in
                int distance = 2;
                gyroActions.initEncoderGyroDriveStateMachine(speed, distance, 45);
                while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 45)) ;


            } else if (propPlace == "right") {
                //Other situation, if the propPlace is on the right this triggers
                //Turn towards the prop
                // changed distance 4 to 8 because we are now on the left side and forward longer
                int distance = 11;
                gyroActions.initGyroSpin(-45);
                while (gyroActions.gyroSpin(speed)) ;

                //Move to the prop. Because moving at an angle, must pass that in

                gyroActions.initEncoderGyroDriveStateMachine(speed, distance, -45);
                while (gyroActions.encoderGyroDriveStateMachine(speed, distance, -45)) ;


            } else {
                //For when it is in the middle. Do not need to use an if statement to check if it is, because
                //if it is not on the left or the right, the only remaining option is the middle

                //Move to the prop.
                int distance = 8;
                gyroActions.initEncoderGyroDriveStateMachine(speed, distance, 0);
                while (gyroActions.encoderGyroDriveStateMachine(speed, distance, 0)) ;

            }
            intake.outTake();
            uptake.setUptakeDown();
            sleep(5000);
        }
    }
}