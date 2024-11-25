package com.mmjang.ankihelper.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.database.MigrationUtil;
import com.mmjang.ankihelper.ui.floating.UserService;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.DarkModeUtils;
import com.mmjang.ankihelper.util.ScreenUtils;

import java.io.File;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 200; // 启动画面持续时间
    private static final int REQUEST_CODE_ANKI = 0;
    private static final int REQUEST_CODE_STORAGE = 1;
    AnkiDroidHelper mAnkiDroid;
    Settings settings;

    private LinearLayout llSplash;
//    private ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = Settings.getInstance(MyApplication.getContext());
        DarkModeUtils.initDarkMode(SplashScreenActivity.this);
        ColorThemeUtils.initColorTheme(SplashScreenActivity.this);
        super.onCreate(savedInstanceState);

        UserService userService = MyApplication.getShizukuService();
        userService.addListeners();
        userService.connectShizuku();

        ScreenUtils.hideStatusBar(SplashScreenActivity.this);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
//        ivLogo = findViewById(R.id.imageViewLogo);
//        ivLogo.setImageResource(R.drawable.sea_turtle);
//        ivLogo.setMaxWidth(600);
//        ivLogo.setMaxHeight(600);

        llSplash = (LinearLayout) findViewById(R.id.ll_splash);
        if (getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            llSplash.setOrientation(LinearLayout.HORIZONTAL);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 加载数据...
                initStoragePermission();
                if(checkAndRequestPermissions()) {
                    Intent intent = new Intent(SplashScreenActivity.this, LauncherActivity.class);
                    startActivity(intent);
                    finish();
                    handler.removeCallbacks(this);
                } else {
                    handler.postDelayed(this, SPLASH_DURATION);
                }
            }
        }, SPLASH_DURATION);
    }

    private boolean checkAndRequestPermissions() {
        if (mAnkiDroid == null) {
            mAnkiDroid = new AnkiDroidHelper(this);
        }
        if (!AnkiDroidHelper.isApiAvailable(MyApplication.getContext())) {
            Toast.makeText(this, R.string.api_not_available_message, Toast.LENGTH_LONG).show();
            return true;
        }

        if (mAnkiDroid.shouldRequestPermission()) {
            mAnkiDroid.requestPermission(this, REQUEST_CODE_ANKI);
            return false;
        } else {
            initStoragePermission();
            return true;
        }
    }

    private void initStoragePermission() {
        int result = ContextCompat.checkSelfPermission(SplashScreenActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(SplashScreenActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(SplashScreenActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
            }
        } else {
            ensureExternalDbDirectoryAndMigrate();
        }
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
                new AlertDialog.Builder(SplashScreenActivity.this)
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

//        // copy tesseract folder from assets to Constant.EXTERNAL_STORAGE_DIRECTORY
//        File tessFolder = StorageUtils.getIndividualTesseractDirectory();
//        if (!tessFolder.exists()) {
//            StorageUtils.copyFileFromAssetsToAnkihelper(MyApplication.getContext(), Constant.EXTERNAL_STORAGE_TESSERACT_SUBDIRECTORY);
//        }

        if (!settings.getOldDataMigrated() && MigrationUtil.needMigration()) {
            Toast.makeText(this, "正在迁移旧版数据请稍等...", Toast.LENGTH_LONG).show();
            MigrationUtil.migrate();
            Toast.makeText(this, "旧版数据迁移完成！", Toast.LENGTH_SHORT).show();
            settings.setOldDataMigrated(true);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}