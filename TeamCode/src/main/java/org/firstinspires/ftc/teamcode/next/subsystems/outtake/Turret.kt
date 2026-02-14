package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.bylazar.configurables.annotations.Configurable
import com.bylazar.panels.Panels
import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.util.Timer
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.DcMotor
import dev.nextftc.control.ControlSystem
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.AngleType
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.control2.model.MotionState
import dev.nextftc.control2.profiles.TrapezoidProfile
import dev.nextftc.control2.profiles.TrapezoidProfileConstraints
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.core.units.Angle
import dev.nextftc.core.units.deg
import dev.nextftc.core.units.rad
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.CRServoEx
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.hardware.impl.ServoEx
import dev.nextftc.units.unittypes.AngleUnit
import dev.nextftc.units.unittypes.Degrees
import dev.nextftc.units.unittypes.degreesPerSecond
import dev.nextftc.units.unittypes.degreesPerSecondSquared
import org.firstinspires.ftc.robotcore.internal.hardware.android.GpioPin
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.goalX
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.goalY
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.turretGoalX
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.turretGoalY
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.sign
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@Config
@Configurable
object Turret: Subsystem {
    private lateinit var tele: MultipleTelemetry

    private val leftServo: ServoEx = ServoEx("dS2", 0.001)
    private val rightServo: ServoEx = ServoEx("dS1", 0.001) // Check direction
    val absEncoder: AnalogInput by lazy { ActiveOpMode.hardwareMap.analogInput.get("aS") }

    val encoder: MotorEx = MotorEx("fR")  // Figure out motor name

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

    override fun periodic() {
        currentX = PedroComponent.follower.pose.x
        currentY = PedroComponent.follower.pose.y
        currentHeading = PedroComponent.follower.heading

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
    }

    private fun swm() {
        val p = PedroComponent.follower.velocity
        val a = PedroComponent.follower.acceleration
        velocityX = p.xComponent
        velocityY = p.yComponent
        velocityH = p.theta

        val mu = atan2(goalY - currentY - velocityY * t1 , turretGoalX - currentX - velocityX * t1 )
        val deltaHeading = (mu - currentHeading - velocityH * t1).rad.normalized.inRad.coerceIn((-maxAngle/180)*PI, (maxAngle/180)*PI) // Coerce Properly
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
    @JvmField var offset2:Double = 12.0
    @JvmField var offset3: Double = 6.0

    fun goToYaw(target:Double) {
        val position: Double = (target+135-offset)/270.0

        currentAngle = target
        leftServo.servo.position = position
        rightServo.servo.position = position
    }

    /**
     * @return yaw in degrees from servo position
     */    fun getYaw(): Double {
        return (currentAngle) * 0.725
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
        encoder.motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
    }
}