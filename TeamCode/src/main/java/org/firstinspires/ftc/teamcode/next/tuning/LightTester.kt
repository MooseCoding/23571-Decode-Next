package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import org.firstinspires.ftc.teamcode.next.subsystems.FlywheelLight
import org.firstinspires.ftc.teamcode.next.subsystems.Light

@TeleOp
@Disabled
class LightTester: NextFTCOpMode() {
    init {
        addComponents(
            BindingsComponent,
            SubsystemComponent(Light, FlywheelLight)
        )
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.circle whenBecomesTrue SequentialGroupLocal(FlywheelLight.Blue(), Light.Azure())
        Gamepads.gamepad1.square whenBecomesTrue SequentialGroupLocal(FlywheelLight.Red(), Light.Orange())
    }

    override fun onUpdate() {
        telemetry.addData("light pos", Light.light.position)
        telemetry.addData("flywheel", FlywheelLight.light.position)
        telemetry.update()
    }
}