package com.example.appcpp2;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends CameraActivity {

    static {
        System.loadLibrary("appcpp2");
    }


    private File cascadeFIle;
    private static String LOGTAG = "OpenCV_Log";
    private CameraBridgeViewBase mOpenCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*if (OpenCVLoader.initDebug()){
            Log.d(LOGTAG, "Open CV Berhasil");
        }*/

        mOpenCameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_surface_view);
        mOpenCameraView.setCameraIndex(1);
        mOpenCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCameraView.setCvCameraViewListener(cvCameraViewListener2);

        try {
            cascadeFIle = new File(getCacheDir(), "haarcascade_frontalface_default.xml");
            if (!cascadeFIle.exists()){
                InputStream inputStream = getAssets().open("haarcascade_frontalface_default.xml");
                FileOutputStream outputStream = new FileOutputStream(cascadeFIle);

                byte[] buffer = new byte[2048];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1){
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
            }
            initFaceDetector(cascadeFIle.getAbsolutePath());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public native void FindFeatures(long addrGray, long addrRGB);
    public native void initFaceDetector(String filePath);
    public native void DetectFace(long addrGray, long addrRGB);

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {

            if (status == LoaderCallbackInterface.SUCCESS){
                Log.v(LOGTAG, "Open CV Tersimpan");
                mOpenCameraView.enableView();
            }
            else {
                super.onManagerConnected(status);
            }

        }
    };

    @Override
    protected List<?extends CameraBridgeViewBase> getCameraViewList(){
        return Collections.singletonList(mOpenCameraView);
    }

    Mat mRgbaT;

    private CameraBridgeViewBase.CvCameraViewListener2 cvCameraViewListener2 = new CameraBridgeViewBase.CvCameraViewListener2() {



        @Override
        public void onCameraViewStarted(int width, int height) {

        }

        @Override
        public void onCameraViewStopped() {

        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            //fix rotation number 3
            /*Mat mRgba = inputFrame.rgba();
            Mat mRgbaT = mRgba.t();
            Core.flip(mRgba.t(), mRgbaT, 1);
            Imgproc.resize(mRgbaT, mRgbaT, mRgba.size());
            return mRgbaT;*/

            Mat input_rgba = inputFrame.rgba();
            Mat input_gray = inputFrame.gray();

            Mat mRgbaT = input_rgba.t();
            Core.flip(input_rgba.t(), mRgbaT, 0);
            Imgproc.resize(mRgbaT, mRgbaT, input_rgba.size());

            //FindFeatures(input_gray.getNativeObjAddr(), input_rgba.getNativeObjAddr());

            Mat grayT = input_gray.t();
            Core.flip(input_gray.t(), grayT, 0);
            Imgproc.resize(grayT, grayT, input_gray.size());

            DetectFace(grayT.getNativeObjAddr(), mRgbaT.getNativeObjAddr());


            /*MatOfPoint corners = new MatOfPoint();
            Imgproc.goodFeaturesToTrack(input_gray,corners,20,0.001,10,new Mat(),3,false);
            Point[] cornersArr = corners.toArray();

            for (int i = 0; i < cornersArr.length; i++){
                Imgproc.circle(input_rgba,cornersArr[i],10,new Scalar(0,255,0),2);
            }
            return input_rgba;*/
            return mRgbaT;
        }
    };

    @Override
    public void onPause(){
        super.onPause();
        if(mOpenCameraView != null){
            mOpenCameraView.disableView();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Log.d(LOGTAG, "Open CV Belum di inisialisasi, Memulai Open CV...");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION,this,mLoaderCallback);
        }
        else{
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mOpenCameraView != null){
            mOpenCameraView.disableView();
        }
    }
}