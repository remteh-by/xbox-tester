package com.remteh.xboxtester.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class StickView extends View {

    private float stickX = 0f;
    private float stickY = 0f;
    private int accentColor = Color.parseColor("#1DB954");
    private Paint gridPaint, outerRingPaint, dotPaint, dotGlowPaint, deadZonePaint, trailPaint;
    private float deadZone = 0.05f;
    private float[] trailX = new float[12];
    private float[] trailY = new float[12];
    private int trailIndex = 0;

    public StickView(Context context) { super(context); init(); }
    public StickView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public StickView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#1A1A35"));
        gridPaint.setStrokeWidth(1f);
        gridPaint.setStyle(Paint.Style.STROKE);

        outerRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerRingPaint.setColor(Color.parseColor("#2A2A45"));
        outerRingPaint.setStyle(Paint.Style.STROKE);
        outerRingPaint.setStrokeWidth(2f);

        deadZonePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        deadZonePaint.setColor(Color.parseColor("#1A1A2E"));
        deadZonePaint.setStyle(Paint.Style.FILL);

        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(accentColor);
        dotPaint.setStyle(Paint.Style.FILL);

        dotGlowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotGlowPaint.setColor(accentColor);
        dotGlowPaint.setAlpha(60);
        dotGlowPaint.setStyle(Paint.Style.FILL);

        trailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trailPaint.setColor(accentColor);
        trailPaint.setStyle(Paint.Style.FILL);
    }

    public void setAccentColor(int color) {
        accentColor = color;
        dotPaint.setColor(color);
        dotGlowPaint.setColor(color);
        dotGlowPaint.setAlpha(60);
        trailPaint.setColor(color);
        invalidate();
    }

    public void setDeadZone(float dz) {
        this.deadZone = Math.max(0f, Math.min(0.5f, dz));
        invalidate();
    }

    public void setPosition(float x, float y) {
        this.stickX = Math.max(-1f, Math.min(1f, x));
        this.stickY = Math.max(-1f, Math.min(1f, y));
        trailX[trailIndex] = stickX;
        trailY[trailIndex] = stickY;
        trailIndex = (trailIndex + 1) % trailX.length;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = Math.min(cx, cy) - 12f;

        // Outer ring
        canvas.drawCircle(cx, cy, radius, outerRingPaint);

        // Grid lines
        canvas.drawLine(cx - radius, cy, cx + radius, cy, gridPaint);
        canvas.drawLine(cx, cy - radius, cx, cy + radius, gridPaint);
        canvas.drawCircle(cx, cy, radius * 0.5f, gridPaint);

        // Dead zone
        float dzRadius = radius * deadZone;
        canvas.drawCircle(cx, cy, dzRadius, deadZonePaint);

        // Motion trail
        for (int i = 0; i < trailX.length; i++) {
            int age = (trailIndex - i + trailX.length) % trailX.length;
            float alpha = Math.max(0f, 1f - age / (float) trailX.length);
            trailPaint.setAlpha((int) (alpha * 40));
            float tx = cx + trailX[i] * radius;
            float ty = cy + trailY[i] * radius;
            canvas.drawCircle(tx, ty, 4f, trailPaint);
        }

        // Stick dot
        float dotX = cx + stickX * radius;
        float dotY = cy + stickY * radius;
        canvas.drawCircle(dotX, dotY, 20f, dotGlowPaint);
        canvas.drawCircle(dotX, dotY, 12f, dotPaint);
    }
}
