package org.firstinspires.ftc.teamcode.pedroPathing

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.utility.InstantCommand
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.alliance
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Motif

@Autonomous
@Configurable
class FarAuto: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(DriveTrain),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    lateinit var poses: Far12

    public override fun onWaitForStart() {
        Gamepads.gamepad1.a whenBecomesTrue { alliance = Alliance.RED }
        Gamepads.gamepad1.b whenBecomesTrue  { alliance = Alliance.BLUE }
        telemetry.run {
            addData("Alliance ", alliance)
            addData("follower X", follower.pose.x)
            addData("follower Y", follower.pose.y)
            addData("follower heading", follower.heading)

            update()
        }
    }

    public override fun onInit() {
        // follower.setStartingPose(Far12.start)
    }

    override fun waitForStart() {

    }

    override fun onStop() {
    }

    public override fun onStartButtonPressed() {
        val p = Far12(alliance)
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