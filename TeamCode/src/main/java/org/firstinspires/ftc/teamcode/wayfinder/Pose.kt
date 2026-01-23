package org.firstinspires.ftc.teamcode.wayfinder

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D
import kotlin.math.PI

class Pose(x: Double, y: Double, h:Double): Pose2D(DistanceUnit.INCH, x,y, AngleUnit.RADIANS, h) {
    val x = x
    val y = y
    val h = h
}
class PedroPose(x: Double, y: Double, h:Double): Pose2D(DistanceUnit.INCH, y-72.0, x-72.0, AngleUnit.RADIANS, h- PI/2) {
    val x = x
    val y = y
    val h = h
}