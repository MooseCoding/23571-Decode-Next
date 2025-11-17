import android.graphics.Canvas
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration
import org.firstinspires.ftc.vision.VisionProcessor
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.util.Arrays
import java.util.Locale

class ArtifactPipeline : VisionProcessor {
    /*
 * Our working image buffers
 */
    var yCrCbMat: Mat = Mat()
    var crMat: Mat = Mat()
    var cbMat: Mat = Mat()
    var contoursOnPlainImageMat: Mat = Mat()

    // purple artifacts
    var pCrMat: Mat = Mat()
    var pCbMat: Mat = Mat()
    var pThresholdMat: Mat = Mat()
    var pMorphedThreshold: Mat = Mat()

    // green artifacts
    var gCrMat: Mat = Mat()
    var gCbMat: Mat = Mat()
    var gThresholdMat: Mat = Mat()
    var gMorphedThreshold: Mat = Mat()

    var contour2f: MatOfPoint2f = MatOfPoint2f()

    /*
 * The elements we use for noise reduction
 */
    var erodeElement: Mat = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
    var dilateElement: Mat = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(6.0, 6.0))


    // list to hold all found artifacts
    var foundArtifacts: ArrayList<Artifact>? = null
        private set

    override fun init(width: Int, height: Int, calibration: CameraCalibration) {
    }

    override fun processFrame(frame: Mat, captureTimeNanos: Long): Any {
        foundArtifacts = ArrayList()
        /*
     * Run the image processing
     */
        analyzeArtifacts(frame, findContours(frame))

        //        switch (stages[stageNum]) {
        //            case FINAL:
        //                return frame;
        //            case Cb:
        //                return cbMat;
        //            case Cr:
        //                return crMat;
        //            case pThresh:
        //                return pThresholdMat;
        //            case pMorph:
        //                return pMorphedThreshold;
        //            case gThresh:
        //                return gThresholdMat;
        //            case gMorph:
        //                return gMorphedThreshold;
        //            case CONTOURS:
        //                return contoursOnPlainImageMat;
        //        }
        return frame
    }

    override fun onDrawFrame(
        canvas: Canvas,
        onscreenWidth: Int,
        onscreenHeight: Int,
        scaleBmpPxToCanvasPx: Float,
        scaleCanvasDensity: Float,
        userContext: Any
    ) {
        //        for (Artifact a : foundArtifacts) {
        //            Paint p = new Paint();
        //            Scalar color;
        //            switch (a.color) {
        //                case GREEN: color = GREEN; break;
        //                case PURPLE: color = PURPLE; break;
        //                default: color = new Scalar(0,0,0);
        //            }
        //
        //            p.setColor(Color.rgb((int) color.val[0], (int) color.val[1], (int) color.val[2]));
        //            p.setStyle(Paint.Style.STROKE);
        //            p.setStrokeWidth(scaleCanvasDensity * 2);
        //            canvas.drawCircle((float) a.center.x, (float) a.center.y, (float) a.radius, p);
        //        }
    }

    /*
 * Some stuff to handle returning our various buffers
 */
    //    enum Stage {
    //        FINAL,
    //        Cb,
    //        Cr,
    //        pThresh,
    //        pMorph,
    //        gThresh,
    //        gMorph,
    //        CONTOURS
    //    }
    //
    //
    //    Stage[] stages = Stage.values();
    //
    //    // Keep track of what stage the viewport is showing
    //    int stageNum = 0;
    fun findContours(input: Mat): List<ArrayList<MatOfPoint>> {
        // A list we'll be using to store the contours we find
        val purpleContours = ArrayList<MatOfPoint>()
        val greenContours = ArrayList<MatOfPoint>()

        // Convert the input image to YCrCb color space, then extract the Cb and Cr channels
        Imgproc.cvtColor(input, yCrCbMat, Imgproc.COLOR_RGB2YCrCb)
        Core.extractChannel(yCrCbMat, crMat, 1)
        Core.extractChannel(yCrCbMat, cbMat, 2)

        // ------ get purple artifacts --------

        // get thresholds for cB and cR channels
        Core.inRange(crMat, Scalar(135.0), Scalar(200.0), pCrMat)
        Core.inRange(cbMat, Scalar(135.0), Scalar(175.0), pCbMat)

        // combine thresholds - if the pixel is in Cr AND Cb thresholds
        Core.bitwise_and(pCrMat, pCbMat, pThresholdMat)

        // erode and dilate
        morphMask(pThresholdMat, pMorphedThreshold)

        // Ok, now actually look for the contours! We only look for external contours.
        Imgproc.findContours(
            pMorphedThreshold,
            purpleContours,
            Mat(),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_NONE
        )


        // ------- green artifacts ---------

        // get thresholds for cB and cR channels
        Core.inRange(crMat, Scalar(50.0), Scalar(105.0), gCrMat)
        Core.inRange(cbMat, Scalar(118.0), Scalar(145.0), gCbMat)

        // combine thresholds - if the pixel is in Cr AND Cb thresholds
        Core.bitwise_and(gCrMat, gCbMat, gThresholdMat)

        // erode and dilate
        morphMask(gThresholdMat, gMorphedThreshold)
        // Ok, now actually look for the contours! We only look for external contours.
        Imgproc.findContours(
            gMorphedThreshold,
            greenContours,
            Mat(),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_NONE
        )

        greenContours.removeIf { contour: MatOfPoint? -> Imgproc.contourArea(contour) <= 675 }
        purpleContours.removeIf { contour: MatOfPoint? -> Imgproc.contourArea(contour) <= 675 }

        // We do draw the contours we find, but not to the main input buffer.
        //        if (stages[stageNum] == Stage.CONTOURS) {
        input.copyTo(contoursOnPlainImageMat)
        Imgproc.drawContours(
            contoursOnPlainImageMat,
            purpleContours,
            -1,
            PURPLE,
            CONTOUR_LINE_THICKNESS,
            8
        )
        Imgproc.drawContours(
            contoursOnPlainImageMat,
            greenContours,
            -1,
            GREEN,
            CONTOUR_LINE_THICKNESS,
            8
        )

        //        }
        return Arrays.asList(purpleContours, greenContours)
    }

    fun morphMask(input: Mat, output: Mat) {
        /*
     * Apply some erosion and dilation for noise reduction.
     * A strong erosion helps separate blobs that are close together.
     */
        Imgproc.erode(input, output, erodeElement)
        Imgproc.erode(output, output, erodeElement)
        Imgproc.erode(output, output, erodeElement) // Add a third erosion

        Imgproc.dilate(output, output, dilateElement)
        Imgproc.dilate(output, output, dilateElement)
        Imgproc.dilate(output, output, dilateElement) // Add a third dilation to compensate
    }

    fun analyzeArtifacts(input: Mat, contours: List<ArrayList<MatOfPoint>>) {
        val purpleContours = contours[0]
        val greenContours = contours[1]

        for (i in purpleContours.indices) {
            val contour = purpleContours[i]
            analyzeArtifactContour(input, contour, PURPLE, Artifact.Color.PURPLE)
        }

        for (i in greenContours.indices) {
            val contour = greenContours[i]
            analyzeArtifactContour(input, contour, GREEN, Artifact.Color.GREEN)
        }
    }

    fun analyzeArtifactContour(
        input: Mat,
        contour: MatOfPoint,
        drawColor: Scalar,
        color: Artifact.Color
    ) {
        contour.convertTo(contour2f, CvType.CV_32F)

        // Fit minimum enclosing circle
        val center = Point()
        val radius = FloatArray(1)
        Imgproc.minEnclosingCircle(contour2f, center, radius)

        // Draw filled circle on mask
        // center point
        Imgproc.circle(input, center, 2, BLACK, 2)
        Imgproc.putText(
            input,
            String.format(Locale.ENGLISH, "%s: %.2f,%.2f", color.name, center.x, center.y),
            center,
            Imgproc.FONT_HERSHEY_PLAIN,
            1.0,
            drawColor
        )
        // border
        Imgproc.circle(input, center, radius[0].toInt(), drawColor, 3) // -1 = filled

        foundArtifacts!!.add(Artifact(color, center, radius[0].toDouble()))
    }

    class Artifact(var color: Color, var center: Point, var radius: Double) {
        enum class Color {
            PURPLE,
            GREEN
        }
    }

    companion object {
        /*
 * Colors
 */
        val TEAL: Scalar = Scalar(3.0, 148.0, 252.0)
        val PURPLE: Scalar = Scalar(158.0, 52.0, 235.0)
        val RED: Scalar = Scalar(255.0, 0.0, 0.0)
        val GREEN: Scalar = Scalar(0.0, 255.0, 0.0)
        val BLACK: Scalar = Scalar(255.0, 255.0, 255.0)
        val BLUE: Scalar = Scalar(0.0, 0.0, 255.0)

        const val CONTOUR_LINE_THICKNESS: Int = 2
    }
}