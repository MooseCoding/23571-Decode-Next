package org.firstinspires.ftc.teamcode.next.tuning

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.hardware.impl.ServoEx
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Light
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret

@TeleOp
@Config
class TestShooter: NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(Intake, Transfer, Light, Outtake),
            BulkReadComponent,
            BindingsComponent
        )
    }
    val f1 = MotorEx("f1")

    val servo = ServoEx("hood")
    companion object {
        @JvmField
        var hoodPos = 0.50
        @JvmField var flywheelVelocity = 1350.0
    }
    override fun onStartButtonPressed() {
        Gamepads.gamepad1.rightBumper whenBecomesTrue Transfer.start() whenBecomesFalse Transfer.stop()
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.runIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.triangle whenBecomesTrue Flywheels.spin()
        Gamepads.gamepad1.cross whenBecomesTrue Outtake.shoot()
    }

    override fun onUpdate() {
        Flywheels.targetVelocity = flywheelVelocity
        servo.position = hoodPos
        telemetry.run {
            addData("f1m.velo", Flywheels.f1.velocity)
            addData("hood pos", servo.position)
            addData("target Velocity", Flywheels.targetVelocity)
            update()
        }
    }
}