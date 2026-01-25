package org.firstinspires.ftc.teamcode.pedroPathing.tuners;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@TeleOp(name="ForwardTuner")
public class ForwardTuner extends OpMode {
    private Follower follower;
    public static double DISTANCE = 48;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72,72));
        follower.update();
    }

    /** This initializes the PoseUpdater as well as the Panels telemetry. */
    @Override
    public void init_loop() {
        telemetry.addLine("Pull your robot forward " + DISTANCE + " inches. Your forward ticks to inches will be shown on the telemetry.");
        telemetry.update();
    }

    @Override
    public void start() {
        follower.startTeleopDrive(false);
        follower.setTeleOpDrive(0,0,0);
    }

    /**
     * This updates the robot's pose estimate, and updates the Panels telemetry with the
     * calculated multiplier and draws the robot.
     */
    @Override
    public void loop() {
        follower.update();
        telemetry.addLine("Distance Moved: " + (follower.getPose().getX() - 72));
        telemetry.addLine("The multiplier will display what your forward ticks to inches should be to scale your current distance to " + DISTANCE + " inches.");
        telemetry.addLine("Multiplier: " + (DISTANCE / ((follower.getPose().getX() - 72) / follower.getPoseTracker().getLocalizer().getForwardMultiplier())));
        telemetry.update();
    }
}