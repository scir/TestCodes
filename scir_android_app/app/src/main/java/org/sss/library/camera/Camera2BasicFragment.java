package org.sss.library.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.scir.scir_android_app.Camera2Activity;
import org.scir.scir_android_app.R;
import org.sss.library.SssPreferences;
import org.sss.library.handler.RequestHandlerThread;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.support.v13.app.FragmentCompat;

/**
 * Taken from web and adapted by khelender on 13-12-2016.
 *
 * Code adopted from Camera2 implementation available over web @
 * http://www.programcreek.com/java-api-examples/index.php?
 * source_dir=android-Camera2Basic-master/Application/src/main/java/com/example/android/camera2basic/Camera2BasicFragment.java
 *
 */

public class Camera2BasicFragment extends Fragment implements View.OnClickListener,FragmentCompat.OnRequestPermissionsResultCallback{

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private static Context mContextCamera2BasicFragment = null ;

    MediaActionSound mMediaActionSound = new MediaActionSound() ;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "Camera2BasicFragment";

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            Log.i("SCIR_Camera2BasicFrag", "onSurfaceTextureAvailable()");
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            Log.i("SCIR_Camera2BasicFrag", "onSurfaceTextureChanged()");
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            Log.i("SCIR_Camera2BasicFrag", "onSurfaceTextureDestroyed()");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
            Log.i("SCIR_Camera2BasicFrag", "onSurfaceTextureUpdated()");
        }

    };

    /**
     * ID of the current {@link CameraDevice}.
     */
    private String mCameraId;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * The {@link android.util.Size} of camera preview.
     */
    private Size mPreviewSize;

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            Log.i("SCIR_Camera2BasicFrag", "CameraDevice.StateCallback onOpened()");
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            Log.i("SCIR_Camera2BasicFrag", "CameraDevice.StateCallback Disconnected()");
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            Log.i("SCIR_Camera2BasicFrag", "CameraDevice.StateCallback onError()");
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /**
     * This is the output file for our picture.
     */
    private File mFile;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            int height = -1, width = -1 ;
            Log.i("SCIR_Camera2BasicFrag", "ImageReader.OnImageAvailableListener onImageAvailable()");
