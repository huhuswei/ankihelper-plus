package com.mmjang.ankihelper.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.balysv.materialripple.MaterialRippleLayout;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.database.ExternalDatabase;
import com.mmjang.ankihelper.data.database.MigrationUtil;
import com.mmjang.ankihelper.data.plan.OutputPlanPOJO;
import com.mmjang.ankihelper.domain.CBWatcherService;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.about.AboutActivity;
import com.mmjang.ankihelper.ui.content.ContentActivity;
import com.mmjang.ankihelper.ui.customdict.CustomDictionaryActivity;
import com.mmjang.ankihelper.ui.floating.FloatingSettingActivity;
import com.mmjang.ankihelper.ui.floating.assist.AssistFloatWindow;
import com.mmjang.ankihelper.ui.intelligence.IntelligenceActivity;
import com.mmjang.ankihelper.ui.mdict.MdictActivity;
import com.mmjang.ankihelper.ui.plan.DefaultPlanDialog;
import com.mmjang.ankihelper.ui.popup.PopupSettingActivity;
import com.mmjang.ankihelper.ui.tango.DictTangoActivity;
import com.mmjang.ankihelper.ui.plan.PlansManagerActivity;
import com.mmjang.ankihelper.ui.stat.StatActivity;
import com.mmjang.ankihelper.ui.translation.CustomTranslationActivity;
import com.mmjang.ankihelper.util.ActivityUtil;
import com.mmjang.ankihelper.util.BuildConfig;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.CrashManager;
import com.mmjang.ankihelper.util.DarkModeUtils;
import com.mmjang.ankihelper.util.StorageUtils;
import com.mmjang.ankihelper.util.SystemUtils;
import com.mmjang.ankihelper.widget.ActivationDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.rosuh.filepicker.bean.FileItemBeanImpl;
import me.rosuh.filepicker.config.AbstractFileFilter;
import me.rosuh.filepicker.config.FilePickerManager;

public class LauncherActivity extends AppCompatActivity {

    AnkiDroidHelper mAnkiDroid;
    Settings settings;
    //views
    SwitchCompat switchMoniteClipboard;
    SwitchCompat switchCancelAfterAdd;
    SwitchCompat switchLeftHandMode;
//    SwitchCompat switchPinkTheme;
    TextView tvColorTheme;
    TextView tvDarkMode;

    TextView tvAnkiDroidDir;
    TextView textViewOpenPlanManager;
    TextView textViewCustomDictionary;
    TextView textViewTangoDict;
    TextView textViewMdict;
    TextView textViewPopupWindowSettings;
    TextView textViewFloatingButton;
    TextView textViewIntelligenceWindowSettings;
    TextView textViewMathEditorWindow;
    TextView textViewAbout;

    TextView textActivation;
    TextView textViewHelp;
//    TextView textViewAddDefaultPlan;
    TextView textViewAddQQGroup;
    TextView textViewRandomQuote;
    TextView textViewCustomTranslation;

    private static final int REQUEST_CODE_ANKI = 0;
    private static final int REQUEST_CODE_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = Settings.getInstance(MyApplication.getContext());
        ColorThemeUtils.initColorTheme(LauncherActivity.this);

//        if(!ActivationCodeUtils.verifyActivationCode(
//            settings.get("REG_USERNAME", ""),
//            SystemUtils.getDeviceID(LauncherActivity.this),
//            settings.get("REG_ACTIVATIONCODE", "")
//        )) {
////            Intent intent = new Intent(LauncherActivity.this, ActivationActivity.class);
////            startActivity(intent);
////            finish();  // 关闭当前Activity
//            ActivationDialog d = new ActivationDialog();
//            d.show(getSupportFragmentManager(), "activation_dialog");
//        }

        super.onCreate(savedInstanceState);
        //初始化 错误日志系统
        CrashManager.getInstance(this);
        checkAndRequestPermissions();
        ActivityUtil.checkStateForAnkiDroid(this);
        setContentView(R.layout.activity_launcher);

