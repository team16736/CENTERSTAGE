package org.firstinspires.ftc.teamcode.src.attachments;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;

public class IntakeFinger {
    public Servo Translation;
    public CRServo Rotation;

    private Telemetry telemetry;
    private HardwareMap hardwareMap;
    private ElapsedTime runtime = new ElapsedTime();

    public IntakeFinger(Telemetry opModeTelemetry, HardwareMap opModeHardware) {
        this.telemetry = opModeTelemetry;
        this.hardwareMap = opModeHardware;
        Rotation = hardwareMap.get(CRServo.class, ConfigConstants.FINGER_CR);
        Translation = hardwareMap.get(Servo.class, ConfigConstants.TRANSLATE_FINGER);

        Translation.setPosition(-0.3);
    }

    public void RotateFinger() {
        Rotation.setPower(1.0);
    }

    public void StopRotatingFinger() {
        Rotation.setPower(0.0);
    }

    public void TranslateFingerDown() {
        Translation.setPosition(0.7);
    }

    public void TranslateFingerUp() {
        Translation.setPosition(-0.3);
    }
}

