package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
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
            SubsystemComponent(Intake, Transfer, FlywheelPID),
            BulkReadComponent,
            BindingsComponent
        )
    }
    val hood: ServoEx = ServoEx("s0")

    @JvmField var hoodP = 0.2

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.a whenBecomesTrue InstantCommand {FlywheelPID.targetVelo = 200.0}
        Gamepads.gamepad1.b whenBecomesTrue InstantCommand {FlywheelPID.targetVelo = 0.0}
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.runIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftBumper whenBecomesTrue Transfer.start()
        Gamepads.gamepad1.rightBumper whenBecomesTrue Transfer.stop()
        Gamepads.gamepad1.dpadUp whenBecomesTrue { Transfer.transferMotor.power=0.3 } whenBecomesFalse InstantCommand{ Transfer.transferMotor.power=0.0 }
    }

    override fun onUpdate() {
        // hood.position = hoodP
        telemetry.run {
            addData("f1m.velo", FlywheelPID.f1.velocity)
            addData("f2m.velo", FlywheelPID.f2.velocity)
            addData("hood pose", hood.position)
            addData("is this working?", hood.position)
            update()
        }
    }
}