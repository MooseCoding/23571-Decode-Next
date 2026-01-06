package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.hardware.impl.ServoEx
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer

@TeleOp
class TestShooter: NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(Intake, Transfer),
            BulkReadComponent,
            BindingsComponent
        )
    }

    val f1: MotorEx = MotorEx("em0")
    val f2: MotorEx = MotorEx("em1").reversed()
    val hood: ServoEx = ServoEx("s0")

    @JvmField var hoodP = 0.2

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.a whenBecomesTrue InstantCommand { f1.power = 1.0; f2.power = 1.0}
        Gamepads.gamepad1.b whenBecomesTrue InstantCommand { f1.power = 0.0; f2.power = 0.0}
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.runIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftBumper whenBecomesTrue Transfer.start()
        Gamepads.gamepad1.rightBumper whenBecomesTrue Transfer.stop()
        Gamepads.gamepad1.dpadUp whenBecomesTrue { Transfer.transferMotor.power=0.3 } whenBecomesFalse InstantCommand{ Transfer.transferMotor.power=0.0 }
    }

    override fun onUpdate() {
        // hood.position = hoodP
        telemetry.run {
            addData("f1m.velo", f1.velocity)
            addData("f2m.velo", f2.velocity)
            addData("hood pose", hood.position)
            update()
        }
    }
}