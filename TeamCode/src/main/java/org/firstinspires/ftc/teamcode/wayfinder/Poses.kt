package org.firstinspires.ftc.teamcode.wayfinder

import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.atan2
// Our poses will use Pedro poses but then we'll convert to poses
class FarPoses { // Red Alliance Poses
    companion object {
        var start = PedroPose(89.5, 8.0, PI / 2)
        var row3 = PedroPose(102.0, 35.5, 0.0)
        var row3End = PedroPose(129.0, 35.5, 0.0)
        var row2 = PedroPose(102.0, 59.5, 0.0)
        var row2End = PedroPose(129.0, 59.5, 0.0)

        var row2ShootPoint = PedroPose(102.0, 50.0, PI - atan((50-59.5)/(129.0-102.0)))
        var humanPlayer = PedroPose(133.0, 10.5, -10.62 / 180.0 * PI)
        var cyclePose = PedroPose(130.0, 21.35, 30/180.0 * PI)
        var shoot1 = PedroPose(93.0, 18.0, atan((35.5-18.0)/(102.0-93.0)))
        var shoot2 = PedroPose(93.0, 18.0, atan((10.5-18)/(133-93)))
        var cycleShoot = PedroPose(93.0, 18.0, 50.0/180.0 * PI)
        var rampIntake = PedroPose(131.0, 16.0, 0.0)
        var park = PedroPose(106.0, 8.5, PI / 2)

        fun flipPose(p: PedroPose): PedroPose = PedroPose(144 - p.x, p.y, p.h)

        fun switchToBlue() {
            start = flipPose(start)
            row3 = flipPose(row3)
            row3End = flipPose(row3End)
            row2 = flipPose(row2)
            row2End = flipPose(row2End)
            row2ShootPoint = flipPose(row2ShootPoint)
            humanPlayer = flipPose(humanPlayer)
            shoot1 = flipPose(shoot1)
            shoot2 = flipPose(shoot2)
            cycleShoot = flipPose(cycleShoot)
            rampIntake = flipPose(rampIntake)
            park = flipPose(park)
            cyclePose = flipPose(cyclePose)
        }
    }

}