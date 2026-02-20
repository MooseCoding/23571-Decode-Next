package org.firstinspires.ftc.teamcode.wayfinder

import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.LambdaCommand
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D
import org.firstinspires.ftc.robotcore.external.navigation.Position

fun WayfinderDrive(wF: MotorWayfinder, targetPose: Pose2D): LambdaCommand = LambdaCommand()
    .setStart {
        wF.drive(targetPose)
    }
    .setUpdate {
        wF.drive(targetPose)
    }
    .setIsDone {
        wF.atTarget
    }

fun WayfinderTurn(wF: MotorWayfinder, targetHeading: Double): LambdaCommand = LambdaCommand()
    .setStart {
        wF.turn(targetHeading)
    }
    .setUpdate {
        wF.turn(targetHeading)
    }
    .setIsDone {
        wF.atTarget
    }