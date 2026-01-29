package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.NextFTCOpMode
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.next.subsystems.Light

@TeleOp
@Disabled
class LightTester: NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(Light)
        )
    }

    override fun onUpdate() {
        telemetry.addData("light pos", Light.light.position)
    }
}