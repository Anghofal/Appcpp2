// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("appcpp2");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("appcpp2")
//      }
//    }

#include "jni.h"
#include "opencv2/core.hpp"
#include "opencv2/imgproc.hpp"
#include "opencv2/features2d.hpp"
#include "opencv2/objdetect.hpp"
#include "vector"

#include "string"

using namespace cv;

extern "C" {
    JNIEXPORT void JNICALL Java_com_example_appcpp2_MainActivity_FindFeatures(JNIEnv* jniEnv, jobject, jlong addrGray, jlong addrRGBA)
    {
        Mat* mGray = (Mat*) addrGray;
        Mat* mRGBA = (Mat*) addrRGBA;

        std::vector<Point2f> corners;
        goodFeaturesToTrack(*mGray, corners, 20, 0.01, 10,Mat(), 3, false, 0.04);

        for (int i = 0; i < corners.size(); i++) {
            circle(*mRGBA, corners[i], 10, Scalar(0,255,0), 2);
        }
    }

    CascadeClassifier face_cascade;

    JNIEXPORT void JNICALL Java_com_example_appcpp2_MainActivity_initFaceDetector(JNIEnv* jniEnv, jobject, jstring jFilePath)
        {
            const char * jnamestr = jniEnv -> GetStringUTFChars(jFilePath, NULL);
            std::string filePath = (jnamestr);
            face_cascade.load(filePath);
        }

    JNIEXPORT void JNICALL Java_com_example_appcpp2_MainActivity_DetectFace(JNIEnv* jniEnv, jobject, jlong addrGray, jlong addrRGBA)
    {
        Mat* mGray = (Mat*) addrGray;
        Mat* mRGBA = (Mat*) addrRGBA;
        std::vector<Rect> faces;


        face_cascade.detectMultiScale(*mGray, faces);

        for (int i = 0; i < faces.size(); i++) {
            rectangle(*mRGBA, Point(faces[i].x, faces[i].y), Point(faces[i].x+faces[i].width, faces[i].y+faces[i].height), Scalar (0,0,255),2);
        }

    }
}