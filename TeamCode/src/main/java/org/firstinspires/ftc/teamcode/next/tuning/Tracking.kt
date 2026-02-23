package org.firstinspires.ftc.teamcode.next.tuning

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.NextFTCOpMode
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI

@TeleOp
class Tracking: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            BindingsComponent,
            SubsystemComponent(Turret)
        )
    }

    override fun onStartButtonPressed() {
        PedroComponent.follower.setStartingPose(Pose(72.0, 72.0, PI/2))
    }
}