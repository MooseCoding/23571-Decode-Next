package org.firstinspires.ftc.teamcode.next.tuning

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@Config
@TeleOp
class NewFlywheel: NextFTCOpMode() {
    init {
        addComponents(
            BulkReadComponent,
            BindingsComponent,
        )
    }

    val f1M = MotorEx("f1M")
    val f2M = MotorEx("f2M").reversed()
    var pid = false

    companion object {
        @JvmField
        var veloTarget = -2000.0
    }

    @JvmField var flywheelPID = PIDCoefficients(0.005, 0.0, 0.0)
    @JvmField var flywheelFF = BasicFeedforwardParameters(1/1800.0 , 0.0, 0.07)
    private var control = controlSystem {
        velPid(flywheelPID)
        basicFF(flywheelFF)
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.a whenBecomesTrue InstantCommand {  pid = true }
        Gamepads.gamepad1.b whenBecomesTrue InstantCommand { pid = false; f1M.power=0.0; f2M.power=0.0;}
    }

    override fun onUpdate() {
        if (pid) {
            control.goal = KineticState(0.0, veloTarget)
            f1M.power = control.calculate(f1M.state)
            f2M.power = f1M.power
        }
        else {
            f1M.power = 0.0
            f2M.power = 0.0
        }
        telemetry.addData("gamepad1 left stick", gamepad1.left_stick_x)
        telemetry.addData("pid", pid)
        telemetry.addData("motor velo", f1M.velocity)
        telemetry.addData("flywheel power", control.calculate(f1M.state))
        telemetry.update()
    }
}