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
import  dev.nextftc.ftc.ActiveOpMode;
import org.firstinspires.ftc.robotcore.internal.hardware.android.GpioPin.Active
import kotlin.time.Duration.Companion.seconds


@Configurable
object Outtake: Subsystem {
     val gS = MotorEx("spin")
     val f1 = MotorEx("f1M")
     val f2 = MotorEx("f2M").reversed()
     val hS = ServoEx("flap")

    // Constants
    @JvmField
    var targetOnVelo = 950.0
    @JvmField
    var targetBackVelo = 400.0
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
    @JvmField
    var manualAim = 12
    @JvmField
    var manual = false

    override fun periodic() {
        if (velocityTrue) {
            f1.power = controller.calculate(f1.state)
            f2.power = f1.power
            controller.goal = KineticState(0.0, targetVelo)
        }

        gS.power= gP

        hS.position = hP

        aimDistance()
        autoSpin()
    }

    fun autoSpin(){
        if(!manual){//positive left, negative right // Right has negative numberline, Left has positive numberline
            if(Camera.Rotation > 0.1){
                gP = -0.1 * Camera.Rotation
            }else if(Camera.Rotation < -0.1){
                gP = 0.1 * Camera.Rotation
            }else{
                gP = 0.0
            }
        }
    }
    // Commands
    fun aimDistance() {
        if(manual) {
            when (manualAim) {
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
            when (manualAim) {
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
    val toggleManual = InstantCommand{
        manual = !manual
    }
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
    val flywheelOff:InstantCommand = InstantCommand { velocityTrue = false; targetVelo = 0.0; f1.power=0.0; f2.power=0.0}
    val flywheelBack: InstantCommand = InstantCommand { velocityTrue=false; f1.power=-1.0; f2.power=-1.0 }
    val flywheelOn: InstantCommand = InstantCommand { velocityTrue=true; targetVelo= targetOnVelo }
}