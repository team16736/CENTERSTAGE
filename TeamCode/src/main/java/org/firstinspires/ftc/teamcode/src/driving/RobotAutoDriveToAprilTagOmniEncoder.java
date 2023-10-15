/* Copyright (c) 2023 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.src.driving;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This OpMode illustrates using a camera to locate and drive towards a specific AprilTag.
 * The code assumes a Holonomic (Mecanum or X Drive) Robot.
 *
 * The drive goal is to rotate to keep the Tag centered in the camera, while strafing to be directly in front of the tag, and
 * driving towards the tag to achieve the desired distance.
 * To reduce any motion blur (which will interrupt the detection process) the Camera exposure is reduced to a very low value (5mS)
 * You can determine the best Exposure and Gain values by using the ConceptAprilTagOptimizeExposure OpMode in this Samples folder.
 *
 * The code assumes a Robot Configuration with motors named: leftfront_drive and rightfront_drive, leftback_drive and rightback_drive.
 * The motor directions must be set so a positive power goes forward on all wheels.
 * This sample assumes that the current game AprilTag Library (usually for the current season) is being loaded by default,
 * so you should choose to approach a valid tag ID (usually starting at 0)
 *
 * Under manual control, the left stick will move forward/back & left/right.  The right stick will rotate the robot.
 * Manually drive the robot until it displays Target data on the Driver Station.
 *
 * Press and hold the *Left Bumper* to enable the automatic "Drive to target" mode.
 * Release the Left Bumper to return to manual driving mode.
 *
 * Under "Drive To Target" mode, the robot has three goals:
 * 1) Turn the robot to always keep the Tag centered on the camera frame. (Use the Target Bearing to turn the robot.)
 * 2) Strafe the robot towards the centerline of the Tag, so it approaches directly in front  of the tag.  (Use the Target Yaw to strafe the robot)
 * 3) Drive towards the Tag to get to the desired distance.  (Use Tag Range to drive the robot forward/backward)
 *
 * Use DESIRED_DISTANCE to set how close you want the robot to get to the target.
 * Speed and Turn sensitivity can be adjusted using the SPEED_GAIN, STRAFE_GAIN and TURN_GAIN constants.
 *
 * Use Android Studio to Copy this Class, and Paste it into the TeamCode/src/main/java/org/firstinspires/ftc/teamcode folder.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list.
 *
 */

@Disabled
@TeleOp(name="Omni Drive To AprilTag James", group = "Concept")
public class RobotAutoDriveToAprilTagOmniEncoder extends LinearOpMode
{
    // Adjust these numbers to suit your robot.
    double DESIRED_Y = 4.0; //  this is how close the camera should get to the target (inches)
    double DESIRED_X = 0.0;
    double DESIRED_BEARING = 0.0;
    boolean elapsedTimeBit = true;
    double prevTime;
    double currentTime;
    double elapsedTime;

    //  Set the GAIN constants to control the relationship between the measured position error, and how much power is
    //  applied to the drive motors to correct the error.
    //  Drive = Error * Gain    Make these values smaller for smoother control, or larger for a more aggressive response.
    final double SPEED_GAIN  =  0.015  ;   //  Forward Speed Control "Gain". eg: Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
    final double STRAFE_GAIN =  0.015 ;   //  Strafe Speed Control "Gain".  eg: Ramp up to 25% power at a 25 degree Yaw error.   (0.25 / 25.0)
    final double TURN_GAIN   =  0.01  ;   //  Turn Control "Gain".  eg: Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

    final double MAX_AUTO_SPEED = 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_STRAFE= 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_TURN  = 0.3;   //  Clip the turn speed to this max value (adjust for your robot)

    private DcMotorEx motorFrontL = null;  //  Used to control the left front drive wheel
    private DcMotorEx motorFrontR = null;  //  Used to control the right front drive wheel
    private DcMotorEx motorBackL = null;  //  Used to control the left back drive wheel
    private DcMotorEx motorBackR = null;  //  Used to control the right back drive wheel

    private static final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
    private static final int DESIRED_TAG_ID = 5;     // Choose the tag you want to approach or set to -1 for ANY tag.
    private VisionPortal visionPortal;               // Used to manage the video source.
    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.
    private AprilTagDetection desiredTag = null;     // Used to hold the data for a detected AprilTag

