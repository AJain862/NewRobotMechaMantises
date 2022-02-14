package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
@Autonomous(name = "AutonomousRed2", group = "LinearOpMode")
//@Disabled
public class AutonomousRed2 extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_DM.tflite";
    private static final String[] LABELS = {
            "Duck",
            "Marker"
    };
    private static final String VUFORIA_KEY =
            "AckJVyv/////AAABmVTYhXy+2kKblacQ/Kj23axJUG1tC4BGaCJHvXW9RO9dIdeQxmVRyLL70kcUjvC2eNt3nxcokoC4d+E0H4N+ah4PAaqkxk1q20takUJ3ILjj19Md6iMYrSToAoRXP0mF1GbB7zSECEXduXe2bs08F9qekY4M0QoTHXeSiaCHo2X8TfA0NsvqSE9nBGgVJi3hUGe/h+/ug5MUAmZsbWKQMQNCpG3E/Lu44sbeet4rs2AimQITW33KXR3t99OEmYYfkjKa+jAl3yvbq1zuNVGauURIX9wlvueS5mRuk4tkN3Ax5O+67wuL+UoGNmoXS3Fb1X/ur4ZdX+FaWd03aBjKN+MVTUYDZqnVfgZpXenUV9sg";
    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;
    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;
    @Override
    public void runOpMode() {
        MantisesClass mantis = new MantisesClass(this);
        telemetry.addData("Initializing", "DO NOT START OPMODE!");
        telemetry.update();
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();
        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1, 16.0/9.0);
        }
        /** Wait for the game to begin */
        telemetry.addData("Ready To Start OpMode", "Press The Start Button To Start!");
        telemetry.update();
        waitForStart();
        sleep(500);
        if (tfod != null) {
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());
                sleep(1000);
                int location = 3;
                for (Recognition recognition : updatedRecognitions) {
                    if(recognition.getLabel().equals("Duck")){
                        if(recognition.getLeft()>100&&recognition.getLeft()<300){
                            location = 2;
                        }else if(recognition.getLeft()>0&&recognition.getLeft()<349){
                            location = 3;

                        }
                        else{
                            location = 1;
                        }
                    }
                }
                telemetry.addData("Location", location);
                telemetry.update();

                //sleep(1000);
                if(location == 1){
                    AutonomousRun(mantis, (384*2)+235);

                }else if(location == 2){
                    AutonomousRun(mantis, (384*2)+235 );
                }else if(location == 3){
                    AutonomousRun(mantis, (384*2)+235);
                }

            }
        }
        stop();
    }
    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "MantisCam");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
    private void AutonomousRun(MantisesClass mantis, int position){
        mantis.curve(40, 1);

        mantis.move(3, 1, 0.2, 8);

        mantis.lineararm.setTargetPosition(position);
        mantis.lineararm.setPower(0.8);
        mantis.lineararm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while(mantis.lineararm.isBusy()){}
        //mantis.move(0+offset, 2, 0.2);
        mantis.rotatearm.setPosition(0.2);
        sleep(1000);
        mantis.intakebox.setPosition(0.5);
        sleep(2000);
        mantis.intakebox.setPosition(0);
        sleep(1000);
        mantis.rotatearm.setPosition(.52);
        sleep(1000);
        mantis.lineararm.setTargetPosition(0);
        mantis.lineararm.setPower(0.8);
        mantis.lineararm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        mantis.lineararm.setTargetPosition(0);
        mantis.move(5, 2, 1, 3);
        mantis.move(40, 4, 1, 8);
        mantis.turn(21, 2);
        mantis.move(5, 2, 1, 3);
        mantis.move(40, 1, 1, 5);


    }
}