        setVersion();
        switchMoniteClipboard = findViewById(R.id.switch_monite_clipboard);
        switchCancelAfterAdd = findViewById(R.id.switch_cancel_after_add);
        switchLeftHandMode = findViewById(R.id.left_hand_mode);
        tvColorTheme = findViewById(R.id.btn_color_theme);
        tvDarkMode = findViewById(R.id.btn_dark_mode);
        tvAnkiDroidDir = findViewById(R.id.btn_ankidroid_dir);
        tvAnkiDroidDir.setText(MyApplication.getContext().getResources().getString(R.string.str_ankidroid_path) + "\n" + settings.getAnkiDroidDir());
        textViewOpenPlanManager = findViewById(R.id.btn_open_plan_manager);
        textViewCustomDictionary = findViewById(R.id.btn_open_custom_dictionary);
        textViewTangoDict = findViewById(R.id.btn_set_tango);
        textViewMdict = findViewById(R.id.btn_set_mdict);
        textViewPopupWindowSettings = findViewById(R.id.btn_set_popupwindow);
        textViewFloatingButton = findViewById(R.id.btn_set_floating);
        textViewIntelligenceWindowSettings = findViewById(R.id.btn_set_intelligence_window);
        textViewMathEditorWindow = findViewById(R.id.btn_matheditor_window);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
        {
            MaterialRippleLayout mrl = findViewById(R.id.mrl_dark_mode);
            mrl.setVisibility(View.GONE);
        }
        //切换 debug release
        if(!BuildConfig.isDebug) {
            textViewAbout = findViewById(R.id.btn_about_and_support);
            textViewAbout.setText(Html.fromHtml("<font color='red'>❤</font>" + getResources().getString(R.string.btn_about_and_support_str)));
            MaterialRippleLayout mrl = (MaterialRippleLayout) textViewAbout.getParent();
            mrl.setVisibility(View.VISIBLE);

            mrl = (MaterialRippleLayout) textViewTangoDict.getParent();
            mrl.setVisibility(View.GONE);

            // 隐藏mdict词典
//            mrl = (MaterialRippleLayout) textViewMdict.getParent();
//            mrl.setVisibility(View.GONE);
        }

        textViewAddQQGroup = findViewById(R.id.btn_qq_group);

        textViewHelp = findViewById(R.id.btn_help);
        //切换 debug release
        if(!BuildConfig.isDebug) {
            MaterialRippleLayout mrl = (MaterialRippleLayout) textViewAddQQGroup.getParent();
            mrl.setVisibility(View.VISIBLE);

            mrl = (MaterialRippleLayout) textViewHelp.getParent();
            mrl.setVisibility(View.VISIBLE);
        }
        textActivation = findViewById(R.id.btn_activate);
        textViewRandomQuote = findViewById(R.id.btn_show_random_content);
        textViewCustomTranslation = findViewById(R.id.btn_set_custom_fanyi);
        switchMoniteClipboard.setChecked(
                settings.getMoniteClipboardQ()
        );

        switchCancelAfterAdd.setChecked(
                settings.getAutoCancelPopupQ()
        );

        switchLeftHandMode.setChecked(
                settings.getLeftHandModeQ()
        );

