package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.Servo
import dev.nextftc.control.ControlSystem
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.core.units.deg
import dev.nextftc.core.units.rad
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.CRServoEx
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.hardware.impl.ServoEx
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentHeading
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentX
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentY
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.goalX
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.goalY

import kotlin.math.PI
import kotlin.math.atan2

object Turret: Subsystem {
    private val tele = MultipleTelemetry(FtcDashboard.getInstance().telemetry,ActiveOpMode.telemetry)

    private val leftServo: CRServoEx = CRServoEx("cr0")
    private val rightServo: CRServoEx = CRServoEx("cr1") // Check direction
    private val encoder: AnalogInput = ActiveOpMode.hardwareMap.analogInput.get("turret")

    @JvmField var autoTurret = true

    @JvmField var turretPID = PIDCoefficients(2.5,0.0,0.0)
    var turretController = controlSystem {
        posPid(turretPID)
    }

    private var currentAngle = 0.0
    private var lastValue = 0.0

    private var pow: Double = 0.0

    @JvmField var coeffs: PIDCoefficients = PIDCoefficients(0.0,0.0,0.0)

    private var controller: ControlSystem = controlSystem {
        velPid(coeffs)
    }

    override fun periodic() {
        updateAngle()

        if(autoTurret) {
            autoAim()
        }

        leftServo.power = pow
        rightServo.power = pow

        tele.run {
            addData("goal", turretController.goal.position)
            addData("turret Pos", getYaw())
        }
    }

    private fun autoAim() {
        val mu = atan2(goalY - currentY, goalX - currentX)
        val deltaHeading = (mu - currentHeading).rad.normalized.inRad.coerceIn((-160.0/180)*PI, (160.0/180)*PI) // Coerce Properly
        controller.goal = KineticState(deltaHeading)
        pow = controller.calculate(KineticState(currentAngle))
    }

    private fun updateAngle() {
        val pos = encoder.voltage / 3.3
        var delta = pos - lastValue

        if(delta > PI) delta -= 2*PI
        else if(delta < -PI) delta += 2*PI

        currentAngle += delta
        lastValue = pos
    }

    /**
     * @return yaw in radians from servo position
     */
    fun getYaw(): Double {
        return currentAngle
    }

    /**
     * @return Spins a little left
     */
    fun spinLeft(): Command = InstantCommand {
        pow = -0.5
    }

    /**
     * @return Spins a little right
     */
    fun spinRight(): Command = InstantCommand {
        pow = 0.5
    }

    /**
     * @return Stops spinning
     */
    fun stopSpin(): Command = InstantCommand {
        pow = 0.0
    }
}
