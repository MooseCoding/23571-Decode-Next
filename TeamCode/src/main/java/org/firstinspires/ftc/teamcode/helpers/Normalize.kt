package org.firstinspires.ftc.teamcode.helpers

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun normalize_angle(theta:Double): Double {
    return atan2(sin(theta), cos(theta))
}