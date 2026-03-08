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
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.isMoving
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

    var turretGoalX = 0.0;

    override fun periodic() {
        currentX = PedroComponent.follower.pose.x
        currentY = PedroComponent.follower.pose.y
        currentHeading = PedroComponent.follower.heading
        distance = sqrt((turretGoalX - currentX).pow(2) + (144.0 - currentY).pow(2))

        if(autoTurret) {
            swm()
            //trackStatic()
            //track()
        }

        goToYaw(target)

        /*
        ActiveOpMode.telemetry.run {
            addData("current Pos", PedroComponent.follower.pose)
            addData("current target", target)
            addData("yaw", getYaw())
            addData("velocity", PedroComponent.follower.velocity.magnitude)
            update()
        }
         */
    }

    @JvmField var test:Double = 0.0

    /*
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
        return corrected.toPose
    }*/

    val Vector.toPose: Pose
        get() = Pose(this.xComponent, this.yComponent)

    private fun swm() {
        if(isMoving()) {
            val gY: Double = Outtake.targetPose.y
            val gX:Double = Outtake.targetPose.x

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
            track()
        }
    }

    private fun track() {
        val mu = atan2(Outtake.targetPose.y - currentY, Outtake.targetPose.x - currentX)

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
        val position: Double = ((gear_ratio * target+135 - offset)/270.0).coerceIn(gear_ratio*-maxAngle/270.0 + 135/270.0, gear_ratio*maxAngle/270.0 + 135/270.0,)

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
        offset += 3
    }

    /**
     * @return Spins a little right
     */    fun spinRight(): Command = InstantCommand {
        offset -= 3
    }

    /**
     * @return A [Command] to zero the encoder
     */    fun zero(): Command = InstantCommand {
    }
}