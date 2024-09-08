package com.mmjang.ankihelper.ui.mdict;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.dict.Mdict;
import com.mmjang.ankihelper.data.dict.DictionaryRegister;
import com.mmjang.ankihelper.data.dict.IDictionary;
import com.mmjang.ankihelper.data.dict.mdict.MdictInformation;
import com.mmjang.ankihelper.data.dict.mdict.MdictManager;
import com.mmjang.ankihelper.ui.mdict.helper.SimpleItemTouchHelperCallback;
import com.mmjang.ankihelper.util.ActivityUtil;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.CrashManager;
import com.mmjang.ankihelper.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.rosuh.filepicker.config.FilePickerManager;
import me.rosuh.filepicker.filetype.FileType;

public class MdictActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
//    private static final int FILE_CODE = 2;
    private List<IDictionary> mDictionaries;
    private List<Mdict> mMdicts = new ArrayList<>();
    private FloatingActionButton mFloatBtnImportCustomDict;
    private ProgressBar mProgressBarImportMdict;
    private RecyclerView mDictListView;
    private MdictAdapter mMdictAdapter;
    private List<MdictInformation> mDictInfoList;
    //async event
    private static final int DICT_ADDED = 3;
    private static final int DICT_ADD_FAILED = 4;
    private static final int DICTS_REMOVED = 5;

//    static {
//        System.loadLibrary("jni-layer");
//    }

    private int mMaxId = -1;

    private boolean startProgressBarQ = false;
    //async
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DICT_ADDED:
                    Toast.makeText(MdictActivity.this, R.string.str_added, Toast.LENGTH_SHORT).show();
                    reFreshData();
                    setProgressBar(false);
                    break;
                case DICT_ADD_FAILED:
                    Toast.makeText(MdictActivity.this, R.string.load_mdict_error_version, Toast.LENGTH_SHORT).show();
                    setProgressBar(false);
                    break;
                case DICTS_REMOVED:
                    Toast.makeText(MdictActivity.this, R.string.delete_all_mdict, Toast.LENGTH_SHORT).show();
                    reFreshData();
                    setProgressBar(false);
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (Settings.getInstance(this).getPinkThemeQ()) {
//            setTheme(R.style.AppThemePink);
//        }
        ColorThemeUtils.setColorTheme(MdictActivity.this, Constant.StyleBaseTheme.CustomDicTheme);
        super.onCreate(savedInstanceState);
        //初始化 错误日志系统
        CrashManager.getInstance(this);
        ActivityUtil.checkStateForAnkiDroid(this);
        setContentView(R.layout.activity_mdict_manager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //views
        mFloatBtnImportCustomDict = (FloatingActionButton) findViewById(R.id.float_import_mdict);
        mProgressBarImportMdict = (ProgressBar) findViewById(R.id.progress_bar_import_mdict);

        initCustomDictList();
    }

    private void initCustomDictList() {
        mDictInfoList = new ArrayList<>();
        mDictListView = (RecyclerView) findViewById(R.id.mdict_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mDictListView.setLayoutManager(llm);
        mMdictAdapter = new MdictAdapter(MdictActivity.this, mDictInfoList);
        //planList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mDictListView.setAdapter(mMdictAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mMdictAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mDictListView);
    }

    void reFreshData() {
        MdictManager mm = new MdictManager(MyApplication.getContext(), "");
        List<MdictInformation> newList = mm.getDictInfoList();
        mMdicts.clear();
        mDictInfoList.clear();
        mDictInfoList.addAll(newList);
        mMdictAdapter.notifyDataSetChanged();

        mDictionaries = DictionaryRegister.getDictionaryObjectList();
        for (IDictionary dict : mDictionaries) {
            if (dict instanceof Mdict) {
                mMdicts.add((Mdict) dict);
            }
        }

        mMaxId = -1;
        for (Mdict cd : mMdicts) {
            int id = cd.getId();
            if (id > mMaxId) {
                mMaxId = id;
            }
        }
        updateOrder();

    }

    @Override
    protected void onStart() {
        setProgressBar(true);
        reFreshData();
        setProgressBar(false);
        super.onStart();
        permission();
    }

    private void permission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {

            } else {
                mFloatBtnImportCustomDict.setClickable(false);
                requestPermission(); // Code for permission
            }
        }
        //listener
        mFloatBtnImportCustomDict.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectFile();
                    }
                }
        );

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MdictActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MdictActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        } else {
            ActivityCompat.requestPermissions(MdictActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    mBtnImportMdict.setClickable(true);
                    mFloatBtnImportCustomDict.setClickable(true);
                } else {
                    Log.e("value", "Permission Denied, You cannot load custom dictionaries.");
                }
                break;
        }
    }

    class PlainTextFileType implements FileType {
        @Override
        public int getFileIconResId() {
//            return Settings.getInstance(MyApplication.getContext()).getPinkThemeQ() ?
//                    R.drawable.ic_set_dict_pink : R.drawable.ic_set_dict;
            TypedValue outValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.icon_custom_dict, outValue, true);
            return outValue.resourceId;
        }

        @NonNull
        @Override
        public String getFileType() {
            return "mdx";
        }

        @Override
        public boolean verify(@NonNull String s) {
            return s.endsWith("mdx");
        }
    }

    void selectFile() {
//        FilePickerManager.INSTANCE
//                .from(this)
//                .setTheme(
//                        Settings.getInstance(MyApplication.getContext()).getPinkThemeQ() ?
//                                R.style.AndroidFilePickerThemePink : R.style.AndroidFilePickerTheme)
//                .registerFileType(Arrays.asList(new PlainTextFileType()), true)
//                .forResult(FilePickerManager.REQUEST_CODE);
        FilePickerManager.INSTANCE
                .from(this)
                .setTheme(ColorThemeUtils.getColorTheme(MdictActivity.this, Constant.StyleBaseTheme.AndroidFilePickerTheme))
                .registerFileType(Arrays.asList(new PlainTextFileType()), true)
                .forResult(FilePickerManager.REQUEST_CODE);
    }

