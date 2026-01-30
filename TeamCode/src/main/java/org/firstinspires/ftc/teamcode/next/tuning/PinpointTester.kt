package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.next.subsystems.Pinpoint

@TeleOp
@Disabled
class PinpointTester: NextFTCOpMode() {
    init{
        addComponents(
            BindingsComponent,
            BulkReadComponent,
            SubsystemComponent(
                Pinpoint
            )
        )
    }

    override fun onInit() {
    }

    override fun onUpdate() {
        telemetry.run {
            addData("X", Pinpoint.getX())
            addData("Y", Pinpoint.getY())
            addData("Heading", Pinpoint.getHeading())
            update()
        }
    }
}