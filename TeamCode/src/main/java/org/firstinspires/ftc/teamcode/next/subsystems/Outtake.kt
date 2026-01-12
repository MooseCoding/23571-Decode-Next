package org.firstinspires.ftc.teamcode.next.subsystems

import androidx.core.content.pm.ShortcutInfoCompatSaver
import com.acmerobotics.dashboard.config.Config
import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.ftccommon.FtcEventLoopHandler
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.conditionals.IfElseCommand
import dev.nextftc.core.commands.conditionals.switchCommand
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.delays.WaitUntil
import dev.nextftc.core.commands.groups.ParallelGroup
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.commands.utility.LambdaCommand
import dev.nextftc.core.subsystems.SubsystemGroup
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentX
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.currentY
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Aimbot
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Dist
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object Outtake: SubsystemGroup(Flywheels, Hood, Light) {
    var fullManual = false
    var distance: Dist = Dist.CLOSE

    var goalX = 6.0;
    val goalY = 144 - 8.0

    val shootTime: Duration = 0.05.seconds

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
        } else {
            Turret.autoTurret = false
        }
        manualAim()
    }

    /**
    * Manual Aim From Hardcoded Values
     */
    fun manualAim() {
        var fP = 0.0
        var hP = 0.0

        when (distance) {
            Dist.FAR -> {
                fP = 2400.0
                hP = 0.4
            }

            Dist.CLOSE -> {
                fP = 1350.0
                hP = 0.50
            }
        }

        Flywheels.updatePid(fP)
        Hood.hoodPosition = hP
    }

    /**
     * Auto flywheel and auto hood positioning
     */
    fun auto() {
        val dist: Double = sqrt((goalX - currentX).pow(2) + (goalY - currentY).pow(2))
        val values: DoubleArray = Aimbot.getValues(dist)

        Hood.hoodPosition = values[0]
        Flywheels.updatePid(values[1])
    }

    /**
     * @return Command to run for the shoot command
     */
    fun shoot(): Command {
        return SequentialGroupLocal(
            ParallelGroup(
                Intake.runIntake(),
                Transfer.start(),
                Delay(0.75.seconds),
                Intake.stopIntake(),
                Transfer.stop(),
                Flywheels.stop()
            ),
        )
    }
}