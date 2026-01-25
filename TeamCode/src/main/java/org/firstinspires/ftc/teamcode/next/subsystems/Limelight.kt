package org.firstinspires.ftc.teamcode.next.subsystems

import com.acmerobotics.dashboard.config.Config
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
import org.firstinspires.ftc.robotcore.internal.hardware.android.GpioPin
import org.firstinspires.ftc.teamcode.helpers.controllers.FusionLocalizer
import org.firstinspires.ftc.teamcode.next.filters.Kalman
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Motif
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI

@Config
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

        fusionLocalizer = FusionLocalizer(
            PinpointLocalizer(ActiveOpMode.hardwareMap, Constants.localizerConstants),
            doubleArrayOf(0.2, 0.2, 0.2),
            doubleArrayOf(0.04, 0.04, 0.04),
            doubleArrayOf(0.2, 0.2, 0.2),
            50
        )
        follower = FollowerBuilder(Constants.followerConstants, ActiveOpMode.hardwareMap)
            .pathConstraints(Constants.pathConstraints)
            .setLocalizer(fusionLocalizer)
            .mecanumDrivetrain(Constants.driveConstants)
            .build()

       follower.setStartingPose(PedroComponent.follower.pose)
    }

    override fun periodic() {
        follower.update()

        if (!limelightOn) return

        val visionPose = megatag2() ?: return

        fusionLocalizer.addMeasurement(visionPose,
            (System.nanoTime() - ll.latestResult.captureLatency).toLong()
        )
        /*val dx = follower.pose.x - lastX
        val dy = follower.pose.y - lastY

        lastX = follower.pose.x
        lastY = follower.pose.y

        kx.predict(dx)
        ky.predict(dy)

        val sigma = 0.300
        val r = sigma.pow(2)

        kx.update(visionPose.x, r)
        ky.update(visionPose.y, r)
         */

        ActiveOpMode.telemetry.run {
            addData("Limelight X: ", visionPose.x)
            addData("Limelight Y: ", visionPose.y)
            addData("LL Heading: ",visionPose.heading )
            addData("Fusion X", follower.pose.x)
            addData("Fusion Y", follower.pose.y)
            addData("Fusion H", follower.heading)
            addData("Predicted X: ", kx.x)
            addData("Predicted Y: ", ky.x)
        }

//        follower.pose = Pose(
//            kx.x,
//            ky.x,
//            follower.heading
//        )
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

    fun grabResultData(): LLResult? {
        val lR = ll.latestResult
        return if (lR != null && lR.isValid) lR else null
    }

    fun megatag2(): Pose? {
        val lR = grabResultData() ?: return null
        if (lR.fiducialResults.isEmpty()) return null

        ll.updateRobotOrientation(lR.botpose.orientation.yaw)
        // val pos = (Math.PI/4 + PI/2)*180/PI
        // ll.updateRobotOrientation(135.0)

        val botpose = lR.botpose_MT2 ?: return null

        return Pose(
            botpose.position.y * 39.37 + 72,
            -(botpose.position.x * 39.37) + 72,
        )
    }
}
