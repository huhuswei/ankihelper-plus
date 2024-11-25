package com.mmjang.ankihelper.ui.floating;


import static com.mmjang.ankihelper.util.Constant.ASSIST_SERVICE_INFO_ID;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.service.quicksettings.TileService;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.balysv.materialripple.MaterialRippleLayout;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.floating.assist.AssistFloatWindow;
import com.mmjang.ankihelper.util.ActivityUtil;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.ViewUtil;
import java.util.List;


/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.popup
 * @ClassName: PopupSettingActivity
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/5/19 9:57 PM
 * @UpdateUser: ss
 * @UpdateDate: 2023/12/2 6:57 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class FloatingSettingActivity extends AppCompatActivity {
    private SeekBar seekBarFloatingAlpha;
    private SeekBar seekBarFloatingHovering;

    //    private SeekBar seekBarSidelineWidth;
//    private SeekBar seekBarSidelineHeight;
    private SeekBar seekBarFloatingSize;
    private TextView textViewFloatBallAccessibility;
    private TextView textViewFloatBallOverlays;
    private TextView textViewKillBattery;
    private SwitchCompat switchFloatBall;
    private SwitchCompat switchPinFloating;
    private SwitchCompat switchFloatingAutoSide;
    private SwitchCompat switchSnipSearch;
    private SwitchCompat switchClearSearched;
    private Settings settings;
    private SharedPreferences.OnSharedPreferenceChangeListener callbackSwitchFloatBall;


    int number = 0;
    int alpha = 100;
    int size = ViewUtil.dp2px(60);
    int hoveringTime = 200;
    //    int[] sidelineSize = {ViewUtil.dp2px(30), ViewUtil.dp2px(50)};
    private static final int REQUEST_OVERLAYS_WINDOW = 2;
    private static final int REQUEST_ACCESSIBILITY_SETTINGS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ColorThemeUtils.initColorTheme(FloatingSettingActivity.this);
        super.onCreate(savedInstanceState);

        UserService userService = MyApplication.getShizukuService();
        userService.addListeners();
        userService.connectShizuku();

        ActivityUtil.checkStateForAnkiDroid(this);
        setContentView(R.layout.activity_floating_settings);
        checkAndRequestPermissions();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settings = Settings.getInstance(MyApplication.getContext());
        textViewFloatBallAccessibility = (TextView) findViewById(R.id.tv_open_float_ball_accessibility);
        textViewFloatBallOverlays = (TextView) findViewById(R.id.tv_open_float_ball_overlays);
        textViewKillBattery = (TextView) findViewById(R.id.tv_kill_battery);
        seekBarFloatingAlpha = (SeekBar) findViewById(R.id.sb_floating_alpha);
        seekBarFloatingHovering = (SeekBar) findViewById(R.id.sb_floating_hovering);
        seekBarFloatingSize = (SeekBar) findViewById(R.id.sb_floating_size);
        switchFloatBall = (SwitchCompat) findViewById(R.id.switch_float_ball);
        switchPinFloating = (SwitchCompat) findViewById(R.id.switch_pin_Floating);
        switchFloatingAutoSide = (SwitchCompat) findViewById(R.id.switch_Floating_auto_side);
        switchSnipSearch = (SwitchCompat) findViewById(R.id.switch_floating_snip_search_switch);
        switchClearSearched = (SwitchCompat) findViewById(R.id.switch_clear_searched);
        callbackSwitchFloatBall = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switchFloatBall.setChecked(settings.getFloatBallEnable());
                if(switchFloatBall.isChecked()) {
                    AssistFloatWindow.Companion.getInstance().show();
                    MyApplication.getShizukuService().enabledAccessibilityService();
                } else {
                    AssistFloatWindow.Companion.getInstance().hide();
                    MyApplication.getShizukuService().disabledAccessibilityService();
                }
            }
        };
        //        startQSTileService();
        settings.getSharedPreferences().registerOnSharedPreferenceChangeListener(callbackSwitchFloatBall);

        //设置最大值(设置不了最小值)
        seekBarFloatingAlpha.setMax(100);
        seekBarFloatingHovering.setMax(500);
        seekBarFloatingSize.setMin(ViewUtil.dp2px(20f));
        seekBarFloatingSize.setMax(ViewUtil.dp2px(100f));
        //设置初始值
        Settings settings = Settings.getInstance(getApplicationContext());
        switchFloatBall.setChecked(settings.getFloatBallEnable());
        switchPinFloating.setEnabled(switchFloatBall.isChecked());
        switchPinFloating.setChecked(settings.get(Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, false));
        switchFloatingAutoSide.setEnabled(switchFloatBall.isChecked() && !switchPinFloating.isChecked());
        switchFloatingAutoSide.setChecked(settings.get(Settings.FlOATING_BUTTON_AUTO_SIDE, false));
        seekBarFloatingAlpha.setEnabled(switchFloatBall.isChecked());
        seekBarFloatingSize.setEnabled(switchFloatBall.isChecked());
        seekBarFloatingHovering.setEnabled(switchFloatBall.isChecked());
        switchSnipSearch.setChecked(settings.get(Settings.FLOATING_SNIP_SEARCH_SWITCH, false));
        switchClearSearched.setEnabled(switchFloatBall.isChecked());
        switchClearSearched.setChecked(settings.getClearSearchedEnable());
        alpha = settings.getFloatingButtonAlpha();
        hoveringTime = settings.get(Settings.FLOATING_HOVERING_MILLISECOND, Constant.FLOATING_HOVERING_DEFAULT_TIME_MS);
        size = settings.getFloatingButtonSize();
