package org.firstinspires.ftc.teamcode.next.subsystems

import com.acmerobotics.dashboard.config.Config
import com.bylazar.configurables.annotations.Configurable
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.commands.utility.LambdaCommand
import dev.nextftc.core.subsystems.SubsystemGroup
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentX
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentY
import org.firstinspires.ftc.teamcode.next.subsystems.Spindexer.balls
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Aimbot
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import kotlin.math.pow
import kotlin.math.sqrt

object Outtake: SubsystemGroup(Flywheels, Hood, Turret) {
    var fullManual = false
    var distance = 12
    var autoShoot = false

    var goalX = 0.0
    val goalY = 144 - 8.0

    override fun initialize() {
        goalX = if (DriveTrain.alliance == Alliance.RED) {
            144 - 6.0
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
    }

    // Manual Aim From Hardcoded Values
    fun manualAim() {
        var fP = 0.0
        var hP = 0.0

        when (distance) {
            12 -> {
                fP = 850.0 // Idk tune
                hP = 0.0
            }

            24 -> {
                fP = 890.0
                hP = 0.1
            }

            36 -> {
                fP = 950.0
                hP = 0.15
            }

            48 -> {
                fP = 1000.0
                hP = 0.2
            }

            60 -> {
                fP = 1200.0
                hP = 0.3
            }

            72 -> {
                fP = 1300.0
                hP = 0.4
            }

            84 -> {
                fP = 1400.0
                hP = 0.6
            }

            96 -> {
                fP = 1600.0
                hP = 0.7
            }

            108 -> {
                fP = 1800.0
                hP = 0.7
            }

            120 -> {
                fP = 1900.0
                hP = 0.8
            }

            132 -> {
                fP = 2000.0
                hP = 0.8
            }

            144 -> {
                fP = 2200.0
                hP = 0.9
            }
        }

        Flywheels.updatePid(fP)
        Hood.hoodPosition = hP
    }

    // Auto Functions
    fun auto() {
        val dist: Double = sqrt((goalX - currentX).pow(2) + (goalY - currentY).pow(2))
        val values: DoubleArray = Aimbot.getValues(dist)

        Hood.hoodPosition = values[0]
        Flywheels.updatePid(values[1])
    }

    fun shoot(): Command { return SequentialGroup(*buildList {
                    for (i in 0..balls()) {
                        add(
                            SequentialGroup(
                                Transfer.spin,
                                Delay(0.5),
                                InstantCommand {
                                    Spindexer.currentShot[i] =
                                        Spindexer.ballsHeld[Spindexer.currentPos]
                                    Spindexer.ballsHeld[Spindexer.currentPos] = null
                                },
                                Delay(0.8)
                            )
                        )
                    }
                }.toTypedArray())
    }
}