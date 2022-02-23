package com.qearner.quiz.UI;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.qearner.quiz.Constant;
import com.qearner.quiz.R;

@SuppressWarnings("FieldCanBeLocal")
public class GradientProgress extends View {


    public static final int NO_GRADIENT = 0;
    public static final int LINEAR_GRADIENT = 1;
    public static final int RADIAL_GRADIENT = 2;
    public static final int SWEEP_GRADIENT = 3;
    public static final int DIRECTION_CLOCKWISE = 0;
    public static final int DIRECTION_COUNTERCLOCKWISE = 1;
    private static final int DEFAULT_TEXT_SIZE_SP = Constant.PROGRESS_TEXT_SIZE;
    private static final int DEFAULT_STROKE_WIDTH_DP = Constant.PROGRESS_STROKE_WIDTH;
    private static final int DEFAULT_PROGRESS_START_ANGLE = 270;
    private static final int ANGLE_START_PROGRESS_BACKGROUND = 0;
    private static final int ANGLE_END_PROGRESS_BACKGROUND = 360;

    private static final int DESIRED_WIDTH_DP = 150;


    private static final int DEFAULT_ANIMATION_DURATION = 1000;

    private static final String PROPERTY_ANGLE = "angle";


    private Paint progressPaint;
    private Paint progressBackgroundPaint;

    private Paint textPaint;

    private int startAngle = DEFAULT_PROGRESS_START_ANGLE;
    private int sweepAngle = 0;

    private RectF circleBounds;

    private String progressText;
    private float textX;
    private float textY;


    private double maxProgressValue = 100.0;
    private double progressValue = 0.0;

    @Direction
    private int direction = DIRECTION_COUNTERCLOCKWISE;

    private ValueAnimator progressAnimator;

    @NonNull
    private ProgressTextAdapter progressTextAdapter = null;

    public GradientProgress(Context context) {
        super(context);
        init(context, null);
        progressTextAdapter = null;
    }

    public GradientProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GradientProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GradientProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        int progressColor = ContextCompat.getColor(context, R.color.progress_start);
        int gradientColorEnd = ContextCompat.getColor(context, R.color.progress_end);
        int progressBackgroundColor = ContextCompat.getColor(context, R.color.card_color);
        int progressStrokeWidth = dp2px(DEFAULT_STROKE_WIDTH_DP);
        int textColor = ContextCompat.getColor(context, R.color.txt_color);
        int textSize = sp2px(DEFAULT_TEXT_SIZE_SP);

        if (attrs != null) {
            @SuppressLint("CustomViewStyleable") TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleTimer);
            progressColor = a.getColor(R.styleable.CircleTimer_progressColor, progressColor);
            progressBackgroundColor = a.getColor(R.styleable.CircleTimer_progressBackgroundColor, progressBackgroundColor);
            progressStrokeWidth = a.getDimensionPixelSize(R.styleable.CircleTimer_progressStrokeWidth, progressStrokeWidth);
            textColor = a.getColor(R.styleable.CircleTimer_textColor, textColor);
            textSize = a.getDimensionPixelSize(R.styleable.CircleTimer_textSize, textSize);
            startAngle = a.getInt(R.styleable.CircleTimer_startAngle, DEFAULT_PROGRESS_START_ANGLE);
            if (startAngle < 0 || startAngle > 360) {
                startAngle = DEFAULT_PROGRESS_START_ANGLE;
            }

            direction = a.getInt(R.styleable.CircleTimer_direction, DIRECTION_COUNTERCLOCKWISE);
            String formattingPattern = a.getString(R.styleable.CircleTimer_formattingPattern);
            if (formattingPattern != null) {
                progressTextAdapter = new PatternProgressTextAdapter(formattingPattern);
            } else {
                progressTextAdapter = new DefaultProgressTextAdapter();
            }

