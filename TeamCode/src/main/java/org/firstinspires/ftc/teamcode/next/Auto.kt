package org.firstinspires.ftc.teamcode.next

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.tuning.Drive
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

@Autonomous
class Auto() : NextFTCOpMode() {
    /*val fL = MotorEx("frontLeft").reversed()
    val fR = MotorEx("frontRight")
    val bL = MotorEx("backLeft").reversed()
    val bR = MotorEx("backRight")

    override fun onStartButtonPressed() {
        SequentialGroup(
            InstantCommand {
                fL.power = 0.3
                fR.power = 0.3
                bR.power = 0.3
                bR.power = 0.3
            },
            Delay(4.seconds),
            InstantCommand {
                fL.power = 0.0
                fR.power = 0.0
                bR.power = 0.0
                bR.power = 0.0
            }
        ).schedule()
    }*/
}