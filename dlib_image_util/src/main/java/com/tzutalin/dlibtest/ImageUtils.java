package com.tzutalin.dlibtest;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Keep;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public class ImageUtils  {

    private void drawResizedBitmap(final Bitmap src, final Bitmap dst, final int orientation) {

        int mScreenRotation;

        if (orientation == 0 ){
            mScreenRotation = -90;
        } else {
            mScreenRotation = 0;
        }

        final Matrix matrix = new Matrix();

        matrix.preTranslate(-(dst.getWidth())/2f,-(dst.getHeight())/2f);
        matrix.setRotate(mScreenRotation,(dst.getWidth())/2f,(dst.getHeight())/2f);

        final Canvas canvas = new Canvas(dst);
        canvas.drawBitmap(src, matrix, null);
    }

    public Bitmap imageSideInversion(Bitmap src){
        Matrix sideInversion = new Matrix();
        sideInversion.setScale(-1, 1,(src.getWidth())/2f,(src.getHeight())/2f);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), sideInversion, false);
    }

    public Bitmap imageSideInversion2(Bitmap src){
        Matrix sideInversion = new Matrix();
        sideInversion.setScale(-1, -1,(src.getWidth())/2f,(src.getHeight())/2f);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), sideInversion, false);
    }

    public Bitmap get888BitMap(Image image, int orientaion, boolean cameraSelector){
        int mPreviewWdith;
        int mPreviewHeight;
        Log.d(TAG,"width: " + image.getWidth() + "\nheight: "+ image.getHeight());
        Log.d(TAG,"orientation degree: " + orientaion);
        int[] mRGBBytes;
        final Image.Plane[] planes = image.getPlanes();
        Bitmap mRGBframeBitmap;
        byte[][] mYUVBytes;
        mPreviewWdith = image.getWidth();
        mPreviewHeight = image.getHeight();
        mRGBBytes = new int[mPreviewWdith * mPreviewHeight];
        mRGBframeBitmap = Bitmap.createBitmap(mPreviewWdith, mPreviewHeight, Bitmap.Config.ARGB_8888);
        Bitmap mCroppedBitmap = Bitmap.createBitmap(mPreviewWdith, mPreviewHeight , Bitmap.Config.ARGB_8888);
        mYUVBytes = new byte[planes.length][];
        for (int i = 0; i < planes.length; ++i) {
            mYUVBytes[i] = new byte[planes[i].getBuffer().capacity()];
        }
        for (int i = 0; i < planes.length; ++i) {
            planes[i].getBuffer().get(mYUVBytes[i]);
        }

        final int yRowStride = planes[0].getRowStride();
        final int uvRowStride = planes[1].getRowStride();
        final int uvPixelStride = planes[1].getPixelStride();
        ImageUtils.convertYUV420ToARGB8888(
                mYUVBytes[0],
                mYUVBytes[1],
                mYUVBytes[2],
                mRGBBytes,
                mPreviewWdith,
                mPreviewHeight,
                yRowStride,
                uvRowStride,
                uvPixelStride,
                false);

        mRGBframeBitmap.setPixels(mRGBBytes, 0, mPreviewWdith, 0, 0, mPreviewWdith, mPreviewHeight);
        drawResizedBitmap(mRGBframeBitmap,mCroppedBitmap, orientaion);
        Bitmap mResizedBitmap = Bitmap.createScaledBitmap(mCroppedBitmap, (int) (mPreviewWdith), (int) (mPreviewHeight), true);
        if (!cameraSelector){
            return imageSideInversion(mResizedBitmap);
        }else {
            return imageSideInversion2(mResizedBitmap);
        }
    }


    public Bitmap yuv420ToBitmap2(Image image) {
        int width = image.getWidth();
        int height = image.getHeight();
        // Pass this array to native code to write to.
        int[] argbResult = new int[width * height];
        Image.Plane yPlane = image.getPlanes()[0];
        Image.Plane uPlane = image.getPlanes()[1];
        Image.Plane vPlane = image.getPlanes()[2];
        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        // call the JNI method
        yuv420ToBitmapNative(
                width,
                height,
                yBuffer,
                yPlane.getPixelStride(),
                yPlane.getRowStride(),
                uBuffer,
                uPlane.getPixelStride(),
                uPlane.getRowStride(),
                vBuffer,
                vPlane.getPixelStride(),
                vPlane.getRowStride(),
                argbResult);
        return Bitmap.createBitmap(argbResult, width, height, Bitmap.Config.ARGB_8888);
    }

    // native interface
    static native void yuv420ToBitmapNative(
            int width,
            int height,
            ByteBuffer yBuffer,
            int yPixelStride,
            int yRowStride,
            ByteBuffer uBuffer,
            int uPixelStride,
            int uRowStride,
            ByteBuffer vBuffer,
            int vPixelStride,
            int vRowStride,
            int[] argbResult);

    private static final String TAG = ImageUtils.class.getSimpleName();

    /**
     * Utility method to compute the allocated size in bytes of a YUV420SP image
     * of the given dimensions.
     */
    public static int getYUVByteSize(final int width, final int height) {
        // The luminance plane requires 1 byte per pixel.
        final int ySize = width * height;

        // The UV plane works on 2x2 blocks, so dimensions with odd size must be rounded up.
        // Each 2x2 block takes 2 bytes to encode, one each for U and V.
        final int uvSize = ((width + 1) / 2) * ((height + 1) / 2) * 2;

        return ySize + uvSize;
    }

    /**
     * Saves a Bitmap object to disk for analysis.
     *
     * @param bitmap The bitmap to save.
     */
    @SuppressLint("TimberArgCount")
    public static void saveBitmap(final Bitmap bitmap) {
        final String root =
                Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dlib";
        Log.d(TAG,String.format("Saving %dx%d bitmap to %s.", bitmap.getWidth(), bitmap.getHeight(), root));
        final File myDir = new File(root);
        if (!myDir.mkdirs()) {
            Log.e(TAG,"Make dir failed");
        }

        final String fname = "preview.png";
        final File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }
        try {
            final FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 99, out);
            out.flush();
            out.close();
        } catch (final Exception e) {
            Log.e(TAG,"Exception! " + e);
        }
    }

    /**
     * Converts YUV420 semi-planar data to ARGB 8888 data using the supplied width
     * and height. The input and output must already be allocated and non-null.
     * For efficiency, no error checking is performed.
     *
     * @param input    The array of YUV 4:2:0 input data.
     * @param output   A pre-allocated array for the ARGB 8:8:8:8 output data.
     * @param width    The width of the input image.
     * @param height   The height of the input image.
     * @param halfSize If true, downsample to 50% in each dimension, otherwise not.
     */
    public static native void convertYUV420SPToARGB8888(
            byte[] input, int[] output, int width, int height, boolean halfSize);

    /**
     * Converts YUV420 semi-planar data to ARGB 8888 data using the supplied width
     * and height. The input and output must already be allocated and non-null.
     * For efficiency, no error checking is performed.
     *
     * @param y
     * @param u
     * @param v
     * @param uvPixelStride
     * @param width         The width of the input image.
     * @param height        The height of the input image.
     * @param halfSize      If true, downsample to 50% in each dimension, otherwise not.
     * @param output        A pre-allocated array for the ARGB 8:8:8:8 output data.
     */
    @Keep
    public static native void convertYUV420ToARGB8888(
            byte[] y,
            byte[] u,
            byte[] v,
            int[] output,
            int width,
            int height,
            int yRowStride,
            int uvRowStride,
            int uvPixelStride,
            boolean halfSize);

    /**
     * Converts YUV420 semi-planar data to RGB 565 data using the supplied width
     * and height. The input and output must already be allocated and non-null.
     * For efficiency, no error checking is performed.
     *
     * @param input  The array of YUV 4:2:0 input data.
     * @param output A pre-allocated array for the RGB 5:6:5 output data.
     * @param width  The width of the input image.
     * @param height The height of the input image.
     */
    @Keep
    public static native void convertYUV420SPToRGB565(
            byte[] input, byte[] output, int width, int height);

    /**
     * Converts 32-bit ARGB8888 image data to YUV420SP data.  This is useful, for
     * instance, in creating data to feed the classes that rely on raw camera
     * preview frames.
     *
     * @param input  An array of input pixels in ARGB8888 format.
     * @param output A pre-allocated array for the YUV420SP output data.
     * @param width  The width of the input image.
     * @param height The height of the input image.
     */
    @Keep
    public static native void convertARGB8888ToYUV420SP(
            int[] input, byte[] output, int width, int height);

    /**
     * Converts 16-bit RGB565 image data to YUV420SP data.  This is useful, for
     * instance, in creating data to feed the classes that rely on raw camera
     * preview frames.
     *
     * @param input  An array of input pixels in RGB565 format.
     * @param output A pre-allocated array for the YUV420SP output data.
     * @param width  The width of the input image.
     * @param height The height of the input image.
     */
    @Keep
    public static native void convertRGB565ToYUV420SP(
            byte[] input, byte[] output, int width, int height);
}
