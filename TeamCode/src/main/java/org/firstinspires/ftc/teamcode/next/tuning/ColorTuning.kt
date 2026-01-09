package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.Sensor
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@TeleOp
@Disabled
class ColorTuning: NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(Sensor),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    override fun onStartButtonPressed() {

    }
}