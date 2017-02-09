package com.sien.cpsvgcase.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.sien.cpsvgcase.R;
import com.sien.cpsvgcase.utils.CPScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sien
 * @date 2017/2/7
 * @descript com.sien.app.main.widget.tpsvg
 */
public class DynamicSvgView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * Utils to catch the paths from the svg.
     */
    private final DynamicSvgUtils dynamicSvgUtils = new DynamicSvgUtils(paint);
    /**
     * All the paths provided to the view. Both from Path and Svg.
     */
    private List<DynamicSvgUtils.SvgPath> paths = new ArrayList<DynamicSvgUtils.SvgPath>(0);
    private List<DynamicSvgUtils.SvgPath> secpaths = new ArrayList<DynamicSvgUtils.SvgPath>(0);

    /**
     * If the used colors are from the svg or from the set color.
     */
    private boolean naturalColors;

    /**
     * The svg image from the raw directory.
     */
    private int svgResourceId;

    /**
     * If the view is filled with its natural colors after path drawing.
     */
    private boolean fillAfter;
    /**
     * The width of the view.
     */
    private int width;
    /**
     * The height of the view.
     */
    private int height;


    public DynamicSvgView(Context context) {
        this(context, null);
    }

    /**
     * Default constructor.
     *
     * @param context The Context of the application.
     * @param attrs   attributes provided from the resources.
     */
    public DynamicSvgView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Default constructor.
     *
     * @param context  The Context of the application.
     * @param attrs    attributes provided from the resources.
     * @param defStyle Default style.
     */
    public DynamicSvgView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        paint.setStyle(Paint.Style.STROKE);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        getFromAttributes(context, attrs);

        width = CPScreenUtil.getScreenWidth(context);
        height = CPScreenUtil.getScreenHeight(context);

    }

    public void setup(){
        dynamicSvgUtils.load(getContext(), svgResourceId);

        paths = dynamicSvgUtils.getPathsForViewport(width, height);

        updatePathsPhaseLocked();
    }

    /**
     * If the real svg need to be drawn after the path animation.
     *
     * @param fillAfter - boolean if the view needs to be filled after path animation.
     */
    public void setFillAfter(final boolean fillAfter) {
        this.fillAfter = fillAfter;
    }

    /**
     * If you want to use the colors from the svg.
     */
    public void useNaturalColors() {
        naturalColors = true;
    }

    /**
     * Set the svg resource id.
     *
     * @param svgResource - The resource id of the raw svg.
     */
    public void setSvgResource(int svgResource) {
        svgResourceId = svgResource;
    }

    /**
     * This refreshes the paths before draw and resize.
     */
    private void updatePathsPhaseLocked() {
        final int count = paths.size();
        for (int i = 0; i < count; i++) {
            DynamicSvgUtils.SvgPath svgPath = paths.get(i);
            svgPath.path.reset();
            svgPath.measure.getSegment(0.0f, svgPath.length, svgPath.path, true);
            // Required only for Android 4.4 and earlier
            svgPath.path.rLineTo(0.0f, 0.0f);
        }
    }

    /**
     * Get all the fields from the attributes .
     *
     * @param context The Context of the application.
     * @param attrs   attributes provided from the resources.
     */
    private void getFromAttributes(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DynamicSvgPathView);
        try {
            if (a != null) {
                paint.setColor(a.getColor(R.styleable.DynamicSvgPathView_pathColor, 0xff00ff00));
                paint.setStrokeWidth(a.getFloat(R.styleable.DynamicSvgPathView_pathWidth, 8.0f));
                svgResourceId = a.getResourceId(R.styleable.DynamicSvgPathView_svgRes, 0);
            }
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        final int count = paths.size();
        for (int i = 0; i < count; i++) {
            final DynamicSvgUtils.SvgPath svgPath = paths.get(i);
            final Path path = svgPath.path;
            final Paint paint1 = naturalColors ? svgPath.paint : paint;
            canvas.drawPath(path, paint1);
        }
        fillAfter(canvas);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

//        width = w - getPaddingLeft() - getPaddingRight();
//        height = h - getPaddingTop() - getPaddingBottom();
//        paths = dynamicSvgUtils.getPathsForViewport(width, height);
//        updatePathsPhaseLocked();
    }

    /**
     * If there is svg , the user called setFillAfter(true) and the progress is finished.
     *
     * @param canvas Draw to this canvas.
     */
    private void fillAfter(final Canvas canvas) {
        if (svgResourceId != 0 && fillAfter) {
            dynamicSvgUtils.drawSvgAfter(canvas, width, height);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (svgResourceId != 0) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthSize, heightSize);
            return;
        }

        int desiredWidth = 0;
        int desiredHeight = 0;
        final float strokeWidth = paint.getStrokeWidth() / 2;
        for (DynamicSvgUtils.SvgPath path : paths) {
            desiredWidth += path.bounds.left + path.bounds.width() + strokeWidth;
            desiredHeight += path.bounds.top + path.bounds.height() + strokeWidth;
        }
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(widthMeasureSpec);

        int measuredWidth, measuredHeight;

        if (widthMode == MeasureSpec.AT_MOST) {
            measuredWidth = desiredWidth;
        } else {
            measuredWidth = widthSize;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            measuredHeight = desiredHeight;
        } else {
            measuredHeight = heightSize;
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }
}
