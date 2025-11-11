package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentHeading
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentX
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentY
import org.firstinspires.ftc.teamcode.next.subsystems.NewOuttake.goalX
import org.firstinspires.ftc.teamcode.next.subsystems.NewOuttake.goalY

import org.firstinspires.ftc.teamcode.next.subsystems.data.Alliance
import kotlin.math.PI
import kotlin.math.atan2

object Turret: Subsystem {
    private val turret = MotorEx("turret")
    private val gearRatio = 3.47

    @JvmField var autoTurret = true



    @JvmField var turretPID = PIDCoefficients(0.8,0.0,0.0)
    private var turretController = controlSystem {
        posPid(turretPID)
    }

    private val ppr = 537.7 // The resolution of our motor encoder on the goBilda site
    private val rpt = 2* PI /(ppr * gearRatio) // The amount of radians per turn of the motor


    override fun periodic() {
        if(autoTurret) {
            autoAim()
        }
    }

    private fun autoAim() {
        val mu = atan2(goalY - currentY, goalX - currentX)
        val deltaHeading = normalizeAngle(mu - currentHeading)
        val clampedHeading = deltaHeading.coerceIn(-PI, PI)
        turretController.goal = KineticState(clampedHeading, 0.0)
    }

    fun goToYaw(yaw:Double) { // Go to a specific position
        turretController.goal = KineticState(yaw, 0.0)
    }

    fun getYaw(): Double { // Get the current yaw of the turret from [-pi, pi]
        return normalizeAngle(turret.currentPosition * rpt)
    }


    fun normalizeAngle(angleRadians: Double): Double { // Returns a normalized angle between [-pi, pi]
        var angle = angleRadians % (2.0 * PI)
        if (angle <= -PI) {
            angle += 2.0 * PI
        }
        if (angle > PI) {
            angle -= 2.0 * PI
        }
        return angle
    }
}