package cn.iterlog.xmimagepicker;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class RippleChoiceView extends View {
    // 点击控件的点
    private float mDensity;
    private float scaledDensity;

    private float mRadius;

    private int mUnCheckColor = Color.WHITE;
    private int mCheckColor = Color.GREEN;
    private int mCrossColor = Color.BLUE;
    private int mBgColor = Color.argb(128, 0, 0, 0);
    private String mCrossType = TYPE_CROSS_CROSS;
    private float mBorderWidth = dp2px(5);
    private float mTextSize = sp2px(16);
    private RectF mRectF;
    private RectF mNRectF = new RectF();

    private Paint mUncheckPaint;
    private Paint mCheckPaint;
    private float mFraction = 0.0f;
    private OnCheckedChangeListener onCheckedChangeListener;
    private ValueAnimator mFractionAnimator;
    private boolean mIsAnimating = false;
    private int mDuration = 1200;
    private int mCircleDuration = 800;
    private int mHookDuration = 400;

    private PointF hookStart;
    private PointF hookMiddle;
    private PointF hookEnd;

    private boolean mChecked = true;
    private boolean isHookShow = false;
    private int mNumber = 0;

    private static final String TYPE_CROSS_NUMBER = "number";
    private static final String TYPE_CROSS_CROSS = "cross";

    public RippleChoiceView(Context context) {
        super(context);
        init(null, 0);
    }

    public RippleChoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RippleChoiceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mDensity = getContext().getResources().getDisplayMetrics().density;
        scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RippleChoiceView, defStyle, 0);
        mUnCheckColor = a.getColor(R.styleable.RippleChoiceView_borderColor, mUnCheckColor);
        mCheckColor = a.getColor(R.styleable.RippleChoiceView_checkedColor, mCheckColor);
        mCrossColor = a.getColor(R.styleable.RippleChoiceView_crossColor, mCrossColor);
        mBgColor = a.getColor(R.styleable.RippleChoiceView_backgroundColor, mBgColor);
        mTextSize = a.getDimension(R.styleable.RippleChoiceView_textSize, mTextSize);
        String crossType = a.getString(R.styleable.RippleChoiceView_crossType);
        if (crossType.equals(TYPE_CROSS_NUMBER)) {
            mCrossType = TYPE_CROSS_NUMBER;
        } else {
            mCrossType = TYPE_CROSS_CROSS;
        }
        mBorderWidth = a.getDimension(R.styleable.RippleChoiceView_rippleBorderWidth, dp2px(2));
        mDuration = a.getInt(R.styleable.RippleChoiceView_rippleduration, mDuration);
        mChecked = a.getBoolean(R.styleable.RippleChoiceView_checked, mChecked);
        mNumber = a.getInt(R.styleable.RippleChoiceView_number, mNumber);
        a.recycle();
        mHookDuration = (int) (mDuration * 0.3);
        mCircleDuration = mDuration - mHookDuration;
        mUncheckPaint = new Paint();
        mUncheckPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mUncheckPaint.setStrokeWidth(mBorderWidth);
        mUncheckPaint.setColor(mUnCheckColor);
        mUncheckPaint.setStyle(Paint.Style.STROKE);
        mCheckPaint = new Paint();
        mCheckPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mCheckPaint.setColor(mCheckColor);
        mCheckPaint.setStyle(Paint.Style.STROKE);
        isHookShow = mChecked;
        mFraction = mChecked ? 1.0f : 0.0f;
    }

    private void initValueAnimator(long duration) {
        mFractionAnimator = ValueAnimator.ofFloat(0, 1).setDuration(duration);
        mFractionAnimator.setInterpolator(new AccelerateInterpolator());
        mFractionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mFraction = (Float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        mFractionAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsAnimating = false;
                switchState();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mIsAnimating = false;
                mFraction = mChecked ? 1.0f : 0.0f;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    private void switchState() {
        if (mChecked && !isHookShow) {
            isHookShow = !isHookShow;
            initValueAnimator(mHookDuration);
            mFractionAnimator.start();
        } else if (!mChecked && isHookShow) {
            isHookShow = !isHookShow;
            initValueAnimator(mCircleDuration);
            mFractionAnimator.start();
        }
    }

    private int dp2px(int dp) {
        return (int) (dp * mDensity + 0.5f);
    }

    private int sp2px(int dp) {
        return (int) (dp * scaledDensity + 0.5f);
    }


    private int width;
    private int height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        mRectF = new RectF(getPaddingLeft(), getPaddingTop(),
                w - getPaddingRight(), h - getPaddingBottom());
        mRadius = Math.min(mRectF.width(), mRectF.height()) / 2;

        hookStart = new PointF(mRectF.centerX() - mRadius / 2, mRectF.centerY() + mRadius / 10);
        hookMiddle = new PointF(mRectF.centerX() - mRadius * 1 / 6, mRectF.centerY() + mRadius * 2 / 5);
        hookEnd = new PointF(mRectF.centerX() + mRadius / 2, mRectF.centerY() - mRadius * 1 / 3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!isEnabled()) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (mIsAnimating) {
                    return true;
                }
                if (x + getLeft() < getRight() && y + getTop() < getBottom()) {
                    mChecked = !mChecked;
                    if (mIsAnimating) {
                        mFractionAnimator.cancel();
                    }
                    if (mChecked) {
                        initValueAnimator(mCircleDuration);
                    } else {
                        initValueAnimator(mHookDuration);
                    }
                    mFractionAnimator.start();
                    if (onCheckedChangeListener != null) {
                        onCheckedChangeListener.onCheckedChanged(this, mChecked);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mUncheckPaint.setColor(mBgColor);
        mUncheckPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), mRadius - mBorderWidth, mUncheckPaint);
        mUncheckPaint.setColor(mUnCheckColor);
        mUncheckPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), mRadius - mBorderWidth + 1, mUncheckPaint);
        if (mChecked || mIsAnimating) {
            float stroke = mFraction * mRadius;
            if (!mIsAnimating) {
                stroke = mRadius;
            }

            if (mChecked) {
                mCheckPaint.setStrokeWidth(stroke);
            } else {
                mCheckPaint.setStrokeWidth(mRadius - stroke);
            }

            if (isHookShow) {
                mNRectF.left = getPaddingLeft() + mRadius / 2;
                mNRectF.top = getPaddingTop() + mRadius / 2;
                mNRectF.right = width - getPaddingRight() - mRadius / 2;
                mNRectF.bottom = height - getPaddingBottom() - mRadius / 2;
                mCheckPaint.setStrokeWidth(mRadius);
            } else if (mChecked) {
                mNRectF.left = getPaddingLeft() + stroke / 2;
                mNRectF.top = getPaddingTop() + stroke / 2;
                mNRectF.right = width - getPaddingRight() - stroke / 2;
                mNRectF.bottom = height - getPaddingBottom() - stroke / 2;
            } else {
                mNRectF.left = getPaddingLeft() + (mRadius - stroke) / 2;
                mNRectF.top = getPaddingTop() + (mRadius - stroke) / 2;
                mNRectF.right = width - getPaddingRight() - (mRadius - stroke) / 2;
                mNRectF.bottom = height - getPaddingBottom() - (mRadius - stroke) / 2;
            }
            canvas.drawArc(mNRectF, 0f, 360f, false, mCheckPaint);
        }

        mUncheckPaint.setColor(mCrossColor);
        if (mCrossType.equals(TYPE_CROSS_NUMBER)) {
            if (mChecked) {
                mUncheckPaint.setTextSize(mTextSize);
                mUncheckPaint.setStyle(Paint.Style.FILL);
                Paint.FontMetrics metrics = mUncheckPaint.getFontMetrics();
                int baseline = (int) ((mRectF.bottom + mRectF.top - metrics.bottom - metrics.top) / 2);
                mUncheckPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(String.valueOf(mNumber), mRectF.centerX(), baseline, mUncheckPaint);
                mUncheckPaint.setStyle(Paint.Style.STROKE);
            }
        } else {

            if (mIsAnimating) {
                if (!mChecked && isHookShow) {
                    mFraction = 1 - mFraction;
                }

                if (isHookShow && mFraction > 0) {// y1 - x1
                    if (mFraction < 0.4) {
                        canvas.drawLine(hookStart.x, hookStart.y, getr1x((float) (mFraction / 0.4)), getr1y((float) (mFraction / 0.4)), mUncheckPaint);
                    } else {
                        canvas.drawLine(hookStart.x, hookStart.y, hookMiddle.x + 2, hookMiddle.y + 2, mUncheckPaint);
                        canvas.drawLine(hookMiddle.x, hookMiddle.y, getr2x((float) ((mFraction - 0.4) / 0.6)), getr2y((float) ((mFraction - 0.4f) / 0.6)), mUncheckPaint);
                    }
                }
            } else {
                if (mChecked && isHookShow) {
                    canvas.drawLine(hookStart.x, hookStart.y, hookMiddle.x + 2, hookMiddle.y + 2, mUncheckPaint);
                    canvas.drawLine(hookMiddle.x, hookMiddle.y, getr2x((float) ((1 - 0.4) / 0.6)), getr2y((float) ((1 - 0.4f) / 0.6)), mUncheckPaint);
                }
            }
        }
    }

    private float getr1y(float fraction) {
        return hookStart.y + (hookMiddle.y - hookStart.y) * fraction;
    }

    private float getr1x(float fraction) {
        return hookStart.x + (hookMiddle.x - hookStart.x) * fraction;
    }

    private float getr2y(float fraction) {
        return hookMiddle.y + (hookEnd.y - hookMiddle.y) * fraction;
    }

    private float getr2x(float fraction) {
        return hookMiddle.x + (hookEnd.x - hookMiddle.x) * fraction;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mIsAnimating) {
            mFractionAnimator.cancel();
        }
    }

    public static interface OnCheckedChangeListener {
        void onCheckedChanged(RippleChoiceView view, boolean isChecked);
    }

    public int getmUnCheckColor() {
        return mUnCheckColor;
    }

    public void setmUnCheckColor(int mUnCheckColor) {
        this.mUnCheckColor = mUnCheckColor;
    }

    public int getmCheckColor() {
        return mCheckColor;
    }

    public void setmCheckColor(int mCheckColor) {
        this.mCheckColor = mCheckColor;
    }

    public int getmCrossColor() {
        return mCrossColor;
    }

    public void setmCrossColor(int mCrossColor) {
        this.mCrossColor = mCrossColor;
    }

    public String getmCrossType() {
        return mCrossType;
    }

    public void setmCrossType(String mCrossType) {
        this.mCrossType = mCrossType;
    }

    public float getmBorderWidth() {
        return mBorderWidth;
    }

    public void setmBorderWidth(float mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public int getmNumber() {
        return mNumber;
    }

    public void setmNumber(int mNumber) {
        this.mNumber = mNumber;
        postInvalidate();
    }

    public boolean ismChecked() {
        return mChecked;
    }

    public void setmChecked(boolean mChecked) {
        this.mChecked = mChecked;
        this.isHookShow = mChecked;
        invalidate();
    }
}
