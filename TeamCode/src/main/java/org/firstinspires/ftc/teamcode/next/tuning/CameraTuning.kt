package org.firstinspires.ftc.teamcode.next

import ArtifactPipeline
import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.vision.VisionPortal

@TeleOp
@Disabled
class CameraTuning: NextFTCOpMode() {
    val tele = JoinedTelemetry(PanelsTelemetry.ftcTelemetry, telemetry)

    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    lateinit var vP: VisionPortal
    val proc: ArtifactPipeline = ArtifactPipeline()

    override fun onInit() {

    }

    override fun onStartButtonPressed() {
        super.onStartButtonPressed()
    }

    override fun onUpdate() {
        if(proc.foundArtifacts != null) {
            for (a in proc.foundArtifacts!!) {
                telemetry.addData("center", a.center)
                telemetry.addData("colour", a.color)
            }
        }

        tele.run {
            update()
        }
    }
}