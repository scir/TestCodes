package org.sss.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by khelender on 07-12-2016.
 */

public class SssPreferences {
    private static final String PREF_FILE_NAME = "preferences.cfg" ;
    SharedPreferences mPreferences = null ;
    boolean mStoreFullPicture = false;

    private static SssPreferences mSssPreferences = null ;

    private SssPreferences() {
    }

    public static synchronized SssPreferences getSssPreferences() {
        if( mSssPreferences == null ) {
            mSssPreferences = new SssPreferences() ;
        }
        return mSssPreferences ;
    }

    float mImageRatio = 0.1f ;

    int mImageHeight = 1 ;
    int mImageWidth = 1 ;

    final String fullPicture = "FullPicture";
    final String imageRatio = "ImageRatio";
    final String imageHeight = "ImageHeight";
    final String imageWidth = "ImageWidth";
    final String fileName = "preferences.cfg";

    public void initializePreferences(Context context) {
        // TODO : Ensure that initialization takes place only once
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mStoreFullPicture = mPreferences.getBoolean(fullPicture, false);
        mImageRatio = mPreferences.getFloat(imageRatio, 0.2f);
        mImageHeight = mPreferences.getInt(imageHeight, 768);
        mImageWidth = mPreferences.getInt(imageWidth, 1024);
//        (< 1/4 of VGA)
//        final int PHOTO_WIDTH = 400 ;
//        final int PHOTO_HEIGHT = 300 ;
    }

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
