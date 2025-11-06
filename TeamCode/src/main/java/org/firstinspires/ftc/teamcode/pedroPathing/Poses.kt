package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.follower.Follower
import com.pedropathing.geometry.Pose
import org.firstinspires.ftc.teamcode.next.subsystems.data.Alliance
import kotlin.math.PI

// These poses are for the red alliance
var start = Pose(89.5,6.5,0.0)
var row1 = Pose(110.0, 35.5, 0.0)// Row closest to the front of the field
var row1End = Pose() // End of row 1 (e.g. where we stop intake)
var row2 = Pose(110.0, 59.5, 0.0) // Row seconds closest
var row2End = Pose()
var row3 = Pose(110.0, 83.5, 0.0) // Row closest to the classifier
var row3End = Pose()
var shootFar = Pose(84.5, 14.0, PI)

public class Close12(f: Follower, a: Alliance) {
    var index = 0
    var pathCount = 11
    init {
        if (a == Alliance.BLUE) {
            start = start.mirror()
            row1 = row1.mirror()
            row2 = row2.mirror()
            row3 = row3.mirror()
            shootFar = shootFar.mirror()
        }
    }


}

public class Far12 {

}