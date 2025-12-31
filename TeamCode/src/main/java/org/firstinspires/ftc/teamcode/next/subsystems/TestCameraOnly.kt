package org.firstinspires.ftc.teamcode.next.subsystems

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.ftc.ActiveOpMode
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor

class TestCameraOnly {
    @Disabled
    @TeleOp(name = "Concept: AprilTag Easy", group = "Concept")
    class ConceptAprilTagEasy : LinearOpMode() {

        companion object {
            private const val USE_WEBCAM = true // true for webcam, false for phone camera
        }

        /**
         * The variable to store our instance of the AprilTag processor.
         */
        private lateinit var aprilTag: AprilTagProcessor

        /**
         * The variable to store our instance of the vision portal.
         */
        private lateinit var visionPortal: VisionPortal

        override fun runOpMode() {
            initAprilTag()

            // Wait for the DS start button to be touched.
            telemetry.addData("DS preview on/off", "3 dots, Camera Stream")
            telemetry.addData(">", "Touch START to start OpMode")
            telemetry.update()
            waitForStart()

            if (opModeIsActive()) {
                while (opModeIsActive()) {
                    telemetryAprilTag()

                    // Push telemetry to the Driver Station.
                    telemetry.update()

                    // Save CPU resources; can resume streaming when needed.
                    when {
                        gamepad1.dpad_down -> visionPortal.stopStreaming()
                        gamepad1.dpad_up -> visionPortal.resumeStreaming()
                    }

                    // Share the CPU.
                    sleep(20)
                }
            }

            // Save more CPU resources when camera is no longer needed.
            visionPortal.close()
        }

        /**
         * Initialize the AprilTag processor.
         */
        private fun initAprilTag() {
            // Create the AprilTag processor the easy way.
            aprilTag = AprilTagProcessor.easyCreateWithDefaults()

            // Create the vision portal the easy way.
            visionPortal = VisionPortal.Builder()
                .setCamera(ActiveOpMode.hardwareMap.get(WebcamName::class.java, "The Eye")) // Webcam name from config
                .addProcessor(aprilTag)
                .setLiveViewContainerId(0)
                .build()
        }

        /**
         * Add telemetry about AprilTag detections.
         */
        private fun telemetryAprilTag() {
            val currentDetections: List<AprilTagDetection> = aprilTag.detections
            telemetry.addData("# AprilTags Detected", currentDetections.size)

            // Step through the list of detections and display info for each one.
            for (detection in currentDetections) {
                if (detection.metadata != null) {
                    telemetry.addLine("\n==== (ID ${detection.id}) ${detection.metadata.name}")
                    telemetry.addLine(
                        String.format(
                            "XYZ %6.1f %6.1f %6.1f  (inch)",
                            detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z
                        )
                    )
                    telemetry.addLine(
                        String.format(
                            "PRY %6.1f %6.1f %6.1f  (deg)",
                            detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw
                        )
                    )
                    telemetry.addLine(
                        String.format(
                            "RBE %6.1f %6.1f %6.1f  (inch, deg, deg)",
                            detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation
                        )
                    )
                } else {
                    telemetry.addLine("\n==== (ID ${detection.id}) Unknown")
                    telemetry.addLine(
                        String.format(
                            "Center %6.0f %6.0f   (pixels)",
                            detection.center.x, detection.center.y
                        )
                    )
                }
            }

            // Add "key" information to telemetry
            telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.")
            telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)")
            telemetry.addLine("RBE = Range, Bearing & Elevation")
        }
    }

}