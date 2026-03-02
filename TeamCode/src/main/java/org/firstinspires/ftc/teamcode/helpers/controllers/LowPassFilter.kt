package org.firstinspires.ftc.teamcode.helpers.controllers

class LowPassFilter(var alpha: Float) {
    private var outputDeg: Float? = null  // null until first value

    /**
     * @param headingDeg input heading in degrees [0, 360)
     * @return smoothed heading in degrees [0, 360)
     */
    fun filter(headingDeg: Float): Float {
        val current = outputDeg
        if (current == null) {
            outputDeg = headingDeg
            return headingDeg
        }

        // Compute shortest angular difference (-180 to 180)
        var delta = headingDeg - current
        delta = ((delta + 180f) % 360f + 360f) % 360f - 180f

        // Apply EMA on the delta
        val newOutput = (current + alpha * delta + 360f) % 360f
        outputDeg = newOutput
        return newOutput
    }

    fun reset() { outputDeg = null }
}
