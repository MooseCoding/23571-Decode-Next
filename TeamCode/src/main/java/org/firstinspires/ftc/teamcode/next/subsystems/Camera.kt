package org.firstinspires.ftc.teamcode.next.subsystems

import android.annotation.SuppressLint
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode.hardwareMap
import dev.nextftc.ftc.ActiveOpMode.telemetry
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor


object Camera: Subsystem {
    var Distance = 0.0
    var Rotation = 0.0
    var targetAcquired = false
    var Target = 20

    var aprilTag = AprilTagProcessor.easyCreateWithDefaults()

    private var visionPortal = VisionPortal.Builder()
    .setCamera(hardwareMap.get(WebcamName::class.java, "The Eye")) // Webcam name from config
    .addProcessor(aprilTag)
    .setLiveViewContainerId(0)
    .build()

    override fun periodic(){
        telemetryAprilTag()
        telemetry.update()
    }

    @SuppressLint("DefaultLocale")
    private fun telemetryAprilTag() {
        val currentDetections: List<AprilTagDetection> = aprilTag.detections
        // Step through the list of detections and display info for each one.
        for (detection in currentDetections) {
            if (detection.metadata.id == Target) { //Blue ID = 20 // Red ID = 24
                targetAcquired = true
                Rotation = detection.ftcPose.x
                Distance = detection.ftcPose.y
            }
            else if(detection.metadata.id != Target) {
                targetAcquired = false
                Rotation = 0.0
                Distance = 100.0
            }
        }
    }

}