        switchMoniteClipboard.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    settings.setMoniteClipboardQ(isChecked);
                    if (isChecked) {
                        startCBService();
                    } else {
                        stopCBService();
                    }
                }
        );

        switchLeftHandMode.setOnCheckedChangeListener(
                (buttonView, isChecked) -> settings.setLeftHandModeQ(isChecked)
        );

        switchCancelAfterAdd.setOnCheckedChangeListener(
                (buttonView, isChecked) -> settings.setAutoCancelPopupQ(isChecked)
        );

        tvColorTheme.setOnClickListener(
                v -> ColorThemeUtils.themeSettingDialog(LauncherActivity.this)
        );

        tvDarkMode.setOnClickListener(
                v -> DarkModeUtils.darkModeSettingDialog(LauncherActivity.this)
        );

        tvAnkiDroidDir.setOnClickListener(
                v -> {
                    showDialogWithSelectingPath();
//                    AbstractFileFilter aFilter = new AbstractFileFilter() {
//                        @NotNull
//                        @Override
//                        public ArrayList<FileItemBeanImpl> doFilter(@NotNull final ArrayList<FileItemBeanImpl> arrayList) {
//                            ArrayList<FileItemBeanImpl> fileItemBeans = new ArrayList<>();
//                            for (FileItemBeanImpl fileItemBean : arrayList){
//                                if (fileItemBean.isDir() || fileItemBean.isChecked()){
//                                    fileItemBeans.add(fileItemBean);
//                                }
//                            }
//                            return fileItemBeans;
//                        }
//                    };
//
//                    FilePickerManager.INSTANCE.
//                            from(this).
//                            filter(aFilter).
//                            skipDirWhenSelect(false).
//                            enableSingleChoice().
//                            setTheme(ColorThemeUtils.getColorTheme(LauncherActivity.this, Constant.StyleBaseTheme.AndroidFilePickerTheme)).
//                            forResult(FilePickerManager.REQUEST_CODE);
                }
        );

        textViewOpenPlanManager.setOnClickListener(
                v -> {
//                    // activation
//                    if(!ActivationCodeUtils.verifyActivationCode(
//                            settings.get(Constant.REG_USERNAME, ""),
//                            SystemUtils.getDeviceID(LauncherActivity.this),
//                            settings.get(Constant.REG_ACTIVATIONCODE, "")))
//                        return;

                    if (mAnkiDroid == null) {
                        mAnkiDroid = new AnkiDroidHelper(LauncherActivity.this);
                    }
                    if (!AnkiDroidHelper.isApiAvailable(MyApplication.getContext())) {
                        Toast.makeText(LauncherActivity.this, R.string.api_not_available_message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (mAnkiDroid.shouldRequestPermission()) {
                        mAnkiDroid.requestPermission(LauncherActivity.this, 0);
                        return;
                    }
                    Intent intent = new Intent(LauncherActivity.this, PlansManagerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
        );

        textViewCustomDictionary.setOnClickListener(
                v -> {
                    Intent intent = new Intent(LauncherActivity.this, CustomDictionaryActivity.class);
                    startActivity(intent);
                }
        );

        textViewTangoDict.setOnClickListener(
                v -> {
                    Intent intent = new Intent(LauncherActivity.this, DictTangoActivity.class);
                    startActivity(intent);
                }
        );

        textViewMdict.setOnClickListener(
                v -> {
                    Intent intent = new Intent(LauncherActivity.this, MdictActivity.class);
                    startActivity(intent);
                }
        );
        textViewPopupWindowSettings.setOnClickListener(
                v -> {
                    Intent intent = new Intent(LauncherActivity.this, PopupSettingActivity.class);
                    startActivity(intent);
                }
        );
        textViewFloatingButton.setOnClickListener(
                v -> {
                    Intent intent = new Intent(LauncherActivity.this, FloatingSettingActivity.class);
                    startActivity(intent);
                }
        );
        textViewIntelligenceWindowSettings.setOnClickListener(
                v -> {
                    Intent intent = new Intent(LauncherActivity.this, IntelligenceActivity.class);
                    startActivity(intent);
                }
        );

        textViewMathEditorWindow.setOnClickListener(
                v -> startLatexEditor()
        );

        //切换 debug release
        if(!BuildConfig.isDebug) {
            textViewAbout.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(LauncherActivity.this, AboutActivity.class);
                        startActivity(intent);
                    }
            );
        }

        textViewCustomTranslation.setOnClickListener(
                view -> {
                    Intent intent = new Intent(LauncherActivity.this, CustomTranslationActivity.class);
                    startActivity(intent);
                }
        );

        textActivation.setOnClickListener(v -> {
//            Intent intent = new Intent(LauncherActivity.this, ActivationActivity.class);
//            startActivity(intent);
            ActivationDialog d = new ActivationDialog();
            d.show(getSupportFragmentManager(), "activation_dialog");
        });

        textViewHelp.setOnClickListener(
                v -> {
                    String url = "https://github.com/mmjang/ankihelper/blob/master/README.md";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
        );

        //切换 debug release
        if(!BuildConfig.isDebug) {
            textViewAddQQGroup.setOnClickListener(
                    view -> qqGroupDialog()
            );
        }

        if (settings.getMoniteClipboardQ()) {
            startCBService();
        } else {
            stopCBService();
        }

        if(settings.getFloatBallEnable() && android.provider.Settings.canDrawOverlays(getApplicationContext())) {
            AssistFloatWindow.Companion.getInstance().show();
        } else {
            AssistFloatWindow.Companion.getInstance().hide();
        }

        textViewRandomQuote.setOnClickListener(
                view -> {
                    Intent intent = new Intent(LauncherActivity.this, ContentActivity.class);
                    startActivity(intent);
                }
        );

//        // activation
//        if((new Random().nextInt(5))>3 && !ActivationCodeUtils.verifyActivationCode(
//                settings.get(Constant.REG_USERNAME, ""),
//                SystemUtils.getDeviceID(LauncherActivity.this),
//                settings.get(Constant.REG_ACTIVATIONCODE, ""))) {
//            new ActivationDialog().show(getSupportFragmentManager(), "activation_dialog");
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityUtil.checkStateForAnkiDroid(this);
    }

    public boolean startLatexEditor() {
        PackageManager manager = getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(Constant.LATEX_EDITOR_NAME);
        if (i == null) {
            return false;
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(i);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    private void checkAndRequestPermissions() {
        if (mAnkiDroid == null) {
            mAnkiDroid = new AnkiDroidHelper(this);
        }
        if (!AnkiDroidHelper.isApiAvailable(MyApplication.getContext())) {
            Toast.makeText(this, R.string.api_not_available_message, Toast.LENGTH_LONG).show();
        }

        if (mAnkiDroid.shouldRequestPermission()) {
            mAnkiDroid.requestPermission(this, REQUEST_CODE_ANKI);
        } else {
            initStoragePermission();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_about_menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//                case R.id.menu_item_book_shelf:
//                    Intent intent = new Intent(this, BookshelfActivity.class);
//                    startActivity(intent);
//                    break;
            case R.id.menu_item_stat:
                Intent intent2 = new Intent(this, StatActivity.class);
                startActivity(intent2);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length == 0) {
            return;
        }

        if (requestCode == REQUEST_CODE_ANKI) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initStoragePermission();
            } else {
                //Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(LauncherActivity.this)
                        .setMessage(R.string.permission_denied)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                openSettingsPage();
                            }
                        }).show();
            }
        }

        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ensureExternalDbDirectoryAndMigrate();
            } else {
                Toast.makeText(this, "storage permission denied, go to the settings and grant it manually!", Toast.LENGTH_SHORT).show();
            }
