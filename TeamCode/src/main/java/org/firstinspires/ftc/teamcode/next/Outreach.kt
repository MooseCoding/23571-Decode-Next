package org.firstinspires.ftc.teamcode.next

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@TeleOp
class Outreach: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(Intake, Outtake, DriveTrain),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.runIntake whenBecomesFalse Intake.stopIntake
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake whenBecomesFalse Intake.stopIntake

        Gamepads.gamepad2.rightTrigger.greaterThan(0.3) whenBecomesTrue Outtake.aimUp
        Gamepads.gamepad2.leftTrigger.greaterThan(0.3) whenBecomesTrue Outtake.aimDown

        Gamepads.gamepad2.a whenBecomesTrue SequentialGroup(InstantCommand{ Outtake.canSpin = true}, Outtake.flywheelOn)
        Gamepads.gamepad2.b whenBecomesTrue SequentialGroup(InstantCommand { Outtake.canSpin = false }, Outtake.flywheelOff)

        Gamepads.gamepad2.rightBumper whenBecomesTrue  Outtake.spinGearRight whenBecomesFalse Outtake.stopGear
        Gamepads.gamepad2.leftBumper whenBecomesTrue  Outtake.spinGearLeft whenBecomesFalse Outtake.stopGear
        Gamepads.gamepad2.dpadRight whenBecomesTrue  Outtake.gearAlittleLeft whenBecomesFalse Outtake.stopGear
        Gamepads.gamepad2.dpadLeft whenBecomesTrue  Outtake.gearAlittleRight whenBecomesFalse Outtake.stopGear

        Gamepads.gamepad2.dpadUp whenBecomesTrue Outtake.FlapDown
        Gamepads.gamepad2.dpadDown whenBecomesTrue Outtake.FlapUp
    }

    override fun onUpdate() {
        telemetry.run {
            addData("Hood Position ", Outtake.hP)
            addData("Power ", Outtake.targetVelo)
            addData("Distance in Tiles ", Outtake.manualAim/24.0)
            addData("Manual Mode ", Outtake.manualOn)
            addData("Can Shoot", Outtake.canSpin)
            update()
        }
    }
}