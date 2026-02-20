package org.firstinspires.ftc.teamcode.pedroPathing.tuners;

import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.stopRobot;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.ArrayList;

/**
 * This is the ForwardVelocityTuner autonomous follower OpMode. This runs the robot forwards at max
 * power until it reaches some specified distance. It records the most recent velocities, and on
 * reaching the end of the distance, it averages them and prints out the velocity obtained. It is
 * recommended to run this multiple times on a full battery to get the best results. What this does
 * is, when paired with StrafeVelocityTuner, allows FollowerConstants to create a Vector that
 * empirically represents the direction your mecanum wheels actually prefer to go in, allowing for
 * more accurate following.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 3/13/2024
 */
@TeleOp(name="ForwardVelocity", group="tuners")
@Disabled
public class ForwardVelocity extends OpMode {
    private final ArrayList<Double> velocities = new ArrayList<>();
    static ArrayList<String> changes = new ArrayList<>();
    public static double DISTANCE = 48;
    public static double RECORD_NUMBER = 10;

    private boolean end;
    private Follower follower;

    @Override
    public void init() {

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 72));
    }

    /** This initializes the drive motors as well as the cache of velocities and the Panels telemetry. */
    @Override
    public void init_loop() {
        telemetry.addLine("The robot will run at 1 power until it reaches " + DISTANCE + " inches forward.");
        telemetry.addLine("Make sure you have enough room, since the robot has inertia after cutting power.");
        telemetry.addLine("After running the distance, the robot will cut power from the drivetrain and display the forward velocity.");
        telemetry.addLine("Press B on game pad 1 to stop.");
        telemetry.addData("pose", follower.getPose());
        telemetry.update();
        follower.update();
    }

    /** This starts the OpMode by setting the drive motors to run forward at full power. */
    @Override
    public void start() {
        for (int i = 0; i < RECORD_NUMBER; i++) {
            velocities.add(0.0);
        }
        follower.startTeleopDrive(true);
        follower.update();
        end = false;
    }

    /**
     * This runs the OpMode. At any point during the running of the OpMode, pressing B on
     * game pad 1 will stop the OpMode. This continuously records the RECORD_NUMBER most recent
     * velocities, and when the robot has run forward enough, these last velocities recorded are
     * averaged and printed.
     */
    @Override
    public void loop() {
        if (gamepad1.bWasPressed()) {
            stopRobot(follower);
            requestOpModeStop();
        }

        follower.update();

        if (!end) {
            if (Math.abs(follower.getPose().getX()) > (DISTANCE + 72)) {
                end = true;
                stopRobot(follower);
            } else {
                follower.setTeleOpDrive(1,0,0,true);
                //double currentVelocity = Math.abs(follower.getVelocity().getXComponent());
                double currentVelocity = Math.abs(follower.poseTracker.getLocalizer().getVelocity().getX());
                velocities.add(currentVelocity);
                velocities.remove(0);
            }
        } else {
            stopRobot(follower);
            double average = 0;
            for (double velocity : velocities) {
                average += velocity;
            }
            average /= velocities.size();
            telemetry.addLine("Forward Velocity: " + average);
            telemetry.addLine("\n");
            telemetry.addLine("Press A to set the Forward Velocity temporarily (while robot remains on).");

            for (int i = 0; i < velocities.size(); i++) {
                telemetry.addData(String.valueOf(i), velocities.get(i));
            }

            telemetry.update();
            telemetry.update();

            if (gamepad1.aWasPressed()) {
                follower.setXVelocity(average);
                String message = "XMovement: " + average;
                changes.add(message);
            }
        }
    }
}