    @Override public void runOpMode()
    {
        boolean targetFound     = false;    // Set to true when an AprilTag target is detected
        double  drive           = 0;        // Desired forward power/speed (-1 to +1)
        double  strafe          = 0;        // Desired strafe power/speed (-1 to +1)
        double  turn            = 0;        // Desired turning power/speed (-1 to +1)
        double[] driveArray = {0,0,0};

        // Initialize the Apriltag Detection process
        initAprilTag();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must match the names assigned during the robot configuration.
        // step (using the FTC Robot Controller app on the phone).
        motorFrontL = hardwareMap.get(DcMotorEx.class, "leftFront");
        motorFrontR = hardwareMap.get(DcMotorEx.class, "rightFront");
        motorBackL = hardwareMap.get(DcMotorEx.class, "leftRear");
        motorBackR = hardwareMap.get(DcMotorEx.class, "rightRear");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        motorFrontL.setDirection(DcMotorEx.Direction.REVERSE);
        motorBackL.setDirection(DcMotorEx.Direction.FORWARD);
        motorFrontR.setDirection(DcMotorEx.Direction.REVERSE);
        motorBackR.setDirection(DcMotorEx.Direction.FORWARD);

        if (USE_WEBCAM)
            setManualExposure(6, 250);  // Use low exposure time to reduce motion blur

        // Wait for driver to press start
        telemetry.addData("Camera preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch Play to start OpMode");
        telemetry.update();
        waitForStart();

        while (opModeIsActive())
        {
            targetFound = false;
            desiredTag  = null;

            // Step through the list of detected tags and look for a matching tag
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            for (AprilTagDetection detection : currentDetections) {
                if ((detection.metadata != null)
                        && ((DESIRED_TAG_ID >= 0) && (detection.id == DESIRED_TAG_ID))  ){
                    targetFound = true;
                    desiredTag = detection;
                    break;  // don't look any further.
                }
            }

            // Tell the driver what we see, and what to do.
            if (targetFound) {
                telemetry.addData(">","HOLD Left-Bumper to Drive to Target\n");
                telemetry.addData("Target", "ID %d (%s)", desiredTag.id, desiredTag.metadata.name);
                telemetry.addData("Y",  "%5.1f inches", desiredTag.ftcPose.y);
                telemetry.addData("X","%5.1f inches", -desiredTag.ftcPose.x);
                telemetry.addData("Bearing","%3.0f degrees", -desiredTag.ftcPose.bearing);
                telemetry.addData("Yaw","%3.0f degrees", -desiredTag.ftcPose.yaw);
            } else {
                telemetry.addData(">","Drive using joystick to find target\n");
            }

            // If Left Bumper is being pressed, AND we have found the desired target, Drive to target Automatically .
            if (gamepad1.left_trigger < 0.1 && targetFound) {
                currentTime = System.currentTimeMillis() / 1000.0;
                if (elapsedTimeBit) {
                    prevTime = currentTime;
                    elapsedTimeBit = false;
                }
                elapsedTime = currentTime - prevTime;
                prevTime = currentTime;

                boolean resetCalc = false;
                if ((Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.right_stick_x) + Math.abs(gamepad1.left_stick_y)) > 0.05) {
                    resetCalc = true;
                    DESIRED_Y += gamepad1.left_stick_y * elapsedTime;
                    DESIRED_X -= gamepad1.left_stick_x * elapsedTime;
                    DESIRED_BEARING += gamepad1.right_stick_x * elapsedTime * 3;
                }
                telemetry.addData("Desired Y", DESIRED_Y);
                telemetry.addData("Desired X", DESIRED_X);
                telemetry.addData("Desired Bearing", DESIRED_BEARING);

                // Determine heading, range and Yaw (tag image rotation) error so we can use them to control the robot automatically.
                double yError = (desiredTag.ftcPose.y - DESIRED_Y);
                double headingError = (desiredTag.ftcPose.yaw - DESIRED_BEARING);
                // Because X does not give same answer regardless of rotation, must derive it using angle and range
                double X = Math.sin(Math.toRadians(desiredTag.ftcPose.yaw)) * desiredTag.ftcPose.range;
                double xError = (X - DESIRED_X);
                telemetry.update();
                sleep(5000);
                encoderSpin(300, headingError, true);
                encoderStrafe(150, xError, false);
                encoderDrive(200, yError);

                // Use the speed and turn "gains" to calculate how we want the robot to move.
//                drive  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
//                turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN) ;
//                strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);
            } else {
                runWithoutEncoders();
                telemetry.addData("Auto","Drive %5.2f, Strafe %5.2f, Turn %5.2f ", driveArray[0], driveArray[1], driveArray[2]);
                // drive using manual POV Joystick mode.  Slow things down to make the robot more controlable.
                driveArray = new double[]{-gamepad1.left_stick_y / 2.0, -gamepad1.left_stick_x / 2.0, -gamepad1.right_stick_x / 3.0};
//                drive  = -gamepad1.left_stick_y  / 2.0;  // Reduce drive rate to 50%.
//                strafe = -gamepad1.left_stick_x  / 2.0;  // Reduce strafe rate to 50%.
//                turn   = -gamepad1.right_stick_x / 3.0;  // Reduce turn rate to 33%.
                telemetry.addData("Manual","Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);
                moveRobot(driveArray[0], driveArray[1], driveArray[2]);
            }
            telemetry.update();

            // Apply desired axes motions to the drivetrain.

//            sleep(5);
        }
    }

