
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
        servo = hardwareMap.get(Servo.class, ConfigConstants.LEFT_RELEASE);
        telemetry.addData("Waiting for start", "");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            if (gamepad1.x) {
                servo.setPosition(1.0);
            } else if (gamepad1.y) {
                servo.setPosition(0.0);
            }


        }
    }
}