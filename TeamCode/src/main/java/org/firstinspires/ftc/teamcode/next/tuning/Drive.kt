package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.driving.MecanumDriverControlled
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Robot

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

    }
}