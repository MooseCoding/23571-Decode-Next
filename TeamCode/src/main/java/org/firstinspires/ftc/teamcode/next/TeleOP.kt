package org.firstinspires.ftc.teamcode.next

import com.bylazar.panels.Panels
import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.JoinedTelemetry
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent

@TeleOp
class TeleOP: NextFTCOpMode() {
    val telemetry = JoinedTelemetry(PanelsTelemetry.ftcTelemetry, telemetry)
    init {
        addComponents(
            BulkReadComponent,
            BindingsComponent,
        )
    }
}