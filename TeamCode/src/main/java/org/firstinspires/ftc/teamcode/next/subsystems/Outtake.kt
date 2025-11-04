package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.AnalogInputController
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforward
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.control.feedforward.FeedforwardElement
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.delays.WaitUntil
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.commands.utility.LambdaCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.controllable.RunToVelocity
import dev.nextftc.hardware.impl.CRServoEx
import dev.nextftc.hardware.impl.FeedbackCRServoEx
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.hardware.impl.ServoEx
import dev.nextftc.hardware.powerable.SetPower
import java.time.Instant
import kotlin.time.Duration.Companion.seconds
import  dev.nextftc.ftc.ActiveOpMode;
import org.firstinspires.ftc.robotcore.internal.hardware.android.GpioPin.Active
import org.firstinspires.ftc.teamcode.helpers.getIndex
import org.firstinspires.ftc.teamcode.helpers.normalize_angle
import org.firstinspires.ftc.teamcode.next.subsystems.data.Aimbot
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Configurable
object Outtake: Subsystem {
    val gS = FeedbackCRServoEx(
        cacheTolerance = 0.01,
        feedbackFactory = { ActiveOpMode.hardwareMap.analogInput.get("gSA") },
        servoFactory = { ActiveOpMode.hardwareMap.crservo.get("gS") }
    )

    // val gS = CRServoEx("gS")
    val f1 = MotorEx("f1M")
    val f2 = MotorEx("f2M").reversed()
    val hS = ServoEx("flap")

    // Constants
    @JvmField
    var targetOnVelo = 950.0

    @JvmField
    var targetBackVelo = 400.0

    var pid = PIDCoefficients(0.0033, 0.0, 0.0)
    var ff = BasicFeedforwardParameters(1.66667E-4, 0.0, 0.003)
    var controller = controlSystem {
        velPid(pid)
        basicFF(ff)
    }

    @JvmField
    var gPid = PIDCoefficients(0.0, 0.0, 0.0)
    var gController = controlSystem {
        posPid(gPid)
    }

    // Changing Vars
    @JvmField
    var targetVelo = 0.0

    @JvmField
    var gP = 0.0 // Gear Power

    @JvmField
    var hP = 0.0 // Hood Position

    @JvmField
    var velocityTrue = true // Use the VPID

    @JvmField
    var turrentAngle = 0.0 // Turrent Angle relative Pedro Pathing's starting orientation

    @JvmField
    var currentX = 0.0

    @JvmField
    var currentY = 0.0

    @JvmField
    var currentHeading = 0.0

    var prevAngle = 0.0
    var turretHeading = 0.0
    var dHeading = 0.0
    var totalAngle = 0.0
    var gearRatio = 3.47
    var dist = 0.0

    var xcord = 12.80
    var ycord = 138.35
    var height = 36.0 // Units initally
    var turretHeight = 0.0 // Inches

    override fun periodic() {
        if (velocityTrue) {
            f1.power = controller.calculate(f1.state)
            f2.power = f1.power
            controller.goal = KineticState(0.0, targetVelo)
        }

        gS.power = gController.calculate(gS.state)
        turrentAngle = gS.currentPosition
        hS.position = 1-hP

        calculateCurrentServoAngle()

        aimbot()
    }

    fun aimbot() {
        // Find phi

        // X,Y, height coordinate of the hoop needs to be hardcoded, and the turretHeight
        // Note x & y are in units and height and turret height will be in inches

        // currentX = DriveTrain.follower.pose.x
        // currentY = DriveTrain.follower.pose.y
        // currentHeading = DriveTrain.follower.pose.heading

        var mu = normalize_angle(atan2(ycord - currentY, xcord - currentX))
        var deltaHeading = normalize_angle(mu - currentHeading)

        // Find theta (MAYBE I THINK I CAN JUST HARD CODE IN POSITION).
        dist = sqrt((xcord - currentX).pow(2) + (ycord - currentY).pow(2))

        // Find hP, and Power to run at using our lookup table
        val other = Aimbot.points[getIndex(dist)]

        hP = other[0]
        targetOnVelo = other[1]
    }

    // Commands
    val spinGearLeft = InstantCommand {
        gP = 0.7 // Some Constant
    }
    val spinGearRight = InstantCommand {
        gP = -0.7 // Some Constant
    }
    val gearAlittleLeft = InstantCommand {
        gP = -0.2
    }
    val gearAlittleRight = InstantCommand {
        gP = -0.2
    }
    val stopGear = InstantCommand {
        gP = 0.0
    }
    val FlapDown = InstantCommand {
        hP += 0.05
    }
    val FlapUp = InstantCommand {
        hP -= 0.05
    }
    val flywheelOff: InstantCommand =
        InstantCommand { velocityTrue = false; targetVelo = 0.0; f1.power = 0.0; f2.power = 0.0 }
    val flywheelBack: InstantCommand =
        InstantCommand { velocityTrue = false; f1.power = -0.2; f2.power = -0.2 }
    val flywheelOn: InstantCommand =
        InstantCommand { velocityTrue = true; targetVelo = targetOnVelo }

    fun calculateCurrentServoAngle() {
        var cA = gS.currentPosition
        dHeading = cA - prevAngle

        if (dHeading > PI) dHeading -= 2 * PI
        else if (dHeading < -PI) dHeading += 2 * PI

        totalAngle += dHeading
        prevAngle = cA
    }

    fun getTurretAngle():Double {
        var angle = (totalAngle/gearRatio) % (2*PI)
        if (angle < 0) angle+=2* PI
        return angle
    }
}