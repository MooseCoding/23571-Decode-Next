package org.firstinspires.ftc.teamcode.next.tuning

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo

@TeleOp
@Configurable
class J3Drivetrain(): OpMode() {
    companion object {
        @JvmField
        var f1P: Double = 0.0 //Flywheel Power
        @JvmField
        var f2P: Double = 0.0 //Flywheel Power
        @JvmField
        var iP: Double = 0.0 //Intake Power
        @JvmField
        var gP: Double = 0.0 //Gear Power
    }

    private lateinit var f1M: DcMotorEx //Flywheel Motor Left
    private lateinit var f2M:DcMotorEx // Flywheel Motor Right
    private lateinit var iM: DcMotorEx //Intake Motor

    private lateinit var gS: CRServo //Gear Servo

    private lateinit var fS: Servo //Flap Servo

    override fun init() {
        f1M= hardwareMap.dcMotor.get("f1M") as DcMotorEx
        f2M= hardwareMap.dcMotor.get("f2M") as DcMotorEx
        iM=hardwareMap.dcMotor.get("iM") as DcMotorEx
        gS=hardwareMap.crservo.get("gS") as CRServo
        fS=hardwareMap.servo.get("fs") as Servo
    }

    override fun loop() {
        if(gamepad1.right_trigger > 0) {
            f1M.power = f1P
            f2M.power = f2P
        }else{
            f1M.power = 0.0
            f2M.power = 0.0
        }

        if(gamepad1.left_trigger > 0) {
            iM.power = iP
        }else{
            iM.power = 0.0
        }

        if(gamepad1.dpad_left){
            gS.power = gP
        }else if(gamepad1.dpad_right){
            gS.power = -gP
        }else {
            gS.power = 0.0
        }
    }
}