//    void selectFile() {
//        Intent i = new Intent(this, CustomDictFilePickerActivity.class);
//        // This works if you defined the intent filter
//        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//
//        // Set these depending on your use case. These are the defaults.
//        Settings settings = Settings.getInstance(MyApplication.getContext());
//        if(!settings.getFilePickerHistoryPath().equals(""))
//            i.putExtra(CustomDictFilePickerActivity.EXTRA_START_PATH, settings.getFilePickerHistoryPath());
//        i.putExtra(CustomDictFilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
//        i.putExtra(CustomDictFilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
//        i.putExtra(CustomDictFilePickerActivity.EXTRA_MODE, CustomDictFilePickerActivity.MODE_FILE);
//        i.putExtra(CustomDictFilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
//        startActivityForResult(i, FILE_CODE);
//    }

    private void clearAllMdict() {
        setProgressBar(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MdictManager mm = new MdictManager(MyApplication.getContext(), "");
                    mm.db.clearMdictDB();
                    reFreshData();
                    Message message = mHandler.obtainMessage();
                    message.what = DICTS_REMOVED;
                    mHandler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    public void clearMdict(int id) {
        setProgressBar(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MdictManager mm = new MdictManager(MyApplication.getContext(), "");
                    mm.clearDictById(id);
                    reFreshData();
                    Message message = mHandler.obtainMessage();
                    message.what = DICTS_REMOVED;
                    mHandler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void updateOrder() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MdictManager mm = new MdictManager(MyApplication.getContext(), "");
                    for(int i=0; i < mDictInfoList.size(); i++) {
                            mm.updateOrder(mDictInfoList.get(i).getId(), i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FilePickerManager.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<String> paths = FilePickerManager.obtainData();
            if (paths.size() > 0) {
                if (paths.size() > 1) ToastUtil.showLong(R.string.toast_import_multi);
                startProgressBarQ = true;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MdictManager mm = new MdictManager(MyApplication.getContext(), "");
                            boolean success = false;
                            int id = mMaxId;
                            for(String path : paths) {
//                                MDictDictionary dict = MDictDictionary.loadDictionary(path);
//                                if(Float.parseFloat(dict.getMdxVersion()) >= 2.0) {
//                                    id += 1;
//                                    success = success | mm.processOneDictionaryFile(id, new File(path));
//                                }

                                id += 1;
                                success = success | mm.processOneDictionaryFile(id, new File(path));
                            }
                            if (success) {
                                Message message = mHandler.obtainMessage();
                                message.what = DICT_ADDED;
                                mHandler.sendMessage(message);
                            } else {
                                Message message = mHandler.obtainMessage();
                                message.what = DICT_ADD_FAILED;
                                mHandler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_mdict_manager_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (startProgressBarQ) {
            setProgressBar(true);
            startProgressBarQ = false;
        }
        reFreshData();
        ActivityUtil.checkStateForAnkiDroid(this);
    }

    private void setProgressBar(boolean b) {
        if (b) {
            mProgressBarImportMdict.setVisibility(View.VISIBLE);
        } else {
            mProgressBarImportMdict.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_all_mdict:
                clearAllMdict();
                break;
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native float getVersion(String argument1);

}
