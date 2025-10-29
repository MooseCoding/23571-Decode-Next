package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
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
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.hardware.impl.ServoEx
import dev.nextftc.hardware.powerable.SetPower
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

@Configurable
object Outtake: Subsystem {
     val gear = CRServoEx("gS")
     val f1 = MotorEx("f1M")
     val f2 = MotorEx("f2M").reversed()
     val fS = ServoEx("flap")

    @JvmField
    var gP = 0.0
    @JvmField
    var fP = 0.0

    @JvmField
    var velocityTrue = true

    @JvmField
    var pid = PIDCoefficients(0.0033,0.0,0.0)

    @JvmField
    var ff = BasicFeedforwardParameters(1.66667E-4, 0.0, 0.003)

    var controller = controlSystem {
        velPid(pid)
        basicFF(ff)
    }

    @JvmField
    var targetVelo = 800.0

    override fun periodic() {
        if (velocityTrue) {
            f1.power = controller.calculate(f1.state)
            f2.power = f1.power
            controller.goal = KineticState(0.0, targetVelo)
        }

        gear.power = gP
        fS.position = fP
    }

    @JvmField
    var targetOnVelo = 0.0

    @JvmField
    var targetBackVelo = 0.0


    val flywheelOff = InstantCommand { velocityTrue = false; targetVelo = 0.0}
    val flywheelBack: InstantCommand = InstantCommand { velocityTrue=true; targetVelo= targetBackVelo }
    val flywheelOn: InstantCommand = InstantCommand { velocityTrue=true; targetVelo= targetOnVelo }

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
        fP += 0.01
    }

    val FlapUp = InstantCommand {
        fP -= 0.01
    }
}