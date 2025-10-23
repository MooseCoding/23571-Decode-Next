package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx

@TeleOp
class FlywheelSpin(): OpMode() {
    private lateinit var f1M: DcMotorEx
    private lateinit var f2M:DcMotorEx
    private lateinit var iM: DcMotorEx
    private var aStatus = false;
    private var aNum = 0.0
    private var bStatus = false;
    private var bNum = 0.0;

    @JvmField
    var flywheel_power = 0.0

    override fun init() {
        f1M= hardwareMap.dcMotor.get("f1M") as DcMotorEx
        f2M= hardwareMap.dcMotor.get("f2M") as DcMotorEx
        iM=hardwareMap.dcMotor.get("iM") as DcMotorEx
    }

    override fun loop() {
        if (gamepad1.a) {
            aStatus = !aStatus
        }

        if(aStatus){
            aNum = 1.0;
        }else{
            aNum = 0.0
        }

        if (gamepad1.b) {
            bStatus = !bStatus
        }
        if(bStatus){
            bNum = 1.0;
        }else{
            bNum = 0.0
        }
        f1M.power = aNum
        f2M.power = aNum
        iM.power = bNum
    }
}