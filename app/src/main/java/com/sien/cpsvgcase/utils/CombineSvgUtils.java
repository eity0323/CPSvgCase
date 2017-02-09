package com.sien.cpsvgcase.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sien
 * @date 2017/2/8
 * @descript 合成svg
 */
public class CombineSvgUtils {
    private Context mcontext;

    public CombineSvgUtils(Context context){
        mcontext = context;
    }

    /**
     * 显示单张svg (InputStream格式)
     *
     * @param inputStream
     * @return
     * @throws SVGParseException
     */
    public Bitmap displayByInputStream(InputStream inputStream) throws SVGParseException{
        Bitmap newBitmap = null;

        if (inputStream == null){
            return newBitmap;
        }

        List<InputStream> inputStreamList = new ArrayList<>();
        inputStreamList.add(inputStream);

        newBitmap = combineMultiByInputStream(inputStreamList);

        return newBitmap;
    }

    /**
     * 显示多张svg (InputStream格式)
     * @param inputStreamList
     * @return
     * @throws SVGParseException
     */
    public Bitmap combineMultiByInputStream(List<InputStream> inputStreamList) throws SVGParseException{
        Canvas canvas = null;
        Bitmap newBitmap = null;
        Paint paint = null;
        int wholeWidth;
        int wholeHeight;

        if (inputStreamList == null || inputStreamList.size() <= 0){
            return newBitmap;
        }

        {//初始图片显示区域
            wholeWidth = CPScreenUtil.getScreenWidth(mcontext);
            wholeHeight = CPScreenUtil.getScreenHeight(mcontext);

            paint = new Paint();
            paint.setAlpha(0);//背景透明

            newBitmap = Bitmap.createBitmap(wholeWidth, wholeHeight, Bitmap.Config.ARGB_8888);

            canvas = new Canvas(newBitmap);
            //绘制区域
            canvas.drawRect(0, 0, wholeWidth, wholeHeight, paint);
        }

        SVG mSvg;
        for ( int i = 0,j = inputStreamList.size();i<j;i++){
            mSvg = SVG.getFromInputStream(inputStreamList.get(i));

            //做位移、拉伸处理
            RectF viewBox = mSvg.getDocumentViewBox();

            float scale = Math.min(wholeWidth / (viewBox.width()), wholeHeight / (viewBox.height()));
            canvas.translate((wholeWidth - viewBox.width() * scale) / 2.0f, (i*2 + 1) * (wholeHeight - viewBox.height() * scale) / 4.0f);

            if (i == 0) {
                canvas.scale(scale, scale);
            }

            //绘制svg路径
            mSvg.renderToCanvas(canvas);

            canvas.save(Canvas.ALL_SAVE_FLAG);
        }

        // 存储新合成的图片
        canvas.restore();

        return newBitmap;
    }

    /**
     * 显示单张svg (resId)
     * @param resId
     * @return
     * @throws SVGParseException
     */
    public Bitmap displayByResource(int resId) throws SVGParseException {
        Bitmap newBitmap = null;

        if (resId > 0){
            return newBitmap;
        }

        List<Integer> inputStreamList = new ArrayList<>();
        inputStreamList.add(resId);

        newBitmap = combineMultiByResource(inputStreamList);

        return newBitmap;
    }

    /**
     * 显示多张svg (resId格式)
     * @param resIdList
     * @return
     * @throws SVGParseException
     */
    public Bitmap combineMultiByResource(List<Integer> resIdList) throws SVGParseException {
        Canvas canvas = null;
        Bitmap newBitmap = null;
        Paint paint = null;
        int wholeWidth;
        int wholeHeight;

        if (resIdList == null || resIdList.size() <= 0){
            return newBitmap;
        }

        {//初始图片显示区域
            wholeWidth = CPScreenUtil.getScreenWidth(mcontext);
            wholeHeight = CPScreenUtil.getScreenHeight(mcontext);

            paint = new Paint();
            paint.setAlpha(0);//背景透明

            newBitmap = Bitmap.createBitmap(wholeWidth, wholeHeight, Bitmap.Config.ARGB_8888);

            canvas = new Canvas(newBitmap);
            //绘制区域
            canvas.drawRect(0, 0, wholeWidth, wholeHeight, paint);
        }


        //读取svg文件资源
        SVG mSvg;
        for(int i = 0,j = resIdList.size();i<j;i++){
            mSvg = SVG.getFromResource(mcontext, resIdList.get(i));

            //做位移、拉伸处理
            RectF viewBox = mSvg.getDocumentViewBox();
            float scale = Math.min(wholeWidth / (viewBox.width()), wholeHeight / (viewBox.height()));
            canvas.translate((wholeWidth - viewBox.width() * scale) / 2.0f, (i*2 + 1) * (wholeHeight - viewBox.height() * scale) / 4.0f);

            if (i == 0) {
                canvas.scale(scale, scale);
            }

            //绘制svg路径
            mSvg.renderToCanvas(canvas);

            canvas.save(Canvas.ALL_SAVE_FLAG);
        }

        return newBitmap;
    }
}
