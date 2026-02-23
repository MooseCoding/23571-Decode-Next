package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.core.units.rad
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.ServoEx
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.ShotTime
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

object Turret: Subsystem {
    private val leftServo: ServoEx = ServoEx("dS2", 0.001)
    private val rightServo: ServoEx = ServoEx("dS1", 0.001) // Check direction

    //val absEncoder: AnalogInput by lazy { ActiveOpMode.hardwareMap.analogInput.get("aS") }

    private val gear_ratio = 75.0 / 99.0; // 75 turns servo to 99 turns turret

    // val encoder: MotorEx = MotorEx("fR")  // Figure out motor name

    @JvmField var autoTurret = true

    private var currentAngle = 0.0

    @JvmField var maxAngle = 90.0
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

    val goalX = 144.0;
    val goalY = 144.0;
    val turretGoalY = 144.0;
    val turretGoalX = 144.0;

    override fun periodic() {
        currentX = PedroComponent.follower.pose.x
        currentY = PedroComponent.follower.pose.y
        currentHeading = PedroComponent.follower.heading
        distance = sqrt((goalX - currentX).pow(2) + (goalY - currentY).pow(2))

        if(autoTurret) {
            // swm()
            track()
        }

        if(!ActiveOpMode.opModeInInit) {
          goToYaw(target)
        }
        else {
            goToYaw(0.0)
        }

        ActiveOpMode.telemetry.run {
            addData("current Pos", PedroComponent.follower.pose)
            addData("current target", target)
            addData("yaw", getYaw())
            update()
        }
    }

    private fun swm() {
        val p = PedroComponent.follower.velocity
        val a = PedroComponent.follower.acceleration
        velocityX = p.xComponent
        velocityY = p.yComponent
        velocityH = p.theta

        val shotTime = ShotTime.get(distance)

        val mu = atan2(goalY - currentY - velocityY * shotTime , turretGoalX - currentX - velocityX * shotTime )
        val deltaHeading = (mu - currentHeading - velocityH * shotTime).rad.normalized.inRad.coerceIn((-maxAngle/180)*PI, (maxAngle/180)*PI) // Coerce Properly
        target = -deltaHeading * 180/PI
    }

    private fun track() {
        val mu = atan2(turretGoalY - currentY, turretGoalX - currentX)

        val error = (mu - currentHeading).rad.normalized.inRad

        val clampedError =
            error.coerceIn(
                (-maxAngle / 180.0) * PI,
                ( maxAngle / 180.0) * PI
            )

        target = -clampedError * 180.0 / PI
    }

    @JvmField var offset: Double = 1.0

    fun goToYaw(target:Double) {
        val position: Double = (gear_ratio * target+135)/270.0

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