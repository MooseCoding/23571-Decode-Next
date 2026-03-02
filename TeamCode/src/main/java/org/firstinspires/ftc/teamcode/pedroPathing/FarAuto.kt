package org.firstinspires.ftc.teamcode.pedroPathing

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.FollowPath
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.alliance
import org.firstinspires.ftc.teamcode.next.subsystems.FlywheelLight
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Light
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import kotlin.time.Duration.Companion.seconds

@Autonomous(preselectTeleOp = "TeleOp")
@Configurable
class FarAuto: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(DriveTrain, Intake, Transfer, Outtake, FlywheelLight, Light),
            BulkReadComponent,
            BindingsComponent,
        )
    }


    val poses: NewPoses =  NewPoses()

    override fun onInit() {
        Turret.autoTurret = true
    }

    override fun onWaitForStart() {
        if (gamepad1.triangle) { alliance = Alliance.RED }
        if (gamepad1.circle) { alliance = Alliance.BLUE }


        telemetry.run {
            addData("Alliance ", alliance)
            addData("follower X", follower.pose.x)
            addData("follower Y", follower.pose.y)
            addData("follower heading", follower.heading)
            update()
        }

        when(alliance) {
            Alliance.RED -> {
                FlywheelLight.Red().schedule()
            }
            Alliance.BLUE -> {
                FlywheelLight.Blue().schedule()
            }
        }
    }


    override fun onStop() {
    }

    /**
     * Executes the autonomous sequence once the start button is pressed.
     * The sequence involves:
     * 1. Initial shot.
     * 2. Intaking and scoring from Row 2 and Row 1.
     * 3. Continuous human player intake cycles until the end of the match.
     * 4. Parking.
     */
    override fun onStartButtonPressed() {
        if(alliance == Alliance.RED) {
            poses.flipPose()
        }

        follower.pose = (poses.farStart)


        SequentialGroupLocal(
            Flywheels.spin(),
            Delay(1.5.seconds),
            Outtake.shoot(),
            FollowPath(poses.farIntake),
            Delay(0.3.seconds),
            Outtake.shootFar(),
            FollowPath(poses.humanPlayer),
            Delay(0.4.seconds),
            FollowPath(poses.humanPlayerShoot),
            Outtake.shootFar(), // 9 ball
            FollowPath(poses.rampIntake),
            Delay(0.4.seconds),
            FollowPath(poses.rampShoot),
            Outtake.shootFar(), // 12 ball
            FollowPath(poses.rampIntake),
            Delay(0.4.seconds),
            FollowPath(poses.rampShoot),
            Outtake.shootFar(), // 15 ball
            FollowPath(poses.rampIntake),
            Delay(0.4.seconds),
            FollowPath(poses.rampShoot),
            Outtake.shootFar(), // 18 ball
            FollowPath(poses.rampIntake),
            Delay(0.4.seconds),
            FollowPath(poses.rampShoot),
            Outtake.shootFar(), // 21 ball
            FollowPath(poses.humanPlayer), // Park
        ).schedule()

    }

    public override fun onUpdate() {
        telemetry.run {
            addData("follower X", follower.pose.x)
            addData("follower Y", follower.pose.y)
            addData("follower heading", follower.heading)
            update()
        }
    }
}
