package github.alexchueng.shapebutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.xanarry.lantrans.R;


/**
 * Created by AlexCheung on 2015/11/21.
 */
@SuppressWarnings("ALL")
public class ShapeButton extends Button {
    private int width;
    private int height;
    private GradientDrawable backgroundDrawable;
    private float topLeftRadius = 8;
    private float topRightRadius = 8;
    private float bottomLeftRadius = 8;
    private float bottomRightRadius = 8;
    private int backgroundColorNormal = android.R.color.transparent;
    private int backgroundColorSelected = android.R.color.transparent;
    private int textColorNormal = android.R.color.black;
    private int textColorSelected = android.R.color.black;
    private float textSizeNormal = 14;
    private float textSizeSelected = 14;
    private int strokeColor = android.R.color.transparent;
    private int strokeWidth = 1;

    /**
     * 0  圆形 <br/>
     * 1 矩形
     */
    private int shapeType;

    public ShapeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initShapeButton(attrs);
    }

    private void initShapeButton(AttributeSet attrs) {
        /*if(isInEditMode()){
            return ;
        }*/
        typeVauleComplex();
        arrayGetResource(attrs);
        backgroundDrawable = (backgroundDrawable == null) ? new GradientDrawable () : backgroundDrawable;
        if (shapeType == 0) {
            int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            measure(spec, spec);
            int width = (int)getPaint().measureText(getText().toString());
            setWidth(width+getPaddingLeft()+getPaddingRight());
            setHeight(width+getPaddingLeft()+getPaddingRight());
            backgroundDrawable.setShape(GradientDrawable.OVAL);

        } else if (shapeType == 1) {
            //topLeftRadius
            float[] outerRadii = {topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius};
            /*无内矩形*/
            backgroundDrawable.setShape(GradientDrawable.RECTANGLE);
            backgroundDrawable.setCornerRadii(outerRadii);

        } else if (shapeType == 2) {
            backgroundDrawable.setShape(GradientDrawable.RING);
        }
        backgroundDrawable.setColor(backgroundColorNormal);
        backgroundDrawable.setStroke(strokeWidth, strokeColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(backgroundDrawable);
        }else{
            setBackgroundDrawable(backgroundDrawable);
        }
        setTextColor(textColorNormal);
        setOnTouchListener(new MyOnTouchListener());
    }
    private final class MyOnTouchListener implements OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    backgroundDrawable.setColor(backgroundColorSelected);
                    setTextColor(textColorSelected);
                    invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    backgroundDrawable.setColor(backgroundColorNormal);
                    setTextColor(textColorNormal);
                    invalidate();
                    break;
            }
            return false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float max = Math.max(width,height);
        //LogUtils.e("width = " + width + " height = " + height + " max = " + max);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }
    private void typeVauleComplex() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        textSizeNormal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSizeNormal, metrics);
        textSizeSelected = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSizeSelected, metrics);
        topLeftRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topLeftRadius, metrics);
        topRightRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topRightRadius, metrics);
        bottomLeftRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomLeftRadius, metrics);
        bottomRightRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomRightRadius, metrics);
        strokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, strokeWidth, metrics);
    }
    private void arrayGetResource(AttributeSet attrs) {
        final TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.ShapeButton);
        backgroundColorNormal = array.getColor(R.styleable.ShapeButton_backgroundColor_normal, getResources().getColor(android.R.color.transparent));
        backgroundColorSelected = array.getColor(R.styleable.ShapeButton_backgroundColor_selected, getResources().getColor(android.R.color.transparent));
        textColorNormal = array.getColor(R.styleable.ShapeButton_textColor_normal, getResources().getColor(android.R.color.black));
        textColorSelected = array.getColor(R.styleable.ShapeButton_textColor_selected, getResources().getColor(android.R.color.black));
        textSizeNormal = array.getDimension(R.styleable.ShapeButton_textSize_normal, textSizeNormal);
        textSizeSelected = array.getDimension(R.styleable.ShapeButton_textSize_selected, textSizeSelected);
        shapeType = array.getInt(R.styleable.ShapeButton_shapeType, 1);
        topRightRadius = array.getDimension(R.styleable.ShapeButton_topRightRadius, topRightRadius);
        topLeftRadius = array.getDimension(R.styleable.ShapeButton_topLeftRadius, topLeftRadius);
        bottomLeftRadius = array.getDimension(R.styleable.ShapeButton_bottomLeftRadius, bottomLeftRadius);
        bottomRightRadius = array.getDimension(R.styleable.ShapeButton_bottomRightRadius, bottomRightRadius);
        strokeWidth = (int)array.getDimension(R.styleable.ShapeButton_strokeWidth, strokeWidth);
        strokeColor = array.getColor(R.styleable.ShapeButton_strokeColor, getResources().getColor(android.R.color.transparent));
        array.recycle();
    }
}
