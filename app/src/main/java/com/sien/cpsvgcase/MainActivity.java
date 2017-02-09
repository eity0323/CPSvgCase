package com.sien.cpsvgcase;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.caverock.androidsvg.SVGParseException;
import com.guardanis.imageloader.ImageLoader;
import com.guardanis.imageloader.ImageRequest;
import com.sien.cpsvgcase.network.BaseOkHttpFileCallBack;
import com.sien.cpsvgcase.utils.CombineSvgUtils;
import com.sien.cpsvgcase.utils.DownloadUtils;
import com.sien.cpsvgcase.widgets.DynamicSvgPathView;
import com.sien.cpsvgcase.widgets.DynamicSvgView;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private String url = "http://dingzhaobing.oss-cn-shanghai.aliyuncs.com/grooming.svg";//https://pixabay.com/get/e83db10e2afc063ecd1f4101e14c4696e76ae3d111b7144291f1c97f/grooming-1801287.svg";
    private int currentSize = 0;
    private List<InputStream> inputStreamList;
    private BaseOkHttpFileCallBack fileCallBack;
    private Context mcontext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mcontext = this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                retrofitImageMultiDownload();
            }
        },500);
    }

    private void loadSvgSource(){
        ImageView iv = (ImageView) findViewById(R.id.svgIV);
        ImageRequest.create(iv)
//                .setTargetAsset("grooming.svg") // ok
//                .setTargetResource(R.raw.web_landscape, ImageUtils.ImageType.SVG) // ok
                .setTargetUrl(url)//ok
//                .setTargetFile(new File("/sdcard/Download/grooming.svg")) //ok
                .execute();
    }

    /*绘制动态svg*/
    private void dynamicDrawSvgPath(){
        final DynamicSvgPathView pathView = (DynamicSvgPathView) findViewById(R.id.pathView);
        pathView.setVisibility(View.VISIBLE);
        pathView.setFillAfter(true);
        pathView.useNaturalColors();
        pathView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathView.getPathAnimator().
                        delay(100).
                        duration(10000).
                        interpolator(new AccelerateDecelerateInterpolator()).
                        start();
            }
        });
    }

    /*绘制静态svg*/
    private void staticDrawSvg(){
        final DynamicSvgView pathView = (DynamicSvgView) findViewById(R.id.pathView);
        pathView.setVisibility(View.VISIBLE);
        pathView.setFillAfter(true);
        pathView.useNaturalColors();
//        pathView.setSvgResource(R.raw.red_shoe);
        pathView.setup();
    }

    /*利用ImageLoader加载图片*/
    private void customImageDownload(){
        final ImageRequest imageRequest = new ImageRequest(this);
        Thread tempThread = new Thread(new Runnable() {
            @Override
            public void run() {
                imageRequest.setTargetUrl(url);
                File fileSvg = ImageLoader.getInstance(mcontext).download(imageRequest);
                if (fileSvg.exists()){
                    Log.e("customImageDownload",fileSvg.getAbsolutePath().toString());
                    String filePath = "file://" + fileSvg.getAbsolutePath().toString();

                    ImageView iv = (ImageView)findViewById(R.id.svgIV);
                    iv.setVisibility(View.VISIBLE);
                    ImageRequest.create(iv).setTargetFile(new File("/sdcard/Download/grooming.svg")).execute();
                }else {
                    Log.e("customImageDownload","download error");
                }
            }
        });
        tempThread.start();

    }

    /**
     * 加载多张
     */
    private void retrofitImageMultiDownload(){
        currentSize = 0;
        inputStreamList = new ArrayList<>();

        String rootPath = _getAppCacheRootFilePath(this) + "/download/";
        String fileNameWithPatch = getPackageName().replaceAll("\\.","_");

        fileCallBack = new BaseOkHttpFileCallBack(rootPath, fileNameWithPatch) {
            @Override
            public void onProgress(float progress, long total) {
            }

            @Override
            public void onFail(Call call, Exception e) {
            }

            @Override
            public void onSuccess(InputStream response) {
                if (response != null) {
                    Log.e("retrofitImageDownload","load success");

                    currentSize++;

                    inputStreamList.add(response);
                    if (currentSize < 2){
                        DownloadUtils.getInstance().ApkDownload(url,mcontext,fileCallBack);
                    }else {
                        //下载完成
                        try {
                            CombineSvgUtils svgUtils = new CombineSvgUtils(mcontext);
                            Bitmap bitmap = svgUtils.combineMultiByInputStream(inputStreamList);
                            if (bitmap != null) {
                                Log.e("retrofitImageDownload", "combine success");

                                ImageView iv = (ImageView) findViewById(R.id.svgIV);
                                iv.setVisibility(View.VISIBLE);
                                iv.setImageBitmap(bitmap);
                            }
                        } catch (SVGParseException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
                Log.e("retrofitImageDownload","load failure");
            }
        };

        DownloadUtils.getInstance().ApkDownload(url,this,fileCallBack);
    }

    /*使用retrofit加载单张图片*/
    private void retrofitImageDownload(){
        String rootPath = _getAppCacheRootFilePath(this) + "/download/";
        String fileNameWithPatch = getPackageName().replaceAll("\\.","_");

        BaseOkHttpFileCallBack fileCallBack = new BaseOkHttpFileCallBack(rootPath, fileNameWithPatch) {
            @Override
            public void onProgress(float progress, long total) {
            }

            @Override
            public void onFail(Call call, Exception e) {
            }

            @Override
            public void onSuccess(InputStream response) {
                if (response != null) {
                    Log.e("retrofitImageDownload","load success");

                    //下载完成
                    try {
                        CombineSvgUtils svgUtils = new CombineSvgUtils(mcontext);
                        Bitmap bitmap = svgUtils.displayByInputStream(response);
                        if (bitmap != null){
                            Log.e("retrofitImageDownload","combine success");

                            ImageView iv = (ImageView)findViewById(R.id.svgIV);
                            iv.setVisibility(View.VISIBLE);
                            iv.setImageBitmap(bitmap);
                        }
                    } catch (SVGParseException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                Log.e("retrofitImageDownload","load failure");
            }
        };

        DownloadUtils.getInstance().ApkDownload(url,this,fileCallBack);
    }

    /*手动绘制并组合svg图片*/
    private void drawMultiByManual(){
        try {
            CombineSvgUtils svgUtils = new CombineSvgUtils(this);
            List<Integer> resIdList = new ArrayList<>();
            resIdList.add(R.raw.web_animals);
            resIdList.add(R.raw.web_landscape);

            Bitmap bitmap = svgUtils.combineMultiByResource(resIdList);
            if (bitmap != null){
                ImageView iv = (ImageView)findViewById(R.id.svgIV);
                iv.setVisibility(View.VISIBLE);
                iv.setImageBitmap(bitmap);
            }
        } catch (SVGParseException e) {
            e.printStackTrace();
        }
    }

    /*获取文件路径*/
    private String _getAppCacheRootFilePath(Context context){
        Context appContext = context.getApplicationContext();
        String dir = null;
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            if(appContext.getExternalCacheDir() != null) {
                dir = appContext.getExternalCacheDir().getAbsolutePath();
            }else {
                dir = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        }else {
            dir = appContext.getCacheDir().getAbsolutePath();
        }
        return dir;
    }
}