//            askIfAddDefaultPlan();
        }
    }

    private void ensureExternalDbDirectoryAndMigrate() {
        File f = new File(Environment.getExternalStorageDirectory(), Constant.EXTERNAL_STORAGE_DIRECTORY);
        if (!f.exists()) {
            f.mkdirs();
        }
        //the content folder
        File f2 = new File(f, Constant.EXTERNAL_STORAGE_CONTENT_SUBDIRECTORY);
        if (!f2.exists()) {
            f2.mkdir();
        }

        // copy tesseract folder from assets to Constant.EXTERNAL_STORAGE_DIRECTORY
        File tessFolder = StorageUtils.getIndividualTesseractDirectory();
        if (!tessFolder.exists()) {
            StorageUtils.copyFileFromAssetsToAnkihelper(MyApplication.getContext(), Constant.EXTERNAL_STORAGE_TESSERACT_SUBDIRECTORY);
        }

        File formsFolder = StorageUtils.getFormsDirectory();
        File latestVersion = new File(formsFolder, Constant.MDX_FORMS_LATEST_VERSION);
        if (!formsFolder.exists() || !latestVersion.exists()) {
            StorageUtils.copyFileFromAssetsToAnkihelper(MyApplication.getContext(), Constant.EXTERNAL_STORAGE_FORMS_SUBDIRECTORY);
        }

        if (!settings.getOldDataMigrated() && MigrationUtil.needMigration()) {
            Toast.makeText(this, "正在迁移旧版数据请稍等...", Toast.LENGTH_LONG).show();
            MigrationUtil.migrate();
            Toast.makeText(this, "旧版数据迁移完成！", Toast.LENGTH_SHORT).show();
            settings.setOldDataMigrated(true);
        }
    }

    private void startCBService() {
        Intent intent = new Intent(this, CBWatcherService.class);
        startService(intent);
    }

    private void stopCBService() {
        Intent intent = new Intent(this, CBWatcherService.class);
        stopService(intent);
    }

    void askIfAddDefaultPlan() {
        DefaultPlanDialog dialog = new DefaultPlanDialog(LauncherActivity.this);
        dialog.showSelectionDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Intent intent = new Intent(this, LauncherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void openSettingsPage() {
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    /****************
     *
     * 发起添加群流程。群号：安卓划词助手用户群(871406754) 的 key 为： -1JxtFYckXpYUMpZKRbrMWuceCgM23R7
     * 调用 joinQQGroup(-1JxtFYckXpYUMpZKRbrMWuceCgM23R7) 即可发起手Q客户端申请加群 安卓划词助手用户群(871406754)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    private void qqGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.str_add_qq_group);  // 设置对话框标题

        // 将两个视图添加到对话框
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setMinimumWidth(100);
        layout.setPadding(120,50,120,30);

        // 创建布局参数
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        // 设置按钮间距
        layoutParams.setMargins(0, 0, 0, 20); // 设置右边间距为20像素

        Button tv1group = new Button(this);
        tv1group.setBackgroundResource(R.drawable.backgroup_cursor);
        tv1group.setLayoutParams(layoutParams);
        tv1group.setAllCaps(false);
        tv1group.setText(new SpannableString("Anki划词助手官方群"));

        Button tv2group = new Button(this);
        tv2group.setBackgroundResource(R.drawable.backgroup_cursor);
        tv2group.setLayoutParams(layoutParams);
        tv2group.setAllCaps(false);
        tv2group.setText("Anki划词助手+群");

        layout.addView(tv1group);
        layout.addView(tv2group);


        builder.setView(layout);  // 将自定义布局添加到对话框
        // 创建和显示对话框
        AlertDialog dialog = builder.create();
        tv1group.setOnClickListener(v -> {joinQQGroup("-1JxtFYckXpYUMpZKRbrMWuceCgM23R7"); dialog.dismiss();});
        tv2group.setOnClickListener(v -> {joinQQGroup("Xk8hNFrTWTICq8me0sp9r6oSednwc8BX"); dialog.dismiss();});
        dialog.show();
    }
    public void setVersion() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView versionTextView = findViewById(R.id.textview_version);
            versionTextView.setText(
                    "Ver: " + versionName
            );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initStoragePermission() {
        int result = ContextCompat.checkSelfPermission(LauncherActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(LauncherActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(LauncherActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
            }
        } else {
            ensureExternalDbDirectoryAndMigrate();
        }
    }


    public void showDialogWithSelectingPath() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LauncherActivity.this);
        builder.setTitle(R.string.str_ankidroid_path).
        setMessage(R.string.str_ankidroid_directory_setting);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AbstractFileFilter aFilter = new AbstractFileFilter() {
                    @NotNull
                    @Override
                    public ArrayList<FileItemBeanImpl> doFilter(@NotNull final ArrayList<FileItemBeanImpl> arrayList) {
                        ArrayList<FileItemBeanImpl> fileItemBeans = new ArrayList<>();
                        for (FileItemBeanImpl fileItemBean : arrayList){
                            if (fileItemBean.isDir() || fileItemBean.isChecked()){
                                fileItemBeans.add(fileItemBean);
                            }
                        }
                        return fileItemBeans;
                    }
                };

                FilePickerManager.INSTANCE.
                        from(LauncherActivity.this).
                        filter(aFilter).
                        skipDirWhenSelect(false).
                        enableSingleChoice().
                        setTheme(ColorThemeUtils.getColorTheme(LauncherActivity.this, Constant.StyleBaseTheme.AndroidFilePickerTheme)).
                        forResult(FilePickerManager.REQUEST_CODE);
            }
        });
//        builder.setNegativeButton("取消", null);
        builder.setNeutralButton(R.string.open_ankidroid, null)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
//        builder.show();
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnNeutral = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                btnNeutral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyApplication.getAnkiDroid().startAnkiDroid();
                    }
                });
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        dialog.show();
    }

    private void updatePlans() {
        Map<Long, String> modelMap = mAnkiDroid.getApi().getModelList();
        HashMap<Long, String> oldModelMap = settings.getModelsHashMap();
        List<OutputPlanPOJO> planList = ExternalDatabase.getInstance().getAllPlan();
        for (int index=0; index < planList.size(); index++) {
            String name = oldModelMap.get(planList.get(index).getOutputModelId());
            for(Long id : modelMap.keySet()) {
                if(modelMap.get(id).equals(name)) {
//                    oldModelMap.remove(mPlanList.get(index).getOutputModelId());
                    if(oldModelMap.containsKey(id))
                        oldModelMap.replace(id, name);
                    else
                        oldModelMap.put(id, name);
                    planList.get(index).setOutputModelId(id);
                    ExternalDatabase.getInstance().updatePlan(planList.get(index), planList.get(index).getPlanName());
                    break;
                }
            }
        }
        settings.setModelsHashMap(oldModelMap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FilePickerManager.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<String> paths = FilePickerManager.obtainData();
            if (paths.size() == 1) {
                String mediePath = paths.get(0).endsWith(Constant.ANKIDROID_MEDIA_DIRECTORY) ?
                        paths.get(0) :
                        paths.get(0) + File.separator + Constant.ANKIDROID_MEDIA_DIRECTORY;
                File dir = new File(mediePath);
                if(dir.exists() && dir.isDirectory()) {
                    settings.setAnkiDroidDir(mediePath);
                    tvAnkiDroidDir.setText(MyApplication.getContext().getResources().getString(R.string.str_ankidroid_path) + "\n" + settings.getAnkiDroidDir());
                    updatePlans();
                } else {
                    Toast.makeText(this, R.string.dir_no_exist, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
