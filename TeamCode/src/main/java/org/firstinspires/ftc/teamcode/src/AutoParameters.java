package org.firstinspires.ftc.teamcode.src;

public class AutoParameters {
    //** GLOBAL PARAM - START **//
    //default = -1200
    //changed for thor in 2/14/24 perfect height : -1225 for thor
    public static int PIXEL_DROP_HEIGHT_HIGH = -1200;
    public static int PIXEL_DROP_HEIGHT_LOW = -800;
    //default -800
    //** GLOBAL PARAM - END **//

    // Back Board Side Red - START //
    // up to 10 seconds delay 0 to 10000
    public static long BOARDSIDE_RED_INITIAL_DELAY = 0;
    public static long BOARDSIDE_RED_INTERMEDIATE_DELAY = 0;
    // 5 sec, 5000 ms for THOR
    public static double BOARDSIDE_RED_SPEED = 400;
    //default = 400

    public static boolean BOARDSIDE_RED_HAS_PIXEL = false;
    //default = true

    public static int BOARDSIDE_RED_PARK_STRAFE_DISTANCE = 24;
    //default = 24
    public static boolean BOARDSIDE_RED_PARK_MIDDLE = false;
    //default = false
    // Back Board Side Red - END //


    // Back Board Side Blue - START //
    public static long BOARDSIDE_BLUE_INITIAL_DELAY = 0;
    // up to 10 seconds delay 0 to 10000
    public static long BOARDSIDE_BLUE_INTERMEDIATE_DELAY = 0;
    // 5 sec, 5000 ms for THOR
    public static double BOARDSIDE_BLUE_SPEED = 400;
    //default = 400
    public static boolean BOARDSIDE_BLUE_HAS_PIXEL = false;
    //default = false changed for thor in 2/14/24 for thor: true
    public static int BOARDSIDE_BLUE_PARK_STRAFE_DISTANCE = 24;
    //default = 24
    public static boolean BOARDSIDE_BLUE_PARK_MIDDLE = false;
    //default = false
    // Back Board Side Blue - END //


    // Audience Side Red - START //
    public static long AUDIENCE_RED_INITIAL_DELAY = 0;
    // up to 3 seconds delay 0 to 30000
    public static long AUDIENCE_RED_INTERMEDIATE_DELAY = 0;
    public static double AUDIENCE_RED_SPEED = 400;
    //default = 400
    public static boolean AUDIENCE_RED_HAS_PIXEL = true;
    //default = true
    public static int AUDIENCE_RED_PARK_STRAFE_DISTANCE = 26;
    //default = 27
    public static boolean AUDIENCE_RED_PARK_MIDDLE = true;
    //default = true
    // Audience Side Red - END //


    // Audience Side Blue - START //
    public static long AUDIENCE_BLUE_INITIAL_DELAY = 0;
    // up to 3 seconds delay 0 to 3000
    public static long AUDIENCE_BLUE_INTERMEDIATE_DELAY = 0;
    public static double AUDIENCE_BLUE_SPEED = 400;
    //default = 400
    public static boolean AUDIENCE_BLUE_HAS_PIXEL = true;
    //default = true
    public static int AUDIENCE_BLUE_PARK_STRAFE_DISTANCE = 24;
    //default = 24
    public static boolean AUDIENCE_BLUE_PARK_MIDDLE = true;
    //default = true
    // Audience Side Blue - END //
}
