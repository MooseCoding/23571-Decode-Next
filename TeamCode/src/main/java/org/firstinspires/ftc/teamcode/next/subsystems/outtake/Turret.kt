package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.core.units.rad
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentHeading
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentX
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentY
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.goalX
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.goalY

import kotlin.math.PI
import kotlin.math.atan2

object Turret: Subsystem {
    private val tele = MultipleTelemetry(FtcDashboard.getInstance().telemetry,ActiveOpMode.telemetry)
    val turret = MotorEx("em1").reversed()
    private val gearRatio = 1.50

    @JvmField var autoTurret = true

    @JvmField var turretPID = PIDCoefficients(1.0,0.0,0.2)
    var turretController = controlSystem {
        posPid(turretPID)
    }

    private val ppr = 537.7 // The resolution of our motor encoder on the goBilda site
    private val rpt = 2* PI /(ppr * gearRatio) // The amount of radians per turn of the motor

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
        val deltaHeading = (mu - currentHeading).rad.normalized
        turretController.goal = KineticState(deltaHeading.inRad.coerceIn(-PI/4.0,PI/4), 0.0)
        turret.power = turretController.calculate(KineticState(getYaw(), 0.0))
    }

    fun getYaw(): Double { // Get the current yaw of the turret from [-pi, pi]
        return (turret.currentPosition * rpt).rad.normalized.inRad
    }
}
