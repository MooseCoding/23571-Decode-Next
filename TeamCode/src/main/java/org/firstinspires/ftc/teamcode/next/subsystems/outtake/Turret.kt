package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
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

@Config
object Turret: Subsystem {
    private lateinit var tele: MultipleTelemetry

    private val leftServo: CRServoEx = CRServoEx("cr0")
    private val rightServo: CRServoEx = CRServoEx("cr1") // Check direction
    private lateinit var encoder: AnalogInput

    @JvmField var autoTurret = false

    @JvmField var turretPID = PIDCoefficients(2.5,0.0,0.0)
    var turretController = controlSystem {
        posPid(turretPID)
    }

    override fun initialize() {
        tele = MultipleTelemetry(FtcDashboard.getInstance().telemetry, ActiveOpMode.telemetry)
        encoder = ActiveOpMode.hardwareMap.analogInput.get("turret")
    }

    private var lastValue = 0.0

    private var pow: Double = 0.0

    @JvmField var coeffs: PIDCoefficients = PIDCoefficients(0.0,0.0,0.0)

    private val offset = 3.0578
    private var currentAngle = 0.0


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
            addData("pos", getYaw())
            addData("current angle", currentAngle)
            addData("pos in deg", 180/PI*getYaw())
            update()
        }
    }

    private fun autoAim() {
        val mu = atan2(goalY - currentY, goalX - currentX)
        val deltaHeading = (mu - currentHeading).rad.normalized.inRad.coerceIn((-125.0/180)*PI, (125.0/180)*PI) // Coerce Properly
        controller.goal = KineticState(deltaHeading)
        pow = controller.calculate(KineticState(currentAngle))
    }

    private fun updateAngle() {
        val pos = encoder.voltage / 3.3 * 2*PI - offset
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
        return (currentAngle) * 90/22.0 * (319/1628.0)
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
