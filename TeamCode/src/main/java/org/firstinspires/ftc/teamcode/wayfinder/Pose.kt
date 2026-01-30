package org.firstinspires.ftc.teamcode.wayfinder

import com.pedropathing.geometry.Pose
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D
import kotlin.math.PI

fun Pose(x: Double, y: Double, h: Double): Pose2D {
    return Pose2D(DistanceUnit.INCH, x,y, AngleUnit.RADIANS, h)
}
fun PedroPose(x: Double, y: Double, h: Double): Pose2D {
    return Pose2D(DistanceUnit.INCH, y-72.0, x-72.0, AngleUnit.RADIANS, h+PI/2)
}

fun PedroFromPose2D(p: Pose2D): Pose {
    return Pose(p.getY(DistanceUnit.INCH) + 72.0, p.getX(DistanceUnit.INCH) + 72.0, p.getHeading(AngleUnit.RADIANS) - PI/2)
}
