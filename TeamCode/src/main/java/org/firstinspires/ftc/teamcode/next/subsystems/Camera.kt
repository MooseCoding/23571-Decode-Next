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
    val Distance = 0.0
    val Rotation = 0.0

    private val aprilTag: AprilTagProcessor? = null

    private var aprilTagProcessor = AprilTagProcessor.Builder()
        .build()

    private var visionPortal = VisionPortal.Builder()
    .setCamera(hardwareMap.get(WebcamName::class.java, "The Eye")) // Webcam name from config
    .addProcessor(aprilTagProcessor)
    .setLiveViewContainerId(0)
    .build()

    override fun periodic(){
        telemetryAprilTag()
        telemetry.update()
    }

    @SuppressLint("DefaultLocale")
    private fun telemetryAprilTag() {
        val currentDetections: MutableList<AprilTagDetection> = aprilTagProcessor.getDetections()
        telemetry.addData("# AprilTags Detected", currentDetections.size)

        // Step through the list of detections and display info for each one.
        for (detection in currentDetections) {
            if (detection.metadata != null) { //Blue ID = 20 // Red ID = 24
                telemetry.addLine(
                    String.format(
                        "\n==== (ID %d) %s",
                        detection.id,
                        detection.metadata.name
                    )
                )
                telemetry.addLine(
                    String.format(
                        "XYZ %6.1f %6.1f %6.1f  (inch)",
                        detection.ftcPose.x, //x is horizontal distance aka turret rotation
                        detection.ftcPose.y, //y is distance from tag
                        detection.ftcPose.z
                    )
                )
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id))
                telemetry.addLine(
                    String.format(
                        "Center %6.0f %6.0f   (pixels)",
                        detection.center.x,
                        detection.center.y
                    )
                )
            }
        } // end for() loop


        // Add "key" information to telemetry
        telemetry.addLine("\nkey:\nXYZ = X (Right/Left), Y (Forward), Z (Up/Down) dist.")
    } // end method telemetryAprilTag()

}