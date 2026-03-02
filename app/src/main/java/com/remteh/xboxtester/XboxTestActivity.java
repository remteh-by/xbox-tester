package com.remteh.xboxtester;

import android.graphics.Color;

public class XboxTestActivity extends BaseTestActivity {
    @Override protected int getAccentColor() { return Color.parseColor("#107C10"); }
    @Override protected String getControllerTitle() { return "🟢  XBOX SERIES"; }
    @Override protected String[] getFaceButtonLabels() { return new String[]{"A", "B", "X", "Y"}; }
    @Override protected int[] getFaceButtonColors() {
        return new int[]{ 0xFF1DB954, 0xFFE74856, 0xFF0078D7, 0xFFFFB900 };
    }
    @Override protected String getShoulderLeftLabel() { return "LB"; }
    @Override protected String getShoulderRightLabel() { return "RB"; }
    @Override protected String getTriggerLeftLabel() { return "LT"; }
    @Override protected String getTriggerRightLabel() { return "RT"; }
    @Override protected String getStartLabel() { return "MENU ≡"; }
    @Override protected String getSelectLabel() { return "VIEW ⧉"; }
    @Override protected String getSystemLabel() { return "XBOX ⓧ"; }
    @Override protected String getExtraLabel() { return "SHARE ⬆"; }
}
