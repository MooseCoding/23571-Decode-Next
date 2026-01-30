package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.field.Drawable
import com.bylazar.telemetry.PanelsTelemetry
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.groups.ParallelGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.SubsystemGroup
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.ActiveOpMode
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Aimbot
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Dist
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Drawing
import kotlin.math.pow
import kotlin.math.sqrt

object Outtake: SubsystemGroup(Flywheels, Hood, Light, Turret) {
    var fullManual = false
    var distance: Dist = Dist.CLOSE

    var goalX = 6.0
    val goalY = 144

    var turretGoalX = 0.0
    val turretGoalY = 144.0

    var restore = 0.0

    var isShooting: Boolean = false

    override fun periodic() {
        goalX = if (DriveTrain.alliance == Alliance.RED) {
            144 - 6.0
        } else {
            6.0
        }
        turretGoalX = if (DriveTrain.alliance == Alliance.RED) {
            140.0
        } else {
            0.0
        }

        ActiveOpMode.telemetry.run {
            addData("goalX", turretGoalX)
        }

        Drawing.drawRobot(follower.pose)

        if (!fullManual) {
            Turret.autoTurret = true
            auto()
        } else {
            manualAim()
            Turret.autoTurret = false
        }
    }

    /**
    * Manual Aim From Hardcoded Values
     */
    fun manualAim() {
        var fP = 0.0
        var hP = 0.0

        when (distance) {
            Dist.FAR -> {
                fP = 1500.0
                hP = 0.6
            }

            Dist.CLOSE -> {
                fP = 900.0
                hP = 0.50
            }
        }

        Flywheels.targetVelocity = (fP)
        Hood.hoodPosition = hP
    }

    /**
     * Auto flywheel and auto hood positioning
     */
    fun auto() {
        val dist: Double = sqrt((goalX - PedroComponent.follower.pose.x).pow(2) + (goalY - PedroComponent.follower.pose.y).pow(2))
        val values: DoubleArray = Aimbot.get(dist)

        ActiveOpMode.telemetry.addData("dist", dist)
        ActiveOpMode.telemetry.addData("isShooting", isShooting)
        ActiveOpMode.telemetry.addData("restore", restore)


        if(!isShooting) {
            Hood.hoodPosition = values[0]

            if(PedroComponent.follower.pose.y < 44.0 ) {
                Flywheels.targetVelocity = (values[1]) + 110.0
            }
            else {
                Flywheels.targetVelocity = values[1] + 70.0
            }
        }

        ActiveOpMode.telemetry.addData("hood[0]", values[0])
        ActiveOpMode.telemetry.addData("flywheel[1]", values[1])

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
                    Hood.setRestore(),
                    setSetBack()
                ),
                Delay(0.11),
                InstantCommand {
                    Flywheels.targetVelocity += 300
                },
                Hood.sequence(-0.22),
                Delay(0.12),
                InstantCommand {
                    Flywheels.targetVelocity += 325
                },
                Hood.sequence(-0.32),
                Delay(0.12),
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
}