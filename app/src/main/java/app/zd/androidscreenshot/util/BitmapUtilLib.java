package app.zd.androidscreenshot.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import app.zd.androidscreenshot.R;

/**
 * bitmap 工具类
 * Created by zhangdong on 2017/6/27.
 */
public class BitmapUtilLib {
    public static final int DEFAULT_IMAGE_LIMIT = 32;
    private static final String LOG_TAG = BitmapUtilLib.class.getSimpleName();
    private static final int DEFAULT_MEGA_SIZE = 1024;
    private static final int STEP = 10;

    /**
     * Don't let anyone instantiate this class
     */
    private BitmapUtilLib() {
    }

    /**
     * Get bitmap from specified image path
     *
     * @param imgPath 图片存储路径
     * @return bitmap
     */
    public static Bitmap getBitmap(String imgPath) {
        // Get bitmap through image path
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        // Do not compress
        newOpts.inSampleSize = 1;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        try {
            return BitmapFactory.decodeFile(imgPath, newOpts);
        } catch (RuntimeException re) {
            Log.e(LOG_TAG, "file decode RuntimeException");
        }
        return null;
    }

    /**
     * 质量压缩方法
     *
     * @param image 原图
     * @return 压缩后
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos); //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); //重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos); //这里压缩options%，把压缩后的数据存放到baos中
            options -= 10; //每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray()); //把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null); //把ByteArrayInputStream数据生成图片
    }

    /**
     * 图片按比例大小压缩方法
     *
     * @param image （根据Bitmap图片压缩）
     * @return 压缩后
     */
    public static Bitmap compressScale(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos); // 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm;
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap;
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        Log.i(LOG_TAG, w + "---------------" + h);
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        // float hh = 800f;// 这里设置高度为800f
        // float ww = 480f;// 这里设置宽度为480f
        float hh = 512f;
        float ww = 512f;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1; // be=1表示不缩放
        if (w > h && w > ww) { // 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be; // 设置缩放比例
        // newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

        return compressImage(bitmap); // 压缩好比例大小后再进行质量压缩
    }

    /**
     * 压缩图片小于size kb
     *
     * @param bitmap 传入的位图对象
     * @param size   对象大小阀值
     */
    public static Bitmap compressBitmapBySize(Bitmap bitmap, float size) {
        double originSize = getBitmapSize(bitmap); //原图大小
        if (size > originSize) {
            return bitmap;
        }
        float zoom = (float) Math.sqrt(size / originSize); //首次缩放的比例,因为是边的缩放所以要开方
        return zoomImage(bitmap, zoom); //图像开始进行压缩
    }

