package org.firstinspires.ftc.teamcode.next.tuning

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.CRServoEx
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@TeleOp

class TurretTuning: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(
                Turret
            ),
            BindingsComponent,
            BulkReadComponent
        )
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.leftBumper whenBecomesTrue Turret.spinLeft() whenBecomesFalse Turret.stopSpin()
        Gamepads.gamepad1.rightBumper whenBecomesTrue  Turret.spinRight() whenBecomesFalse Turret.stopSpin()
        Gamepads.gamepad1.triangle whenBecomesTrue Turret.zero()
        Gamepads.gamepad1.cross whenBecomesTrue { Turret.autoTurret = false } whenBecomesFalse { Turret.autoTurret = true }
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue { Turret.pow = 1.0 } whenBecomesFalse { Turret.pow = 0.0 }
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