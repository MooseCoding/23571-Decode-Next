package org.firstinspires.ftc.teamcode.helpers

import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin

fun normalize_angle(theta:Double): Double {
    return atan2(sin(theta), cos(theta))
}