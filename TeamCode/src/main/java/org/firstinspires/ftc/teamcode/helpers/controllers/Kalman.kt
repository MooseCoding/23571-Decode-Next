package org.firstinspires.ftc.teamcode.next.filters

import kotlin.math.abs

class Kalman(
    initialState: Double,
    initialCovariance: Double,
    var q: Double,
    var gateK: Double = 9.0
) {
    var x: Double = initialState
        private set

    var p: Double = initialCovariance
        private set

    fun predict(delta: Double) {
        x += delta
        p += q
    }

    fun update(measurement: Double, r: Double): Boolean {
        val residual = measurement - x
        val threshold = gateK * (p + r)

        if (residual * residual > threshold) {
            return false
        }

        val k = p / (p + r)
        x += k * residual
        p *= (1 - k)
        return true
    }

    fun reset(state: Double, covariance: Double) {
        x = state
        p = covariance
    }
}
