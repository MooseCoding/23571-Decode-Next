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

    @JvmField var p: PIDCoefficients = PIDCoefficients(7.0,0.0,0.1)

    @JvmField var v: PIDCoefficients = PIDCoefficients(0.0003,0.0,0.0)
    @JvmField var ff: BasicFeedforwardParameters = BasicFeedforwardParameters(0.0001,0.0001,0.069)

    @JvmField var f: BasicFeedforwardParameters = BasicFeedforwardParameters(0.0,0.0,0.069)
    @JvmField var betterP: PIDCoefficients = PIDCoefficients(0.04,0.0,0.0)

    var c: ControlSystem = controlSystem {
        velPid(v)
        basicFF(ff)
    }

    var cP: ControlSystem = controlSystem {
        posPid(p)
    }

    var c2: ControlSystem = controlSystem {
        posPid(betterP)
    }

    private var lastValue = 0.0

    var pow: Double = 0.0

    private var currentAngle = 0.0

    @JvmField var maxAngle = 90.0
    @JvmField var minPower = 0.071

    // Shoot while moving variables
    var velocityX: Double = 0.0
    var velocityY: Double = 0.0
    var velocityH: Double = 0.0
    val timer: Timer = Timer()

    @JvmField var target: Double = 0.0
    @JvmField var targetVelo: Double = 0.0

    @JvmField var t1: Double = 0.6
    @JvmField var t2: Double = 0.6

    var currentX: Double = 0.0
    var currentY = 0.0
    var currentHeading = 0.0

    @JvmField var velocity = 0.0

    override fun periodic() {
        currentX = PedroComponent.follower.pose.x
        currentY = PedroComponent.follower.pose.y
        currentHeading = PedroComponent.follower.heading

        // updateRelative()

        // updateAbsolute()

        if(autoTurret) {
            track()
        }

        //goToControl2()

        // goToTarget()
        //leftServo.power = pow
            //rightServo.power = pow

        if(!ActiveOpMode.opModeInInit) {
            goToYaw(target)
        }


        velocity = encoder.velocity * 360.0/4000.0 * 0.725

        ActiveOpMode.telemetry.run {
              addData("yaw", getYaw())
              addData("target", target)
//            addData("targetVelo", c.goal.velocity)
//            addData("currentX",currentX)
//            addData("currentY", currentY)
//            addData("currentH", currentHeading)
            addData("turretGoalX", turretGoalX)
            addData("goalY", goalY)
        }
        PanelsTelemetry.telemetry.run {
            addData("Velocity", velocity)
            addData("Target Velocity", targetVelo)
            addData("target", target)
            addData("yaw", getYaw())
            update()
        }
    timer.resetTimer()
    }
    private fun swm() {
        val p = PedroComponent.follower.velocity
        val a = PedroComponent.follower.acceleration
        velocityX = p.xComponent
        velocityY = p.yComponent
        velocityH = p.theta
        val accelX = a.xComponent
        val accelY = a.yComponent
        val accelH = a.theta

        val mu = atan2(goalY - currentY + velocityY * t1 + accelY * t2, turretGoalX - currentX + velocityX * t1 + accelX * t2)
        val deltaHeading = (mu - currentHeading - velocityH * t1 + accelH * t2).rad.normalized.inRad.coerceIn((-maxAngle/180)*PI, (maxAngle/180)*PI) // Coerce Properly
        target = deltaHeading * 180/PI
    }

    @JvmField var trapezoid: TrapezoidProfileConstraints<AngleUnit> = TrapezoidProfileConstraints(500.0.degreesPerSecond, 850.0.degreesPerSecondSquared)
    private val control2: TrapezoidProfile<AngleUnit> = TrapezoidProfile(trapezoid)

    @JvmField var goalVelo: Double = 0.0

    private fun goToControl2() {
        if(((target - getYaw()).absoluteValue < 1.0)) return
        val x = control2.calculate(TimeSource.Monotonic.markNow(), MotionState(Degrees, getYaw(), velocity) , MotionState(Degrees,target))
        c2.goal = KineticState(x.position.magnitude, x.velocity.magnitude)
        velocity = x.velocity.magnitude
        pow = c2.calculate(KineticState(getYaw(), velocity)) + f.kS * (sign(x.position.magnitude)) + f.kV * x.velocity.magnitude
    }

    private fun track() {
        val mu = atan2(turretGoalY - currentY, turretGoalX - currentX)

        ActiveOpMode.telemetry.addData("mu", mu*180/PI)

        val error = (mu - currentHeading).rad.normalized.inRad

        val clampedError =
            error.coerceIn(
                (-maxAngle / 180.0) * PI,
                ( maxAngle / 180.0) * PI
            )

        target = -clampedError * 180.0 / PI
    }

    @JvmField var offset: Double = 26.5
    @JvmField var offset2:Double = 12.0
    @JvmField var offset3: Double = 6.0

    private fun goToYaw(target:Double) {
        var position: Double = (target+135-offset)/270.0
        /*if(PedroComponent.follower.pose.y < 50.0) {
             position -=  offset / 270.0
        }
        else {
            position -= offset3/270.0
        }
        if(target > 0.0) {
            position -= offset2 / 270.0
        }*/

        currentAngle = target
        leftServo.position = position
        rightServo.position = position
    }

    private fun goToTarget() {
        cP.goal = KineticState(target,0.0)
        targetVelo = cP.calculate(KineticState(getYaw(), -encoder.velocity * 360.0/4000.0 * 0.725))
        c.goal = KineticState(target,  targetVelo)
        pow = c.calculate(KineticState(getYaw(), encoder.velocity * 360.0/4000.0 * 0.725))

        val error = target - getYaw()

        if (error.absoluteValue in 1.6..9.0 && pow.absoluteValue < 0.1) {
            pow = sign(error) * minPower
        }
        else if(error.absoluteValue < 1.6) {
            pow = 0.0
        }
    }
    private fun updateAbsolute() {
        val pos = absEncoder.voltage / 3.3 * 2*PI - offset
        var delta = pos - lastValue

        if(delta > PI) delta -= 2*PI
        else if(delta < -PI) delta += 2*PI

        currentAngle += delta
        lastValue = pos
    }

    private fun updateRelative() {
        currentAngle = encoder.currentPosition * 360.0 / 4000.0
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
     * @return Stops spinning
     */    fun stopSpin(): Command = InstantCommand {
        pow = 0.0
    }

    /**
     * @return A [Command] to zero the encoder
     */    fun zero(): Command = InstantCommand {
        encoder.motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
    }
}