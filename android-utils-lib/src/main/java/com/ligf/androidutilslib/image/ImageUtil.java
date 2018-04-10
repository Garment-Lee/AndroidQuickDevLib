package com.ligf.androidutilslib.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 图片相关工具类
 * Created by ligf on 2017/9/15.
 */
public class ImageUtil {

    public static final int DEFAULT_COMPRESS_QUALITY = 100;

    /**
     * 从文件系统中加载图片到内存中
     *
     * @param imagePath
     * @return
     */
    public static Bitmap getBitmapFromFile(String imagePath) {
        Bitmap bitmap = null;
        File imageFile = new File(imagePath);
        if (imageFile != null && imageFile.exists()) {
            bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        }
        return bitmap;
    }

    /**
     * 保存Bitmap到文件系统中
     *
     * @param bitmap
     * @param imagePath
     */
    public static void saveBitmapToFile(Bitmap bitmap, String imagePath) {
        File imageFile = new File(imagePath);
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(imageFile));
            bitmap.compress(Bitmap.CompressFormat.PNG, DEFAULT_COMPRESS_QUALITY, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                    ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Bitmap转换为字节数组
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, DEFAULT_COMPRESS_QUALITY, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 字节数组转换为图片
     *
     * @param bytes
     * @return
     */
    public static Bitmap bytesToBitmap(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    /**
     * Drawable转换为Bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap转换为Drawable
     * @param context
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap){
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        return drawable;
    }

    /**
     * 获取图片的类型<p>
     * options.outMimeType获取图片的格式，"image/png"、"image/jpeg"、"image/gif"
     * @param bitmapPath 图片的路径
     * @return
     */
    public static String getBitmapType(String bitmapPath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        //图片不加载到内存中
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bitmapPath, options);
        String bitmapType = options.outMimeType;
        if (TextUtils.isEmpty(bitmapType)) {
            bitmapType = "未能识别的图片";
        } else {
            bitmapType = bitmapType.substring(6, bitmapType.length());
        }
        return bitmapType;
    }

    /**
     *图片的缩放处理，通过使用BitmapFactory的Options设置缩放参数<p>
     * 该方法只需把图片加载到内存中一次
     * @param path 缩放原图片的路径
     * @param targetW 缩放后目标图片的宽
     * @param targetH 缩放后目标图片的高
     * @return
     */
    public static Bitmap resizeImageByBitmapFractoryOptions(String path, int targetW, int targetH){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inSampleSize = 1;
        if (targetW > 0 && targetH > 0 && outWidth > 0 && outHeight > 0){
            options.inSampleSize = Math.min(outWidth / targetW, outHeight / targetH);
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 图片的缩放处理，通过使用Matrix进行图片处理<p>
     * 该方法会重新创建一张新的图片
     * @param bitmap 缩放原图片
     * @param targetW 缩放后目标图片的宽
     * @param targetH 缩放后目标图片的高
     * @return
     */
    public static Bitmap resizeImageByMatrix(Bitmap bitmap, int targetW, int targetH){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleX = ((float) targetW) / width;
        float scaleY = ((float)targetH) / height;
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        Bitmap scaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return  scaleBitmap;
    }

}
