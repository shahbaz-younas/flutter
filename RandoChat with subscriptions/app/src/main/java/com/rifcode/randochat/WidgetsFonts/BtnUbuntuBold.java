package com.rifcode.randochat.WidgetsFonts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import com.rifcode.randochat.R;


public class BtnUbuntuBold extends androidx.appcompat.widget.AppCompatButton  {


    AttributeSet attr;

    public BtnUbuntuBold(Context context) {
        super(context);
        setCustomFont(context, attr);
    }

    public BtnUbuntuBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public BtnUbuntuBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        String customFont = null;
        TypedArray a = null;
        if (attrs != null) {
            a = ctx.obtainStyledAttributes(attrs, R.styleable.UbuntuBold);
            customFont = a.getString(R.styleable.UbuntuBold_customFontUbuntuBold);
        }
        if (customFont == null)
            customFont = "fonts/Ubuntu-Bold.ttf";
        setCustomFont(ctx, customFont);
        if (a != null) {
            a.recycle();
        }
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e("textView", "Could not get typeface", e);
            return false;
        }
        setTypeface(tf);
        return true;
    }
}
