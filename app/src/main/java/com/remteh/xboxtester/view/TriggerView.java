package com.remteh.xboxtester.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class TriggerView extends View {

    private float value = 0f;
    private int accentColor = Color.parseColor("#107C10");
    private Paint bgPaint, fillPaint, glowPaint, textPaint, borderPaint;
    private RectF bounds = new RectF();
    private boolean isVertical = false;

    public TriggerView(Context context) { super(context); init(); }
    public TriggerView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public TriggerView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#1A1A35"));

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(accentColor);

        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setColor(accentColor);
        glowPaint.setAlpha(40);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.parseColor("#2A2A45"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2f);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#F0F0F5"));
        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setAccentColor(int color) {
        accentColor = color;
        fillPaint.setColor(color);
        glowPaint.setColor(color);
        glowPaint.setAlpha(40);
        invalidate();
    }

    public void setValue(float v) {
        this.value = Math.max(0f, Math.min(1f, v));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        float r = 16f;

        // Background
        bounds.set(0, 0, w, h);
        canvas.drawRoundRect(bounds, r, r, bgPaint);
        canvas.drawRoundRect(bounds, r, r, borderPaint);

        // Fill
        if (value > 0.01f) {
            float fillW = w * value;
            bounds.set(0, 0, fillW, h);
            canvas.drawRoundRect(bounds, r, r, fillPaint);

            // Glow at edge
            bounds.set(fillW - 20, 0, fillW + 10, h);
            canvas.drawRoundRect(bounds, 4, 4, glowPaint);
        }

        // Percentage text
        String pct = Math.round(value * 100) + "%";
        float textY = h / 2f - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText(pct, w / 2f, textY, textPaint);
    }
}
