package org.firstinspires.ftc.teamcode.helpers

import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap

class Encoder(hardwareMap: HardwareMap, name: String) {
    val encoder: DcMotorEx by lazy { hardwareMap.dcMotor.get(name) as DcMotorEx}

    /**
     * @return [Int] that is the position of the encoder
     */
    fun getPosition() = encoder.currentPosition
}