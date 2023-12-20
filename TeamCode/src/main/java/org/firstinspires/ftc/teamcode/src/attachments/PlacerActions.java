package org.firstinspires.ftc.teamcode.src.attachments;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;

public class PlacerActions {
    public Servo placer;
    public StateManager stateManager;

    public PlacerActions(StateManager stateManager, HardwareMap opModeHardware) {
        placer = opModeHardware.get(Servo.class, ConfigConstants.PLACER);

        this.stateManager = stateManager;
        placer.setPosition(0.4);
    }

    public void setPosition(double position) {
        placer.setPosition(position);
    }

    public void releasePixel() {
        placer.setPosition(0.8);
        stateManager.setPlacerState(stateManager.PLACER_PLACING);
    }

    public void closePlacer() {
        placer.setPosition(0.4);
        stateManager.setPlacerState(stateManager.PLACER_NO_PIXEL);
    }
}
