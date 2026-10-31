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
    private boolean sampled = false, available = true;
    private int accentColor = Color.parseColor("#107C10");
    private Paint bgPaint, fillPaint, glowPaint, textPaint, borderPaint;
    private RectF bounds = new RectF();
    private boolean isVertical = false;

    public TriggerView(Context context) { super(context); init(); }
    public TriggerView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public TriggerView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#1B252F"));

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(accentColor);

        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setColor(accentColor);
        glowPaint.setAlpha(40);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.parseColor("#344454"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2f);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#F0F0F5"));
        textPaint.setTextSize(13f * getResources().getDisplayMetrics().scaledDensity);
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
        sampled = true; available = !Float.isNaN(v) && !Float.isInfinite(v);
        this.value = available ? Math.max(0f, Math.min(1f, v)) : 0f;
        invalidate();
    }

    public void clear() { sampled = false; value = 0; invalidate(); }

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
        String pct = !sampled ? "Не измерено" : !available ? "Недоступен API" : Math.round(value * 100) + "%";
        float textY = h / 2f - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText(pct, w / 2f, textY, textPaint);
    }
}
