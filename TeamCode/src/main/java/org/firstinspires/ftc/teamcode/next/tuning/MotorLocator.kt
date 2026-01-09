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
@Disabled
class MotorLocator: NextFTCOpMode() {
    init {
        addComponents(
            BulkReadComponent,
            BindingsComponent,
        )
    }

    val m1 = MotorEx("cm0") // BR
    val m2 = MotorEx("cm1") // BL
    val m3 = MotorEx("cm2") // FL
    val m4 = MotorEx("cm3") // Trasnfer
    val m5 = MotorEx("em0") // Flywheel Right { leave }
    val m6 = MotorEx("em1") // Flywheel Left { must reverse }
    val m7 = MotorEx("em2") // Intake
    val m8 = MotorEx("em3") // FR

    val cr1 = CRServoEx("cr0") // One of the turret
    val cr2 = CRServoEx("cr1") // One of the turret
    val s1 = ServoEx("s0") // Hood
    val light = ServoEx("light") // Light

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.cross whenBecomesTrue InstantCommand { m1.power = 0.5 } whenBecomesFalse InstantCommand { m1.power = 0.0 }
        Gamepads.gamepad1.triangle whenBecomesTrue InstantCommand { m2.power = 0.5 } whenBecomesFalse InstantCommand { m2.power = 0.0 }
        Gamepads.gamepad1.square whenBecomesTrue InstantCommand { m3.power = 0.5 } whenBecomesFalse InstantCommand { m3.power = 0.0 }
        Gamepads.gamepad1.circle whenBecomesTrue InstantCommand { m4.power = 0.5 } whenBecomesFalse InstantCommand { m4.power = 0.0}
        Gamepads.gamepad1.dpadUp whenBecomesTrue InstantCommand { m5.power = 0.5 } whenBecomesFalse InstantCommand { m5.power = 0.0 }
        Gamepads.gamepad1.dpadDown whenBecomesTrue InstantCommand { m6.power = 0.5 } whenBecomesFalse InstantCommand { m6.power = 0.0 }
        Gamepads.gamepad1.dpadRight whenBecomesTrue InstantCommand { m7.power = 0.5 } whenBecomesFalse InstantCommand { m7.power = 0.0 }
        Gamepads.gamepad1.dpadLeft whenBecomesTrue InstantCommand { m8.power = 0.5 } whenBecomesFalse InstantCommand { m8.power = 0.0}

        Gamepads.gamepad1.rightBumper whenBecomesTrue InstantCommand { s1.position += 0.05 } whenBecomesFalse InstantCommand { s1.position -= 0.05}

        Gamepads.gamepad1.leftStickButton whenBecomesTrue InstantCommand { cr1.power = 0.1 } whenBecomesFalse InstantCommand { cr1.power = 0.0 }
        Gamepads.gamepad1.rightStickButton whenBecomesTrue InstantCommand { cr2.power = 0.1} whenBecomesFalse InstantCommand { cr2.power = 0.0 }

        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue InstantCommand { light.position = 0.5 } whenBecomesFalse InstantCommand{light.position = 0.0}

    }
}