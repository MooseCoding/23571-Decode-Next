package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Tuning {
    public static void stopRobot(Follower dt) {
        dt.startTeleopDrive(true);
        dt.setTeleOpDrive(0,0,0,true);
    }
}
