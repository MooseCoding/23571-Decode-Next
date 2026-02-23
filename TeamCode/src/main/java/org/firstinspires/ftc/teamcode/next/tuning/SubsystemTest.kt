package org.firstinspires.ftc.teamcode.next.tuning

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.NextFTCOpMode
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI

@TeleOp
class SubsystemTest: NextFTCOpMode() {
    init {
        addComponents(
            BindingsComponent,
            PedroComponent(Constants::createFollower),
            SubsystemComponent(
                Intake, Transfer, Outtake
            )
        )
    }

    override fun onInit() {
        PedroComponent.follower.setStartingPose(Pose(72.0, 72.0, PI/2))
    }
}