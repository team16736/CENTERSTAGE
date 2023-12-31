package org.firstinspires.ftc.teamcode.src.attachments;

import android.graphics.Bitmap;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;

public class UpTake {
    public CRServo uptake;
    public StateManager stateManager;

    public UpTake(StateManager stateManager, HardwareMap opModeHardware) {
        uptake = opModeHardware.get(CRServo.class, ConfigConstants.UPTAKE);

        this.stateManager = stateManager;
    }

    public void setUptakeUp() {
        stateManager.setUptakeState(stateManager.UPTAKE_UP);
        uptake.setPower(-1.0);
    }

    public void setUptakeDown() {
        stateManager.setUptakeState(stateManager.UPTAKE_DOWN);
        uptake.setPower(1.0);
    }
    public void setUptakeOff() {
        stateManager.setUptakeState(stateManager.UPTAKE_OFF);
        uptake.setPower(0.0);
    }

    public void uptakeButtons(Gamepad gamepad) {
        if (gamepad.b) {
            setUptakeUp();
        } else if (gamepad.a) {
            setUptakeDown();
        } else {
            setUptakeOff();
        }
    }

    public void setPower(double power) {
        if (power < 0) {
            stateManager.setUptakeState(stateManager.UPTAKE_DOWN);
        } else if (power > 0) {
            stateManager.setUptakeState(stateManager.UPTAKE_UP);
        } else {
            stateManager.setUptakeState(stateManager.UPTAKE_OFF);
        }
        uptake.setPower(power);
    }
}
