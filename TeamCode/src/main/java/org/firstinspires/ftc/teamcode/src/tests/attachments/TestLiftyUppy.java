package org.firstinspires.ftc.teamcode.src.tests.attachments;

import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;
import org.firstinspires.ftc.teamcode.src.fakes.drive.FakeDcMotorEx;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.src.fakes.FakeTelemetry;
import org.firstinspires.ftc.teamcode.src.fakes.util.FakeHardwareMapFactory;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.firstinspires.ftc.teamcode.src.attachments.LiftyUppyActions;

public class TestLiftyUppy {

    HardwareMap fakeHwMap;
    FakeTelemetry fakeTelemetry;
    FakeDcMotorEx fakeFlippyTurny;
    LiftyUppyActions liftyUppyActions;

    @Before
    public void setUp() throws IOException, ParserConfigurationException, SAXException {
        fakeHwMap = FakeHardwareMapFactory.getFakeHardwareMap("sample_hardware_map.xml");
        fakeFlippyTurny = new FakeDcMotorEx();
        fakeHwMap.put(ConfigConstants.FLIPPY_TURNY, fakeFlippyTurny);
        fakeTelemetry = new FakeTelemetry();
        liftyUppyActions = new LiftyUppyActions(fakeHwMap);
    }

    @Test
    public void testFlippyTurny() {
        fakeFlippyTurny.setCurrentPosition(0);
        liftyUppyActions.flippyTurnyUp();
        fakeFlippyTurny.setBusy(true);
        Assert.assertEquals(LiftyUppyActions.FlippyTurnyState.upping, liftyUppyActions.flippyTurnyState);
        liftyUppyActions.update();
        Assert.assertEquals(LiftyUppyActions.FlippyTurnyState.upping, liftyUppyActions.flippyTurnyState);
        fakeFlippyTurny.setBusy(false);
        liftyUppyActions.update();
        Assert.assertEquals(LiftyUppyActions.FlippyTurnyState.up, liftyUppyActions.flippyTurnyState);
        liftyUppyActions.flippyTurnyDown();
        fakeFlippyTurny.setBusy(false);
        liftyUppyActions.update();
        Assert.assertEquals(LiftyUppyActions.FlippyTurnyState.down, liftyUppyActions.flippyTurnyState);
    }
}
