package org.firstinspires.ftc.teamcode.src.tests.attachments;

import org.firstinspires.ftc.teamcode.src.attachments.IntakeClass;
import org.firstinspires.ftc.teamcode.src.attachments.StateManager;
import org.junit.Assert;
import org.junit.Test;

public class TestIntakeClass {
    @Test
    public void testintakeOn0() {
        StateManager stateManager = new StateManager();
        IntakeClass intakeClass = new IntakeClass(stateManager);
        stateManager.setIntakeState(stateManager.INTAKE_HAS_NO_PIXEL);
        stateManager.setPlacerState(stateManager.PLACER_NO_PIXEL);
        stateManager.pixelCount = 0;
        intakeClass.intakeOn();
        Assert.assertEquals(stateManager.INTAKE_TAKING_PIXEL1, stateManager.getIntakeState());
    }

    @Test
    public void testintakeOn2() {
        StateManager stateManager = new StateManager();
        IntakeClass intakeClass = new IntakeClass(stateManager);
        stateManager.setIntakeState(stateManager.INTAKE_HAS_NO_PIXEL);
        stateManager.pixelCount = 2;
        intakeClass.intakeOn();
        Assert.assertEquals(stateManager.INTAKE_HAS_NO_PIXEL, stateManager.getIntakeState());
    }

    @Test
    public void testintakeOnPlacing() {
        StateManager stateManager = new StateManager();
        IntakeClass intakeClass = new IntakeClass(stateManager);
        stateManager.setIntakeState(stateManager.INTAKE_HAS_NO_PIXEL);
        stateManager.setPlacerState(stateManager.PLACER_PLACING);
        stateManager.pixelCount = 0;
        intakeClass.intakeOn();
        Assert.assertEquals(stateManager.INTAKE_HAS_NO_PIXEL, stateManager.getIntakeState());
    }
}
