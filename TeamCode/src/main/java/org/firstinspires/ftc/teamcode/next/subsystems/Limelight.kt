package org.firstinspires.ftc.teamcode.next.subsystems

import com.qualcomm.hardware.limelightvision.Limelight3A
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode

class Limelight: Subsystem {
    val ll: Limelight3A = ActiveOpMode.hardwareMap.get(Limelight3A::class.java, "ll")

    override fun initialize() {
        ll.setPollRateHz(100)
        ll.start()
    }

    override fun periodic() {

    }
}