package org.firstinspires.ftc.teamcode.next

import android.util.Size
import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.NewOuttake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.util.ArtifactCirclePipeline
import org.firstinspires.ftc.vision.VisionPortal

@TeleOp
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
    val proc: ArtifactCirclePipeline = ArtifactCirclePipeline()

    override fun onInit() {
        vP = VisionPortal.Builder()
            .setCamera(hardwareMap.get(WebcamName::class.java, "cam1"))
            .addProcessor(proc)
            .setCameraResolution(Size(640,480))
            .enableLiveView(true)
            .setAutoStopLiveView(true)
            .setStreamFormat(VisionPortal.StreamFormat.YUY2)
            .build()
    }

    override fun onStartButtonPressed() {
        super.onStartButtonPressed()
    }

    override fun onUpdate() {
        tele.run {

        }
    }
}