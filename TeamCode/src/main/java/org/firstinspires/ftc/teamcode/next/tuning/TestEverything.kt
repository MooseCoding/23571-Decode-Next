package org.firstinspires.ftc.teamcode.next.tuning

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@TeleOp
class TestEverything: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(Turret, Outtake),
            BindingsComponent,
            BulkReadComponent
        )
    }

    override fun onStartButtonPressed() {

    }

    override fun onInit() {
        PedroComponent.follower.setStartingPose(Pose(72.0, 72.0, Math.PI/2))
    }

    override fun onUpdate() {
        telemetry.run {
            addData("X", PedroComponent.follower.pose.x)
            addData("Y", PedroComponent.follower.pose.y)
            addData("H", PedroComponent.follower.pose.heading)
            update()
        }
    }
}