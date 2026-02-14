package org.firstinspires.ftc.teamcode.next.tuning

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels.spinSlow
import kotlin.math.absoluteValue

@TeleOp
@Configurable
@Disabled
class FlywheelTuner: NextFTCOpMode() {
    init {
        addComponents(
            BindingsComponent,
            BulkReadComponent
        )
    }

    val f1 = MotorEx("f1")
    val f2 = MotorEx("f2").reversed()

    companion object {

    @JvmField
    var flywheelPID = PIDCoefficients(0.00003, 0.0, 0.0)
    @JvmField
    var flywheelFF = BasicFeedforwardParameters(1 / 2400.0, 0.0, 0.3)
    private var flywheelController = controlSystem {
        velPid(flywheelPID)
        basicFF(flywheelFF)
    }

    @JvmField
    var targetVelocity = 1350.0
}

    var isShooting: Boolean = false

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.triangle.whenBecomesTrue { isShooting = !isShooting }
    }


    override fun onUpdate() {
        if (isShooting) {
            f1.power = flywheelController.calculate(KineticState(0.0, f1.velocity.absoluteValue))
            f2.power = f1.power
            flywheelController.goal = KineticState(0.0, targetVelocity)
        }
        PanelsTelemetry.telemetry.run {
            addData("Target Velocity", targetVelocity)
            addData("Current Velocity", f1.velocity.absoluteValue)
            update()
        }
    }
}