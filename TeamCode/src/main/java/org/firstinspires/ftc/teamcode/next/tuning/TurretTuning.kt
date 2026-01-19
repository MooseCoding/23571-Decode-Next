package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.CRServoEx
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret

@TeleOp
class TurretTuning: NextFTCOpMode() {
    init {
        addComponents(
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
    }

    override fun onUpdate() {
        telemetry.run {
            addData("Yaw", Turret.getYaw())
            addData("Yaw", Turret.encoder.voltage/3.3 * 360)
            update()
        }
    }
}