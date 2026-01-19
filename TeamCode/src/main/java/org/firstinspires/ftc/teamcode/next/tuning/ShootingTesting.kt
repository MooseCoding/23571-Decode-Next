package org.firstinspires.ftc.teamcode.next.tuning

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Pinpoint
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood

@TeleOp
@Config
class ShootingTesting: NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(
                Flywheels, Hood
            ),
            BindingsComponent,
            BulkReadComponent
        )
    }

    companion object {
        var dist: Double = 11.5*12
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.cross whenBecomesTrue Outtake.shoot()
    }
}