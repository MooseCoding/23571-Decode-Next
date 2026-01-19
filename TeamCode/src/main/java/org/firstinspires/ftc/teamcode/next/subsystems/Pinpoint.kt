package org.firstinspires.ftc.teamcode.next.subsystems

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

object Pinpoint: Subsystem {
    lateinit var pinpoint: GoBildaPinpointDriver

    override fun periodic() {
        DriveTrain.currentX = pinpoint.getPosX(DistanceUnit.INCH)
        DriveTrain.currentY = pinpoint.getPosY(DistanceUnit.INCH)
        DriveTrain.currentHeading = pinpoint.getHeading(org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.RADIANS)

        pinpoint.update()
    }

    override fun initialize() {
        pinpoint = ActiveOpMode.hardwareMap.get(GoBildaPinpointDriver::class.java, "pp")
        pinpoint.setOffsets(-3.5, -2.0, DistanceUnit.INCH)
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED, GoBildaPinpointDriver.EncoderDirection.REVERSED)
        pinpoint.resetPosAndIMU()
    }

    fun getX(): Double {
        return pinpoint.getPosX(DistanceUnit.INCH)
    }

    fun getY(): Double {
        return pinpoint.getPosY(DistanceUnit.INCH)
    }

    fun getHeading(): Double {
        return pinpoint.getHeading(org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES)
    }
}