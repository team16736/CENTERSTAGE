
package org.firstinspires.ftc.teamcode.src;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.src.attachments.DetectPropActions;
import org.firstinspires.ftc.teamcode.src.attachments.LiftyUppyActions;
import org.firstinspires.ftc.teamcode.src.attachments.OpenCV;
import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;
import org.firstinspires.ftc.teamcode.src.driving.GyroActions;
import org.firstinspires.ftc.teamcode.src.driving.HelperActions;

@TeleOp(name = "James Test", group = "Linear Opmode")
public class JamesTesting extends HelperActions {

    Servo servo;

    @Override
    public void runOpMode() {
       // servo = hardwareMap.get(Servo.class, "intakeRight");
//        servo = hardwareMap.get(Servo.class, ConfigConstants.LEFT_RELEASE);
//        DetectPropActions detectPropActions = new DetectPropActions(hardwareMap, "RedThingTempl", true);
//        detectPropActions.setToTemplateMatching();
        GyroActions gyroActions = new GyroActions(this, telemetry, hardwareMap);
        telemetry.addData("Waiting for start", "");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
//            detectPropActions.whereProp(3);
//            while (detectPropActions.whereProp(3) == "") ;
//            telemetry.addData("prop place", detectPropActions.propPlace);
            telemetry.update();
//            gyroActions.gyroAprilDrive(4.5, 5, 500);
        }
    }
}