package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.telemetry.PanelsTelemetry
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup
import dev.nextftc.core.commands.groups.ParallelGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.SubsystemGroup
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.ActiveOpMode
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Aimbot
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Dist
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.seconds

object Outtake: SubsystemGroup(Flywheels, Hood, Light, Turret) {
    var fullManual = false
    var distance: Dist = Dist.CLOSE

    var goalX = 6.0
    const val goalY = 144

    var turretGoalX = 0.0
    const val turretGoalY = 144.0

    var restore = 0.0

    var isShooting: Boolean = false

    override fun periodic() {
        goalX = if (DriveTrain.alliance == Alliance.RED) {
            144 - 6.0
        } else {
            6.0
        }
        turretGoalX = if (DriveTrain.alliance == Alliance.RED) {
            144.0
        } else {
            0.0
        }

        ActiveOpMode.telemetry.run {
            addData("goalX", turretGoalX)
        }


        if (!fullManual) {
           Turret.autoTurret = true
             auto()
        } else {
            Turret.autoTurret = false
        }
    }

    /**
    * Manual Aim From Hardcoded Values
     */
    @JvmField var farHood: Double = 0.35
    @JvmField var farVelocity: Double = 1550.0
    @JvmField var closeHood: Double = 0.48
    @JvmField var closeVelocity:Double = 1350.0

    /**
     * Auto flywheel and auto hood positioning
     */
    var dist:Double = 0.0
    fun auto() {
        dist = sqrt((goalX - PedroComponent.follower.pose.x).pow(2) + (goalY - PedroComponent.follower.pose.y).pow(2))
        val values: DoubleArray = Aimbot.get(dist)

        PanelsTelemetry.telemetry.addData("dist", dist)
        PanelsTelemetry.telemetry.addData("isShooting", isShooting)
        PanelsTelemetry.telemetry.addData("restore", restore)


        if(!isShooting) {
            Flywheels.targetVelocity = values[1] + 150
            Hood.hoodPosition = values[0] + 0.04
        }

        PanelsTelemetry.telemetry.addData("hood[0]", values[0])
        PanelsTelemetry.telemetry.addData("flywheel[1]", values[1])
    }
    fun setBack(): InstantCommand = InstantCommand{
        Flywheels.targetVelocity = restore
    }
    fun setSetBack(): InstantCommand = InstantCommand{
        restore = Flywheels.targetVelocity
    }

    /**
     * @return Command to run for the shoot command
     */
    fun shoot(): Command {
        // isShooting = true
        return SequentialGroupLocal(
            InstantCommand {isShooting = true},
            ParallelGroup (
                    Intake.runIntake(),
                    Transfer.start(),
                ),
                Delay(0.35),
            InstantCommand {
                isShooting = false
            },
            Transfer.stop(),
            Intake.stopIntake()
        )
    }

    fun shootFar(): Command {
        // isShooting = true
        return SequentialGroupLocal(
            InstantCommand { isShooting = true },
            ParallelGroup(
                Intake.runIntake(),
                Transfer.start(),
                Hood.setRestore(),
                setSetBack()
            ),
            Delay(0.22.seconds),
            Hood.sequence(0.2),
            Delay(0.1.seconds),

            ParallelGroup(
                Hood.restore(),
                Intake.stopIntake(),
                Transfer.stop(),
                setBack()
            ),

            InstantCommand {
                isShooting = false
            }//ball shoots every 0.2 seconds
        )
    }

    fun getHoodForSort(norm: Double) {
        for(tA in Transfer.target) {
            if(Transfer.ballsHeld[0] == tA) {
                Hood.hoodPosition = norm
            }
            else if(Transfer.ballsHeld[1] == tA) {
                Hood.hoodPosition = norm + 0.1
            }
            else if(Transfer.ballsHeld[2] == tA) {
                Hood.hoodPosition = norm + 0.2
            }
            else {
                Hood.hoodPosition = norm
            }
        }
    }

    /*fun sortShoot(): Command {
        return SequentialGroupLocal(
            InstantCommand {isShooting = true},
            ParallelDeadlineGroup(
                SequentialGroupLocal(
                    ParallelGroup (
                        Intake.runIntake(),
                        Transfer.start(),
                    ),
                    Delay(0.12),
                    InstantCommand {
                        Transfer.currentBall--
                        Transfer.ballsHeld[0] = Transfer.ballsHeld[1]
                        Transfer.ballsHeld[1] = Transfer.ballsHeld[2]
                        Transfer.ballsHeld[2] = null
                    },
                    Delay(0.12),
                    InstantCommand {
                        Transfer.currentBall--
                        Transfer.ballsHeld[0] = Transfer.ballsHeld[1]
                        Transfer.ballsHeld[1] = null
                    },
                    Delay(0.12),
                    InstantCommand {
                        Transfer.currentBall--
                        Transfer.ballsHeld[0] = null
                    },
                    InstantCommand {
                        isShooting = false
                    },
                    Intake.stopIntake(),
                    Transfer.stop()//ball shoots every 0.2 second
                ),
                InstantCommand {
                    getHoodForSort(Aimbot.getHood(dist, 1500.0))
                }
            )
        )
    }*/
}