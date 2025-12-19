package org.firstinspires.ftc.teamcode.next.subsystems.vision

import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.ftc.ActiveOpMode.telemetry
import kotlinx.coroutines.delay
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor

object AprilCam: Subsystem {
    private val aprilTagProcessor = AprilTagProcessor.Builder()
        .build()

    private val visionPortal = VisionPortal.Builder()
        .setCamera(ActiveOpMode.hardwareMap.get(WebcamName::class.java, "The Eye")) // Webcam name from config
        .addProcessor(aprilTagProcessor)
        .setLiveViewContainerId(0)
        .build()


    override fun periodic(){
        telemetryAprilTag()
        telemetry.update()
    }
    private fun telemetryAprilTag() {
        val currentDetections: MutableList<AprilTagDetection> = aprilTagProcessor.detections
        telemetry.addData("# AprilTags Detected", currentDetections.size)

        // Step through the list of detections and display info for each one.
        for (detection in currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(
                    String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name)
                )
                telemetry.addLine(
                    String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z)
                )
                telemetry.addLine(
                    String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw)
                )
                telemetry.addLine(
                    String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation)
                )
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id))
                telemetry.addLine(
                    String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y)
                )
            }
        }
        telemetry.addLine("XYZ = X (Right), Y (Forward), Z (Up) dist.")
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)")
        telemetry.addLine("RBE = Range, Bearing & Elevation")
    }

}