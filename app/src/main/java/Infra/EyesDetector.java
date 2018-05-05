package Infra;

import android.app.Activity;
import android.content.res.AssetManager;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class EyesDetector {


    public void initClassifiers( AssetManager assets, File deviceStorage)
    {
        m_classifierStoragePath = copyClassifier(assets, deviceStorage);
    }

    public int getEyesFromImage(byte[] image)
    {
        if (m_classifierStoragePath == null || m_classifierStoragePath.isEmpty())
            return -1;
        System.loadLibrary("opencv_java3");
        Mat source=null;
        Mat template=null;
        source = Imgcodecs.imdecode(new MatOfByte(image), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
        CascadeClassifier classifier = new CascadeClassifier(m_classifierStoragePath);

        MatOfRect eyes = new MatOfRect();
        classifier.detectMultiScale(source, eyes);
        for (Rect rect : eyes.toArray()) {
            Imgproc.rectangle(source, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(200, 200, 100),2);
        }
        return eyes.toArray().length;
    }

    private String copyClassifier(AssetManager assets, File deviceStorage)
    {
        //create dir
        File dir = new File(deviceStorage,CLASSIFIERS_DIR);
        if(!dir.exists()){
            dir.mkdir();
        }

        //Destination
        File classifier = new File(dir, CASCADE_NAME);
        if (classifier.exists())
            classifier.delete();

        try {
            InputStream source = assets.open(CASCADE_NAME);
            OutputStream out = new FileOutputStream(classifier);
            byte[] buffer = new byte[1024];
            int read;
            while((read = source.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
            source.close();
            out.flush();
            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return classifier.getAbsolutePath();
    }

    private String m_classifierStoragePath;
    private final String CASCADE_NAME = "Classifier.xml";
    private final String CLASSIFIERS_DIR = "Classifiers";
}
