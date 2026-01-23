package org.firstinspires.ftc.teamcode.wayfinder

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D

class MotorWayfinder(bL: MotorEx, bR: MotorEx, fR: MotorEx, fL: MotorEx, pinpoint: GoBildaPinpointDriver): Wayfinder(pinpoint) {
    val fL = fL
    val fR = fR
    val bL = bL
    val bR = bR

    fun drive(to: Pose2D): Boolean {
        fL.power = this.frontLeftMotorOutput
        fR.power = this.frontRightMotorOutput
        bL.power = this.backLeftMotorOutput
        bR.power = this.backRightMotorOutput
        return driveTo(to)
    }

    fun turn(target: Double): Boolean {
        fL.power = this.frontLeftMotorOutput
        fR.power = this.frontRightMotorOutput
        bL.power = this.backLeftMotorOutput
        bR.power = this.backRightMotorOutput
        return turnTo(target)
    }
}