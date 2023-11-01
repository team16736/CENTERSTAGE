package org.firstinspires.ftc.teamcode.src.attachments;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad2;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;

/**
 * @noinspection UnresolvedClassReferenceRepair
 */
public class IntakeClass {

    public CRServo intakeLeft;
    public CRServo intakeRight;
    double servoPosition = 0.0;
    public StateManager stateManager;

    public IntakeClass(StateManager stateManager, HardwareMap opModeHardware) {
        intakeLeft = opModeHardware.get(CRServo.class, ConfigConstants.INTAKE_LEFT);
        intakeRight = opModeHardware.get(CRServo.class, ConfigConstants.INTAKE_RIGHT);

        this.stateManager = stateManager;
    }

    public void intakeOn() {
        // if ((stateManager.MAX_PIXELS != stateManager.pixelCount) && (stateManager.getPlacerState() != stateManager.PLACER_PLACING)) {
        //    stateManager.setIntakeState(stateManager.INTAKE_TAKING_PIXEL1);

        stateManager.setIntakeState(stateManager.INTAKE_ON);
        intakeLeft.setPower(1.0);
        intakeRight.setPower(-1.0);
    }

    public void intakeOff() {
        stateManager.setIntakeState(stateManager.INTAKE_OFF);
        intakeLeft.setPower(0.0);
        intakeRight.setPower(0.0);
    }

    public void outTake() {
        stateManager.setIntakeState(stateManager.INTAKE_OUT);
        intakeLeft.setPower(-1.0);
        intakeRight.setPower(1.0);
    }

    public void intakeButtons(Gamepad gamepad) {
        if (gamepad.x) {
            intakeOn();
        } else if (gamepad.y) {
            outTake();
        } else {
            intakeOff();
        }
    }
}

