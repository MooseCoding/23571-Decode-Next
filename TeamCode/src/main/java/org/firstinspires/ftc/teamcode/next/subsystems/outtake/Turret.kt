package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.geometry.Pose
import com.pedropathing.math.Vector
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.core.units.rad
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.ServoEx
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.ShotTime
import java.util.Arrays
import java.util.LinkedList
import java.util.Queue
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sqrt

@Configurable
object Turret: Subsystem {
    private val leftServo: ServoEx = ServoEx("dS2", 0.001)
    private val rightServo: ServoEx = ServoEx("dS1", 0.001) // Check direction

    //val absEncoder: AnalogInput by lazy { ActiveOpMode.hardwareMap.analogInput.get("aS") }

    private val gear_ratio = 75.0 / 99.0; // 75 turns servo to 99 turns turret

    // val encoder: MotorEx = MotorEx("fR")  // Figure out motor name

    @JvmField var autoTurret = true

    private var currentAngle = 0.0

    @JvmField var maxAngle = 160.0
    @JvmField var minPower = 0.071

    // Shoot while moving variables
    var velocityX: Double = 0.0
    var velocityY: Double = 0.0
    var velocityH: Double = 0.0

    @JvmField var t1: Double = 0.04e-20
    @JvmField var t2: Double = 0.04e-20

    @JvmField var target:Double = 0.0

    var currentX: Double = 0.0
    var currentY = 0.0
    var currentHeading = 0.0

    override fun initialize() {

    }

    var distance = 0.0

    val goalX = 0.0;
    val goalY = 144.0;
    val turretGoalY = 144.0;
    val turretGoalX = 0.0;

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

    private fun initializeFilterBuffers() {
        for (i in 0..<MEDIAN_FILTER_SIZE) {
            velXBuffer.add(0.0)
            velYBuffer.add(0.0)
            headingVelBuffer.add(0.0)
        }
    }

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
        if (!isMoving()) {
            velX = 0.0
            velY = 0.0
        }

        val g: Pose = Pose(turretGoalX, goalY)
        val p =  Pose(
            g.getX() - velX * flyTime,
            g.getY() - velY * flyTime,
            g.getHeading()
        )

        ActiveOpMode.telemetry.run {
            addData("New pos", p)
        }

        return p
    }

    var flyTime:Double = 0.60


    override fun periodic() {
        currentX = PedroComponent.follower.pose.x
        currentY = PedroComponent.follower.pose.y
        currentHeading = PedroComponent.follower.heading
        distance = sqrt((goalX - currentX).pow(2) + (goalY - currentY).pow(2))
        flyTime = ShotTime.get(distance)

        if(autoTurret) {
            swm()
            //trackStatic()
            //track()
        }

        goToYaw(target)

        ActiveOpMode.telemetry.run {
            addData("current Pos", PedroComponent.follower.pose)
            addData("current target", target)
            addData("yaw", getYaw())
            addData("velocity", PedroComponent.follower.velocity.magnitude)
            update()
        }
    }

    @JvmField var test:Double = 0.0

    private fun swm30099(): Pose {
        val botPose = PedroComponent.follower.pose
        val velocity = PedroComponent.follower.velocity

        val r = Vector(
            hypot(targetPose.x - botPose.x, targetPose.y - botPose.y),
            atan2(botPose.y - targetPose.y, botPose.x - targetPose.x)
        )
        var corrected = r
        repeat(6) {
            val t = ShotTime.get(r.magnitude) // Distance vector magnitude is distance
            corrected = r.plus(velocity.times(t))
        }
        /*ActiveOpMode.telemetry.addData("targetPose.x", targetPose.x)
        ActiveOpMode.telemetry.addData("targetPose.y",targetPose.y)
        ActiveOpMode.telemetry.addData("botPose.x",botPose.x)
        ActiveOpMode.telemetry.addData("botPose.y",botPose.y)
        ActiveOpMode.telemetry.addData("correct.x",corrected.xComponent)
        ActiveOpMode.telemetry.addData("correct.y", corrected.yComponent)
        ActiveOpMode.telemetry.addData("velocity.x", velocity.xComponent)
        ActiveOpMode.telemetry.addData("velocity.y", velocity.yComponent)*/
        return corrected.toPose
    }

    val Vector.toPose: Pose
        get() = Pose(this.xComponent, this.yComponent)

    private fun swm() {
        /*
        val p = PedroComponent.follower.velocity
        val a = PedroComponent.follower.acceleration
        if(p.magnitude > 50.0) {
            velocityX = p.xComponent
            velocityY = p.yComponent
            velocityH = p.theta
        }
        else {
            velocityX = 0.0
            velocityY = 0.0
            velocityH = 0.0
        }

        val shotTime = ShotTime.get(distance)

        val mu = atan2(goalY - currentY - velocityY * shotTime / test, turretGoalX - currentX - velocityX * shotTime/ test)
        val deltaHeading = (mu - currentHeading - velocityH * shotTime/ test).rad.normalized.inRad.coerceIn((-maxAngle/180)*PI, (maxAngle/180)*PI) // Coerce Properly
        target = -deltaHeading * 180/PI
         */

        if(isMoving()) {
            updateFilteredVelocities()

            val pT: Pose = getExpectedPose()

            val gY: Double = pT.y
            val gX:Double = pT.x

            val mu = atan2(gY - currentY, gX - currentX)

            val error = (mu - currentHeading).rad.normalized.inRad

            val clampedError =
                error.coerceIn(
                    (-maxAngle / 180.0) * PI,
                    (maxAngle / 180.0) * PI
                )

            target = -clampedError * 180.0 / PI
        }
        else {
            trackStatic()
            track()
        }
    }

    private fun trackStatic() {
        targetPose = Pose(turretGoalX, turretGoalY)
    }

    private fun track() {
        val mu = atan2(targetPose.y - currentY, targetPose.x - currentX)

        val error = (mu - currentHeading).rad.normalized.inRad

        val clampedError =
            error.coerceIn(
                (-maxAngle / 180.0) * PI,
                ( maxAngle / 180.0) * PI
            )

        target = -clampedError * 180.0 / PI
    }

    @JvmField var offset: Double = 0.0

    fun goToYaw(target:Double) {
        val position: Double = (gear_ratio * target+135 - offset)/270.0

        currentAngle = target
        leftServo.servo.position = position
        rightServo.servo.position = position
    }

    /**
     * @return yaw in degrees from servo position
     */    fun getYaw(): Double {
        return (currentAngle)
    }

    /**
     * @return Spins a little left
     */    fun spinLeft(): Command = InstantCommand {
        target -= 5
    }

    /**
     * @return Spins a little right
     */    fun spinRight(): Command = InstantCommand {
        target += 5
    }

    /**
     * @return A [Command] to zero the encoder
     */    fun zero(): Command = InstantCommand {
    }
}