// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("mylibrary");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("mylibrary")
//      }
//    }

#include "jni.h"
#include "opencv2/core.hpp"
#include "opencv2/imgproc.hpp"
#include "opencv2/features2d.hpp"
#include "vector"
#include "string"
#include "opencv2/objdetect.hpp"


#include "opencv2/imgproc.hpp"

using namespace cv;

extern "C"{
    Mat cameraFrame,cameraT;

    struct CornerInfo{
        int x;
        int y;
    };

    struct FaceInfo{
        int x;
        int y;
    };

    void initOpenCVFrame(int width, int height){
        cameraFrame = Mat(height, width, CV_8UC4);
        //cameraT = Mat(height, width, CV_8UC4);
        //grey = Mat(height, width, CV_8UC4);


    }

    void getFeatures(unsigned char ** rawImage, CornerInfo* cornerInfo){
        cameraFrame.data = *rawImage;
        Mat grey;
        cvtColor(cameraFrame, grey, COLOR_RGBA2GRAY);

        std::vector<Point2f> corners;

        goodFeaturesToTrack(grey, corners, 20, 0.01, 10, Mat(),3, false, 0.04);

        for (int i = 0; i < corners.size(); i++) {
            circle(cameraFrame, corners[i], 8, Scalar(0,255,0,0), 1);
            CornerInfo &cornerInfo1 = cornerInfo[i];
            cornerInfo1.x = corners[i].x;
            cornerInfo1.y = corners[i].y;
            
        }
    }
    CascadeClassifier face_cascade;
    void initFaceDetector(char* filePath){
        //char* filePath = (char*)"/storage/emulated/0/Android/data/com.DefaultCompany.UnityCV/files/haarcascade_frontalface_default.xml";
        face_cascade.load((char*)filePath);
    }



    void DetectFaces(unsigned char** rawImage, FaceInfo* faceInfo){
        cameraFrame.data = *rawImage;
        //cameraT.data = *rawImage;
        //grey.data = *rawImage;
        Mat grey;


        //resize(cameraT,cameraT,cameraT.size());
        cvtColor(cameraFrame, grey, COLOR_RGBA2GRAY);

        std::vector<Rect> faces;
        face_cascade.detectMultiScale(grey, faces);
        for (int i = 0; i < faces.size(); i++) {
            rectangle(cameraFrame, Point(faces[i].x, faces[i].y),Point(faces[i].x+faces[i].width, faces[i].y+faces[i].height),Scalar(0,255,0,255),1);
            FaceInfo &faceInfo1 = faceInfo[0];
            faceInfo1.x = faces[0].x;
            faceInfo1.y = faces[0].y;
        }



    }
}