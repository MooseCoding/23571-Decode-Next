package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.hardware.Servo
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.core.units.deg
import dev.nextftc.core.units.rad
import dev.nextftc.ftc.ActiveOpMode
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

    private val leftServo: ServoEx = ServoEx("s0")
    private val rightServo: ServoEx = ServoEx("s1") // Check direction

    @JvmField var autoTurret = true

    @JvmField var turretPID = PIDCoefficients(2.5,0.0,0.0)
    var turretController = controlSystem {
        posPid(turretPID)
    }

    override fun periodic() {
        if(autoTurret) {
            autoAim()
        }

        tele.run {
            addData("goal", turretController.goal.position)
            addData("turret Pos", getYaw())
        }
    }

    private fun autoAim() {
        val mu = atan2(goalY - currentY, goalX - currentX)
        val deltaHeading = (mu - currentHeading).rad.normalized.inRad.coerceIn((-160.0/180)*PI, (160.0/180)*PI) // Coerce Properly
        val pos = convertToServo(deltaHeading)
        leftServo.position = pos
        rightServo.position = pos
    }

    /**
     * @return yaw in radians from servo position
     */
    fun getYaw(): Double {
        return leftServo.position*320.0 - 160.0
    }

    /**
     * @param t: Double in [-160,160]
     * @return Double in [0, 1] for servo position
     */
    fun convertToServo(t:Double): Double {
        return t/320.0 + 0.5
    }
}
