package org.firstinspires.ftc.teamcode.next.tuning

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import dev.nextftc.ftc.Gamepads
import dev.nextftc.hardware.driving.MecanumDriverControlled
import dev.nextftc.hardware.impl.MotorEx

@TeleOp
@Configurable
@Config
class FlywheelSpin(): OpMode() {
    private lateinit var f1M: DcMotorEx
    private lateinit var f2M:DcMotorEx
    private lateinit var iM: DcMotorEx
    private lateinit var gS: CRServo
    private lateinit var fS: Servo
    private var aStatus = false;
    private var aNum = 0.0

    private val fL = MotorEx("frontLeft").reversed()
    private val fR = MotorEx("frontRight")
    private val bL = MotorEx("backLeft").reversed()
    private val bR = MotorEx("backRight")

    // 0.33 is top of fS
    // 0 is bottom

    companion object {
        @JvmField
        var g_servo:Double = 0.0;

        @JvmField
        var flywheel_power: Double = 0.0

        @JvmField
        var flap_pos: Double = 0.0
    }
    override fun init() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        f1M= hardwareMap.dcMotor.get("f1M") as DcMotorEx
        f2M= hardwareMap.dcMotor.get("f2M") as DcMotorEx
        iM=hardwareMap.dcMotor.get("iM") as DcMotorEx
        gS= hardwareMap.crservo.get("gS") as CRServo
        f2M.direction= DcMotorSimple.Direction.REVERSE
        fS = hardwareMap.servo.get("flap") as Servo

        var drive = MecanumDriverControlled(
                fL,
            fR,
            bL,
            bR,
        -Gamepads.gamepad1.leftStickY,
        Gamepads.gamepad1.leftStickX,
        Gamepads.gamepad1.rightStickX
        )
        drive()
    }

    override fun loop() {
        if (gamepad1.a) {
            aStatus = !aStatus
        }

        if(aStatus){
            flywheel_power=1.0
        }else{
            flywheel_power = 0.0
        }

        if (gamepad1.b) {
            iM.power = 0.5
        }else{
            iM.power = 0.0
        }

        if(gamepad1.left_trigger > 0){
            g_servo = 1.0
        }else if(gamepad1.right_trigger > 0){
            g_servo = -1.0
        }else{
            g_servo = 0.0;
        }

        if (gamepad1.left_bumper) {
            g_servo = -1.0
        }
        else if (gamepad1.right_bumper) {
            g_servo = 1.0
        }
        else {
            g_servo = 0.0
        }

        f1M.power = flywheel_power
        f2M.power = flywheel_power
        gS.power = g_servo
        fS.position= flap_pos

        telemetry.run {
            addData("f1V", f1M.velocity)
            addData("f1P", f1M.power)
            addData("f2V", f2M.velocity)
            addData("f2P", f2M.power)
            addData("flapPos", fS.position)
            addData("g power", gS.power)
            addData("flywheel power", flywheel_power)
            update()
        }
    }
}