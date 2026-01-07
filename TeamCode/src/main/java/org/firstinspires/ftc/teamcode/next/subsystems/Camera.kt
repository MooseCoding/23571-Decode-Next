package org.firstinspires.ftc.teamcode.next.subsystems

import android.annotation.SuppressLint
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode.hardwareMap
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor

object Camera: Subsystem {
    var Distance = 0.0
    var Rotation = 0.0
    var targetAcquired = false
    var Target = 20
    var aprilTag: AprilTagProcessor? = AprilTagProcessor.easyCreateWithDefaults()

    private var visionPortal = VisionPortal.Builder()
    .setCamera(hardwareMap.get(WebcamName::class.java, "The Eye")) // Webcam name from config
    .addProcessor(aprilTag)
    .setLiveViewContainerId(0)
    .build()

    override fun periodic(){
        telemetryAprilTag()
    }
    fun startCamera(){
        visionPortal.resumeStreaming()
    }
    private fun telemetryAprilTag() {
        val currentDetections: List<AprilTagDetection> = aprilTag!!.detections
        val targetDetection = currentDetections.find { it.metadata.id == Target }
        if (targetDetection != null) {
            targetAcquired = true
            Rotation = targetDetection.ftcPose.x
            Distance = targetDetection.ftcPose.y
        } else {
            targetAcquired = false
            Rotation = 0.0
            Distance = 100.0
        }

    }

}