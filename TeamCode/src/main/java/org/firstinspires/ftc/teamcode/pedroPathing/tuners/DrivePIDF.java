package org.firstinspires.ftc.teamcode.pedroPathing.tuners;


import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

/**
 * This is the Drive PIDF Tuner OpMode. It will run the robot in a straight line going forward and back.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/12/2024
 */
@TeleOp(name="DrivePIDF", group="tuners")
public class DrivePIDF extends OpMode {
    public static double DISTANCE = 40;
    private boolean forward = true;

    private PathChain forwards;
    private PathChain backwards;
    private Follower follower;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 72));
    }

    /**
     * This initializes the Follower and creates the forward and backward Paths. Additionally, this
     * initializes the Panels telemetry.
     */
    @Override
    public void init_loop() {
        telemetry.addLine("This will run the robot in a straight line going " + DISTANCE + "inches forward.");
        telemetry.addLine("The robot will go forward and backward continuously along the path.");
        telemetry.addLine("Make sure you have enough room.");
        telemetry.update();
        follower.update();
    }

    @Override
    public void start() {
        follower.deactivateAllPIDFs();
        follower.activateDrive();

        forwards = follower.pathBuilder()
                .setGlobalDeceleration()
                .addPath(new BezierLine(new Pose(72,72), new Pose(DISTANCE + 72,72)))
                .setConstantHeadingInterpolation(0)
                .build();

        backwards = follower.pathBuilder()
                .setGlobalDeceleration()
                .addPath(new BezierLine(new Pose(DISTANCE + 72,72), new Pose(72,72)))
                .setConstantHeadingInterpolation(0)
                .build();

        follower.followPath(forwards);
    }

    /**
     * This runs the OpMode, updating the Follower as well as printing out the debug statements to
     * the Telemetry, as well as the Panels.
     */
    @Override
    public void loop() {
        follower.update();

        if (!follower.isBusy()) {
            if (forward) {
                forward = false;
                follower.followPath(backwards);
            } else {
                forward = true;
                follower.followPath(forwards);
            }
        }

        telemetry.addLine("Driving forward?: " + forward);
        telemetry.addData("Zero Line", 0);
        telemetry.addData("Error", follower.errorCalculator.getDriveErrors()[1]);
        telemetry.update();
    }
}