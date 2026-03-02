package org.firstinspires.ftc.teamcode.next.subsystems.outtake


import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.hardware.VoltageSensor
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.FlywheelLight
import kotlin.math.absoluteValue

@Configurable
object Flywheels: Subsystem {
    val f1 = MotorEx("f1")
    val f2 = MotorEx("f2").reversed()

    @JvmField var flywheelPID = PIDCoefficients(0.0000, 0.0, 0.0)
    @JvmField var flywheelFF = BasicFeedforwardParameters(0.00038, 0.0, 0.09)
    private var flywheelController = controlSystem {
        velPid(flywheelPID)
        basicFF(flywheelFF)
    }

    @JvmField var targetVelocity = 0.0 //  Outtake.farVelocity
   
    /**
     * To determine if we are spinning slow or at PID
     */
    @JvmField var spinSlow = false

    var motorRpm: Double = 0.0

    @JvmField var velocity: Double = 0.0

    val voltage: VoltageSensor by lazy { ActiveOpMode.hardwareMap.voltageSensor.get("Control Hub")  }

    override fun periodic() {
        if(!ActiveOpMode.opModeInInit) {
            if (!spinSlow) {
                f1.power = flywheelController.calculate(KineticState(0.0, f1.velocity.absoluteValue / voltage.voltage))
                f2.power = f1.power
                flywheelController.goal = KineticState(0.0, targetVelocity)
            } else {
                f1.power = 0.5
                f2.power = 0.5
            }
        }


        if(ActiveOpMode.opModeIsActive) {
            if (f1.velocity.absoluteValue + 200.0 > targetVelocity) {
                FlywheelLight.Green().schedule()
            } else {
                FlywheelLight.Blue().schedule()
            }
        }

        /*
        PanelsTelemetry.telemetry.run {
            addData("Flywheel Target Velo", targetVelocity)
            addData("Flywheel velocity", f1.velocity.absoluteValue)
        }
         */
    }

    /**
     * @return Spins the Flywheel
     */
    fun spin(): Command = InstantCommand {
        spinSlow = false
    }

    /**
     * @return Stops the Flywheel to cruise speed
     */
    fun stop(): Command = InstantCommand {
        spinSlow = true
    }
    /**
     * @return Hard Stop
     */
    fun hardStop(): Command = InstantCommand {
        spinSlow = false
        targetVelocity = 0.0
    }
}