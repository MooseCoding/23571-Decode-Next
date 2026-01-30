package org.firstinspires.ftc.teamcode.wayfinder

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D

class MotorWayfinder(val bL: MotorEx, val bR: MotorEx, val fR: MotorEx,val fL: MotorEx, pinpoint: GoBildaPinpointDriver): Wayfinder(pinpoint) {
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