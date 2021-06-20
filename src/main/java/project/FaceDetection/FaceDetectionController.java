package project.FaceDetection;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;

import org.opencv.xfeatures2d.BEBLID;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import project.GUI.MainApp;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class FaceDetectionController
{
    @FXML
    private Button cameraButton;

    @FXML
    private ImageView originalFrame;

    @FXML
    private Button mainPage;

    @FXML
    private Button capturePhoto;


    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;
    // the OpenCV object that performs the video capture
    private VideoCapture capture;
    // a flag to change the button behavior
    private boolean cameraActive;
    private boolean faceDetected;

    // face cascade classifier
    private CascadeClassifier faceCascade;
    private int absoluteFaceSize;
    private static final File FOLDER = new File("/Users/irfankaradeniz/Documents/LastVersion/Project-2.2/resources/PhotoDatabase");
    private boolean saveable;

    @FXML
    private TextField textField;


    public static Stage newStage = new Stage();

    protected void init()
    {
        this.capture = new VideoCapture();
        this.faceCascade = new CascadeClassifier();
        this.faceCascade.load("resources/haarcascades/haarcascade_frontalface_alt.xml");
        this.absoluteFaceSize = 0;
        this.faceDetected = false;
        this.cameraButton.setDisable(false);
        originalFrame.setFitWidth(600);
        originalFrame.setPreserveRatio(true);
    }

    @FXML
    protected void goToMain() throws IOException {
        StageChanger();
        FaceDetection.stage.hide();
        newStage.showAndWait();
    }

    public void StageChanger() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/fxml/"+"primary" + ".fxml"));
        Scene type2ViewScene = new Scene(fxmlLoader.load());
        newStage.setScene(type2ViewScene);
        newStage.setTitle("MaAssist");
        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                System.exit(0);

            }
        });
    }


    @FXML
    protected void startCamera()
    {
        if (!this.cameraActive)
        {

            this.capture.open(0);

            if (this.capture.isOpened())
            {
                this.cameraActive = true;

                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = new Runnable() {

                    @Override
                    public void run()
                    {
                        // grab and process a single frame
                        Mat frame = grabFrame();
                        // Utils face is for converting the image
                        // Update the image in original frame
                        Image imageToShow = UtilsFace.mat2Image(frame);
                        updateImageView(originalFrame, imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

                // change the button after starting the camera
                this.cameraButton.setText("Stop Camera");
            }
            else
            {
                System.err.println("Unsuccessful attempt");
            }
        }
        else
        {
            this.cameraActive = false;
            this.cameraButton.setText("Start Camera");
            this.stopAcquisition();
        }
    }

    private Mat grabFrame()
    {
        Mat frame = new Mat();

        // check if the capture is open
        if (this.capture.isOpened())
        {
            try
            {
                // read the current frame
                this.capture.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty())
                {
                    // face detection
                    this.detectAndDisplay(frame);
                }

            }
            catch (Exception e)
            {
                // log the (full) error
                System.err.println("Failed to grab " + e);
            }
        }

        return frame;
    }


    private void detectAndDisplay(Mat frame)
    {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();
        boolean saveable = isSaveable();
        String name = getFileName();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        if (this.absoluteFaceSize == 0)
        {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0)
            {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());


        // each rectangle in facesArray is a face
        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 0) {
            this.faceDetected = true;
            this.mainPage.setDisable(false);
        } else {
            this.faceDetected = false;
            this.mainPage.setDisable(true);
        }

        //Selecting color for the rectangle on face
        java.awt.Color defaultColor = Color.YELLOW;
        Scalar color = new Scalar(defaultColor.getBlue(), defaultColor.getGreen(), defaultColor.getRed());

        for (Rect face : facesArray){

            Mat cropped = new Mat(frame, face);

            if (saveable)
                savePhoto(cropped, name);

            Imgproc.putText(frame, "Name: " + recognizeFace(cropped), face.tl(), Font.BOLD, 3.0, color);
            Imgproc.rectangle(frame, face.tl(), face.br(), color);
        }
    }

    private static String recognizeFace(Mat img)
    {
        int errThreshold = 3;

        int mostRecognized = -1;

        File mostSimilar = null;

        for (File capture : Objects.requireNonNull(FOLDER.listFiles()))
        {
            int similarity = matchFaceBEBLID(img, capture.getAbsolutePath());
            if (similarity > mostRecognized)
            {
                mostRecognized = similarity;
                mostSimilar = capture;
            }
        }

        if (mostSimilar != null && mostRecognized > errThreshold)
        {
            String faceName = mostSimilar.getName();
            String delimiter = faceName.contains(" (") ? "(" : ".";
            return faceName.substring(0, faceName.indexOf(delimiter)).trim();
        }
        else
            return "Not Recognized";
    }

    private static int matchFaceORB(Mat currImg, String file)
    {
        Mat compImg = Imgcodecs.imread(file);
        ORB orb = ORB.create();
        int similarity = 0;


        // ORB - Matching
        MatOfKeyPoint keyPointsFirst = new MatOfKeyPoint();
        MatOfKeyPoint keyPointsSecond = new MatOfKeyPoint();

        orb.detect(currImg, keyPointsFirst);
        orb.detect(compImg, keyPointsSecond);

        Mat descriptorsFirst = new Mat();
        Mat descriptorsSecond = new Mat();


        orb.compute(currImg, keyPointsFirst, descriptorsFirst);
        orb.compute(compImg, keyPointsSecond, descriptorsSecond);


        if (descriptorsFirst.cols() == descriptorsSecond.cols())
        {
            MatOfDMatch matrix = new MatOfDMatch();
            DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING).match(descriptorsFirst, descriptorsSecond, matrix);

            for (DMatch match : matrix.toList())
                if (match.distance <= 50)
                    similarity++;
        }

        return similarity;
    }

    private static int matchFaceBEBLID(Mat currImg, String file){
        Mat compImg = Imgcodecs.imread(file);
        BEBLID beblid = BEBLID.create(0.75f);
        ORB orb = ORB.create();

        int similarity = 0;

        // BEBLID - Matching
        MatOfKeyPoint keyPoints1Beblid = new MatOfKeyPoint();
        MatOfKeyPoint keyPoints2Beblid= new MatOfKeyPoint();

        orb.detect(currImg, keyPoints1Beblid);
        orb.detect(compImg, keyPoints2Beblid);

        Mat descriptors1Beblid = new Mat();
        Mat descriptors2Beblid = new Mat();

        beblid.compute(currImg, keyPoints1Beblid, descriptors1Beblid);
        beblid.compute(compImg, keyPoints2Beblid, descriptors2Beblid);



        if (descriptors1Beblid.cols() == descriptors2Beblid.cols())
        {
            MatOfDMatch matrix = new MatOfDMatch();
            DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING).match(descriptors1Beblid, descriptors2Beblid, matrix);

            for (DMatch match : matrix.toList())
                if (match.distance <= 50)
                    similarity++;
        }

        return similarity;
    }




    private void stopAcquisition()
    {
        if (this.timer!=null && !this.timer.isShutdown())
        {
            try
            {
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                System.err.println("Error in stopping " + e);
            }
        }

        if (this.capture.isOpened())
        {
            // release the camera
            this.capture.release();
        }
    }

    private void updateImageView(ImageView view, Image image)
    {
        UtilsFace.onFXThread(view.imageProperty(), image);
    }

    protected void setClosed()
    {
        this.stopAcquisition();
    }

    private static void savePhoto(Mat img, String format)
    {
        File path;
        String type = ".png";
        String fileName = FOLDER + File.separator + format;
        File file = new File(fileName + type);

        if (!file.exists())
            path = file;
        else
        {
            int index = 0;

            do
                path = new File(fileName + " (" + index++ + ")" + type);
            while (path.exists());
        }

        Imgcodecs.imwrite(path.toString(), img);
    }



    boolean isSaveable()
    {
        boolean previous = saveable;
        saveable = false;
        return previous;
    }

    public void saveImage(ActionEvent actionEvent) {
        saveable = true;
    }

    String getFileName()
    {
        return textField.getText();
    }

}
