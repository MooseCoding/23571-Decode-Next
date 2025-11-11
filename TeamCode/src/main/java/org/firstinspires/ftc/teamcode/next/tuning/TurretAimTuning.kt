package org.firstinspires.ftc.teamcode.next

import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.NewOuttake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.next.tuning.Drive
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@TeleOp
class TurretAimTuning: NextFTCOpMode() {
    val tele = JoinedTelemetry(PanelsTelemetry.ftcTelemetry, telemetry)

    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(NewOuttake, DriveTrain),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    override fun onInit() {

    }

    override fun onUpdate() {
        telemetry.run {
            addData("x", DriveTrain.currentX)
            addData("y", DriveTrain.currentY)
            addData("heading", DriveTrain.currentHeading)
            addData("current yaw", Turret.getYaw())
            addData("goal", Turret.turretController.goal)
            update()
        }
    }
}