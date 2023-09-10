package org.firstinspires.ftc.teamcode.src.tests.attachments;

import org.firstinspires.ftc.teamcode.src.constants.ConfigConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.src.fakes.FakeTelemetry;
import org.firstinspires.ftc.teamcode.src.fakes.drive.FakeServo;
import org.firstinspires.ftc.teamcode.src.fakes.util.FakeHardwareMapFactory;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.firstinspires.ftc.teamcode.src.attachments.GrabberActions;

public class TestGrabber {
    HardwareMap fakeHwMap;
    FakeTelemetry fakeTelemetry;
    FakeServo fakeServo;
    GrabberActions grabberActions;

    @Before
    public void setUp() throws IOException, ParserConfigurationException, SAXException {
        fakeHwMap = FakeHardwareMapFactory.getFakeHardwareMap("sample_hardware_map.xml");
        fakeServo = new FakeServo();
        fakeHwMap.put(ConfigConstants.GRABBERSERVO, fakeServo);
        fakeTelemetry = new FakeTelemetry();
        grabberActions = new GrabberActions(fakeTelemetry, fakeHwMap);
    }

    @Test
    public void testGetAdjustedDistance() {
        grabberActions.openGrabber();
        Assert.assertEquals(false, grabberActions.isGrabberOpen());
        while (grabberActions.getTime() < 1.1) {}
        Assert.assertEquals(true, grabberActions.isGrabberOpen());
    }
}
