package org.firstinspires.ftc.teamcode.pedroPathing.tuners;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.io.DataInput;

/**
 * This is the Translational PIDF Tuner OpMode. It will keep the robot in place.
 * The user should push the robot laterally to test the PIDF and adjust the PIDF values accordingly.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/12/2024
 */
@TeleOp(name="TranslationalPIDF", group="tuners")
@Disabled
public class TranslationalPIDF extends OpMode {
    public static double DISTANCE = 40;
    private boolean forward = true;

    private Path forwards;
    private Path backwards;

    private Follower follower;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 72));
    }

    /** This initializes the Follower and creates the forward and backward Paths. */
    @Override
    public void init_loop() {
        telemetry.addLine("This will activate the translational PIDF(s)");
        telemetry.addLine("The robot will try to stay in place while you push it laterally.");
        telemetry.addLine("You can adjust the PIDF values to tune the robot's translational PIDF(s).");
        telemetry.update();
        follower.update();
    }

    @Override
    public void start() {
        follower.deactivateAllPIDFs();
        follower.activateTranslational();
        forwards = new Path(new BezierLine(new Pose(72,72), new Pose(DISTANCE + 72,72)));
        forwards.setConstantHeadingInterpolation(0);
        backwards = new Path(new BezierLine(new Pose(DISTANCE + 72,72), new Pose(72,72)));
        backwards.setConstantHeadingInterpolation(0);
        follower.followPath(forwards);
    }

    /** This runs the OpMode, updating the Follower as well as printing out the debug statements to the Telemetry */
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

        telemetry.addLine("Push the robot laterally to test the Translational PIDF(s).");
        telemetry.addData("Zero Line", 0);
        telemetry.addData("Error X", follower.errorCalculator.getTranslationalError().getXComponent());
        telemetry.addData("Error Y", follower.errorCalculator.getTranslationalError().getYComponent());
        telemetry.update();
    }
}