package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.geometry.Pose
import com.pedropathing.math.Vector
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
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.ShotTime
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret.turretGoalX
import java.util.Arrays
import java.util.LinkedList
import java.util.Queue
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.seconds

object Outtake: SubsystemGroup(Flywheels, Hood, Light, Turret) {
    var fullManual = false
    var distance: Dist = Dist.CLOSE

    var goalX = 0.0
    const val goalY = 144
    var restore = 0.0

    var isShooting: Boolean = false

    override fun periodic() {
        goalX = if (DriveTrain.alliance == Alliance.RED) {
            144.0
        } else {
            0.0
        }

        Turret.turretGoalX = if(DriveTrain.alliance == Alliance.RED) {
            144-3.0
        }
        else {
            -3.0
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
        updateFilteredVelocities()
        targetPose = if(isMoving()) {
            getExpectedPose()
        } else {
            Pose(turretGoalX, 144.0)
        }
        dist = sqrt((targetPose.x - PedroComponent.follower.pose.x).pow(2) + (targetPose.y - PedroComponent.follower.pose.y).pow(2))
        val values: DoubleArray = Aimbot.get(dist)
        flyTime = ShotTime.get(dist)
        /*
        PanelsTelemetry.telemetry.addData("dist", dist)
        PanelsTelemetry.telemetry.addData("isShooting", isShooting)
        PanelsTelemetry.telemetry.addData("restore", restore)
        */

        if(!isShooting) {
            Flywheels.targetVelocity = values[1] + 160
            Hood.hoodPosition = values[0] + 0.06
        }

        /*
        PanelsTelemetry.telemetry.addData("hood[0]", values[0])
        PanelsTelemetry.telemetry.addData("flywheel[1]", values[1])
         */
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
            Intake.runIntake(),
            Transfer.start(),
            Delay(0.48),
            InstantCommand {
                isShooting = false
            },
            Transfer.stop(),
            Intake.stopIntake()
        )
    }

    var targetPose:Pose = Pose()
    const val MEDIAN_FILTER_SIZE: Int = 10
    private val velXBuffer: Queue<Double> = LinkedList<Double>()
    private val velYBuffer: Queue<Double> = LinkedList<Double>()
    private val headingVelBuffer: Queue<Double> = LinkedList<Double>()
    private val velXArray = DoubleArray(MEDIAN_FILTER_SIZE)
    private val velYArray = DoubleArray(MEDIAN_FILTER_SIZE)
    private val headingVelArray = DoubleArray(MEDIAN_FILTER_SIZE)

    var epsilonStopXY: Double = 30.0
    var epsilonStopH: Double = 10.0
    private var filteredVelX = 0.0
    private var filteredVelY = 0.0
    private var filteredHeadingVel = 0.0
    private fun applyMedianFilter(
        buffer: Queue<Double>,
        newValue: Double,
        array: DoubleArray
    ): Double {
        buffer.poll()
        buffer.add(newValue)

        var index = 0
        for (value in buffer) {
            array[index++] = value
        }

        Arrays.sort(array)

        return array[MEDIAN_FILTER_SIZE / 2]
    }

    private fun updateFilteredVelocities() {
        val vel: Vector = PedroComponent.follower.velocity
        val rawVelX = vel.getXComponent()
        val rawVelY = vel.getYComponent()
        val rawHeadingVel: Double = PedroComponent.follower.angularVelocity

        filteredVelX = applyMedianFilter(velXBuffer, rawVelX, velXArray)
        filteredVelY = applyMedianFilter(velYBuffer, rawVelY, velYArray)
        filteredHeadingVel = applyMedianFilter(headingVelBuffer, rawHeadingVel, headingVelArray)
    }

    public fun isMoving(): Boolean {
        return PedroComponent.follower.velocity.magnitude > epsilonStopH
    }

    fun getExpectedPose(): Pose {
        var velX: Double = filteredVelX
        var velY: Double = filteredVelY
        var velH: Double = filteredHeadingVel

        if (!isMoving()) {
            velX = 0.0
            velY = 0.0
        }

        val g: Pose = Pose(turretGoalX, 144.0)
        val p =  Pose(
            g.x - velX * flyTime,
            g.y - velY * flyTime,
            g.heading - velH * flyTime
        )

        return p
    }

    var flyTime:Double = 0.60


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
            Delay(0.28.seconds),
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
}