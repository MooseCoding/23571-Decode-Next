package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Sensor
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import dev.nextftc.core.components.BindingsComponent

@TeleOp
class SensorTest: NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(Sensor, Intake, Transfer),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.runIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftBumper whenBecomesTrue Transfer.start()
        Gamepads.gamepad1.rightBumper whenBecomesTrue Transfer.stop()
        Gamepads.gamepad1.dpadUp whenBecomesTrue { Transfer.transferMotor.power=0.3 } whenBecomesFalse InstantCommand{ Transfer.transferMotor.power=0.0 }
    }

    override fun onUpdate() {
        telemetry.run {
            update()
        }
    }
}