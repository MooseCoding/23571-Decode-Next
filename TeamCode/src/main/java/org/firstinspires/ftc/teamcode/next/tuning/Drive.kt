package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.driving.MecanumDriverControlled
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import java.time.Instant

@TeleOp(name="Tuning for Drivetrain")
class Drive: NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(DriveTrain),
            BulkReadComponent,
            BindingsComponent
        )
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.a whenBecomesTrue  InstantCommand { DriveTrain.fL.power = 1.0 } whenBecomesFalse InstantCommand { DriveTrain.fL.power = 0.0}
        Gamepads.gamepad1.b whenBecomesTrue  InstantCommand { DriveTrain.fR.power = 1.0 } whenBecomesFalse InstantCommand { DriveTrain.fR.power = 0.0}
        Gamepads.gamepad1.x whenBecomesTrue  InstantCommand { DriveTrain.bR.power = 1.0 } whenBecomesFalse InstantCommand { DriveTrain.bR.power = 0.0}
        Gamepads.gamepad1.y whenBecomesTrue  InstantCommand { DriveTrain.bL.power = 1.0 } whenBecomesFalse InstantCommand { DriveTrain.bL.power = 0.0}

    }
}