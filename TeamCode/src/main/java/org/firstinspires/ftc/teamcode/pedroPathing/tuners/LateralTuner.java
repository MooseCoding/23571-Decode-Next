package org.firstinspires.ftc.teamcode.pedroPathing.tuners;


import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

/**
 * This is the LateralTuner OpMode. This tracks the strafe movement of the robot and displays the
 * necessary ticks to inches multiplier. This displayed multiplier is what's necessary to scale the
 * robot's current distance in ticks to the specified distance in inches. So, to use this, run the
 * tuner, then pull/push the robot to the specified distance using a ruler on the ground. When you're
 * at the end of the distance, record the ticks to inches multiplier. Feel free to run multiple trials
 * and average the results. Then, input the multiplier into the strafe ticks to inches in your
 * localizer of choice.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 2.0, 6/26/2025
 */
@TeleOp(name="LateralTuner", group="tuners")
public class LateralTuner extends OpMode {
    public static double DISTANCE = 48;
    private Follower follower;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72,72));
        follower.update();
    }

    /** This initializes the PoseUpdater as well as the Panels telemetry. */
    @Override
    public void init_loop() {
        telemetry.addLine("Pull your robot to the right " + DISTANCE + " inches. Your strafe ticks to inches will be shown on the telemetry.");
        telemetry.update();
    }

    /**
     * This updates the robot's pose estimate, and updates the Panels telemetry with the
     * calculated multiplier and draws the robot.
     */
    @Override
    public void loop() {
        follower.update();

        telemetry.addLine("Distance Moved: " + (follower.getPose().getY() - 72));
        telemetry.addLine("The multiplier will display what your strafe ticks to inches should be to scale your current distance to " + DISTANCE + " inches.");
        telemetry.addLine("Multiplier: " + (DISTANCE / ((follower.getPose().getY() - 72) / follower.getPoseTracker().getLocalizer().getLateralMultiplier())));
        telemetry.update();
    }
}