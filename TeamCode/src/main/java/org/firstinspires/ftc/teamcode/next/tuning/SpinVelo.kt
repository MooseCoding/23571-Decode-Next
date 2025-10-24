package org.firstinspires.ftc.teamcode.next.tuning

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo

@TeleOp
@Configurable
class SpinVelo(): OpMode() {
    private lateinit var f1M: DcMotorEx
    private lateinit var f2M:DcMotorEx
    private lateinit var fS: Servo
    private lateinit var iM: DcMotorEx
    private lateinit var gS: CRServo
    private var aStatus = false;
    private var aNum = 0.0
    private var bStatus = false;
    private var bNum = 0.0;

    companion object {
        @JvmField
        var gServoPower = 0.0;

        @JvmField
        var flywheel_velo = 0.0

        @JvmField
        var flap_pos = 0.0
    }
    override fun init() {
        f1M= hardwareMap.dcMotor.get("f1M") as DcMotorEx
        f2M= hardwareMap.dcMotor.get("f2M") as DcMotorEx
        iM=hardwareMap.dcMotor.get("iM") as DcMotorEx
        gS= hardwareMap.crservo.get("gS") as CRServo
        fS=hardwareMap.servo.get("flap") as Servo
        f2M.direction=DcMotorSimple.Direction.REVERSE
    }

    override fun loop() {
        f1M.velocity = flywheel_velo
        f2M.velocity = flywheel_velo
        gS.power = gS.power
        fS.position = flap_pos

        telemetry.run {
            addData("f1V", f1M.velocity)
            addData("f1P", f1M.power)
            addData("f2V", f2M.velocity)
            addData("f2P", f2M.power)
            update()
        }
    }
}