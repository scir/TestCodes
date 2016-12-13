package org.sss.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

/**
 * Created by khelender on 07-12-2016.
 */

public class SssPreferences {
    private static final String PREF_FILE_NAME = "sp_preferences.cfg" ;
    SharedPreferences mPreferences = null ;
    boolean mStoreFullPicture = false;

    private static SssPreferences mSssPreferences = null ;

    private SssPreferences() {
    }

    public static synchronized SssPreferences getSssPreferences(Context context) {
        if( mSssPreferences == null ) {
            mSssPreferences = new SssPreferences() ;
            mSssPreferences.initializePreferences(context);
        }
        return mSssPreferences ;
    }

    public static SssPreferences getSssPreferences() {
        return mSssPreferences ;
    }


    float mImageRatio = 0.1f ;

    private int mImageHeight = 1 ;
    private int mImageWidth = 1 ;

    final String fullPicture = "SP_FullPicture";
    final String imageRatio = "SP_ImageRatio";
    final String imageHeight = "SP_ImageHeight";
    final String imageWidth = "SP_ImageWidth";
    final String fileName = "sp_preferences.cfg";

    public void initializePreferences(Context context) {

        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        // TODO : Ensure that initialization takes place only once
        mImageRatio = mPreferences.getFloat(imageRatio, 0.2f);
        mStoreFullPicture = mPreferences.getBoolean(fullPicture, true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        setImageWidth(Integer.valueOf(sharedPreferences.getString("user_image_width_size","1024")));

        String imageHeight = sharedPreferences.getString("user_image_height_size","768");
        setImageHeight(Integer.valueOf(imageHeight));
    }

//    /*
//     * Used to update Image Width based on various sizes supported by Device,
//     * captured in List<Camera.Size>
//     */
//    public void updateImageWidth(List<Camera.Size> sizesList, Context context) {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        int targetWidth = Integer.valueOf(sharedPreferences.getString("user_image_width_size","1024"));
//
//        float variance[] = new float [sizesList.size()], minVariance = 1.0f ;
//        int minVarianceIndex = 0;
//
//        for(int i = 0 ; i < sizesList.size(); i++ ) {
//            variance[i] = ((float)(sizesList.get(i).width - targetWidth)) / (targetWidth);
//            if( variance[i] < 0 ) {
//                variance[i] = (-variance[i]);
//            }
//            if( variance[i] < minVariance ) {
//                minVariance = variance[i] ;
//                minVarianceIndex = i;
//            }
//        }
//        setImageHeight(sizesList.get(minVarianceIndex).height);
//        setImageWidth(sizesList.get(minVarianceIndex).width);
//        Log.i("PreferenceActivity", "Width size Selected: " + sizesList.get(minVarianceIndex).width + "x" + sizes.get(minVarianceIndex).height +
//                " for variance " + minVariance );
//    }

    public void savePreferences() {
        SharedPreferences.Editor editor = mPreferences.edit() ;
        editor.putFloat(imageRatio,mImageRatio);
        editor.putInt(imageHeight, mImageHeight);
        editor.putInt(imageWidth,mImageWidth);
        editor.putBoolean(fullPicture,mStoreFullPicture);
        editor.putString(fileName,PREF_FILE_NAME);
        editor.commit();
    }
    public boolean isStoreFullPicture() {
        return mStoreFullPicture;
    }

    public void setFlagStoreFullPicture(boolean flagStoreFullPicture) {
        mStoreFullPicture = flagStoreFullPicture ;
    }

    public float getImageRatio() {
        return mImageRatio;
    }

    public int getImageHeight() {
        return mImageHeight;
    }

    public int getImageWidth() { return mImageWidth;}

    public void setImageHeight(int imageHeight) {
        this.mImageHeight = imageHeight;
    }

    public void setImageWidth(int imageWidth) {
        this.mImageWidth = imageWidth;
    }

}
