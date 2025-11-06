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
import dev.nextftc.hardware.impl.FeedbackServoEx
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

    val servo = FeedbackServoEx("analog", "servo");

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
    var gController = controlSystem { // Controller goal will alwyas be in Turret Rads
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
    var manualAim = 0

    @JvmField
    var f = 100.0
    @JvmField
    var h = 0.006

    @JvmField
    var currentHeading = 0.0

    // Handling auto shooting and stuff
    @JvmField 
    var auto = true
    @JvmField
    var autoShoot = true

    @JvmField
    var manualOn = false

    @JvmField
    var targetHeading: Double = 0.0

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

        turrentAngle = calculateAngle()

        if (manualOn) {
            aimDistance()
        }
        else {
            if (auto) {
                aimbot()
            }
            if (autoShoot) {
                betterAimbot()
            }
        }
    }

    fun betterAimbot() {
        if (DriveTrain.canShoot()) {
             SequentialGroup(
                 Intake.runIntake,
                 Delay(0.5.seconds),
                 Intake.stopIntake
             ) 
        }
    }

    fun aimbot() {
        // currentX = DriveTrain.follower.pose.x
        // currentY = DriveTrain.follower.pose.y
        // currentHeading = DriveTrain.follower.pose.heading

        var mu = normalize_angle(atan2(ycord - currentY, xcord - currentX))
        var deltaHeading = normalize_angle(mu - currentHeading)
        targetHeading = turretHeading + deltaHeading

        targetHeading %= 2* PI

        if(targetHeading<0) {
            targetHeading+=2*PI
        }

        gController.goal = KineticState(targetHeading, 0.0)

        // Find theta (MAYBE I THINK I CAN JUST HARD CODE IN POSITION).
        dist = sqrt((xcord - currentX).pow(2) + (ycord - currentY).pow(2))

        // Find hP, and Power to run at using our lookup table
        val other = Aimbot.points[getIndex(dist)]

        hP = other[0] + h
        targetOnVelo = other[1] + f

        hS.position = 1-hP

        gS.power = gController.calculate(KineticState(turretFromServo(gS.state.position), 0.0))
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

    val aimUp = InstantCommand {
        manualAim += 12
    }
    val aimDown = InstantCommand{
        manualAim -= 12
    }



    val flywheelOff: InstantCommand =
        InstantCommand { velocityTrue = false; targetVelo = 0.0; f1.power = 0.0; f2.power = 0.0 }
    val flywheelBack: InstantCommand =
        InstantCommand { velocityTrue = false; f1.power = -1.0; f2.power = -1.0 }
    val flywheelOn: InstantCommand =
        InstantCommand { velocityTrue = true; targetVelo = targetOnVelo }

    val outtakeBalls = SequentialGroup(
        Intake.reverseIntake,
        Delay(0.2.seconds),
        Outtake.flywheelBack,
        Delay(0.1.seconds),
        Intake.stopIntake,
        Delay(0.2.seconds),
        Outtake.flywheelOff,
        Outtake.flywheelOn,
        Intake.runIntake,
        Delay(1.seconds),
        Intake.stopIntake,
        Outtake.flywheelOff
    )


    fun calculateAngle(): Double {
        val cA = gS.currentPosition // current servo angle in radians
        var dHeading = cA - prevAngle

        // Handle wraparound across ±π
        if (dHeading > PI) dHeading -= 2 * PI
        else if (dHeading < -PI) dHeading += 2 * PI

        // Ignore micro noise below 0.05 degrees
        if (dHeading.absoluteValue < (0.05 / 360.0) * 2 * PI) {
            dHeading = 0.0
        }

        // Accumulate rotation
        totalAngle += dHeading
        prevAngle = cA

        // Normalize accumulated angle to [0, 2π)
        totalAngle %= (2 * PI)
        if (totalAngle < 0) totalAngle += 2 * PI

        // Return turret’s physical angle (convert if servo is mapped differently)
        return turretFromServo(totalAngle)
    }


    fun turretFromServo(totalAngle: Double): Double {
        var angle = (totalAngle / gearRatio)
        if (angle < 0) angle += 2 * PI
        return angle
    }

    fun aimDistance() {
        when(manualAim){
            12 -> targetVelo = 835.0 // 0.81
            24 -> targetVelo = 862.0 // 0.93
            36 -> targetVelo = 844.0 // 0.71
            48 -> targetVelo = 848.0 // 0.51
            60 -> targetVelo = 908.0 // 0.51
            72 -> targetVelo = 1025.0 // 0.73
            84 -> targetVelo = 1165.0 // 1
            96 -> targetVelo = 1260.0 // 1
            108 -> targetVelo = 1100.0 // 0.42 Broken
            120 -> targetVelo = 1112.0 // 0.44 Broken
            132 -> targetVelo = 1150.0 // 0.43 Broken
            144 -> targetVelo = 1172.0 // 0.44 Broken
            else -> targetVelo = 0.0 // 0.0
        }
        when(manualAim){
            12 -> hP = 0.81 // 0.81
            24 -> hP = 0.93 // 0.93
            36 -> hP = 0.71 // 0.71
            48 -> hP = 0.51 // 0.51
            60 -> hP = 0.51 // 0.51
            72 -> hP = 0.73 // 0.73
            84 -> hP = 1.0 // 1
            96 -> hP = 1.0 // 1
            108 -> hP = 0.42 // Broken
            120 -> hP = 0.43 // Broken
            134 -> hP = 0.44 // Broken
            146 -> hP = 0.45 // Broken
            else -> hP = 0.0 // 0.0
        }
        if(manualAim > 146){
            manualAim = 146
        }else if (manualAim < 12){
            manualAim = 12
        }

        if(manualAim % 12 != 0){
            manualAim -= manualAim % 12
        }
    }
}
