package org.firstinspires.ftc.teamcode.src.tests.attachments;

import org.firstinspires.ftc.teamcode.src.attachments.IntakeClass;
import org.firstinspires.ftc.teamcode.src.attachments.StateManager;
import org.junit.Test;

public class TestIntakeClass {
    @Test
    public void testintakeOn0() {
        StateManager stateManager = new StateManager();
        IntakeClass intakeClass = new IntakeClass(stateManager);
        stateManager.pixelCount = 0;
        intakeClass.intakeOn();
    }
}
