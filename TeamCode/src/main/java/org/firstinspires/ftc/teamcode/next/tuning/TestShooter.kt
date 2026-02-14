package org.firstinspires.ftc.teamcode.next.tuning

import com.acmerobotics.dashboard.config.Config
import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroDriverControlled
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.driving.DriverControlledCommand
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI

@Config
@TeleOp

class TestShooter: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            BindingsComponent,
            BulkReadComponent,
            SubsystemComponent(Intake, Hood, Flywheels, Transfer)
        )
    }

    val fL = MotorEx("fL")
    val fR = MotorEx("fR")
    val bL = MotorEx("bL")
    val bR = MotorEx("bR")

    val alliance: Alliance = Alliance.BLUE

    lateinit var driveTrain: DriverControlledCommand

    override fun onWaitForStart() {

    }

    override fun onInit() {
        Turret.autoTurret = false
    }


    override fun onStartButtonPressed() {
        driveTrain = PedroDriverControlled(
            -Gamepads.gamepad1.leftStickY,
            Gamepads.gamepad1.leftStickX,
            Gamepads.gamepad1.rightStickX
        )
        driveTrain.scalar = 1.0

        // Intake
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.runIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake() whenBecomesFalse Intake.stopIntake()

        // Shoot
        Gamepads.gamepad1.triangle whenBecomesTrue Outtake.shoot()

        Gamepads.gamepad1.leftBumper whenBecomesTrue Transfer.reverse() whenBecomesFalse Transfer.stop()
        Gamepads.gamepad1.rightBumper whenBecomesTrue Transfer.start() whenBecomesFalse Transfer.stop()

        Gamepads.gamepad1.dpadUp whenBecomesTrue {Flywheels.targetVelocity += 50}
        Gamepads.gamepad1.dpadDown whenBecomesTrue {Flywheels.targetVelocity -= 50}
        Gamepads.gamepad1.dpadLeft whenBecomesTrue {Hood.hoodPosition -= 0.1}
        Gamepads.gamepad1.dpadRight whenBecomesTrue {Hood.hoodPosition += 0.1}

        // Maybe holding Buttons slows down bot for endgame/precision

        Gamepads.gamepad2.options whenBecomesTrue InstantCommand {
            when(DriveTrain.alliance) {
                Alliance.RED -> {
                    PedroComponent.follower.pose = Pose(8.0, 8.0, PI/2)
                }
                Alliance.BLUE -> {
                    PedroComponent.follower.pose = Pose(144-8.0, 8.0, PI/2)
                }
            }
        }

        //  Gamepads.gamepad2.rightStickButton whenBecomesTrue HeadingLock()
    }

    override fun onUpdate() {
        telemetry.addData("Velo", Flywheels.targetVelocity)
        telemetry.addData("HOod", Hood.hoodPosition)
        telemetry.update()
    }
}