package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.util.Timer
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import dev.nextftc.control.ControlSystem
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedback.SquIDElement
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.core.units.rad
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.CRServoEx
import dev.nextftc.hardware.impl.MotorEx

import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.goalX
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.goalY

import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.sign

@Config
@Configurable
object Turret: Subsystem {
    private lateinit var tele: MultipleTelemetry

    private val leftServo: CRServoEx = CRServoEx("dS2", 0.001)
    private val rightServo: CRServoEx = CRServoEx("dS1", 0.001) // Check direction
    val absEncoder: AnalogInput by lazy { ActiveOpMode.hardwareMap.analogInput.get("aS") }

    val encoder: MotorEx = MotorEx("fR")  // Figure out motor name

    @JvmField var autoTurret = true

    @JvmField var p: PIDCoefficients = PIDCoefficients(9.8,0.0,0.4)

    @JvmField var v: PIDCoefficients = PIDCoefficients(0.0003,0.0,0.0)
    @JvmField var ff: BasicFeedforwardParameters = BasicFeedforwardParameters(0.0001,0.0001,0.045)

    var c: ControlSystem = controlSystem {
        velPid(v)
        basicFF(ff)
    }

    var cP: ControlSystem = controlSystem {
        posPid(p)
    }
    override fun initialize() {
        zero().schedule()
    }
    private var lastValue = 0.0

    private var pow: Double = 0.0

    private val offset = 3.0578
    private var currentAngle = 0.0

    @JvmField var maxAngle = 90.0
    @JvmField var maxPower = 0.5
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

    override fun periodic() {
        currentX = PedroComponent.follower.pose.x
        currentY = PedroComponent.follower.pose.y
        currentHeading = PedroComponent.follower.heading

        updateRelative()

        // updateAbsolute()

        if(autoTurret) {
            track()
        }
        goToTarget()

        //leftServo.power = pow
        //rightServo.power = pow
        ActiveOpMode.telemetry.run {
            // addData("yaw", getYaw())
//            addData("encoder", encoder.currentPosition)
              addData("target", cP.goal.position)
//            addData("targetVelo", c.goal.velocity)
//            addData("current velo", -encoder.velocity * 360/4000.0 * 0.725)
//            addData("servo pow", leftServo.power)
//            addData("currentX",currentX)
//            addData("currentY", currentY)
//            addData("currentH", currentHeading)
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

    val mu = atan2(goalY - currentY + velocityY * t1 + accelY * t2, goalX - currentX + velocityX * timer.elapsedTime / 1000.0)
    val deltaHeading = (mu - currentHeading - velocityH * timer.elapsedTime / 1000.0).rad.normalized.inRad.coerceIn((-maxAngle/180)*PI, (maxAngle/180)*PI) // Coerce Properly
    target = deltaHeading * 180/PI
}

private fun track() {
    val mu = atan2(goalY - currentY , goalX - currentX)
    val deltaHeading = (mu - currentHeading).rad.normalized.inRad.coerceIn((-maxAngle/180)*PI, (maxAngle/180)*PI) // Coerce Properly
    target = deltaHeading
}


private fun goToTarget() {
    cP.goal = KineticState(target,0.0)
    c.goal = KineticState(target,  cP.calculate(KineticState(getYaw(), -encoder.velocity * 360.0/4000.0 * 0.725)))
    pow = c.calculate(KineticState(getYaw(), -encoder.velocity * 360.0/4000.0 * 0.725))

    val error = target - getYaw()

    if (error.absoluteValue in 1.5..9.0) {
        pow = sign(error) * minPower
    }
    else if(error.absoluteValue < 1.5) {
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
    currentAngle = -encoder.currentPosition * 360.0 / 4000.0
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