//        sidelineSize = settings.getSidelineSize();
        number = settings.getPopupFontSize();
        seekBarFloatingAlpha.setProgress(alpha);
        seekBarFloatingHovering.setProgress(hoveringTime);
        seekBarFloatingSize.setProgress(size);


        seekBarFloatingAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alpha = progress;
                settings.setFloatingButtonAlpha(alpha);
                if (settings.getFloatBallEnable()) {
                    AssistFloatWindow.Companion.getInstance().show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarFloatingHovering.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hoveringTime = progress;
                settings.put(Settings.FLOATING_HOVERING_MILLISECOND, hoveringTime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarFloatingSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                size = progress;
                settings.setFloatingButtonSize(size);
                if (settings.getFloatBallEnable())
                    AssistFloatWindow.Companion.getInstance().show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        seekBarSidelineWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                sidelineSize[0] = progress;
//                settings.setSidelineSize(sidelineSize);
//                AssistFloatWindow.Companion.getInstance().show();
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        seekBarSidelineHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                sidelineSize[1] = progress;
//                settings.setSidelineSize(sidelineSize);
//                AssistFloatWindow.Companion.getInstance().show();
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

        textViewFloatBallAccessibility.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (requestAccessibility()) {
                            case REQUEST_SUCCESS_SHIZUKU_SERVICE:
                                Toast.makeText(FloatingSettingActivity.this, "Shizuku连接正常（若悬浮按钮未显示，请检查授权情况）", Toast.LENGTH_LONG).show();
                                break;
                            case REQUEST_SUCCESS_ACCESSIBILITY_SERVICE:
                                Toast.makeText(FloatingSettingActivity.this, "辅助服务已打开", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                }
        );

        textViewFloatBallOverlays.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (requestDrawOverLays()) {
                            Toast.makeText(FloatingSettingActivity.this, "悬浮权限已打开", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );

        textViewKillBattery.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        killBATTERY();
                    }
                }
        );
        switchFloatBall.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        if (isChecked) {
//                            AssistFloatWindow.Companion.getInstance().show();
//                            MyApplication.getShizukuService().enabledAccessibilityService();
//                        } else {
//                            AssistFloatWindow.Companion.getInstance().hide();
//                            MyApplication.getShizukuService().disabledAccessibilityService();
//                        }
                        settings.setFloatBallEnable(isChecked);
//                        switchFloatBall.setChecked(isChecked);
                        switchPinFloating.setEnabled(switchFloatBall.isChecked());
                        switchFloatingAutoSide.setEnabled(switchFloatBall.isChecked() && !switchPinFloating.isChecked());
                        seekBarFloatingAlpha.setEnabled(switchFloatBall.isChecked());
                        seekBarFloatingSize.setEnabled(switchFloatBall.isChecked());
                        seekBarFloatingHovering.setEnabled(switchFloatBall.isChecked());
//                        seekBarSidelineWidth.setEnabled(switchFloatBall.isChecked() && switchPinFloating.isChecked() && switchFloatingAutoSide.isChecked());
//                        seekBarSidelineHeight.setEnabled(switchFloatBall.isChecked() && switchPinFloating.isChecked() && switchFloatingAutoSide.isChecked());
                        switchClearSearched.setEnabled(switchFloatBall.isChecked());
                    }
                }
        );

        switchPinFloating.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.put(Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, isChecked);
                        switchFloatingAutoSide.setEnabled(switchFloatBall.isChecked() && !switchPinFloating.isChecked());
//                        seekBarSidelineWidth.setEnabled(switchFloatBall.isChecked() && switchPinFloating.isChecked() && switchFloatingAutoSide.isChecked());
//                        seekBarSidelineHeight.setEnabled(switchFloatBall.isChecked() && switchPinFloating.isChecked() && switchFloatingAutoSide.isChecked());
                    }
                }
        );
        switchFloatingAutoSide.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.put(Settings.FlOATING_BUTTON_AUTO_SIDE, isChecked);
