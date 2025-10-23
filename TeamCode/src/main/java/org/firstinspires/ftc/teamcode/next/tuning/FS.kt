package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx

@TeleOp
class FS(): OpMode() {
    private lateinit var f1M: DcMotorEx
    private lateinit var f2M:DcMotorEx
    private lateinit var iM: DcMotorEx

    override fun init() {
        f1M= hardwareMap.dcMotor.get("f1M") as DcMotorEx
        f2M= hardwareMap.dcMotor.get("f2M") as DcMotorEx
        iM=hardwareMap.dcMotor.get("iM") as DcMotorEx
    }

    override fun loop() {
        if (gamepad1.a) {
            f1M.power=1.0
            f2M.power=1.0
        }
        else {
            iM.power=0.0
            f1M.power=0.0
            f2M.power=0.0
        }
        if (gamepad1.b) {
            iM.power=1.0
        }
    }
}