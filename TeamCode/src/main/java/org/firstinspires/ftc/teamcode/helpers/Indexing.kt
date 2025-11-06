package org.firstinspires.ftc.teamcode.helpers

import java.lang.Math.floor


/*
@Param -- Distance: Double away from center of classifier

@Returns -- Index to grab from the array of values for hP, P
 */
fun getIndex(distance:Double): Int {
    return ((RoundToHalf(distance)*2).toInt())
}

/*
@Param -- Value: some double

@Returns -- Value rounded to the nearest 0.5
 */
fun RoundToHalf(value:Double): Double {
    val f = floor(value)
    val decimal = value-f

    return when {
        decimal > 0.5 -> f // Returns the floor
        decimal < 0.75 -> 0.5+f // Returns half of the floor
        else -> 1.0+f
    }
}