//                        seekBarSidelineWidth.setEnabled(switchFloatBall.isChecked() && switchPinFloating.isChecked() && switchFloatingAutoSide.isChecked());
//                        seekBarSidelineHeight.setEnabled(switchFloatBall.isChecked() && switchPinFloating.isChecked() && switchFloatingAutoSide.isChecked());
                    }
                }
        );
        switchSnipSearch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.put(Settings.FLOATING_SNIP_SEARCH_SWITCH, isChecked);
                    }
                }
        );
        switchClearSearched.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.setClearSearchedEnable(isChecked);
                    }
                }
        );

        if (getIntent().getAction() == TileService.ACTION_QS_TILE_PREFERENCES) {
            ComponentName componentName = (ComponentName) getIntent().getExtras().get(Intent.EXTRA_COMPONENT_NAME);
            if (componentName != null) {
                try {
                    Object t = Class.forName(componentName.getClassName()).newInstance();
                    if (t instanceof QuickStartTileService)
                        requestAccessibility();

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    private void startQSTileService() {
//        if(!QuickStartTileService.Companion.isStarted()) {
//            Intent i = new Intent(this, QuickStartTileService.class);
//            startService(i);
//        }
//    }

    private void setStatusBarColor() {
        int statusBarColor = 0;
        if (Build.VERSION.SDK_INT >= 21) {
            statusBarColor = getWindow().getStatusBarColor();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(statusBarColor);
        }
    }

    public boolean requestDrawOverLays() {
        if (!android.provider.Settings.canDrawOverlays(getApplicationContext())) {
            Toast.makeText(this, "您还没有打开悬浮窗权限", Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(this)
                    .setMessage(R.string.dialog_ask_permission_overlays)
                    .setPositiveButton(R.string.dialog_btn_go_to_permission_window, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //跳转到相应软件的设置页面
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getApplicationContext().getPackageName()));
                            startActivityForResult(intent, REQUEST_OVERLAYS_WINDOW);
                        }
                    }).show();
            return false;
        } else
            return true;
    }

    private static final int REQUEST_SUCCESS_ACCESSIBILITY_SERVICE = 0;
    private static final int REQUEST_SUCCESS_SHIZUKU_SERVICE = 1;
    private static final int REQUEST_FAILURE= -1;
    public int requestAccessibility() {
        if(MyApplication.getShizukuService().checkShizukuServiceState()) {
            return REQUEST_SUCCESS_SHIZUKU_SERVICE;
        } else {
            AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
            List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
            for (AccessibilityServiceInfo info : serviceInfos) {
                String id = info.getId();
//            Trace.i("floating:" + id);
                if (id.contains(ASSIST_SERVICE_INFO_ID)) {
                    return REQUEST_SUCCESS_ACCESSIBILITY_SERVICE;
                }
            }

            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent, REQUEST_ACCESSIBILITY_SETTINGS);
            return REQUEST_FAILURE;
        }
    }

    @SuppressLint("BatteryLife")
    private void killBATTERY() {
        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(packageName))
            intent.setAction(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        else {
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_OVERLAYS_WINDOW:
                if (!android.provider.Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "悬浮授权失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "悬浮授权成功", Toast.LENGTH_SHORT).show();
//                    Settings.getInstance(this).setFloatBallEnable(true);
//                    switchFloatBall.setChecked(true);
//                    switchClearSearched.setEnabled(switchFloatBall.isChecked());
                }
                break;
            case REQUEST_ACCESSIBILITY_SETTINGS:
                AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
                List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
                for (AccessibilityServiceInfo info : serviceInfos) {
                    String id = info.getId();
                    Trace.i("all -->" + id);
                    if (id.contains(ASSIST_SERVICE_INFO_ID)) {
                        Toast.makeText(this, "辅助服务打开成功", Toast.LENGTH_SHORT).show();
//                        Trace.i("Assis", "hide");
//                        AssistFloatWindow.Companion.getInstance().hide();
//                        if (Settings.getInstance(MyApplication.getContext()).getFloatBallEnable())
//                            AssistFloatWindow.Companion.getInstance().show();
                        if (!MyApplication.getAnkiDroid().isAnkiDroidRunning())
                            MyApplication.getAnkiDroid().startAnkiDroid();
                        return;
                    }
                }
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void checkAndRequestPermissions() {
        if (requestDrawOverLays()) {
            Toast.makeText(FloatingSettingActivity.this, "悬浮权限已打开", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean i = powerManager.isIgnoringBatteryOptimizations(this.getPackageName());
            MaterialRippleLayout mrl = (MaterialRippleLayout) textViewKillBattery.getParent();
            if (i) {
                mrl.setVisibility(View.GONE);
            } else {
                mrl.setVisibility(View.VISIBLE);
            }
        }

        if(settings.getFloatBallEnable() != switchFloatBall.isChecked()) {
            settings.setFloatBallEnable(switchFloatBall.isChecked());
        } else {
            if (settings.getFloatBallEnable()) {
                AssistFloatWindow.Companion.getInstance().show();
                MyApplication.getShizukuService().enabledAccessibilityService();
            } else {
                AssistFloatWindow.Companion.getInstance().hide();
                MyApplication.getShizukuService().disabledAccessibilityService();
            }
        }

        ActivityUtil.checkStateForAnkiDroid(this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        settings.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(callbackSwitchFloatBall);
    }
}