    /**
     * Move robot according to desired axes motions
     * Positive X is forward
     * Positive Y is strafe left
     * Positive Yaw is counter-clockwise
     */
    public void encoderDrive(double encoderSpeed, double encoderDistance) { // Deprecated. Look to GyroActions for auto driving
        resetEncoder();

        double ticksPerInch = 32.2;
        int totalTicks = (int) (ticksPerInch * encoderDistance);
        motorFrontL.setTargetPosition(totalTicks);
        motorFrontR.setTargetPosition(totalTicks);
        motorBackL.setTargetPosition(totalTicks);
        motorBackR.setTargetPosition(totalTicks);


        // Switch to RUN_TO_POSITION mode
        runToPosition();


        setVelocity(encoderSpeed, encoderSpeed, encoderSpeed, encoderSpeed);

        // While the Op Mode is running, show the motor's status via telemetry
        whileMotorBusy();
    }
    public void encoderStrafe(double encoderSpeed,
                              double encoderDistance,
                              boolean encoderMoveLeft) { // Deprecated
        encoderStrafeNoWhile(encoderSpeed, encoderDistance, encoderMoveLeft);

        //motorFrontL.isBusy()hile the Op Mode is running, show the motor's status via telemetry
        while (motorFrontL.isBusy()) {
            telemetry.addData("FL is at target", !motorFrontL.isBusy());
            telemetry.addData("FR is at target", !motorFrontR.isBusy());
            telemetry.addData("BL is at target", !motorBackL.isBusy());
            telemetry.addData("BR is at target", !motorBackR.isBusy());
            telemetry.update();
        }
    }
    public void encoderStrafeNoWhile(double encoderSpeed,
                                     double encoderDistance,
                                     boolean encoderMoveLeft) { // No longer used much
        resetEncoder();
        // Set the motor's target position to 6.4 rotations
        double ticksPerInch = 33.6;
        int totalTicks = (int) (ticksPerInch * encoderDistance);
        if (encoderMoveLeft){
            motorFrontL.setTargetPosition(-totalTicks);
            motorFrontR.setTargetPosition(totalTicks);
            motorBackL.setTargetPosition(totalTicks);
            motorBackR.setTargetPosition(-totalTicks);
        }else{
            motorFrontL.setTargetPosition(totalTicks);
            motorFrontR.setTargetPosition(-totalTicks);
            motorBackL.setTargetPosition(-totalTicks);
            motorBackR.setTargetPosition(totalTicks);
        }


        // Switch to RUN_TO_POSITION mode
        runToPosition();

        // Start the motor moving by setting the max velocity to 1 revolution per second
        motorFrontL.setVelocity(-encoderSpeed);
        motorFrontR.setVelocity(-encoderSpeed);
        motorBackL.setVelocity(-encoderSpeed);
        motorBackR.setVelocity(-encoderSpeed);
    }
    public void encoderSpin(double encoderSpeed,
                            double encoderDegrees,
                            boolean encoderSpinLeft) { // Deprecated
        motorFrontL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Set the motor's target position to 6.4 rotations
        double ticksPerDegree = 5.3;
        int totalTicks = (int) (ticksPerDegree * encoderDegrees);
        if (encoderSpinLeft){
            motorFrontL.setTargetPosition(-totalTicks);
            motorFrontR.setTargetPosition(totalTicks);
            motorBackL.setTargetPosition(-totalTicks);
            motorBackR.setTargetPosition(totalTicks);
        }else{
            motorFrontL.setTargetPosition(totalTicks);
            motorFrontR.setTargetPosition(-totalTicks);
            motorBackL.setTargetPosition(totalTicks);
            motorBackR.setTargetPosition(-totalTicks);
        }


        // Switch to RUN_TO_POSITION mode
        motorFrontL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFrontR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Start the motor moving by setting the max velocity to 1 revolution per second
        motorFrontL.setVelocity(-encoderSpeed);
        motorFrontR.setVelocity(-encoderSpeed);
        motorBackL.setVelocity(-encoderSpeed);
        motorBackR.setVelocity(-encoderSpeed);

        //motorFrontL.isBusy()hile the Op Mode is running, show the motor's status via telemetry
        while (motorFrontL.isBusy()) {
            telemetry.addData("FL is at target", !motorFrontL.isBusy());
            telemetry.addData("FR is at target", !motorFrontR.isBusy());
            telemetry.addData("BL is at target", !motorBackL.isBusy());
            telemetry.addData("BR is at target", !motorBackR.isBusy());
            telemetry.update();
        }
    }
    public void whileMotorBusy(){
        while (motorFrontL.isBusy() && motorFrontR.isBusy() && motorBackL.isBusy() && motorBackR.isBusy()) {
            telemetry.addData("FL is at target", !motorFrontL.isBusy());
            telemetry.addData("FR is at target", !motorFrontR.isBusy());
            telemetry.addData("BL is at target", !motorBackL.isBusy());
            telemetry.addData("BR is at target", !motorBackR.isBusy());
            telemetry.update();
        }
    }
    public void setVelocity(double lf, double rf, double lb, double rb){
        motorFrontL.setVelocity(lf);
        motorFrontR.setVelocity(rf);
        motorBackL.setVelocity(lb);
        motorBackR.setVelocity(rb);
    }
    public void resetEncoder(){
        motorFrontL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    public void runToPosition() {
        motorFrontL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFrontR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    public void runWithoutEncoders() {
        motorFrontL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    public void moveRobot(double y, double x, double yaw) {
        // Calculate wheel powers.
        double leftFrontPower    =  y -x -yaw;
        double rightFrontPower   =  y +x +yaw;
        double leftBackPower     =  y +x -yaw;
        double rightBackPower    =  y -x +yaw;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        // Send powers to the wheels.
        motorFrontL.setPower(leftFrontPower);
        motorFrontR.setPower(rightFrontPower);
        motorBackL.setPower(leftBackPower);
        motorBackR.setPower(rightBackPower);
    }

    /**
     * Initialize the AprilTag processor.
     */
    private void initAprilTag() {
        // Create the AprilTag processor by using a builder.
        aprilTag = new AprilTagProcessor.Builder().build();

        // Create the vision portal by using a builder.
        if (USE_WEBCAM) {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                    .addProcessor(aprilTag)
                    .build();
        } else {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(BuiltinCameraDirection.BACK)
                    .addProcessor(aprilTag)
                    .build();
        }
    }

    /*
     Manually set the camera gain and exposure.
     This can only be called AFTER calling initAprilTag(), and only works for Webcams;
    */
    private void    setManualExposure(int exposureMS, int gain) {
        // Wait for the camera to be open, then use the controls

        if (visionPortal == null) {
            return;
        }

        // Make sure camera is streaming before we try to set the exposure controls
        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addData("Camera", "Waiting");
            telemetry.update();
            while (!isStopRequested() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
                sleep(20);
            }
            telemetry.addData("Camera", "Ready");
            telemetry.update();
        }

        // Set camera controls unless we are stopping.
        if (!isStopRequested())
        {
            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                sleep(50);
            }
            exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
            sleep(20);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
            sleep(20);
        }
    }
}
