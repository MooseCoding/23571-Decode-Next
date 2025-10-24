package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
import dev.nextftc.control.builder.controlSystem
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
import kotlin.time.Duration.Companion.seconds

@Configurable
object Outtake: Subsystem {
    private val gear = CRServoEx("gS")
    private val f1 = MotorEx("f1M")
    private val f2 = MotorEx("f2M").reversed()
    private val fS = ServoEx("flap")

    @JvmField
    var f1P = 0.0
    @JvmField
    var f2P = 0.0
    @JvmField
    var gP = 0.0
    @JvmField
    var fP = 0.0

    @JvmField
    var velocityTrue: Boolean = false

    private val controller = controlSystem {
        velPid(0.0,0.0,0.0)
        basicFF(0.0,0.0,0.0)
    }

    @JvmField
    var targetOnVelo = 0.0
    @JvmField
    var targetInVelo = 0.0
    @JvmField
    var targetBackVelo = 0.0

    val flywheelOn: Command = RunToVelocity(controller, targetOnVelo).requires(this).named("FlywheelOn")
    val flywheelOff: Command = RunToVelocity(controller, 0.0).requires(this).named("FlywheelOff")
    val flywheelIn: Command = RunToVelocity(controller, targetInVelo).requires(this).named("FlywheelIn")
    val flywheelBack: Command = RunToVelocity(controller, targetBackVelo).requires(this).named("FlywheelIn")

    override fun periodic() {
        if (velocityTrue) {
            f1.power=controller.calculate(f1.state)
            f2.power=controller.calculate(f2.state)
        }
        else {
            f1.power = f1P
            f2.power = f2P
        }
        gear.power = gP
        fS.position = fP
    }

    val runOuttake = SequentialGroup(
        InstantCommand {
            f1P = 1.0 // Some constant
            f2P = f1P
        }
    )

    val stopOuttake = SequentialGroup(
        InstantCommand {
            f1P = 0.0
            f2P = f1P
        }
    )

    val intakeBall = SequentialGroup(
        flywheelIn,
        Delay(3.seconds),
        flywheelBack,
        Delay(3.seconds)
    )

    val spinGearLeft = InstantCommand {
        gP = -0.8 // Some Constant
    }

    val spinGearRight = InstantCommand {
        gP = 0.8 // Some Constant
    }

    val stopGear = InstantCommand {
        gP = 0.0
    }

    val FlapDown = InstantCommand {
        fP += 0.1
    }

    val FlapUp = InstantCommand {
        fP -= 0.1
    }

    val pushBackOuttake = InstantCommand {
        f1P = -0.1
        f2P = f1P
    }
}