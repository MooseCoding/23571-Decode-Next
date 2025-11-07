package org.firstinspires.ftc.teamcode.pedroPathing

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import kotlinx.coroutines.selects.whileSelect
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.data.Alliance
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor.PoseSolver

@Autonomous
@Configurable
class BetterAuto: NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(Intake, Outtake, DriveTrain),
            BulkReadComponent,
            BindingsComponent,
            PedroComponent(Constants::createFollower)
        )
    }

    @JvmField
    var all: Alliance = Alliance.RED

    public override fun onWaitForStart() {
        Gamepads.gamepad1.a whenBecomesTrue { all = Alliance.RED }
        Gamepads.gamepad1.b whenBecomesTrue  { all = Alliance.BLUE }
        telemetry.run {
            addData("Alliance ", all)
            update()
        }
    }

    lateinit var p:Far12

    public override fun onInit() {
        p = Far12(follower, all)
        follower.setStartingPose(Far12.start)
    }

    public override fun onStartButtonPressed() {
        SequentialGroup(
            p.ShootStart,
            p.StartToRow1,
            p.Row1Intake,
            p.Row1ToShoot,
            p.ShootToRow2,
            p.Row2Intake,
            p.Row2ToShoot,
            p.ShootToRow3,
            p.Row3Intake,
            p.Row3ToShoot,
            p.ToPark,
        ).schedule()
    }
}