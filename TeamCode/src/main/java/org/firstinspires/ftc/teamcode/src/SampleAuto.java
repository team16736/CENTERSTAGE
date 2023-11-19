package org.firstinspires.ftc.teamcode.src;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

// Import Statements. Important if you want to use anything from a file
import org.firstinspires.ftc.teamcode.src.attachments.IntakeClass;
import org.firstinspires.ftc.teamcode.src.attachments.LiftyUppyActions;
import org.firstinspires.ftc.teamcode.src.attachments.DetectPropActions;
import org.firstinspires.ftc.teamcode.src.attachments.StateManager;
import org.firstinspires.ftc.teamcode.src.attachments.UpTake;
import org.firstinspires.ftc.teamcode.src.driving.GyroActions;
import org.firstinspires.ftc.teamcode.src.driving.HelperActions;
import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;

@Autonomous(name = "Sample")
public class SampleAuto extends HelperActions {

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
        detectPropActions = new DetectPropActions(hardwareMap, "RedBoxTemplate");
        intake = new IntakeClass(stateManager, hardwareMap);
        uptake = new UpTake(stateManager, hardwareMap);
        liftyUppyActions = new LiftyUppyActions(hardwareMap, stateManager, telemetry);

        //Ends initialization, waits for the player to hit the start button
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            //First, uses detectPropActions to find the prop. Assigns it to a variable so we can use it later.
            String propPlace = detectPropActions.whereProp(10);
            while (propPlace == "") {
                propPlace = detectPropActions.whereProp(10);
            }
            telemetry.addData("prop place", propPlace);

            //Start the robot moving forwards
            gyroActions.initEncoderGyroDriveStateMachine(speed, 20, 0);
            //Because the driving uses feedback from the gyroscope, we constantly have to update the driving
            while (gyroActions.encoderGyroDriveStateMachine(speed, 20, 0));

            //If statements, in case something could change in the program
            if (propPlace == "left") {
                //Turn towards the prop
                gyroActions.initGyroSpin(45);
                while (gyroActions.gyroSpin(speed));

                //Move to the prop. Because moving at an angle, must pass that in
                gyroActions.initEncoderGyroDriveStateMachine(speed, 8, 45);
                while (gyroActions.encoderGyroDriveStateMachine(speed, 8, 45));

            } else if (propPlace == "right") {
                //Other situation, if the propPlace is on the right this triggers
                //Turn towards the prop
                gyroActions.initGyroSpin(-45);
                while (gyroActions.gyroSpin(speed));

                //Move to the prop. Because moving at an angle, must pass that in
                gyroActions.initEncoderGyroDriveStateMachine(speed, 8, -45);
                while (gyroActions.encoderGyroDriveStateMachine(speed, 8, -45));

            } else {
                //For when it is in the middle. Do not need to use an if statement to check if it is, because
                //if it is not on the left or the right, the only remaining option is the middle

                //Move to the prop.
                gyroActions.initEncoderGyroDriveStateMachine(speed, 15, 0);
                while (gyroActions.encoderGyroDriveStateMachine(speed, 15, 0));
            }
            intake.outTake();
            uptake.setUptakeDown();
            sleep(5000);
        }
    }
}