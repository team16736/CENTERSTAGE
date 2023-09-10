package org.firstinspires.ftc.teamcode.src.attachments;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;

public class GrabberActions {
    public Servo grabberServo;
    public double openStartTime = 0;
    public double timeToOpen = 1;
    public double closeStartTime = 0;
    public double timeToClose = 1;

    private Telemetry telemetry;
    private HardwareMap hardwareMap;
    private ElapsedTime runtime = new ElapsedTime();

    public GrabberActions(Telemetry opModeTelemetry, HardwareMap opModeHardware) {
        this.telemetry = opModeTelemetry;
        this.hardwareMap = opModeHardware;

        grabberServo = hardwareMap.get(Servo.class, ConfigConstants.GRABBERSERVO);

        grabberServo.setPosition(0.9);
    }

    public double getTime() {
        return runtime.seconds();
    }

    public void openGrabber() {
        grabberServo.setPosition(0.9);
        openStartTime = getTime();
    }

    public boolean isGrabberOpen() {
        return (getTime() > openStartTime + timeToOpen);
    }

    public void closeGrabber() {
        grabberServo.setPosition(0.9);
        closeStartTime = getTime();
    }

    public boolean isGrabberClosed() {
        return (getTime() > closeStartTime + timeToClose);
    }
}
