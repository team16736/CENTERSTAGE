package org.firstinspires.ftc.teamcode.src.attachments;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;

public class IntakeFinger {
    public Servo translation;
    public CRServo rotation;

    private Telemetry telemetry;
    private HardwareMap hardwareMap;
    private ElapsedTime runtime = new ElapsedTime();

    public IntakeFinger(Telemetry opModeTelemetry, HardwareMap opModeHardware) {
        this.telemetry = opModeTelemetry;
        this.hardwareMap = opModeHardware;
        rotation = hardwareMap.get(CRServo.class, ConfigConstants.FINGER_CR);
        translation = hardwareMap.get(Servo.class, ConfigConstants.TRANSLATE_FINGER);

        translation.setPosition(0.45);
    }

    public void RotateFinger() {
        rotation.setPower(-1.0);
    }

    public void StopRotatingFinger() {
        rotation.setPower(0.0);
    }

    public void TranslateFingerDown() {
        translation.setPosition(0.0);
    }

    public void TranslateFingerUp() {
        translation.setPosition(0.45);
    }
}


