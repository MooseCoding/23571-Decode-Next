package org.firstinspires.ftc.teamcode.next.subsystems

import com.acmerobotics.dashboard.config.Config
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
@Config
object Outtake: Subsystem {
     val gear = CRServoEx("gS")
     val f1 = MotorEx("f1M")
     val f2 = MotorEx("f2M").reversed()
     val fS = ServoEx("flap")

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

    @JvmField
    var kP: Double = 0.0

    @JvmField
    var kI = 0.0

    @JvmField
    var kD = 0.0

    @JvmField
    var kV = 0.0

    @JvmField
    var kA = 0.0

    @JvmField
    var kS = 0.0

    var controller = controlSystem {
        velPid(kP,kI,kD)
        basicFF(kV,kA,kS)
    }

    @JvmField
    var targetOnVelo = 0.0
    @JvmField
    var targetInVelo = 0.0
    @JvmField
    var targetBackVelo = 0.0
    @JvmField
    var targetVelo = 0.0

    val flywheelOn: Command = RunToVelocity(controller, targetOnVelo).requires(this).named("FlywheelOn")
    val flywheelOff: Command = RunToVelocity(controller, 0.0).requires(this).named("FlywheelOff")
    val flywheelIn: Command = RunToVelocity(controller, targetInVelo).requires(this).named("FlywheelIn")
    val flywheelBack: Command = RunToVelocity(controller, targetBackVelo).requires(this).named("FlywheelIn")

    val flywheelTarget: Command = RunToVelocity(controller, targetVelo).requires(this).named("target")

    override fun periodic() {
        if (velocityTrue) {
            f1.power=controller.calculate(f1.state)
            f2.power=f1.power
        }
        else {
            f1.power = f1P
            f2.power = f2P
        }
        gear.power = gP
        fS.position = fP
        controller = controlSystem {
            velPid(kP,kI,kD)
            basicFF(kV,kA,kS)
        }
    }

    val runOuttake = SequentialGroup(
        InstantCommand {
            f1P = 0.4 // Some constant For Power
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

    val reverseOuttake = InstantCommand {
        f1P = -0.4
        f2P = f1P
    }

    val pushBackOuttake = InstantCommand {
        f1P = -0.2
        f2P = f1P
    }
}