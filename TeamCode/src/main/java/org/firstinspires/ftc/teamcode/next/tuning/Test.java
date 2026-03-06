package org.firstinspires.ftc.teamcode.next.tuning;

import androidx.core.util.TypedValueCompat;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.Locale;

import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;


@Autonomous(name = "Path Skip Debug", group = "Debug")
@Disabled
public class Test extends NextFTCOpMode {

    private static final double START_X = 72;
    private static final double START_Y = 24;
    private static final double START_HEADING = 0;

    // Straight line: drive forward ~48 inches, then right ~48 inches
    private static final double END_X = 72;
    private static final double END_Y = 72;
    private static final double RIGHT_X = 120;
    private static final double RIGHT_Y = 72;

    private PathChain forwardPath;
    private PathChain backPath;

    double LOOP_TIME = 0;
    ElapsedTime LOOP_TIMER = new ElapsedTime();

    public Test() {
        addComponents(
                BindingsComponent.INSTANCE,
                new PedroComponent(Constants::createFollower)
        );
    }

    @Override
    public void onInit() {
        Pose start = new Pose(START_X, START_Y, Math.toRadians(START_HEADING));
        Pose end   = new Pose(END_X,   END_Y,   Math.toRadians(START_HEADING));
        Pose right = new Pose(RIGHT_X, RIGHT_Y, Math.toRadians(START_HEADING));

        PedroComponent.follower().setStartingPose(start);

        forwardPath = PedroComponent.follower().pathBuilder()
                .addPath(new BezierLine(start, end))
                .setConstantHeadingInterpolation(Math.toRadians(START_HEADING))
                .build();

        backPath = PedroComponent.follower().pathBuilder()
                .addPath(new BezierLine(end, right))
                .setConstantHeadingInterpolation(Math.toRadians(START_HEADING))
                .build();

        telemetry.addLine("Path Skip Debug ready. Press Start to run.");
        telemetry.update();
    }

    @Override
    public void onStartButtonPressed() {
        new FollowPath(forwardPath, true)
                .then(new FollowPath(backPath, true))
                .schedule();
    }

    @Override
    public void onUpdate() {
        LOOP_TIMER.reset();

        PedroComponent.follower().update();

        if (PedroComponent.follower().isBusy()) {
            double dotProduct = PedroComponent.follower()
                    .getVectorCalculator()
                    .getDriveVector()
                    .dot(PedroComponent.follower().getClosestPointTangentVector());

            double distanceRemaining = PedroComponent.follower().getDistanceRemaining();

            RobotLog.d("PathSkip | dot=%.4f | distRemaining=%.3f in", dotProduct, distanceRemaining); //this is where im logging

            telemetry.addData("DriveÂ·Tangent dot", String.format(Locale.US, "%.4f", dotProduct));
            telemetry.addData("Distance Remaining (in)", String.format(Locale.US, "%.3f", distanceRemaining));
        } else {
            telemetry.addLine("Path finished / idle");
        }

        Pose pose = PedroComponent.follower().getPose();
        telemetry.addData("X", String.format(Locale.US, "%.3f", pose.getX()));
        telemetry.addData("Y", String.format(Locale.US, "%.3f", pose.getY()));
        telemetry.addData("Heading (deg)", String.format(Locale.US, "%.2f", Math.toDegrees(pose.getHeading())));
        telemetry.addData("Path #", PedroComponent.follower().getCurrentPathNumber());
        telemetry.addData("Loop (ms)", String.format(Locale.US, "%.2f", LOOP_TIME));
        telemetry.update();

        LOOP_TIME = LOOP_TIMER.milliseconds();
    }
}