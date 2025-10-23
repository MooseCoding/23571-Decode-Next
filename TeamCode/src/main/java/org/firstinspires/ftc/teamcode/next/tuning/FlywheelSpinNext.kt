package org.firstinspires.ftc.teamcode.next.tuning

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx

@TeleOp
@Configurable
class FlywheelSpinNext(): OpMode() {
    companion object {
        @JvmField
        var f1P: Double = 0.0
        var f2P: Double = 0.0
        var iP: Double = 0.0
    }

    private lateinit var f1M: DcMotorEx
    private lateinit var f2M:DcMotorEx
    private lateinit var iM: DcMotorEx

    override fun init() {
        f1M= hardwareMap.dcMotor.get("f1M") as DcMotorEx
        f2M= hardwareMap.dcMotor.get("f2M") as DcMotorEx
        iM=hardwareMap.dcMotor.get("iM") as DcMotorEx
    }

    override fun loop() {
        f1M.power = f1P
        f2M.power = f2P
        iM.power = iP
    }
}