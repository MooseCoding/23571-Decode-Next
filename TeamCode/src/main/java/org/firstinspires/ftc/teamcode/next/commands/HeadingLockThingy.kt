package org.firstinspires.ftc.teamcode.next.commands

import com.pedropathing.control.PIDFController
import com.pedropathing.math.MathFunctions
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.hardware.driving.DriverControlledCommand
import java.util.function.Supplier

class HeadingLockThingy @JvmOverloads constructor(
    drivePower: Supplier<Double>,
    strafePower: Supplier<Double>,
    turnPower: Supplier<Double>,
    private val robotCentric: Boolean = true
) : DriverControlledCommand(drivePower, strafePower, turnPower) {

    lateinit var controller: PIDFController
    var headingLock: Boolean = true

    override fun start() {
        controller = PIDFController(follower.constants.coefficientsHeadingPIDF)
        follower.startTeleopDrive()
    }

    override fun calculateAndSetPowers(powers: DoubleArray) {
        val (drive, strafe, turn) = powers
        controller.setCoefficients(follower.constants.coefficientsHeadingPIDF);
        controller.updateError(getHeadingError());
        if (headingLock) {
            follower.setTeleOpDrive(
                drive,
                strafe,
                controller.run()
            );
        } else {
            follower.setTeleOpDrive(
                drive,
                strafe,
                turn
            );
        }
    }

    override fun stop(interrupted: Boolean) {
        if (interrupted) follower.breakFollowing()
    }

    fun getHeadingError(): Double {
        var headingGoal: Double = Math.atan2(140.0-follower.pose.y, 140.0 - follower.pose.x)

        val headingError = MathFunctions.getTurnDirection(follower.getPose().getHeading(), headingGoal) * MathFunctions.getSmallestAngleDifference(follower.getPose().getHeading(), headingGoal);
        return headingError;
    }
}