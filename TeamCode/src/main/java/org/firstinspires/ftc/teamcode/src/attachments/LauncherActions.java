package org.firstinspires.ftc.teamcode.src.attachments;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;

public class LauncherActions {
    public Servo bandHoldy;

    private Telemetry telemetry;
    private HardwareMap hardwareMap;
    private ElapsedTime runtime = new ElapsedTime();

    public LauncherActions(Telemetry opModeTelemetry, HardwareMap opModeHardware) {
        this.telemetry = opModeTelemetry;
        this.hardwareMap = opModeHardware;

        bandHoldy = hardwareMap.get(Servo.class, ConfigConstants.LIFTOFF);

        bandHoldy.setPosition(0.55);
    }

    public void launch() {
        bandHoldy.setPosition(1.0);
    }



}