//            updateImageOnView(reader); // To Update View....

            Image image = reader.acquireNextImage();
            height = image.getHeight() ; width = image.getWidth() ;
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();

            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            image.close();

            updateImageOnView(bytes);
            mBackgroundHandler.post(new ImageSaver(bytes, width, height));
        }


        public void updateImageOnView(final byte[] bytes) {
            Log.i("SCIR_Camera2BasicFrag", "ImageReader.OnImageAvailableListener updateImageOnView()");
            runCodeOnMainThread( new Runnable () {
                     @Override
                     public void run() {
                         final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                         mTextureImageView.setImageBitmap(bitmap);
                     }
                 }
            );
        }

    };

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    private CaptureRequest mPreviewRequest;

    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    private int mState = STATE_PREVIEW;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback process() ");
            switch (mState) {
                case STATE_PICTURE_TAKEN:
                    Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback process() state:STATE_PICTURE_TAKEN");
                    break;
                case STATE_PREVIEW: {
                    Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback process() state:STATE_PREVIEW");
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback process() state:STATE_WAITING_LOCK");
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null
                        || (CaptureResult.CONTROL_AF_STATE_PASSIVE_SCAN == afState)
                        || (CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED == afState)
                            ) {
                        Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback process() state: STATE_WAITING_LOCK, afState:NULL. Capture Still Picture()");
//                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback process() state: STATE_WAITING_LOCK, afState:Locked. Capture Still Picture()");
                            captureStillPicture();

                        } else {
                            Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback process() state: STATE_WAITING_LOCK, afState:LOcked aeState:DOubtful? Capture Still Picture()");
                            runPrecaptureSequence();
                        }
                    } else {
                        Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback process() state: STATE_WAITING_LOCK, afState:" + afState);
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback process() state:STATE_WAITING_PRECAPTURE");
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback process() state:STATE_NON_PRECAPTURE");
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback onCaptureProgressed()");
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback onCaptureCompleted()");
            process(result);
        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback onCaptureFailed()");
            super.onCaptureFailed(session, request, failure);
        }

        @Override
        public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId) {
            Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback onCaptureSequenceAborted()");
            super.onCaptureSequenceAborted(session, sequenceId);
        }

        @Override
        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
            Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback onCaptureSequenceCompleted()"
                    + " SequenceID("+ sequenceId + ")frameNumber(" + frameNumber + ")"
                );
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
            configureViewForImageDisplay();
        }

        @Override
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback onCaptureStarted() : "
                    + " Timestamp("+ timestamp + ")frameNumber(" + frameNumber + ")"
            );
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }
    };

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize   (Size[] choices, int width, int height, Size aspectRatio) {
        Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback onCompleted()");
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    private static RequestHandlerThread mRequestHandlerThread = null;
    private static Camera2Activity mCamera2Activity = null ;

    public static Camera2BasicFragment newInstance(Camera2Activity camera2Activity, RequestHandlerThread requestHandlerThread) {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment newInstance()");
        mContextCamera2BasicFragment = camera2Activity.getApplicationContext();
        mCamera2Activity = camera2Activity;
        mRequestHandlerThread = requestHandlerThread ;

        return new Camera2BasicFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment onCreateView()");
        return inflater.inflate(R.layout.fragment_camera2_basic, container, false);
    }

    private ImageView mTextureImageView = null ;
    private Button mButtonImageCaptureAndShow = null ;
    private final static int STATE_VIEW_IMAGE_CAPTURE = 201 ;
    private final static int STATE_VIEW_IMAGE_DISPLAY = 202 ;

    private int mStateCameraView = STATE_VIEW_IMAGE_CAPTURE;

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment onViewCreated()");
//        view.findViewById(R.id.buttonCamera2BasicFragment_info).setOnClickListener(this);
        mButtonImageCaptureAndShow = (Button)view.findViewById(R.id.buttonCapturePictureAndShow);
        mButtonImageCaptureAndShow.setOnClickListener(this);
        mTextureView = (AutoFitTextureView) view.findViewById(R.id.textureCameraView);
        mTextureImageView = (ImageView) view.findViewById(R.id.textureImageView);
        configureViewForImageCapture();
    }

    private void configureViewForImageCapture() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mButtonImageCaptureAndShow.setText("Capture Image for Reporting");
                mTextureView.setVisibility(View.VISIBLE);
                mTextureImageView.setVisibility(View.INVISIBLE);
                mStateCameraView = STATE_VIEW_IMAGE_CAPTURE ;
            }
        };
        runCodeOnMainThread(runnable);
    }

    protected void configureViewForImageDisplay() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mButtonImageCaptureAndShow.setText("Recapture image for Reporting");
                mTextureView.setVisibility(View.INVISIBLE);
                mTextureImageView.setVisibility(View.VISIBLE);
                mStateCameraView = STATE_VIEW_IMAGE_DISPLAY ;
            }
        };
        runCodeOnMainThread(runnable);
    }

    private void runCodeOnMainThread(Runnable runnable) {
        Context context = getActivity().getApplicationContext() ;
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(runnable);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        mFile = new File(getActivity().getExternalFilesDir(null), "pic.jpg");
    }

    @Override
    public void onResume() {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment onResume()");
        super.onResume();
        startBackgroundThread();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment onPause()");
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void requestCameraPermission() {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment reuqestCameraPermission()");
        if (FragmentCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            FragmentCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment onRequestPermissionResult()");
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.request_permission))
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private Size chooseSizeConfiguration(Size [] sizes) {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment choseSizeConfiguration()");
        SssPreferences sssPreferences = SssPreferences.getSssPreferences();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        int targetWidth = Integer.valueOf(sharedPreferences.getString("user_image_width_size","800"));

        float variance[] = new float [sizes.length], minVariance = 1.0f ;
        int minVarianceIndex = 0;

        for(int i = 0 ; i < sizes.length; i++ ) {
            variance[i] = ((float)(sizes[i].getWidth() - targetWidth) / (targetWidth));
            if( variance[i] < 0 ) {
                variance[i] = (-variance[i]);
            }
            if( variance[i] < minVariance ) {
                minVariance = variance[i] ;
                minVarianceIndex = i;
            }
        }
        sssPreferences.setImageHeight(sizes[minVarianceIndex].getHeight());
        sssPreferences.setImageWidth(sizes[minVarianceIndex].getWidth());

        Log.i("SCIR_Camera2BasicFrag", "Camera sizes set for SP: " + sizes[minVarianceIndex].getWidth() + "x" + sizes[minVarianceIndex].getHeight() +
                " for variance " + minVariance );

        return sizes[minVarianceIndex];
    }


    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private void setUpCameraOutputs(int width, int height) {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment setupCameraOutputs()");
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }
//                // For still image captures, we use the largest available size.
                /*
                 * Refine the Map to only keep required image levels !!!
                 *
                 */
                Size largest = chooseSizeConfiguration(map.getOutputSizes(ImageFormat.JPEG));

                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        width, height, largest);

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
        }
    }

    /**
     * Opens the camera specified by {@link Camera2BasicFragment#mCameraId}.
     */
    private void openCamera(int width, int height) {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment openCamera()");
        if(ActivityCompat.checkSelfPermission(mContextCamera2BasicFragment, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment closeCamera()");
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment startBackgroundThread()");
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment stopBackgroundThread()");
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment createCameraPreviewSession()");
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            showToast("Failed");
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment configureTransform()");
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    /**
     * Initiate a still image capture.
     */
    private void takePicture() {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment takePicture()");
        lockFocus();

    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment lockFocus()");
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment runPrecaptureSequence()");
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture() {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment captureStillPicture()");
        try {
            final Activity activity = getActivity();
            if (null == activity || null == mCameraDevice) {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            // Orientation
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback.capture() being Programmed");
            CameraCaptureSession.CaptureCallback CaptureCallback
                    = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback.onCaptureCompleted() posting");
                    showToast("Saved: " + mFile);
                    Log.d(TAG, mFile.toString());
                    unlockFocus();
//                    configureViewForImageDisplay();
                }

                @Override
                public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
                    Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback.onCaptureSequenceCompleted()");
                    super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
                }

                @Override
                public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                    Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback.onCaptureFailed()");
                    super.onCaptureFailed(session, request, failure);
                }

                @Override
                public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
                    Log.i("SCIR_Camera2BasicFrag", "CameraCaptureSession.CaptureCallback.onCaptureProgressed()");
                    super.onCaptureProgressed(session, request, partialResult);
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment unlockFocus()");
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment onClick() -- ? Texture");
        switch (view.getId()) {
            case R.id.buttonCapturePictureAndShow: {
                Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment onClick() -> Picture button was clicked (State:" + mStateCameraView);
                if (mStateCameraView == STATE_VIEW_IMAGE_CAPTURE) {
                    mMediaActionSound.play(MediaActionSound.SHUTTER_CLICK);
                    takePicture();
                } else {
                    configureViewForImageCapture();
                }
                break;
            }
            /*
            case R.id.buttonCamera2BasicFragment_info: {
                Activity activity = getActivity();
                if (null != activity) {
                    new AlertDialog.Builder(activity)
                            .setMessage(R.string.intro_message)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
                break;
            }
                */
        }
    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private static class ImageSaver implements Runnable {

        /**
         * The file we save the image into.
         */
        private Camera2BasicFragment sCamera2BasicFragment ;
        private byte [] mBytes ;
        private int mHeight = -1 , mWidth = -1 ;

        public ImageSaver(byte [] bytes, int width, int height) {
            mBytes = bytes ;
            mHeight = height;
            mWidth = width;
        }

        @Override
        public void run() {
            Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment ImageSaver run()");
            try {
                mCamera2Activity.setCameraData(mBytes, mWidth, mHeight);
                Log.i("SCIR_Camera2BasicFrag", "DONE: Camera2BasicFragment ImageSaver Content was posted to main Camera2Activity!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment compare()");
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment ErrorDialog newInstance()");
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment ErrorDialog onCreateDialog()");
            final Activity activity;
            activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Log.i("SCIR_Camera2BasicFrag", "Camera2BasicFragment ConfirmationDialog onCreateDialog()");
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentCompat.requestPermissions(parent,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }

}
