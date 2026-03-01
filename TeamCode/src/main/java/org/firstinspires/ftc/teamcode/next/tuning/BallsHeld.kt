package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.control.interpolators.ConstantInterpolator
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Sensor
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Artifact
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@TeleOp
class BallsHeld: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            BindingsComponent,
            SubsystemComponent(
                Sensor, Transfer, Intake, Outtake
            )
        )
    }

    override fun onInit() {
        Transfer.currentBall = 0
    }

    override fun onStartButtonPressed() {
        Transfer.target = arrayOf(Artifact.GREEN, Artifact.PURPLE, Artifact.PURPLE)

        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.partialIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.cross whenBecomesTrue {
            Transfer.currentBall = 0
            for(i in 0..2) {
                Transfer.ballsHeld[i] = null
            }
        }
    }

    override fun onUpdate() {
        telemetry.run {
            addData("T Running", "Yes")
            addData("getColor()", Sensor.getColor())
            addData("artitier", Sensor.artitimer.elapsedTimeSeconds)
            addData("Transfer Current Ball", Transfer.currentBall)
            for(a in Transfer.ballsHeld) {
                addData("Color", a)
            }
            addData("Hood target", Hood.hoodPosition)
            update()
        }
    }
}