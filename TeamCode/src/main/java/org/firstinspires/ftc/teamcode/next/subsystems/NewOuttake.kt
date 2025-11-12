package org.firstinspires.ftc.teamcode.next.subsystems

import dev.nextftc.core.subsystems.SubsystemGroup
import org.firstinspires.ftc.teamcode.helpers.getIndex
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentX
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentY
import org.firstinspires.ftc.teamcode.next.subsystems.data.Aimbot
import org.firstinspires.ftc.teamcode.next.subsystems.data.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Spindexer
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import kotlin.math.pow
import kotlin.math.sqrt

object NewOuttake: SubsystemGroup(Flywheels, Hood, Spindexer, Turret) {
    @JvmField var fullManual = false
    @JvmField var autoShoot = false

    var goalX = 0.0
    val goalY = 144-8.0

    override fun initialize() {
        goalX = if (DriveTrain.alliance == Alliance.RED) {
            144-6.0
        } else {
            6.0
        }
    }

    override fun periodic() {
        if (fullManual) {
            Turret.autoTurret = false
            manualAim()
        } else {
            Turret.autoTurret = true
            auto()
        }

        if(autoShoot) {
            autoShoot()
        }
    }

    // Manual Aim
    fun manualAim() {
        // Jacob's job
    }

    // Auto Functions
    fun auto() {
        val dist: Double = sqrt((goalX-currentX).pow(2) + (goalY-currentY).pow(2))
        val values: DoubleArray = Aimbot.getValues(dist)

        Hood.updatePosition(values[0] + 0.06)
        Flywheels.updatePid(values[1] + 100)
    }

    fun autoShoot() {
        if(DriveTrain.inShootZone()) {
            // Make commands that acc shoot the ball
        }
    }
}