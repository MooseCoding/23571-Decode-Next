package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.CRServoEx
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.hardware.impl.ServoEx
import java.time.Instant

@TeleOp
class MotorLocator: NextFTCOpMode() {
    init {
        addComponents(
            BulkReadComponent,
            BindingsComponent,
        )
    }

    val m1 = MotorEx("bR") // intake
    val m2 = MotorEx("fL") // fl
    val m3 = MotorEx("bL") // bL
    val m4 = MotorEx("fR") // bR
    val m5 = MotorEx("intake") // IS INTAKE
    val m6 = MotorEx("f1") // flywheel
    val m7 = MotorEx("f2") // reversed fluwheel
    val m8 = MotorEx("transfer") // transfer

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.cross whenBecomesTrue InstantCommand { m1.power = 0.5 } whenBecomesFalse InstantCommand { m1.power = 0.0 } // Intake
        Gamepads.gamepad1.triangle whenBecomesTrue InstantCommand { m2.power = 0.5 } whenBecomesFalse InstantCommand { m2.power = 0.0 } // fL
        Gamepads.gamepad1.square whenBecomesTrue InstantCommand { m3.power = 0.5 } whenBecomesFalse InstantCommand { m3.power = 0.0 } // Shooter proper
        Gamepads.gamepad1.circle whenBecomesTrue InstantCommand { m4.power = 0.5 } whenBecomesFalse InstantCommand { m4.power = 0.0} // BR
        Gamepads.gamepad1.dpadUp whenBecomesTrue InstantCommand { m5.power = 1.0 } whenBecomesFalse InstantCommand { m5.power = 0.0 } // Intake
        Gamepads.gamepad1.dpadDown whenBecomesTrue InstantCommand { m6.power = 0.5 } whenBecomesFalse InstantCommand { m6.power = 0.0 } // BL
        Gamepads.gamepad1.dpadRight whenBecomesTrue InstantCommand { m7.power = 0.5 } whenBecomesFalse InstantCommand { m7.power = 0.0 } // FR
        Gamepads.gamepad1.dpadLeft whenBecomesTrue InstantCommand { m8.power = 0.5 } whenBecomesFalse InstantCommand { m8.power = 0.0} // FL
    }
}