    /**
     * 图片的缩放方法
     *
     * @param bgimage ：源图片资源
     * @param zoom    ：缩放比例
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, float zoom) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        matrix.postScale(zoom, zoom);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        //首次进行宽高缩放,得到第一次压缩的bitmap
        double compress = getBitmapSize(bitmap);
        double lastCompress = compress;
        //由于图像第一次压缩过后会有不准的情况，所以进行循环判断每次压缩90%
        //因为第一次的压缩是有保证的，所以循环一般不会超过2次
        while (compress > DEFAULT_IMAGE_LIMIT) {
            matrix.setScale(0.9f, 0.9f);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            lastCompress = compress;
            compress = getBitmapSize(bitmap);
            if (compress == 0 || lastCompress == compress) {
                return bitmap;
            }
        }
        return bitmap;
    }

    /**
     * 获取传入图片的字节大小
     *
     * @param bitmap:传入的位图对象
     */
    public static int getBitmapSize(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitmap == null) {
            return 0;
        }
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //将bitmap写入流
        } catch (Exception e) {
            return 0;
        }
        byte[] b = baos.toByteArray(); //将字节换成KB
        return b.length / DEFAULT_MEGA_SIZE;
    }

    /**
     * 根据尺寸压缩图片
     *
     * @param filePath    源路径
     * @param outFilePath 目标路径
     * @param size        定义尺寸
     * @return
     */
    public static boolean compressBitmapBySize(String filePath, String outFilePath, long size) {
        boolean result = false;
        int height;
        int width;
        if (StringUtil.isNullOrEmpty(filePath) || StringUtil.isNullOrEmpty(outFilePath)) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        if (options.outHeight > options.outWidth) {
            height = 1024;
            width = options.outWidth;
        } else {
            width = 1024;
            height = options.outHeight;
        }
        options.inSampleSize = calculateInSampleSize(options, width, height);

        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        bm = rotateBitmapByDegree(bm, getBitmapDegree(filePath));

        if (bm == null) {
            return false;
        }
        int quality = 100;

        ByteArrayOutputStream baos = null;
        FileOutputStream fos = null;

        baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > size) {
            if (quality <= 0) {
                break;
            }
            baos.reset();
            quality -= STEP;
            bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }
        try {
            fos = new FileOutputStream(outFilePath);
            fos.write(baos.toByteArray());
            fos.flush();
            result = true;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            File file = new File(outFilePath);
            if (file.exists()) {
                file.delete();
            }
            result = false;
        } finally {
            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {

            }
        }
        return result;
    }

    /**
     * 计算宽高的缩放比例
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round((float) height / (float) reqHeight);
            int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static int getBitmapDegree(String path) {
        int degree = 0;
        if (StringUtil.isNullOrEmpty(path)) {
            return degree;
        }
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "get degree error");
        }
        return degree;
    }

    /**
     * 旋转图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        if (bm == null || degree == 0) {
            return bm;
        }
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            Log.i(LOG_TAG, "oom error");
        }
        return bitmap;
    }

    /**
     * 按照尺寸缩放图片
     *
     * @param bm        源
     * @param newWidth  宽
     * @param newHeight 高
     * @return 缩放后的图片
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    /**
     * 按照图片尺寸压缩
     *
     * @param srcPath 源路径
     * @param desPath 目标路径
     */
    public static void compressPicture(String srcPath, String desPath) {
        FileOutputStream fos = null;
        BitmapFactory.Options op = new BitmapFactory.Options();

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        op.inJustDecodeBounds = true;
        Bitmap bitmap;
        op.inJustDecodeBounds = false;

        // 缩放图片的尺寸
        float w = op.outWidth;
        float h = op.outHeight;
        float hh = 1024f;
        float ww = 1024f;
        // 最长宽度或高度1024
        float be = 1.0f;
        if (w > h && w > ww) {
            be = (float) (w / ww);
        } else if (w < h && h > hh) {
            be = (float) (h / hh);
        }
        if (be <= 0) {
            be = 1.0f;
        }
        op.inSampleSize = (int) be; //设置缩放比例,这个数字越大,图片大小越小.
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, op);
        int desWidth = (int) (w / be);
        int desHeight = (int) (h / be);
        bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);
        try {
            fos = new FileOutputStream(desPath);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "FileNotFoundException");
        }
    }

    /**
     * bitmap 转 array
     *
     * @param bmp         bitmap
     * @param needRecycle 是否需要回收
     * @return array
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "bmpToByteArray Exception");
        }
        return result;
    }

    /**
     * 图片转成string
     *
     * @param bitmap bitmap
     * @return string
     */
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray(); // 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    /**
     * string转成bitmap
     *
     * @param string string
     */
    public static Bitmap convertStringToIcon(String string) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 拼接预制图片和屏幕截图
     *
     * @param context     上下文
     * @param src         源
     * @param titleHeight 标题高度
     * @return 拼接后的图片
     */
    public static Bitmap makeBitmapForShare(Context context, Bitmap src, int titleHeight) {
        if (src == null) {
            return null;
        }
        try {
            int w = src.getWidth();
            int h = src.getHeight();
            Bitmap watermark = BitmapFactory.decodeResource(context.getResources(), R.drawable.top_screen_shot);
            int ww = watermark.getWidth();
            int wh = watermark.getHeight();
            int barHeight = ScreenUtil.getStatusBarHeight(context);
            // create the new blank bitmap
            Bitmap newb = Bitmap.createBitmap(w, h + wh - barHeight - titleHeight, Bitmap.Config.ARGB_8888); // 创建一个新的和SRC长度宽度一样的位图
            Canvas cv = new Canvas(newb);
            cv.drawColor(Color.WHITE);
            Paint p = new Paint();
            // draw src into
            cv.drawBitmap(src, 0, wh - barHeight - titleHeight, p); // 在 0，barHeight坐标开始画入src
            // draw watermark into
            cv.drawBitmap(zoomImg(watermark, w, wh), 0, 0, p); // 在src画入水印
            // save all clip
            cv.save(Canvas.ALL_SAVE_FLAG); // 保存
            // store
            cv.restore(); // 存储
            watermark.recycle();
            return newb;
        } catch (OutOfMemoryError err) {
            Log.e(LOG_TAG, err.toString());
        } finally {
            System.gc();
        }
        return null;
    }

}
