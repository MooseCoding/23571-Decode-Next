package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Limelight
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@TeleOp
@Disabled
class KalmanTest: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            BindingsComponent,
            BulkReadComponent,
            SubsystemComponent(DriveTrain, Limelight)
        )
    }

    override fun onUpdate() {
        telemetry.update()
    }
}