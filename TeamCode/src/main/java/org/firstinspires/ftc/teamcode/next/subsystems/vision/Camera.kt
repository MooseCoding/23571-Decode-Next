package org.firstinspires.ftc.teamcode.util

import ArtifactPipeline
import android.graphics.Canvas
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.VisionProcessor
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.util.Arrays
import java.util.Locale

object Camera: Subsystem {
    lateinit var vP: VisionPortal
    val pipeline: ArtifactPipeline = ArtifactPipeline()
    var artifacts: Array<ArtifactPipeline.Artifact?> = arrayOf(null, null, null)
    var incoming: ArtifactPipeline.Artifact? = null
    val right_start: Point = Point(50.0, 50.0) // Place holders
    val right_stop: Point = Point(50.0,50.0)

    override fun initialize() {
        vP = VisionPortal.Builder()
            .setCamera(ActiveOpMode.hardwareMap.get(WebcamName::class.java, "cam1"))
            .addProcessor(pipeline)
            .setCameraResolution(android.util.Size(640, 480))
            .enableLiveView(true)
            .setAutoStopLiveView(true)
            .setStreamFormat(VisionPortal.StreamFormat.YUY2)
            .build()
    }

    override fun periodic() {
        for (a: ArtifactPipeline.Artifact in pipeline.foundArtifacts!!) {
            if(a.center.inside(Rect(right_start, right_stop))) {

            }
        }
    }
}