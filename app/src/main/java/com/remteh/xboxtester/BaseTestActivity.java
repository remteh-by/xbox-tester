package com.remteh.xboxtester;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.remteh.xboxtester.view.StickView;
import com.remteh.xboxtester.view.TriggerView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class BaseTestActivity extends AppCompatActivity implements InputManager.InputDeviceListener {

    // UI
    protected TextView tvStatus, tvControllerName, tvRawData, tvLatency, tvPressCount;
    protected TriggerView triggerLT, triggerRT;
    protected StickView stickLeft, stickRight;
    protected TextView tvLeftStick, tvRightStick;
    protected TextView tvTriggerLLabel, tvTriggerRLabel;
    protected TextView btnLB, btnRB, btnL3, btnR3;
    protected TextView btnDpadUp, btnDpadDown, btnDpadLeft, btnDpadRight;
    protected TextView btnStart, btnSelect, btnSystem, btnExtra;
    protected TextView btnFace1, btnFace2, btnFace3, btnFace4;
    protected TextView btnVibShort, btnVibMedium, btnVibLong, btnVibPattern;
    // Calibration UI
    protected SeekBar seekDeadZone;
    protected TextView tvDeadZoneVal;
    protected TextView tvCalibInfo;
    protected TextView btnResetCalib;

    protected InputManager inputManager;
    protected InputDevice connectedDevice;
    protected int totalPresses = 0;
    protected long lastInputTime = 0;
    protected Map<Integer, Long> buttonPressTime = new HashMap<>();

    // Calibration data
    protected float deadZone = 0.08f;
    protected float lxMin = 0, lxMax = 0, lyMin = 0, lyMax = 0;
    protected float rxMin = 0, rxMax = 0, ryMin = 0, ryMax = 0;
    protected float ltMin = 0, ltMax = 0, rtMin = 0, rtMax = 0;

    // Abstract methods for controller-specific configuration
    protected abstract int getAccentColor();
    protected abstract String getControllerTitle();
    protected abstract String[] getFaceButtonLabels();
    protected abstract int[] getFaceButtonColors();
    protected abstract String getShoulderLeftLabel();   // LB / L1 / L
    protected abstract String getShoulderRightLabel();  // RB / R1 / R
    protected abstract String getTriggerLeftLabel();    // LT / L2 / ZL
    protected abstract String getTriggerRightLabel();   // RT / R2 / ZR
    protected abstract String getStartLabel();          // Menu / Options / +
    protected abstract String getSelectLabel();         // View / Create / -
    protected abstract String getSystemLabel();         // Xbox / PS / Home
    protected abstract String getExtraLabel();          // Share / Touchpad / Capture

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initViews();
        setupControllerTheme();
        inputManager = (InputManager) getSystemService(INPUT_SERVICE);
        inputManager.registerInputDeviceListener(this, null);
        checkConnectedDevices();
        setupVibrationButtons();
        setupCalibration();
    }

    protected void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        tvControllerName = findViewById(R.id.tvControllerName);
        tvRawData = findViewById(R.id.tvRawData);
        tvLatency = findViewById(R.id.tvLatency);
        tvPressCount = findViewById(R.id.tvPressCount);
        triggerLT = findViewById(R.id.triggerLT);
        triggerRT = findViewById(R.id.triggerRT);
        tvTriggerLLabel = findViewById(R.id.tvTriggerLLabel);
        tvTriggerRLabel = findViewById(R.id.tvTriggerRLabel);
        stickLeft = findViewById(R.id.stickLeft);
        stickRight = findViewById(R.id.stickRight);
        tvLeftStick = findViewById(R.id.tvLeftStick);
        tvRightStick = findViewById(R.id.tvRightStick);
        btnLB = findViewById(R.id.btnLB);
        btnRB = findViewById(R.id.btnRB);
        btnL3 = findViewById(R.id.btnL3);
        btnR3 = findViewById(R.id.btnR3);
        btnDpadUp = findViewById(R.id.btnDpadUp);
        btnDpadDown = findViewById(R.id.btnDpadDown);
        btnDpadLeft = findViewById(R.id.btnDpadLeft);
        btnDpadRight = findViewById(R.id.btnDpadRight);
        btnStart = findViewById(R.id.btnStart);
        btnSelect = findViewById(R.id.btnSelect);
        btnSystem = findViewById(R.id.btnSystem);
        btnExtra = findViewById(R.id.btnExtra);
        btnFace1 = findViewById(R.id.btnFace1);
        btnFace2 = findViewById(R.id.btnFace2);
        btnFace3 = findViewById(R.id.btnFace3);
        btnFace4 = findViewById(R.id.btnFace4);
        btnVibShort = findViewById(R.id.btnVibShort);
        btnVibMedium = findViewById(R.id.btnVibMedium);
        btnVibLong = findViewById(R.id.btnVibLong);
        btnVibPattern = findViewById(R.id.btnVibPattern);
        seekDeadZone = findViewById(R.id.seekDeadZone);
        tvDeadZoneVal = findViewById(R.id.tvDeadZoneVal);
        tvCalibInfo = findViewById(R.id.tvCalibInfo);
        btnResetCalib = findViewById(R.id.btnResetCalib);
    }

    protected void setupControllerTheme() {
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getControllerTitle());
        int accent = getAccentColor();
        stickLeft.setAccentColor(accent);
        stickRight.setAccentColor(accent);
        triggerLT.setAccentColor(accent);
        triggerRT.setAccentColor(accent);

        String[] labels = getFaceButtonLabels();
        btnFace1.setText(labels[0]);
        btnFace2.setText(labels[1]);
        btnFace3.setText(labels[2]);
        btnFace4.setText(labels[3]);

        // Set controller-specific labels
        btnLB.setText(getShoulderLeftLabel());
        btnRB.setText(getShoulderRightLabel());
        tvTriggerLLabel.setText(getTriggerLeftLabel());
        tvTriggerRLabel.setText(getTriggerRightLabel());
        btnStart.setText(getStartLabel());
        btnSelect.setText(getSelectLabel());
        btnSystem.setText(getSystemLabel());
        btnExtra.setText(getExtraLabel());

        View accentBar = findViewById(R.id.accentBar);
        GradientDrawable barBg = new GradientDrawable();
        barBg.setCornerRadius(6f);
        barBg.setColor(accent);
        accentBar.setBackground(barBg);
    }

    protected void setupCalibration() {
        seekDeadZone.setMax(30); // 0-30% = 0.00 - 0.30
        seekDeadZone.setProgress((int) (deadZone * 100));
        tvDeadZoneVal.setText(String.format(Locale.US, "%.0f%%", deadZone * 100));

        seekDeadZone.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean user) {
                deadZone = progress / 100f;
                tvDeadZoneVal.setText(String.format(Locale.US, "%.0f%%", deadZone * 100));
                stickLeft.setDeadZone(deadZone);
                stickRight.setDeadZone(deadZone);
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });

        stickLeft.setDeadZone(deadZone);
        stickRight.setDeadZone(deadZone);

        btnResetCalib.setOnClickListener(v -> resetCalibration());
    }

    protected void resetCalibration() {
        lxMin = lxMax = lyMin = lyMax = 0;
        rxMin = rxMax = ryMin = ryMax = 0;
        ltMin = ltMax = rtMin = rtMax = 0;
        tvCalibInfo.setText("Калибровка сброшена. Двигай стики и жми триггеры.");
    }

    protected void updateCalibration(float lx, float ly, float rx, float ry, float lt, float rt) {
        lxMin = Math.min(lxMin, lx); lxMax = Math.max(lxMax, lx);
        lyMin = Math.min(lyMin, ly); lyMax = Math.max(lyMax, ly);
        rxMin = Math.min(rxMin, rx); rxMax = Math.max(rxMax, rx);
        ryMin = Math.min(ryMin, ry); ryMax = Math.max(ryMax, ry);
        ltMin = Math.min(ltMin, lt); ltMax = Math.max(ltMax, lt);
        rtMin = Math.min(rtMin, rt); rtMax = Math.max(rtMax, rt);

        tvCalibInfo.setText(String.format(Locale.US,
                "L-Stick X:[%.2f..%.2f] Y:[%.2f..%.2f]\n" +
                "R-Stick X:[%.2f..%.2f] Y:[%.2f..%.2f]\n" +
                "%s:[%.2f..%.2f]  %s:[%.2f..%.2f]",
                lxMin, lxMax, lyMin, lyMax,
                rxMin, rxMax, ryMin, ryMax,
                getTriggerLeftLabel(), ltMin, ltMax,
                getTriggerRightLabel(), rtMin, rtMax));
    }

    protected void setupVibrationButtons() {
        btnVibShort.setOnClickListener(v -> vibrateGamepad(100));
        btnVibMedium.setOnClickListener(v -> vibrateGamepad(300));
        btnVibLong.setOnClickListener(v -> vibrateGamepad(800));
        btnVibPattern.setOnClickListener(v -> vibrateGamepadPattern());
        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    // --- Gamepad vibration (not system vibrator!) ---

    protected void vibrateGamepad(long ms) {
        Vibrator vibrator = getGamepadVibrator();
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(ms);
            }
        } else {
            // Fallback to system vibrator
            Vibrator sysVib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (sysVib != null && sysVib.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sysVib.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    sysVib.vibrate(ms);
                }
            }
        }
    }

    protected void vibrateGamepadPattern() {
        long[] pattern = {0, 100, 80, 100, 80, 300};
        Vibrator vibrator = getGamepadVibrator();
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
            } else {
                vibrator.vibrate(pattern, -1);
            }
        } else {
            Vibrator sysVib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (sysVib != null && sysVib.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sysVib.vibrate(VibrationEffect.createWaveform(pattern, -1));
                } else {
                    sysVib.vibrate(pattern, -1);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    protected Vibrator getGamepadVibrator() {
        if (connectedDevice == null) return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.os.VibratorManager vm = connectedDevice.getVibratorManager();
            int[] ids = vm.getVibratorIds();
            if (ids.length > 0) return vm.getVibrator(ids[0]);
        }
        return connectedDevice.getVibrator();
    }

    // --- Analog input ---

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        InputDevice device = event.getDevice();
        if (device == null || !isGamepad(device)) return super.onGenericMotionEvent(event);

        long now = System.nanoTime();
        if (lastInputTime > 0) {
            long delta = (now - lastInputTime) / 1_000_000;
            tvLatency.setText(delta + " ms");
        }
        lastInputTime = now;

        // Left Stick — standard AXIS_X / AXIS_Y
        float lx = event.getAxisValue(MotionEvent.AXIS_X);
        float ly = event.getAxisValue(MotionEvent.AXIS_Y);

        // Right Stick — try AXIS_Z/AXIS_RZ first, then AXIS_RX/AXIS_RY
        float rx = event.getAxisValue(MotionEvent.AXIS_Z);
        float ry = event.getAxisValue(MotionEvent.AXIS_RZ);
        if (rx == 0f && ry == 0f) {
            rx = event.getAxisValue(MotionEvent.AXIS_RX);
            ry = event.getAxisValue(MotionEvent.AXIS_RY);
        }

        // Apply dead zone
        lx = applyDeadZone(lx);
        ly = applyDeadZone(ly);
        rx = applyDeadZone(rx);
        ry = applyDeadZone(ry);

        stickLeft.setPosition(lx, ly);
        stickRight.setPosition(rx, ry);
        tvLeftStick.setText(String.format(Locale.US, "X:%.2f Y:%.2f", lx, ly));
        tvRightStick.setText(String.format(Locale.US, "X:%.2f Y:%.2f", rx, ry));

        // Triggers — try LTRIGGER/RTRIGGER, then BRAKE/GAS, then AXIS_RX (DualSense quirk)
        float lt = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);
        float rt = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
        if (lt == 0f) lt = event.getAxisValue(MotionEvent.AXIS_BRAKE);
        if (rt == 0f) rt = event.getAxisValue(MotionEvent.AXIS_GAS);

        triggerLT.setValue(lt);
        triggerRT.setValue(rt);

        // D-Pad via HAT
        float hatX = event.getAxisValue(MotionEvent.AXIS_HAT_X);
        float hatY = event.getAxisValue(MotionEvent.AXIS_HAT_Y);
        setButtonState(btnDpadLeft, hatX < -0.5f);
        setButtonState(btnDpadRight, hatX > 0.5f);
        setButtonState(btnDpadUp, hatY < -0.5f);
        setButtonState(btnDpadDown, hatY > 0.5f);

        // Calibration
        updateCalibration(lx, ly, rx, ry, lt, rt);

        // Raw data
        updateRawData(event, lx, ly, rx, ry, lt, rt, hatX, hatY);

        return true;
    }

    protected float applyDeadZone(float value) {
        if (Math.abs(value) < deadZone) return 0f;
        float sign = value > 0 ? 1f : -1f;
        return sign * (Math.abs(value) - deadZone) / (1f - deadZone);
    }

    // --- Button input ---

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        InputDevice device = event.getDevice();
        if (device != null && isGamepad(device)) {
            onGamepadConnected(device);
            totalPresses++;
            tvPressCount.setText(String.valueOf(totalPresses));
            buttonPressTime.put(keyCode, System.currentTimeMillis());
            setButtonForKeyCode(keyCode, true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        InputDevice device = event.getDevice();
        if (device != null && isGamepad(device)) {
            setButtonForKeyCode(keyCode, false);
            Long pressTime = buttonPressTime.get(keyCode);
            if (pressTime != null) {
                long held = System.currentTimeMillis() - pressTime;
                tvRawData.setText(String.format(Locale.US,
                        "Button: %s\nKeyCode: %d | Held: %d ms",
                        KeyEvent.keyCodeToString(keyCode), keyCode, held));
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    protected void setButtonForKeyCode(int keyCode, boolean pressed) {
        int[] colors = getFaceButtonColors();
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_A:
                setFaceButtonState(btnFace1, pressed, colors[0]); break;
            case KeyEvent.KEYCODE_BUTTON_B:
                setFaceButtonState(btnFace2, pressed, colors[1]); break;
            case KeyEvent.KEYCODE_BUTTON_X:
                setFaceButtonState(btnFace3, pressed, colors[2]); break;
            case KeyEvent.KEYCODE_BUTTON_Y:
                setFaceButtonState(btnFace4, pressed, colors[3]); break;
            case KeyEvent.KEYCODE_BUTTON_L1:
                setButtonState(btnLB, pressed); break;
            case KeyEvent.KEYCODE_BUTTON_R1:
                setButtonState(btnRB, pressed); break;
            case KeyEvent.KEYCODE_BUTTON_L2:
                setButtonState(btnLB, pressed); break;
            case KeyEvent.KEYCODE_BUTTON_R2:
                setButtonState(btnRB, pressed); break;
            case KeyEvent.KEYCODE_BUTTON_THUMBL:
                setButtonState(btnL3, pressed); break;
            case KeyEvent.KEYCODE_BUTTON_THUMBR:
                setButtonState(btnR3, pressed); break;
            case KeyEvent.KEYCODE_BUTTON_START:
                setButtonState(btnStart, pressed); break;
            case KeyEvent.KEYCODE_BUTTON_SELECT:
                setButtonState(btnSelect, pressed); break;
            case KeyEvent.KEYCODE_BUTTON_MODE:
                setButtonState(btnSystem, pressed); break;
            case KeyEvent.KEYCODE_MEDIA_RECORD:
            case KeyEvent.KEYCODE_BUTTON_16:
            case KeyEvent.KEYCODE_BUTTON_15:
            case KeyEvent.KEYCODE_BUTTON_14:
            case KeyEvent.KEYCODE_BUTTON_13:
                setButtonState(btnExtra, pressed); break;
            case KeyEvent.KEYCODE_DPAD_UP:
                setButtonState(btnDpadUp, pressed); break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                setButtonState(btnDpadDown, pressed); break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                setButtonState(btnDpadLeft, pressed); break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                setButtonState(btnDpadRight, pressed); break;
        }
    }

    protected void setButtonState(TextView btn, boolean pressed) {
        if (btn == null) return;
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(28f);
        bg.setColor(pressed ? getAccentColor() : Color.parseColor("#2A2A45"));
        btn.setBackground(bg);
        btn.setScaleX(pressed ? 1.1f : 1f);
        btn.setScaleY(pressed ? 1.1f : 1f);
    }

    protected void setFaceButtonState(TextView btn, boolean pressed, int activeColor) {
        if (btn == null) return;
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        if (pressed) {
            bg.setColor(activeColor);
            bg.setStroke(2, activeColor);
        } else {
            bg.setColor(Color.parseColor("#2A2A45"));
            bg.setStroke(2, Color.parseColor("#3A3A55"));
        }
        btn.setBackground(bg);
        btn.setScaleX(pressed ? 1.15f : 1f);
        btn.setScaleY(pressed ? 1.15f : 1f);
    }

    protected void updateRawData(MotionEvent event, float lx, float ly, float rx, float ry,
                                  float lt, float rt, float hatX, float hatY) {
        // Show ALL axes for debugging
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US, "L-Stick: X=%.3f Y=%.3f\n", lx, ly));
        sb.append(String.format(Locale.US, "R-Stick: X=%.3f Y=%.3f\n", rx, ry));
        sb.append(String.format(Locale.US, "%s=%.3f  %s=%.3f\n",
                getTriggerLeftLabel(), lt, getTriggerRightLabel(), rt));
        sb.append(String.format(Locale.US, "D-Pad: X=%.1f Y=%.1f\n", hatX, hatY));

        // Extra axes for debugging
        float axRx = event.getAxisValue(MotionEvent.AXIS_RX);
        float axRy = event.getAxisValue(MotionEvent.AXIS_RY);
        float axBrake = event.getAxisValue(MotionEvent.AXIS_BRAKE);
        float axGas = event.getAxisValue(MotionEvent.AXIS_GAS);
        if (axRx != 0 || axRy != 0) {
            sb.append(String.format(Locale.US, "RX=%.3f RY=%.3f\n", axRx, axRy));
        }
        if (axBrake != 0 || axGas != 0) {
            sb.append(String.format(Locale.US, "Brake=%.3f Gas=%.3f", axBrake, axGas));
        }

        tvRawData.setText(sb.toString());
    }

    // --- Device detection ---

    protected boolean isGamepad(InputDevice device) {
        int src = device.getSources();
        return (src & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD
                || (src & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK;
    }

    protected void checkConnectedDevices() {
        for (int id : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(id);
            if (device != null && isGamepad(device)) {
                onGamepadConnected(device);
                return;
            }
        }
    }

    protected void onGamepadConnected(InputDevice device) {
        connectedDevice = device;
        tvStatus.setText("✅ Подключён!");
        tvStatus.setTextColor(getAccentColor());
        String info = device.getName();
        // Show vibrator info
        Vibrator gv = getGamepadVibrator();
        if (gv != null && gv.hasVibrator()) {
            info += " | 🔊 Вибро ✓";
        } else {
            info += " | 🔇 Вибро ✗";
        }
        tvControllerName.setText(info);
    }

    @Override
    public void onInputDeviceAdded(int deviceId) {
        InputDevice device = InputDevice.getDevice(deviceId);
        if (device != null && isGamepad(device)) onGamepadConnected(device);
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
        connectedDevice = null;
        tvStatus.setText("❌ Отключён");
        tvStatus.setTextColor(Color.parseColor("#E74856"));
        tvControllerName.setText("");
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {
        InputDevice device = InputDevice.getDevice(deviceId);
        if (device != null && isGamepad(device)) onGamepadConnected(device);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (inputManager != null) inputManager.unregisterInputDeviceListener(this);
    }
}