            reformatProgressText();
            final int gradientType = a.getColor(R.styleable.CircleTimer_gradientType, LINEAR_GRADIENT);
            if (gradientType != NO_GRADIENT) {
                gradientColorEnd = a.getColor(R.styleable.CircleTimer_gradientEndColor, gradientColorEnd);

                if (gradientColorEnd == -1) {
                    throw new IllegalArgumentException("did you forget to specify gradientColorEnd?");
                }

                int finalGradientColorEnd = gradientColorEnd;
                post(() -> setGradient(gradientType, finalGradientColorEnd));
            }
            a.recycle();
        }

        progressPaint = new Paint();
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(progressStrokeWidth);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(progressColor);
        progressPaint.setAntiAlias(true);

        progressBackgroundPaint = new Paint();
        progressBackgroundPaint.setStyle(Paint.Style.STROKE);
        progressBackgroundPaint.setStrokeWidth(progressStrokeWidth);
        progressBackgroundPaint.setColor(progressBackgroundColor);
        progressBackgroundPaint.setAntiAlias(true);


        textPaint = new TextPaint();
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setColor(textColor);
        textPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);

        circleBounds = new RectF();
    }

    public void SetTimerAttributes(int progressColor, int textColor) {
        reformatProgressText();
        calculateTextBounds();
        setProgressColor(progressColor);
        setTextColor(progressColor);
        textPaint = new TextPaint();
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        textPaint.setTextSize(sp2px(13));
        setGradient(NO_GRADIENT, 0);
    }

    public void setGradientAttributes(Context context) {
        reformatProgressText();
        calculateTextBounds();
        setProgressColor(ContextCompat.getColor(context, R.color.progress_start));
        textPaint = new TextPaint();
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setAntiAlias(true);
        textPaint.setColor(ContextCompat.getColor(context, R.color.txt_color));
        textPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        textPaint.setTextSize(sp2px(13));
        setGradient(LINEAR_GRADIENT, ContextCompat.getColor(context, R.color.progress_end));
    }  public void setSelfResultAttributes(Context context) {
        reformatProgressText();
        calculateTextBounds();
        setProgressStrokeWidthDp(10);
        setProgressColor(ContextCompat.getColor(context, R.color.progress_start));
        setProgressBackgroundColor(ContextCompat.getColor(context, R.color.bg_color));
        textPaint = new TextPaint();
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.TRANSPARENT);
        textPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        textPaint.setTextSize(0);
        setGradient(LINEAR_GRADIENT, ContextCompat.getColor(context, R.color.progress_end));
    }

    public void setAudiencePollAttributes(Context context) {

        setProgressStrokeWidthDp(4);
        setProgressBackgroundColor(ContextCompat.getColor(context, R.color.card_color_light));
        setProgressColor(ContextCompat.getColor(context, R.color.progress_end));

        //setGradient(LINEAR_GRADIENT, ContextCompat.getColor(context, R.color.progress_end));
        textPaint = new TextPaint();
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setAntiAlias(true);
        textPaint.setColor(ContextCompat.getColor(context, R.color.txt_color));
        textPaint.setTextSize(sp2px(8));


    }

    public void setResultAttributes(Context context) {

        setProgressStrokeWidthDp(10);
        setProgressColor(ContextCompat.getColor(context, R.color.progress_start));
        setProgressBackgroundColor(ContextCompat.getColor(context, R.color.bg_color));
        setGradient(LINEAR_GRADIENT, ContextCompat.getColor(context, R.color.progress_end));
        textPaint = new TextPaint();
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setAntiAlias(true);
        textPaint.setColor(ContextCompat.getColor(context, R.color.txt_color));
        textPaint.setTextSize(sp2px(20));


    }

    public void setStatisticAttributes(Context context) {
        reformatProgressText();
        calculateTextBounds();
        setProgressStrokeWidthDp(12);
        setProgressColor(ContextCompat.getColor(context, R.color.green));
        setProgressBackgroundColor(ContextCompat.getColor(context, R.color.red));
        textPaint = new TextPaint();
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setAntiAlias(true);
        textPaint.setColor(ContextCompat.getColor(context, R.color.txt_color));
        textPaint.setTextSize(sp2px(20));

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        @SuppressLint("DrawAllocation") Rect textBoundsRect = new Rect();
        textPaint.getTextBounds(progressText, 0, progressText.length(), textBoundsRect);

        float strokeSizeOffset = progressPaint.getStrokeWidth(); // to prevent progress or dot from drawing over the bounds
        int desiredSize = ((int) strokeSizeOffset) + dp2px(DESIRED_WIDTH_DP) + Math.max(paddingBottom + paddingTop, paddingLeft + paddingRight);

        // multiply by .1f to have an extra space for small padding between text and circle
        desiredSize += Math.max(textBoundsRect.width(), textBoundsRect.height()) + desiredSize * .1f;

        int finalWidth;
        if (widthMode == MeasureSpec.EXACTLY) {
            finalWidth = measuredWidth;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            finalWidth = Math.min(desiredSize, measuredWidth);
        } else {
            finalWidth = desiredSize;
        }

        int finalHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            finalHeight = measuredHeight;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            finalHeight = Math.min(desiredSize, measuredHeight);
        } else {
            finalHeight = desiredSize;
        }

        int widthWithoutPadding = finalWidth - paddingLeft - paddingRight;
        int heightWithoutPadding = finalHeight - paddingTop - paddingBottom;

        int smallestSide = Math.min(heightWithoutPadding, widthWithoutPadding);
        setMeasuredDimension(smallestSide, smallestSide);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        calculateBounds(w, h);
    }

    private void calculateBounds(int w, int h) {
        float strokeSizeOffset = progressPaint.getStrokeWidth(); // to prevent progress or dot from drawing over the bounds
        float halfOffset = strokeSizeOffset / 2f;

        circleBounds.left = halfOffset;
        circleBounds.top = halfOffset;
        circleBounds.right = w - halfOffset;
        circleBounds.bottom = h - halfOffset;
        calculateTextBounds();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawProgressBackground(canvas);
        drawProgress(canvas);
        drawText(canvas);
    }

    private void drawProgressBackground(Canvas canvas) {
        canvas.drawArc(circleBounds, ANGLE_START_PROGRESS_BACKGROUND, ANGLE_END_PROGRESS_BACKGROUND,
                false, progressBackgroundPaint);
    }

    private void drawProgress(Canvas canvas) {
        canvas.drawArc(circleBounds, startAngle, sweepAngle, false, progressPaint);
    }


    private void drawText(Canvas canvas) {
        canvas.drawText(progressText, textX, textY, textPaint);
    }

    public void setMaxProgress(double maxProgress) {
        maxProgressValue = maxProgress;
        if (maxProgressValue < progressValue) {
            setCurrentProgress(maxProgress);
        }
        invalidate();
    }

    public void setCurrentProgress(double currentProgress) {
        if (currentProgress > maxProgressValue) {
            maxProgressValue = currentProgress;
        }

        setProgress(currentProgress, maxProgressValue);
        reformatProgressText();
        calculateTextBounds();
    }

    public void setAudienceProgress(double currentProgress) {
        if (currentProgress > maxProgressValue) {
            maxProgressValue = currentProgress;
        }

        setProgress(currentProgress, maxProgressValue);
        reformatProgressTextPercent();
        calculateTextBounds();
    }

    public void setProgress(double current, double max) {
        final double finalAngle;

        if (direction == DIRECTION_COUNTERCLOCKWISE) {
            finalAngle = -(current / max * 360);
        } else {
            finalAngle = current / max * 360;
        }

        final PropertyValuesHolder angleProperty = PropertyValuesHolder.ofInt(PROPERTY_ANGLE, sweepAngle, (int) finalAngle);

        double oldCurrentProgress = progressValue;

        maxProgressValue = max;
        progressValue = Math.min(current, max);

        reformatProgressText();
        calculateTextBounds();

        if (progressAnimator != null) {
            progressAnimator.cancel();
        }

        progressAnimator = ValueAnimator.ofObject((TypeEvaluator<Double>) (fraction, startValue, endValue) -> (startValue + (endValue - startValue) * fraction), oldCurrentProgress, progressValue);
        progressAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
        progressAnimator.setValues(angleProperty);
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimator.addUpdateListener(animation -> {
            sweepAngle = (int) animation.getAnimatedValue(PROPERTY_ANGLE);
            invalidate();
        });
        progressAnimator.addListener(new DefaultAnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
                sweepAngle = (int) finalAngle;

                progressAnimator = null;
            }
        });
        progressAnimator.start();
    }

    private void reformatProgressText() {
        progressText = progressTextAdapter.formatText(progressValue);
    }

    private void reformatProgressTextPercent() {
        progressText = progressTextAdapter.formatText(progressValue) + "%";
    }

    private Rect calculateTextBounds() {
        Rect textRect = new Rect();
        textPaint.getTextBounds(progressText, 0, progressText.length(), textRect);
        textX = circleBounds.centerX() - textRect.width() / 2f;
        textY = circleBounds.centerY() + textRect.height() / 2f;

        return textRect;
    }

    private int dp2px(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    private int sp2px(float sp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }

    // calculates circle bounds, view size and requests invalidation
    private void invalidateEverything() {
        calculateBounds(getWidth(), getHeight());
        requestLayout();
        invalidate();
    }

    public void setProgressColor(@ColorInt int color) {
        progressPaint.setColor(color);
        invalidate();
    }

    public void setProgressBackgroundColor(@ColorInt int color) {
        progressBackgroundPaint.setColor(color);
        invalidate();
    }

    public void setProgressStrokeWidthDp(@Dimension int strokeWidth) {
        setProgressStrokeWidthPx(dp2px(strokeWidth));
    }

    public void setProgressStrokeWidthPx(@Dimension final int strokeWidth) {
        progressPaint.setStrokeWidth(strokeWidth);
        progressBackgroundPaint.setStrokeWidth(strokeWidth);

        invalidateEverything();
    }

    public void setTextColor(@ColorInt int color) {
        textPaint.setColor(color);

        Rect textRect = new Rect();
        textPaint.getTextBounds(progressText, 0, progressText.length(), textRect);

        invalidate(textRect);
    }

    public void setTextSizeSp(@Dimension int size) {
        setTextSizePx(sp2px(size));
    }

    public void setTextSizePx(@Dimension int size) {
        float currentSize = textPaint.getTextSize();

        float factor = textPaint.measureText(progressText) / currentSize;


        float maximumAvailableTextWidth = circleBounds.width();

        if (size * factor >= maximumAvailableTextWidth) {
            size = (int) (maximumAvailableTextWidth / factor);
        }

        textPaint.setTextSize(size);

        Rect textBounds = calculateTextBounds();
        invalidate(textBounds);
    }


    @ColorInt
    public int getTextColor() {
        return textPaint.getColor();
    }

    public float getTextSize() {
        return textPaint.getTextSize();
    }


    public double getProgress() {
        return progressValue;
    }


    @Direction
    public int getDirection() {
        return direction;
    }

    public void setDirection(@Direction int direction) {
        this.direction = direction;
        invalidate();
    }

    @IntDef({DIRECTION_CLOCKWISE, DIRECTION_COUNTERCLOCKWISE})
    private @interface Direction {
    }

    public void setGradient(int type, @ColorInt int endColor) {
        Shader gradient = null;
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        int startColor = progressPaint.getColor();


        switch (type) {
            case LINEAR_GRADIENT:
                gradient = new LinearGradient(0f, 0f, getWidth(), getHeight(), startColor, endColor, Shader.TileMode.CLAMP);
                break;
            case RADIAL_GRADIENT:
                gradient = new RadialGradient(cx, cy, cx, startColor, endColor, Shader.TileMode.MIRROR);
                break;
            case SWEEP_GRADIENT:
                gradient = new SweepGradient(cx, cy, new int[]{startColor, endColor}, null);
                break;
        }
        if (gradient != null) {
            Matrix matrix = new Matrix();
            matrix.postRotate(startAngle, cx, cy);
            gradient.setLocalMatrix(matrix);
        }

        progressPaint.setShader(gradient);
        invalidate();
    }

    public int getGradientType() {
        Shader shader = progressPaint.getShader();

        int type = NO_GRADIENT;

        if (shader instanceof LinearGradient) {
            type = LINEAR_GRADIENT;
        } else if (shader instanceof RadialGradient) {
            type = RADIAL_GRADIENT;
        } else if (shader instanceof SweepGradient) {
            type = SWEEP_GRADIENT;
        }

        return type;
    }

    public interface ProgressTextAdapter {

        @NonNull
        String formatText(double currentProgress);
    }

    public static final class PatternProgressTextAdapter implements GradientProgress.ProgressTextAdapter {

        private String pattern;

        public PatternProgressTextAdapter(String pattern) {
            this.pattern = pattern;
        }

        @NonNull
        @Override
        public String formatText(double currentProgress) {
            return String.format(pattern, currentProgress);
        }
    }

    public static final class DefaultProgressTextAdapter implements GradientProgress.ProgressTextAdapter {

        @NonNull
        @Override
        public String formatText(double currentProgress) {
            return String.valueOf((int) currentProgress);
        }
    }

    static class DefaultAnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation, boolean isReverse) {

        }

        @Override
        public void onAnimationEnd(Animator animation, boolean isReverse) {

        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}