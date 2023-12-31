package org.firstinspires.ftc.teamcode.src.attachments;

public class StateManager {
    public int INTAKE_TAKING_PIXEL1 = 0;
    public int INTAKE_GIVING_PIXEL1 = 1;
    public int INTAKE_TAKING_PIXEL2 = 2;
    public int INTAKE_GIVING_PIXEL2 = 3;
    public int INTAKE_HASPIXEL = 4;

    public int INTAKE_HAS_NO_PIXEL = 6;
    public int INTAKE_OFF = 7;
    public int INTAKE_OUT = 9;
    public int INTAKE_ON = 8;

    // old uptake states
//    public int UPTAKE_TAKING_PIXEL1 = 0;
//    public int  UPTAKE_GIVING_PIXEL1 = 1;
//    public int UPTAKE_TAKING_PIXEL2 = 2;
//    public int UPTAKE_GIVING_PIXEL2 = 3;
//    public int UPTAKE_HASPIXEL = 4;
//    public int UPTAKE_NO_PIXEL = 6;

    public int UPTAKE_OFF = 0;
    public int UPTAKE_UP = 1;
    public int UPTAKE_DOWN = 1;

    public int FLIPPYTURNY_DOWN = 0;
    public int FLIPPYTURNY_UPPING = 1;
    public int FLIPPYTURNY_UP = 2;
    public int FLIPPYTURNY_DOWNING = 3;

    public int LIFTYUPPY_UPPING = 0;
    public int LIFTYUPPY_DOWNING = 1;
    public int LIFTYUPPY_STOPPED = 2;

    // placer states
    public int PLACER_PLACING = 1;
    public int PLACER_HAS_PIXEL = 2;
    public int PLACER_NO_PIXEL = 3;
    public int pixelCount = 0;
    public int MAX_PIXELS = 2;
    public int placerState = PLACER_NO_PIXEL;
    public int uptakeState = UPTAKE_OFF;
    public int intakeState = INTAKE_OFF;
    public int flippyTurnyState = FLIPPYTURNY_DOWN;
    public int liftyUppyState = LIFTYUPPY_STOPPED;

    public int getPlacerState() {
        return placerState;
    }
    public void setPlacerState(int State){
        placerState = State;
    }
    public int getIntakeState() {
        return intakeState;
    }
    public void setIntakeState(int State){
         intakeState = State;
    }
    public int getUptakeState() {
        return uptakeState;
    }
    public void setUptakeState(int State){
        uptakeState = State;
    }
    public int getFlippyTurnyState() { return flippyTurnyState; }
    public void setFlippyTurnyState(int State) { flippyTurnyState = State; }
    public int getLiftyUppyState() { return liftyUppyState; }
    public void setLiftyUppyState(int State) { liftyUppyState = State; }
}


