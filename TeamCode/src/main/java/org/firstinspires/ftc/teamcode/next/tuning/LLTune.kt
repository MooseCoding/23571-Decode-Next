package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.Limelight
@TeleOp
@Disabled
class LLTune:NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(Limelight),
            BulkReadComponent,
            BindingsComponent
        )
    }

    override fun onUpdate() {
        val r = Limelight.grabResultData()
        if (r!=null) {
            telemetry.addData("r.x", r.tx)
            telemetry.addData("r.y", r.ty)
            telemetry.addData("r.a", r.ta)
        }
    }
}