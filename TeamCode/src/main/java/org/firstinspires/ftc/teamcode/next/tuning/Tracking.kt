package org.firstinspires.ftc.teamcode.next.tuning

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max

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

    val fl = MotorEx("fL")// Yse
    val fr = MotorEx("fR")
    val bl = MotorEx("bL")
    val br = MotorEx("bR")// Yes


    override fun onUpdate() {
        val y = -gamepad1.left_stick_y
        val x = gamepad1.left_stick_x
        val t = gamepad1.right_stick_x

        val d = max((abs(y) + abs(x) + abs(t)).toDouble(), 1.0)
        fl.power = (y + x + t) / d
        fr.power = (y - x - t) / d
        bl.power = (y - x + t) / d
        br.power = (y + x - t) / d
    }
}