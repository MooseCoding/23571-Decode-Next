package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.follower.Follower
import com.pedropathing.ftc.FollowerBuilder
import com.pedropathing.ftc.localization.localizers.PinpointLocalizer
import com.pedropathing.geometry.Pose
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.LLResultTypes.FiducialResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.ActiveOpMode
import org.firstinspires.ftc.teamcode.helpers.controllers.FusionLocalizer
import org.firstinspires.ftc.teamcode.next.filters.Kalman
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Motif
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI

object Limelight : Subsystem {

    lateinit var ll: Limelight3A

    // ================= KALMAN FILTERS =================
    private val kx = Kalman(0.0, 0.5, q = 0.1)
    private val ky = Kalman(0.0, 0.5, q = 0.1)


    private var lastX = 0.0
    private var lastY = 0.0

    @JvmField var limelightOn = true

    lateinit var follower: Follower
    lateinit var fusionLocalizer: FusionLocalizer

    override fun initialize() {
        ll = ActiveOpMode.hardwareMap.get(Limelight3A::class.java, "ll")
        ll.setPollRateHz(100)
        ll.pipelineSwitch(0)
        ll.start()

        /*
        fusionLocalizer = FusionLocalizer(
            PinpointLocalizer(ActiveOpMode.hardwareMap, Constants.localizerConstants),
            doubleArrayOf(5.0, 5.0, 5.0),
            doubleArrayOf(2.0, 2.0, 2.0),
            doubleArrayOf(0.1, 0.1, 0.1),
            50
        )
        follower = FollowerBuilder(Constants.followerConstants, ActiveOpMode.hardwareMap)
            .pathConstraints(Constants.pathConstraints)
            .setLocalizer(fusionLocalizer)
            .mecanumDrivetrain(Constants.driveConstants)
            .build()

        // follower.setStartingPose(Pose(88.0, 117.0, 117/180.0*PI))
         // PedroComponent.follower.setStartingPose(Pose(88.0, 117.0, 117/180.0*PI))
        follower.setStartingPose(PedroComponent.follower.pose)

         */
    }

    fun kalman() {
        follower.update()

        if (!limelightOn) return

        val visionPose = megatag2() ?: return

        fusionLocalizer.addMeasurement(visionPose,
            (System.nanoTime() - ll.latestResult.captureLatency).toLong()
        )

        ActiveOpMode.telemetry.run {
            addData("Limelight X: ", visionPose.x)
            addData("Limelight Y: ", visionPose.y)
            addData("LL Heading: ",visionPose.heading )
            addData("Fusion X", follower.pose.x)
            addData("Fusion Y", follower.pose.y)
            addData("Fusion H", follower.heading / PI * 180.0)
        }

        PanelsTelemetry.telemetry.run {
            addData("Limelight X: ", visionPose.x)
            addData("Limelight Y: ", visionPose.y)
            addData("LL Heading: ",visionPose.heading )
            addData("Fusion X", follower.pose.x)
            addData("Fusion Y", follower.pose.y)
            addData("Fusion H", follower.heading)
        }

//        follower.pose = Pose(
//            kx.x,
//            ky.x,
//            follower.heading
//        )
    }

    override fun periodic() {
        val m = megatag2()

        if(m != null) {
            ActiveOpMode.telemetry.run {
                addData("POSE FROM LL", m)
            }
        }
    }

    fun justRelocalize() {
        val vP: Pose = megatag2() ?: return

        PedroComponent.follower.pose = vP
    }

    fun motif(): Motif {
        val fR = grabResultData()
        if (fR != null) {
            for (f: FiducialResult in fR.fiducialResults) {
                when (f.fiducialId) {
                    21 -> return Motif.GPP
                    22 -> return Motif.PGP
                }
            }
        }
        return Motif.PPG
    }

    fun getTx(): Double? {
        val r = grabResultData() ?: return null

        return r.fiducialResults[0].targetXDegrees
    }

    fun grabResultData(): LLResult? {
        val lR = ll.latestResult
        return if (lR != null && lR.isValid) lR else null
    }

    fun relocalize(p: Pose) {
        if (!p.roughlyEquals(PedroComponent.follower.pose, 5.0)) {
            PedroComponent.follower.setPose(p)
        }
    }

    fun megatag2(): Pose? {
        val lR = grabResultData() ?: return null
        if (lR.fiducialResults.isEmpty()) return null

        ll.updateRobotOrientation(lR.botpose.orientation.yaw)

        ActiveOpMode.telemetry.addData("LL Heading", lR.botpose_MT2.orientation.yaw)

        val botpose = lR.botpose_MT2 ?: return null

        return Pose(
            botpose.position.y * 39.37 + 72,
            -(botpose.position.x * 39.37) + 72,
        )
    }
}
