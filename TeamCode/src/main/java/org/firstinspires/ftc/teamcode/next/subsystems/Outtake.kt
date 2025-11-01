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
     val f1 = MotorEx("f1M")
     val f2 = MotorEx("f2M").reversed()
     val hS = ServoEx("flap")

    // Constants
    @JvmField
    var targetOnVelo = 800.0
    @JvmField
    var targetBackVelo = -400.0
    @JvmField
    var pid = PIDCoefficients(0.0033,0.0,0.0)
    @JvmField
    var ff = BasicFeedforwardParameters(1.66667E-4, 0.0, 0.003)
    var controller = controlSystem {
        velPid(pid)
        basicFF(ff)
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
    var hoodPos = 0.0 // Hood position is some function of angle

    override fun periodic() {
        if (velocityTrue) {
            f1.power = controller.calculate(f1.state)
            f2.power = f1.power
            controller.goal = KineticState(0.0, targetVelo)
        }
        
        aimbot()
    }
    
    fun aimbot() {
        // Find phi

        // X,Y, height coordinate of the hoop needs to be hardcoded, and the turretHeight
        // Note x & y are in units and height and turret height will be in inches

        /*var xcord = 0.0
        var ycord = 0.0
        var height = 0.0 // Units initally
        var turretHeight = 0.0 // Inches
        val conversion = 10.0 // Inches per unit

        var phi = atan2((ycord-DriveTrain.follower.pose.y),(xcord-DriveTrain.follower.pose.x))
        var deltaPhi = atan2(sin(phi-DriveTrain.follower.heading), cos(phi-DriveTrain.follower.heading))
        deltaPhi = atan2(sin(deltaPhi - turrentAngle), cos(deltaPhi - turrentAngle))
        */
        // Find theta (MAYBE I THINK I CAN JUST HARD CODE IN POSITION)
        /*var dist = sqrt((xcord-DriveTrain.follower.pose.x).pow(2) + (ycord-DriveTrain.follower.pose.y).pow(2))
        var theta = atan2(dist*conversion,height*conversion-turretHeight)
        */

        // Move aimbot

    }

    // Commands
    val spinGearLeft = InstantCommand {
        gP = -0.5 // Some Constant
    }
    val spinGearRight = InstantCommand {
        gP = 0.5 // Some Constant
    }
    val stopGear = InstantCommand {
        gP = 0.0
    }
    val FlapDown = InstantCommand {
        hP += 0.01
    }
    val FlapUp = InstantCommand {
        hP -= 0.01
    }
    val flywheelOff:InstantCommand = InstantCommand { velocityTrue = false; targetVelo = 0.0; f1.power=0.0; f2.power=0.0}
    val flywheelBack: InstantCommand = InstantCommand { velocityTrue=true; targetVelo= targetBackVelo }
    val flywheelOn: InstantCommand = InstantCommand { velocityTrue=true; targetVelo= targetOnVelo }
}