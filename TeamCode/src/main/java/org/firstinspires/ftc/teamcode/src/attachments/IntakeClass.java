package org.firstinspires.ftc.teamcode.src.attachments;

public class IntakeClass {
    public StateManager stateManager;
    public IntakeClass(StateManager stateManager){
        this.stateManager = stateManager;
    }
    public void intakeOn(){
        if (stateManager.MAX_PIXELS != stateManager.pixelCount) {
            stateManager.setIntakeState(stateManager.INTAKE_TAKING_PIXEL1);
        }
    }
}
