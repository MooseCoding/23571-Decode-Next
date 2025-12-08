package org.firstinspires.ftc.teamcode.next.tuning

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@Disabled
@TeleOp
class FlywheelTuning(): NextFTCOpMode() {
    var tele = JoinedTelemetry(PanelsTelemetry.ftcTelemetry, telemetry)

    init {
        addComponents(
            SubsystemComponent(Intake, Outtake),
            (PedroComponent(Constants::createFollower)),
            BulkReadComponent,
            BindingsComponent,
        )


    }

    override fun onInit() {
        follower.setStartingPose(Pose(144-36.0, 6.5, Math.PI/2))
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.x whenBecomesTrue Outtake.flywheelOn
        Gamepads.gamepad1.y whenBecomesTrue Outtake.flywheelOff
        Gamepads.gamepad1.cross whenBecomesTrue Outtake.outtakeBalls
        Gamepads.gamepad1.dpadUp whenBecomesTrue Outtake.zeroMotor
        Gamepads.gamepad2.rightBumper whenBecomesTrue  Outtake.spinGearRight whenBecomesFalse Outtake.stopGear
        Gamepads.gamepad2.leftBumper whenBecomesTrue  Outtake.spinGearLeft whenBecomesFalse Outtake.stopGear
    }

    override fun onUpdate() {
        tele.run {
            addData("current X", Outtake.currentX)
            addData("current y", Outtake.currentY)
            addData("current H", Outtake.currentHeading)
            addData("f1P", Outtake.f1.power)
            addData("f1V", Outtake.f1.velocity)
            addData("f2V", Outtake.f2.velocity)
            addData("kinetic state", Outtake.f1.state)
            addData("controller", Outtake.controller)
            addData("controller value", Outtake.controller.calculate(Outtake.f1.state))
            addData("targetV", Outtake.targetOnVelo)
            addData("gear pos", Outtake.gP)
            addData("iP", Intake.iP)
            addData("spin power", Outtake.spin.power)
            addData("spin velo", Outtake.spin.velocity)
            addData("spijn pos", Outtake.spin.currentPosition)
            addData("currentAngle", Outtake.turrentAngle)
            addData("prev angle", Outtake.prevAngle)
            addData("total angle", Outtake.totalAngle)
            addData("d heading", Outtake.dHeading)
            addData("outtake turret", Outtake.turretHeading)
            addData("target", Outtake.targetHeading)
            addData("target x", Outtake.xcord)
            addData("target y", Outtake.ycord)
            addData("goal", Outtake.sC.goal)
            addData("Dist", Outtake.dist)
            addData("flap pos", Outtake.hP)
            addData("test", "true")
            update()
        }
    }
}