package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.DcMotor
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.core.units.rad
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.CRServoEx
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentHeading
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentX
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentY
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.goalX
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.goalY

import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.atan2

@Config
object Turret: Subsystem {
    private lateinit var tele: MultipleTelemetry

    private val leftServo: CRServoEx = CRServoEx("dS2")
    private val rightServo: CRServoEx = CRServoEx("dS1") // Check direction
    val absEncoder: AnalogInput by lazy { ActiveOpMode.hardwareMap.analogInput.get("aS") }

    val encoder: MotorEx = MotorEx("fR")  // Figure out motor name

    @JvmField var autoTurret = false

    @JvmField var turretPID = PIDCoefficients(0.2,0.0,0.0)

    var controller = controlSystem {
        posPid(turretPID)
    }

    override fun initialize() {
        tele = MultipleTelemetry(FtcDashboard.getInstance().telemetry, ActiveOpMode.telemetry)
    }

    private var lastValue = 0.0

    private var pow: Double = 0.0

    @JvmField var coeffs: PIDCoefficients = PIDCoefficients(0.0,0.0,0.0)

    private val offset = 3.0578
    private var currentAngle = 0.0

    @JvmField var maxAngle = 90.0

    override fun periodic() {
        updateRelative()

        // updateAbsolute()

        /*
        if(autoTurret) {
            autoAim()
        }

        if(!autoTurret && pow.absoluteValue != 1.0) {
            pow = 0.0
        }
        */
        leftServo.power = pow
        rightServo.power = pow
        ActiveOpMode.telemetry.run {
            addData("yaw", getYaw())
            addData("encoder", encoder.currentPosition)
        }
    }

    private fun autoAim() {
        val mu = atan2(goalY - currentY, goalX - currentX)
        val deltaHeading = (mu - currentHeading).rad.normalized.inRad.coerceIn((-maxAngle/180)*PI, (maxAngle/180)*PI) // Coerce Properly
        controller.goal = KineticState(deltaHeading)
        pow = controller.calculate(KineticState(currentAngle)).coerceIn(-0.5, 0.5)
    }

    private fun updateAbsolute() {
        val pos = absEncoder.voltage / 3.3 * 2*PI - offset
        var delta = pos - lastValue

        if(delta > PI) delta -= 2*PI
        else if(delta < -PI) delta += 2*PI

        currentAngle += delta
        lastValue = pos
    }

    private fun updateRelative() {
        currentAngle = encoder.currentPosition * 360.0 / 4000.0
    }

    /**
     * @return yaw in degrees from servo position
     */
    fun getYaw(): Double {
        return (currentAngle) * 0.725
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

    /**
     * @return A [Command] to zero the encoder
     */
    fun zero(): Command = InstantCommand {
        encoder.motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
    }
}
