
package com.mmjang.ankihelper.ui.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.Md5FileNameGenerator;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.ichi2.anki.api.NoteInfo;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.database.ExternalDatabase;
import com.mmjang.ankihelper.data.dict.BatchClip;
import com.mmjang.ankihelper.data.dict.BingOxford;
import com.mmjang.ankihelper.data.dict.Cloze;
import com.mmjang.ankihelper.data.dict.Collins;
import com.mmjang.ankihelper.data.dict.CollinsEnEn;
import com.mmjang.ankihelper.data.dict.CustomDictionary;
import com.mmjang.ankihelper.data.dict.Definition;
import com.mmjang.ankihelper.data.dict.DictLanguageType;
import com.mmjang.ankihelper.data.dict.DictTango;
import com.mmjang.ankihelper.data.dict.DictionaryDotCom;
import com.mmjang.ankihelper.data.dict.DictionaryRegister;
import com.mmjang.ankihelper.data.dict.Dub91Sentence;
import com.mmjang.ankihelper.data.dict.EnglishSentenceSet;
import com.mmjang.ankihelper.data.dict.EudicSentence;
import com.mmjang.ankihelper.data.dict.Getyarn;
import com.mmjang.ankihelper.data.dict.Handian;
import com.mmjang.ankihelper.data.dict.IDictionary;
import com.mmjang.ankihelper.data.dict.IdiomDict;
import com.mmjang.ankihelper.data.dict.Mdict;
import com.mmjang.ankihelper.data.dict.Mnemonic;
import com.mmjang.ankihelper.data.dict.Ode2;
import com.mmjang.ankihelper.data.dict.RenRenCiDianSentence;
import com.mmjang.ankihelper.data.dict.UrbanAutoCompleteAdapter;
import com.mmjang.ankihelper.data.dict.VocabCom;
import com.mmjang.ankihelper.data.dict.WebsterLearners;
import com.mmjang.ankihelper.data.history.HistoryUtil;
import com.mmjang.ankihelper.data.model.UserTag;
import com.mmjang.ankihelper.data.plan.OutputPlanPOJO;
import com.mmjang.ankihelper.domain.CBWatcherService;
import com.mmjang.ankihelper.domain.FinishActivityManager;
import com.mmjang.ankihelper.domain.PlayAudioManager;
import com.mmjang.ankihelper.domain.PronounceManager;
import com.mmjang.ankihelper.ui.floating.assist.AssistFloatWindow;
import com.mmjang.ankihelper.ui.plan.ComplexElement;
import com.mmjang.ankihelper.ui.share.SharePopupWindow;
import com.mmjang.ankihelper.ui.tango.OutputLocatorPOJO;
import com.mmjang.ankihelper.ui.translation.TranslateBuilder;
import com.mmjang.ankihelper.util.ActivityUtil;
import com.mmjang.ankihelper.util.DarkModeUtils;
import com.mmjang.ankihelper.util.FileUtils;
import com.mmjang.ankihelper.widget.BigBangLayout;
import com.mmjang.ankihelper.widget.BigBangLayoutWrapper;
import com.mmjang.ankihelper.widget.MathxView;
import com.mmjang.ankihelper.widget.MultiSpinner;
import com.mmjang.ankihelper.widget.button.MLLabel;
import com.mmjang.ankihelper.widget.button.MLLabelButton;
import com.mmjang.ankihelper.widget.NoteEditText;
import com.mmjang.ankihelper.widget.button.PerformEditButton;
import com.mmjang.ankihelper.util.BuildConfig;
import com.mmjang.ankihelper.util.ClipboardUtils;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.CrashManager;
import com.mmjang.ankihelper.util.DialogUtil;
import com.mmjang.ankihelper.util.FieldUtil;
import com.mmjang.ankihelper.util.PerformEdit;
import com.mmjang.ankihelper.util.PunctuationUtil;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.ScreenUtils;
import com.mmjang.ankihelper.util.StringUtil;
import com.mmjang.ankihelper.util.TextSplitter;
import com.mmjang.ankihelper.util.ToastUtil;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.Utils;
import com.mmjang.ankihelper.util.StorageUtils;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;
import com.tonyodev.fetch2core.Func;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.litepal.crud.DataSupport;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.mmjang.ankihelper.util.FieldUtil.getBlankSentence;
import static com.mmjang.ankihelper.util.FieldUtil.getBoldSentence;
import static com.mmjang.ankihelper.util.FieldUtil.getNormalSentence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class PopupActivity extends AppCompatActivity implements BigBangLayoutWrapper.ActionListener, TextToSpeech.OnInitListener {
    List<IDictionary> dictionaryList;
    IDictionary currentDictionary;
    List<OutputPlanPOJO> outputPlanList;
    OutputPlanPOJO currentOutputPlan;
    String tempPlan = "";
    Settings settings;
    String mTextToProcess;
    String mPlanNameFromIntent;
    String mCurrentKeyWord;
    String mNoteEditedByUser = "";
    Set<String> mTagEditedByUser = new HashSet<>();
    //posiblle pre set target word
    String mTargetWord;
    //possible url from dedicated borwser
    String mUrl = "";
    //possible specific note id to update
    Long mUpdateNoteId = 0L;
    Long mPreNoteId = 0L;
    //!!!!!!!!!!!important!!! boolean, if the plan spinner is during init, forbid asyncsearch;
    boolean isDuringPlanSpinnerInit = false;
    //update action   replace/append    append is the default action, to prevent data loss;
    String mUpdateAction;
    //possible bookmark id from fbreader
    String mFbReaderBookmarkId;
    //translation
    String mTranslatedResult = "";
    boolean needTranslation = false;
    boolean isRefreshedBigBang  = false;
    int preTranslationEngineIndex = 0;
    //views
    AutoCompleteTextView acTextView;
    Button btnSearch;
    ImageButton btnSetTangoDict;
    ImageButton btnPronounce;
    Spinner planSpinner;
    Spinner pronounceLanguageSpinner;
    private int pickedSentencePronouceLanguageIndex;
    private AnkiDroidHelper mAnkiDroid;
    private Map<Long, String> deckList;
    private long currentDeckId;
    Spinner deckSpinner;

    //RecyclerView recyclerViewDefinitionList;
    CardView mCvPopupToolbar;
    ImageButton mBtnEditNote;
    ImageButton mBtnEditTag;
    ImageButton mBtnTranslation;
    ImageButton mBtnFooterRotateLeft;
    ImageButton mBtnFooterRotateRight;
    ImageButton mBtnFooterScrollTop;
    ImageButton mBtnFooterScrollBottom;
    ImageButton mBtnFooterShare;
    ImageButton mBtnFooterSearch;
    ImageButton mBtnFooterCopy;
    ProgressBar progressBar;
    ProgressBar mMediaProgress;

    CardView mCardViewTranslation;
    EditText mEditTextTranslation;

    //SurfaceView
    SurfaceView mSurfaceView;
    SurfaceHolder surfaceHolder;

    //fab
    //FloatingActionButton mFab;
    ScrollView scrollView;
    //plan b
    LinearLayout viewDefinitionList;
    List<Definition> mDefinitionList;

    //media
//    MediaPlayer mAudioMediaPlayer;
    MediaPlayer mVideoMediaPlayer;

    //tts
    TextToSpeech mTts;

    HashMap<String, String> recordsMap;
    int mTtsStatus = TextToSpeech.STOPPED;
    boolean toLoadTTS = false;

    boolean hasPlusItemBeenClicked = false;
    //save thread running.
    Thread mPreThread;

    //cacheAudio
    HttpProxyCacheServer mProxy;

    //downloader
    Fetch fetch;
    FetchListener fetchListener;

    boolean isClicked = false;
    int heightDpChanged = 0;
    int widthDpChanged = 0;
    private static final int NORMAL_HEIGHT = 2280;

    boolean isFetchDownloading = false;
    boolean isRedrawing = false;

    boolean hasInit = false;
//    String selectedTextMdx = "";
    //async event
    private static final int PROCESS_DEFINITION_LIST = 1;
    private static final int ASYNC_SEARCH_FAILED = 2;
    private static final int TRANSLATION_DONE = 3;
    private static final int TRANSLATIOn_FAILED = 4;
    //view tag
    private static final int TAG_NOTE_ID_LONG = 5;

    private static final String MODE_CREATE = "create";
    private static final String MODE_APPEND = "append";
    private static final String MODE_REPLACE = "replace";
    private static final String MODE_NOTEID = "noteid";
    //async
    @SuppressLint("HandlerLeak")
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESS_DEFINITION_LIST:
                    showSearchButton();
//                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    mDefinitionList = (List<Definition>) msg.obj;
                    scrollTo(acTextView);
                    if(!PopupActivity.this.isDestroyed()) {
                        processDefinitionList(mDefinitionList);
                    }
                    break;
                case ASYNC_SEARCH_FAILED:
                    showSearchButton();
                    ToastUtil.show( (String) msg.obj);
                    break;
                case TRANSLATION_DONE:
                    String result = (String) msg.obj;
                    String[] splitted = result.split("\n");
                    if (splitted.length > 0 && splitted[0].equals("error")) {
                        ToastUtil.show( result);
                        break;
                    }
                    mEditTextTranslation.setText((result));
                    showTranslateDone();
                    showTranslationCardView(true);
                    break;
                default:
                    showTranslateNormal();
                    ToastUtil.show( (String) msg.obj);
                    break;
            }
        }
    };
    private BigBangLayout bigBangLayout;
    private BigBangLayoutWrapper bigBangLayoutWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ColorThemeUtils.setColorTheme(PopupActivity.this, Constant.StyleBaseTheme.Transparent);
        super.onCreate(savedInstanceState);
        settings = Settings.getInstance(MyApplication.getContext());
        if(!settings.get(Settings.POPUP_DISPLAY_STATE, false))
            settings.put(Settings.POPUP_DISPLAY_STATE, true);
        FinishActivityManager.getManager().addActivity(this);
//        StorageUtils.generateCachePath();
        //初始化 错误日志系统
        CrashManager.getInstance(this);
        ActivityUtil.checkStateForAnkiDroid(PopupActivity.this);
        setStatusBarColor();
        setContentView(R.layout.activity_popup);
        initAnkiApi();
//        getActionBar().hide();
        //set animation
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        //
        assignViews();
        initBigBangLayout();
        loadData(); //dictionaryList;
//        populatePlanSpinner();
        setEventListener();
        initVisible();
        initFetch();

        if (settings.getMoniteClipboardQ()) {
            startCBService();
        } else {
            stopCBService();
        }

//        handleIntent();
//        handleIntent();
        ToastUtil.show(Constant.NoteMode.values()[settings.get(Settings.NOTE_MODE, 0)].getNameResId(),
                Gravity.TOP);
//        getIntent().putExtra(Constant.INTENT_ANKIHELPER_ACTION, false);
//        processTextFromFxService();
        //async invoke droid
        asyncInvokeDroid();

        //tts init
        //初始化语音。这是一个异步操作。初始化完成后调用oninitListener(第二个参数)。
        if(mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        mTts = new TextToSpeech(getApplicationContext(), this);
        recordsMap = new HashMap<>();
        //cache
        mProxy = new HttpProxyCacheServer.Builder(PopupActivity.this).maxCacheSize(Constant.DEFAULT_MAX_SIZE).build();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        String process = intent.getStringExtra(Intent.EXTRA_TEXT);
        boolean isAction = intent.getBooleanExtra(Constant.INTENT_ANKIHELPER_ACTION, false);
        if(hasInit && !TextUtils.isEmpty(process) &&
                (process.equals(Constant.FLOATING_USE_CLIPBOARD_CONTENT_FLAG) ||
                        process.equals(Constant.USE_CLIPBOARD_CONTENT_FLAG) ||
                        process.equals(Constant.USE_FX_SERVICE_CB_FLAG) ||
                        isAction)) {
            handleIntent();
            processTextFromFxService();
        }
//            getIntent().putExtra(Constant.INTENT_ANKIHELPER_ACTION, false);
//        }
//        processTextFromFxService();
    }

    private void setTransparent(float alpha) {
        Window window=getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.flags=WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        wl.alpha=alpha;//这句就是设置窗口里崆件的透明度的．０.０全透明．１.０不透明．
        window.setAttributes(wl);
    }

    private boolean isTransparent() {
        Window window=getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        if(wl.alpha > 0.0f) {
            return false;
        }
        return true;
    }

    private void asyncInvokeDroid() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MyApplication.getAnkiDroid().getApi().getDeckList();
                        } catch (Exception e) {
                        }
                    }
                }
        ).start();
    }

    //ed

    private void setTargetWord() {
        setTargetWord(true);
    }
    private void setTargetWord(boolean isAutomaticSearch) {

//        String word = "";
//        if (!TextUtils.isEmpty(mTargetWord)) {
//            word = mTargetWord;
//        } else {
//            word = mTextToProcess;
//        }
//
//        for (BigBangLayout.Line line : bigBangLayout.getLines()) {
//            List<BigBangLayout.Item> items = line.getItems();
//            for (BigBangLayout.Item item : items) {
//                if (item.getText().equals(word)) {
//                    item.setSelected(true);
//                }
//            }
//        }
//        acTextView.setText(word);
//        asyncSearch(word);

        if (!TextUtils.isEmpty(mTargetWord)) {
            for (BigBangLayout.Line line : bigBangLayout.getLines()) {
                List<BigBangLayout.Item> items = line.getItems();
                for (BigBangLayout.Item item : items) {
                    if (item.getText().equals(mTargetWord)) {
                        item.setSelected(true);
                    }
                }
            }
            acTextView.setText(mTargetWord);
            if(isAutomaticSearch) {
                asyncSearch(mTargetWord);
            }
//        } else if (mTextToProcess.matches("[a-zA-Z\\-]*") || mTextToProcess.length() == 1) {
        } else if (RegexUtil.isEnglish(mTextToProcess) ||
                RegexUtil.isRussian(mTextToProcess) ||
                RegexUtil.isSpecialWord(mTextToProcess) ||
                mTextToProcess.length() == 1) {
            //bug
            for (BigBangLayout.Line line : bigBangLayout.getLines()) {
                List<BigBangLayout.Item> items = line.getItems();
                for (BigBangLayout.Item item : items) {
                    if (item.getText().equals(mTextToProcess)) {
                        item.setSelected(true);
                    }
                }
            }
            acTextView.setText(mTextToProcess);
            if(isAutomaticSearch)
                asyncSearch(mTextToProcess);
        }
    }

    private void setStatusBarColor() {
        int statusBarColor = 0;
        if (Build.VERSION.SDK_INT >= 21) {
            statusBarColor = getWindow().getStatusBarColor();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(statusBarColor);
        }
    }

    private void assignViews() {
        acTextView = (AutoCompleteTextView) findViewById(R.id.edit_text_hwd);
        btnSearch = (Button) findViewById(settings.getLeftHandModeQ() ? R.id.btn_search_left : R.id.btn_search);
        progressBar = (ProgressBar) findViewById(settings.getLeftHandModeQ() ? R.id.progress_bar_left : R.id.progress_bar);
        btnSetTangoDict = (ImageButton) findViewById(R.id.btn_set_tango_dict);
        btnPronounce = ((ImageButton) findViewById(R.id.btn_pronounce));
        planSpinner = (Spinner) findViewById(R.id.plan_spinner);
        deckSpinner = (Spinner) findViewById(R.id.deck_spinner);
        pronounceLanguageSpinner = (Spinner) findViewById(R.id.language_spinner);
        //recyclerViewDefinitionList = (RecyclerView) findViewById(R.id.recycler_view_definition_list);
        viewDefinitionList = (LinearLayout) findViewById(R.id.view_definition_list);
        mCvPopupToolbar = (CardView) findViewById(R.id.cv_popup_toolbar);
        mBtnEditNote = (ImageButton) findViewById(R.id.footer_note);
        mBtnEditTag = (ImageButton) findViewById(R.id.footer_tag);
        bigBangLayout = (BigBangLayout) findViewById(R.id.bigbang_layout);
        bigBangLayoutWrapper = (BigBangLayoutWrapper) findViewById(R.id.bigbang_wrapper);
        mCardViewTranslation = (CardView) findViewById(R.id.cardview_translation);
        mBtnTranslation = (ImageButton) findViewById(R.id.footer_translate);
        mEditTextTranslation = (EditText) findViewById(R.id.edittext_translation);
        mSurfaceView = (SurfaceView) findViewById(R.id.sv_surface);
        //mFab = (FloatingActionButton) findViewById(R.id.fab);
        mBtnFooterRotateLeft = (ImageButton) findViewById(R.id.footer_rotate_left);
        mBtnFooterRotateRight = (ImageButton) findViewById(R.id.footer_rotate_right);
        mBtnFooterScrollTop = (ImageButton) findViewById(R.id.footer_scroll_top);
        mBtnFooterScrollBottom = (ImageButton) findViewById(R.id.footer_scroll_bottom);
        mBtnFooterShare = (ImageButton) findViewById(R.id.footer_share);
        mBtnFooterSearch = (ImageButton) findViewById(R.id.footer_search);
        mBtnFooterCopy = (ImageButton) findViewById(R.id.footer_copy);
        mMediaProgress = findViewById(R.id.audio_progress);

    }

    private void initVisible() {
        int visible;

        if(settings.get(Settings.POPUP_SWITCH_SCROLL_BOTTOM, false))
            visible = View.VISIBLE;
        else
            visible = View.GONE;
        mBtnFooterScrollBottom.setVisibility(visible);

        if(settings.getPopupToolbarSearchEnable())
            visible = View.VISIBLE;
        else
            visible = View.GONE;
        mBtnFooterSearch.setVisibility(visible);

        if(settings.getPopupToolbarNoteEnable())
            visible = View.VISIBLE;
        else
            visible = View.GONE;
        mBtnEditNote.setVisibility(visible);

        if(settings.getPopupToolbarTagEnable())
            visible = View.VISIBLE;
        else
            visible = View.GONE;
        mBtnEditTag.setVisibility(visible);

        if(settings.get(Settings.POPUP_SWITCH_COPY, false))
            visible = View.VISIBLE;
        else
            visible = View.GONE;
        mBtnFooterCopy.setVisibility(visible);

//        if(settings.getLeftHandModeQ() || !settings.get(Settings.POPUP_SWITCH_AUTO_SEARCH, true))
            btnSearch.setVisibility(View.VISIBLE);


    }
    private void loadData() {
        dictionaryList = DictionaryRegister.getDictionaryObjectList();
        outputPlanList = planListSelected();//ExternalDatabase.getInstance().getAllPlan();

        //设置一些控件
        deckSpinner.setEnabled(settings.getPopupSpinnerDeckEnable());
//        已被Mdict.class已调用
//        List<OutputLocatorPOJO> newList = OutputLocatorPOJO.readOutputLocatorPOJOListFromSettings();

        //load tag
        boolean loadQ = settings.getSetAsDefaultTag();
        if (loadQ) {
            mTagEditedByUser = Utils.fromStringToTagSet(settings.getDefaulTag());
        }
        //check if outputPlanList is empty
        if (outputPlanList.size() == 0) {
            //ToastUtil.show();
            AlertDialog dialog = Utils.showMessage(this, getResources().getString(R.string.toast_no_available_plan));
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "message", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PopupActivity.this.finish();
                }
            });
        }
    }

    private void populatePlanSpinner() {
        if (outputPlanList.size() == 0) {
            return;
        }
        final String[] planNameArr = new String[outputPlanList.size()];
        for (int i = 0; i < outputPlanList.size(); i++) {
            planNameArr[i] = outputPlanList.get(i).getPlanName();
        }
        ArrayAdapter<String> planSpinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, planNameArr);
        planSpinner.setAdapter(planSpinnerAdapter);
        planSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set plan to last selected plan
        String lastSelectedPlan = settings.getLastSelectedPlan();
        if (lastSelectedPlan.isEmpty()) //first use, set default plan to first one if any
        {
            if (outputPlanList.size() > 0) {
                settings.setLastSelectedPlan(outputPlanList.get(0).getPlanName());
                currentOutputPlan = outputPlanList.get(0);
                currentDictionary = getDictionaryFromOutputPlan(currentOutputPlan);
                if (currentDictionary == null) {
                    String message = String.format(getResources().getString(R.string.str_no_dictionary_in_solution),
                            currentOutputPlan.getPlanName(),
                            currentOutputPlan.getDictionaryKey());
                    Utils.showMessage(PopupActivity.this, message);
                    return;
                } else {
                    setActAdapter(currentDictionary);
                }
            } else {
                return;
            }
        }

        ///////////////if user add intent parameter to control which plan to use
//        mPlanNameFromIntent = getIntent().getStringExtra(Constant.INTENT_ANKIHELPER_PLAN_NAME);
//        if (mPlanNameFromIntent != null && !mPlanNameFromIntent.isEmpty()) {
//            lastSelectedPlan = mPlanNameFromIntent;
//        }
//        mPlanNameFromIntent = intent.getData().getQueryParameter("plan");

        if(!TextUtils.isEmpty(getIntent().getStringExtra(Constant.INTENT_ANKIHELPER_PLAN_NAME))) {
            tempPlan = lastSelectedPlan;
            mPlanNameFromIntent = getIntent().getStringExtra(Constant.INTENT_ANKIHELPER_PLAN_NAME);
            lastSelectedPlan = mPlanNameFromIntent;
        } else if(Intent.ACTION_VIEW.equals(getIntent().getAction()) && getIntent().getData().getQueryParameter("plan") != null) {
            tempPlan = lastSelectedPlan;
            mPlanNameFromIntent = getIntent().getData().getQueryParameter("plan");
            lastSelectedPlan = mPlanNameFromIntent;
        } else if(!TextUtils.isEmpty(tempPlan)) {
            tempPlan = "";
        }
        ///////////////
        int i = 0;
        boolean find = false;
        for (OutputPlanPOJO plan : outputPlanList) {
            if (plan.getPlanName().equals(lastSelectedPlan)) {
                isDuringPlanSpinnerInit = true;
                planSpinner.setSelection(i);
                currentOutputPlan = outputPlanList.get(i);
                currentDictionary = getDictionaryFromOutputPlan(currentOutputPlan);
                if (currentDictionary == null) {
                    String message = String.format(getResources().getString(R.string.str_no_dictionary_in_solution),
                            currentOutputPlan.getPlanName(),
                            currentOutputPlan.getDictionaryKey());
                    Utils.showMessage(PopupActivity.this, message);
                    break;
                } else {
                    setActAdapter(currentDictionary);
                }
                find = true;
                break;
            }
            //if not equal, compare next
            i++;
        }
        if (!find) //if the saved last plan no longer in the plan list, reset to first one
        {
            if (outputPlanList.size() > 0 && TextUtils.isEmpty(tempPlan)) {
                settings.setLastSelectedPlan(outputPlanList.get(0).getPlanName());
                currentOutputPlan = outputPlanList.get(0);
                currentDictionary = getDictionaryFromOutputPlan(currentOutputPlan);
                if (currentDictionary == null) {
                    String message = String.format(getResources().getString(R.string.str_no_dictionary_in_solution),
                            currentOutputPlan.getPlanName(),
                            currentOutputPlan.getDictionaryKey());
                    Utils.showMessage(PopupActivity.this, message);
                    return;
                } else {
                    setActAdapter(currentDictionary);
                }
            }
        } else {
            //if find, then current plan and dictionary must have been set above.
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            scrollView.setOnScrollChangeListener(
//                    new View.OnScrollChangeListener() {
//                        @Override
//                        public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//                            if(i1 > i3){
//                                mFab.hide();
//                            }else{
//                                mFab.show();
//                                //mFab.setAlpha(Constant.FLOAT_ACTION_BUTTON_ALPHA);
//                            }
//                        }
//                    }
//            );
//        }
    }

    private void populateLanguageSpinner() {
        toLoadTTS = false;
        if (mTtsStatus == TextToSpeech.SUCCESS) {
            for (String exportedFieldKeys : currentOutputPlan.getFieldsMap().values()) {
                ComplexElement ce = new ComplexElement(exportedFieldKeys);
                if (mUpdateNoteId > 0 && mUpdateAction.equals(MODE_APPEND)) {
                    exportedFieldKeys = ce.getElementAppending();
                } else {
                    exportedFieldKeys = ce.getElementNormal();
                }

                String[] exportedFieldKeyArr = exportedFieldKeys.split(MultiSpinner.DELIMITER);
                for (String exportedFieldKey : exportedFieldKeyArr) {
                    if (exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_OFFLINE_LINK) ||
                            exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_OFFLINE_VIDEOTAG) ||
                            exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_OFFLINE_SOUNDTAG) ||
                            exportedFieldKey.equals(Constant.DICT_FIELD_KEY_TTS_SOUNDTAG) ||
                            exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_LINK) ||
                            exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_SOUNDTAG) ||
                            exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_TTS_SOUNDTAG)
//                    || exportedFieldKey.equals(Constant.DICT_FIELD_COMPLEX_OFFLINE)
                    ) {
                        toLoadTTS = true;
                        break;
                    }
                }
                if (toLoadTTS == true) {
                    break;
                }
            }
        }

        if(currentDictionary instanceof DictTango) {
            btnSetTangoDict.setVisibility(View.VISIBLE);
            HashMap<String, Boolean> tangoDictCheckerMap;
//            Trace.e("checkMap", String.valueOf(settings.getMdictChckerMap(currentOutputPlan.getPlanName()).size()));
            if(settings.getTangoDictChckerMap(currentOutputPlan.getPlanName()).isEmpty()) {
                tangoDictCheckerMap = new HashMap<>();
                for(OutputLocatorPOJO locator : OutputLocatorPOJO.readOutputLocatorPOJOListFromSettings()) {
//                    tangoDictCheckerMap.put(locator.getDictName(), locator.isChecked());
                    tangoDictCheckerMap.put(locator.getDictName(), false);
                }
                settings.setTangoDictCheckerMap(currentOutputPlan.getPlanName(), tangoDictCheckerMap);
            } else {
                tangoDictCheckerMap = settings.getTangoDictChckerMap(currentOutputPlan.getPlanName());
                List<OutputLocatorPOJO> locatorList = new ArrayList<>();
                OutputLocatorPOJO.readOutputLocatorPOJOListFromSettings().stream().map(a->a.isChecked()?locatorList.add(a):null).collect(Collectors.toList());
                for(OutputLocatorPOJO locator : locatorList) {
                    if(tangoDictCheckerMap.containsKey(locator.getDictName())) {
                        if(locator.isChecked())
                            locator.setChecked(tangoDictCheckerMap.get(locator.getDictName()).booleanValue());
                    } else {
                        tangoDictCheckerMap.put(locator.getDictName(), false);
                        settings.setTangoDictCheckerMap(currentOutputPlan.getPlanName(), tangoDictCheckerMap);
                    }
                }
            }

        } else {
            btnSetTangoDict.setVisibility(View.GONE);
        }
        String[] languages = PronounceManager.getAvailablePronounceLanguage(currentDictionary, toLoadTTS);
        ArrayAdapter<String> languagesSpinnerAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, languages);
        languagesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pronounceLanguageSpinner.setAdapter(languagesSpinnerAdapter);

        int type = getTypeCurrentDictionary();
        int index = settings.getRestorePronounceSpinnerIndex(Settings.ACTION_CLICK, type, currentDictionary.isExistAudioUrl(), toLoadTTS);
        if(index == -1 || index >= languages.length) {
            index = toLoadTTS && currentDictionary.isExistAudioUrl() ? languages.length - 1 : PronounceManager.getSoundInforIndexByList(type);
        }
        pronounceLanguageSpinner.setSelection(index);
        pickedSentencePronouceLanguageIndex = settings.getRestorePronounceSpinnerIndex(Settings.ACTION_LONGCLICK, type, currentDictionary.isExistAudioUrl(), toLoadTTS);
        if(pickedSentencePronouceLanguageIndex == -1 || pickedSentencePronouceLanguageIndex >= languages.length) {
            pickedSentencePronouceLanguageIndex = PronounceManager.getSoundInforIndexByList(type) != -1 ? PronounceManager.getSoundInforIndexByList(type) : 0;
            settings.setRestorePronounceSpinnerIndex(Settings.ACTION_LONGCLICK, pickedSentencePronouceLanguageIndex, type, currentDictionary.isExistAudioUrl(), toLoadTTS);
        }
    }

    //
    private void initAnkiApi() {
        if (mAnkiDroid == null) {
            mAnkiDroid = new AnkiDroidHelper(this);
        }
        if (!AnkiDroidHelper.isApiAvailable(MyApplication.getContext())) {
            ToastUtil.show(R.string.api_not_available_message);
        }

        if (mAnkiDroid.shouldRequestPermission()) {
            mAnkiDroid.requestPermission(this, 0);
        }
    }
    //
    private void loadDecksAndModels() {
//        if (deckListSelected().isEmpty()) {
//            DialogUtil.showSelectingDecksDialog(PopupActivity.this, mAnkiDroid);
//        }
        deckList = deckListSelected();
        if(deckList.isEmpty())
            ToastUtil.show(R.string.toast_failed_to_get_deck_list);
    }

    private LinkedHashMap deckListSelected() {
        HashMap<Long, Boolean> dataCheckerMap = settings.getDeckSelectedLinkedHashMap();
        LinkedHashMap<Long, String> deckList = Utils.hashMap2LinkedHashMap(mAnkiDroid.getApi().getDeckList());
        LinkedHashMap<Long, String> deckSelectedList = new LinkedHashMap<>();

        if(deckList == null)
            return new LinkedHashMap<>();

        List<Long> idList = deckList.keySet().stream().collect(Collectors.toList());
        for (Long id : idList) {
            if (dataCheckerMap.containsKey(id)) {
                if (dataCheckerMap.get(id))
                    deckSelectedList.put(id, deckList.get(id));
            } else
                dataCheckerMap.remove(id);
        }

        if(deckSelectedList.isEmpty()) {
            Long id = deckList.keySet().toArray(new Long[0])[0];
            dataCheckerMap.put(id, true);
            deckSelectedList.put(id, deckList.get(id));
        }

        settings.setDeckSelectedLinkedHashMap(dataCheckerMap);
        return deckSelectedList;
    }

    //
    private void populateDecks() {
        try {
            ArrayAdapter<String> deckSpinnerAdapter = new ArrayAdapter<>(
                    this, R.layout.support_simple_spinner_dropdown_item, Utils.getMapValueArray(deckList));
            deckSpinner.setAdapter(deckSpinnerAdapter);
            if (currentOutputPlan != null) {
                long savedDeckId;
                if (deckSpinner.isEnabled() && settings.getPopupIgnoreDeckScheme() && settings.getLastDeckId() != -1)
                    savedDeckId = settings.getLastDeckId();
                else
                    savedDeckId = currentOutputPlan.getOutputDeckId();
                //int i = 0;
                long[] deckIdList = Utils.getMapKeyArray(deckList);
                //int deckPos = Arrays.asList(deckIdList).indexOf(savedDeckId);
                int deckPos = Utils.getArrayIndex(deckIdList, savedDeckId);
                if (deckPos == -1) {
                    deckPos = 0;
                }

                currentDeckId = deckIdList[deckPos];
                deckSpinner.setSelection(deckPos);
            } else {
                currentDeckId = Utils.getMapKeyArray(deckList)[0];
            }

            deckSpinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            currentDeckId = Utils.getMapKeyArray(deckList)[position];
                            settings.setLastDeckId(currentDeckId);
                            currentOutputPlan.setOutputDeckId(currentDeckId);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    }
            );

            deckSpinner.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Dialog dialog = DialogUtil.showSelectingDecksDialog(PopupActivity.this, mAnkiDroid);
                    if (dialog != null) {
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                loadDecksAndModels();
                                populateDecks();
                            }
                        });
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            Trace.e("deckSpinner initialization", e.getMessage());
            ToastUtil.show("Failed to get deck list");
        }
    }

    private void loadPlans() {
        outputPlanList = planListSelected();
        if(outputPlanList.isEmpty())
            ToastUtil.show(R.string.toast_failed_to_get_plan_list);
    }

    private List<OutputPlanPOJO> planListSelected() {
        HashMap<String, Boolean> dataCheckerMap = settings.getPlanSelectedLinkedHashMap();
        List<OutputPlanPOJO> planList = ExternalDatabase.getInstance().getAllPlan();
        List<OutputPlanPOJO> planSelectedList = new ArrayList<>();

        if(planList.isEmpty())
            return planList;

        for (OutputPlanPOJO plan : planList) {
            if (dataCheckerMap.containsKey(plan.getPlanName())) {
                if (dataCheckerMap.get(plan.getPlanName()))
                    planSelectedList.add(plan);
            } else
                dataCheckerMap.remove(plan.getPlanName());
        }

        if(planSelectedList.isEmpty()) {
            OutputPlanPOJO plan = planList.get(0);
            dataCheckerMap.put(plan.getPlanName(), true);
            planSelectedList.add(plan);
        }

        settings.setPlanSelectedLinkedHashMap(dataCheckerMap);
        return planSelectedList;
    }

    private void initBigBangLayout() {
        bigBangLayout.setShowSymbol(true);
        bigBangLayout.setShowSpace(true);
        bigBangLayout.setShowSection(true);
        bigBangLayout.setItemSpace(4);
        bigBangLayout.setLineSpace(10);
        bigBangLayout.setSymbolTextPadding(10);
        bigBangLayout.setSpaceSymbolTextPadding(0);
        bigBangLayout.setTextPadding(5);
        bigBangLayout.setTextPaddingPort(5);
        bigBangLayoutWrapper.setStickHeader(true);
        bigBangLayoutWrapper.setActionListener(this);

    }

    private void setEventListener() {

        //auto finish
        Button btnCancelBlank = (Button) findViewById(R.id.btn_cancel_blank);
        Button btnCancelBlankAboveCard = (Button) findViewById(R.id.btn_cancel_blank_above_card);
        btnCancelBlank.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if(AssistFloatWindow.Companion.getInstance().isShowing())
//                            AssistFloatWindow.Companion.getInstance().dismiss();
                        finish();
                    }
                }
        );
        btnCancelBlankAboveCard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if(AssistFloatWindow.Companion.getInstance().isShowing())
//                            AssistFloatWindow.Companion.getInstance().dismiss();
                        finish();
                    }
                }
        );

//        scrollView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case (MotionEvent.ACTION_DOWN):
//                        scrollView.requestDisallowInterceptTouchEvent(true);
//                }
//                return false;
//            }
//        });

        planSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //销毁surfaceView
                        mSurfaceView.setVisibility(View.GONE);
                        LinearLayout preLy = (LinearLayout) mSurfaceView.getParent();
                        if (preLy != null)
                            preLy.removeView(mSurfaceView);

                        currentOutputPlan = outputPlanList.get(position);
                        currentDictionary = getDictionaryFromOutputPlan(currentOutputPlan);
                        if (currentDictionary == null) {
                            String message = String.format(getResources().getString(R.string.str_no_dictionary_in_solution),
                                    currentOutputPlan.getPlanName(),
                                    currentOutputPlan.getDictionaryKey());
                            Utils.showMessage(PopupActivity.this, message);
                            return;
                        } else {
                            setActAdapter(currentDictionary);
                        }
                        //memorise last selected plan
                        if (TextUtils.isEmpty(tempPlan))
                            settings.setLastSelectedPlan(currentOutputPlan.getPlanName());
                        populateLanguageSpinner();
                        loadDecksAndModels();
                        populateDecks();

                        String actContent = acTextView.getText().toString();
                        if (isDuringPlanSpinnerInit) {
                            isDuringPlanSpinnerInit = false;
                        } else {
                            if (!actContent.trim().isEmpty()) {
                                if(bigBangLayoutWrapper.getVisibility() == View.VISIBLE)
                                    asyncSearch(actContent);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        planSpinner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog = DialogUtil.showSelectingPlansDialog(PopupActivity.this, ExternalDatabase.getInstance().getAllPlan());
                if (dialog != null) {
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                           loadPlans();
                            populatePlanSpinner();
                        }
                    });
                }
                return true;
            }
        });

        pronounceLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                int type = getTypeCurrentDictionary();
                settings.setLastPronounceLanguage(position);
//                settings.setRestorePronounceSpinnerIndex(position, type, currentDictionary.isExistAudioUrl(), toLoadTTS);
                settings.setRestorePronounceSpinnerIndex(Settings.ACTION_CLICK, position, type, currentDictionary.isExistAudioUrl(), toLoadTTS);
                //例句跟随一起变化
//                pickedSentencePronouceLanguageIndex = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        pronounceLanguageSpinner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int checkedIndex = pickedSentencePronouceLanguageIndex;
                String[] languages = PronounceManager.getAvailablePronounceLanguage(currentDictionary, toLoadTTS);
                boolean[] isCheckedArr = new boolean[languages.length];

                for(int i = 0; i < isCheckedArr.length; i++) {
                    if(i == checkedIndex)
                        isCheckedArr[i] = true;
                    else
                        isCheckedArr[i] = false;
                }

                androidx.appcompat.app.AlertDialog.Builder multiChoiceDialog = new androidx.appcompat.app.AlertDialog.Builder(PopupActivity.this);
                multiChoiceDialog
                        .setTitle(R.string.tv_choose_language_pronounce_picked_sentence);
//                                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
//
//                                });

                multiChoiceDialog.setSingleChoiceItems(languages, checkedIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int type = getTypeCurrentDictionary();
                                settings.setRestorePronounceSpinnerIndex(Settings.ACTION_LONGCLICK, which, type, currentDictionary.isExistAudioUrl(), toLoadTTS);
                                pickedSentencePronouceLanguageIndex = which;
                            }
                        });
                multiChoiceDialog.show();

                return false;
            }
        });

        btnSearch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String word = acTextView.getText().toString();
                        if (!word.isEmpty()) {
                            if(mTextToProcess.isEmpty() || isRefreshedBigBang) {
                                bigBangLayoutWrapper.setVisibility(View.VISIBLE);
                                mTextToProcess = word;
                                populateWordSelectBox();
                                HistoryUtil.savePopupOpen(mTextToProcess);

                                bigBangLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setTargetWord(false);
                                    }
                                });
                            }
                            asyncSearch(word);
                            Utils.hideSoftKeyboard(PopupActivity.this);
//                            scrollView.fullScroll(ScrollView.FOCUS_UP);
                        }
                    }
                }
        );

        btnSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!TextUtils.isEmpty(mTextToProcess))
                    setupEditSentenceDialog();
                return true;
            }
        });

        btnSetTangoDict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    List<OutputLocatorPOJO> locatorList = new ArrayList<>();
                    OutputLocatorPOJO.readOutputLocatorPOJOListFromSettings().stream().map(a->a.isChecked()?locatorList.add(a):null).collect(Collectors.toList());
                    HashMap<String, Boolean> mdictCheckerMap = settings.getTangoDictChckerMap(currentOutputPlan.getPlanName());
                    AlertDialog.Builder multiChoiceDialog = new AlertDialog.Builder(PopupActivity.this);
                    multiChoiceDialog.setTitle("选择词典");

                    String[] dictNameArr = new String[locatorList.size()];
                    boolean[] isCheckedArr = new boolean[locatorList.size()];

                    for(int i=0; i<locatorList.size(); i++) {
                        dictNameArr[i] = String.format("%s %s", locatorList.get(i).getlangName(), locatorList.get(i).getDictName());
                        boolean isChecked = mdictCheckerMap.get(locatorList.get(i).getDictName()).booleanValue();
                        locatorList.get(i).setChecked(isChecked);
                        isCheckedArr[i] = isChecked;
                    }

                    multiChoiceDialog.setMultiChoiceItems(
                            dictNameArr,
                            isCheckedArr,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    OutputLocatorPOJO locator = locatorList.get(which);
                                    locator.setChecked(isChecked);

                                    mdictCheckerMap.replace(locator.getDictName(), Boolean.valueOf(isChecked));
                                    settings.setTangoDictCheckerMap(currentOutputPlan.getPlanName(), mdictCheckerMap);
                                    populateLanguageSpinner();
                                }
                            });
                    multiChoiceDialog.show();
            }
        });

        btnPronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String word = acTextView.getText().toString();
                int lastPronounceLanguageIndex = Settings.getInstance(MyApplication.getContext()).getLastPronounceLanguage();
                PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(lastPronounceLanguageIndex);

                StopPlayingAll();

                mMediaProgress.setVisibility(View.VISIBLE);
                if (infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_TTS)
                    playTts(word);
                else {
                    String finalAudioUrl = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, word, currentDictionary.getAudioUrl());
                    String cacheAudioUrl = getCacheAudioUrl(finalAudioUrl);
                    PlayAudioManager.playPronounceSound(PopupActivity.this, cacheAudioUrl);
                    Trace.i("finalAudioUrl", finalAudioUrl);
                }

                mMediaProgress.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMediaProgress.setVisibility(View.GONE);
                    }
                }, 500);
            }
        });

        btnPronounce.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                String text = StringUtil.stripHtml(mTextToProcess);
                int lastPronounceLanguageIndex = pickedSentencePronouceLanguageIndex;
                PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(lastPronounceLanguageIndex);

                StopPlayingAll();

                mMediaProgress.setVisibility(View.VISIBLE);
                if (infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_TTS)
                    playTts(text, lastPronounceLanguageIndex);
                else {
                    String finalAudioUrl = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, text, currentDictionary.getAudioUrl(), pickedSentencePronouceLanguageIndex);
                    String cacheAudioUrl = getCacheAudioUrl(finalAudioUrl);
                    PlayAudioManager.playPronounceSound(PopupActivity.this, cacheAudioUrl);

                }

                mMediaProgress.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMediaProgress.setVisibility(View.GONE);
                    }
                }, 500);
                return true;
            }
        });

        mBtnEditNote.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setupEditNoteDialog();
                    }
                }
        );

        mBtnEditNote.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        DialogUtil.noteModeSettingDialog(PopupActivity.this);
                        return true;
                    }
                }
        );

        mBtnEditTag.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setupEditTagDialog();
                    }
                }
        );

        acTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        Trace.d("autocomplete", i + "");
                        acTextView.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        btnSearch.callOnClick();
                                        acTextView.clearFocus();
                                    }
                                }
                        );
                    }
                }
        );

        acTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                changePronounceToSpinner(acTextView.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        acTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                if (btnSearch.getVisibility() != View.VISIBLE)
//                    btnSearch.setVisibility(View.VISIBLE);
                return false;
            }
        });

        mBtnTranslation.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEditTextTranslation.setVisibility(View.VISIBLE);
                        int index = settings.getTranslatorCheckedIndex();
                        if (mEditTextTranslation.getText().toString().isEmpty() ||
                                preTranslationEngineIndex != index) {
                            preTranslationEngineIndex = index;
                            asyncTranslate(mTextToProcess);
                        }
                    }
                }
        );

        mBtnTranslation.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        DialogUtil.selectTranslatorSettingDialog(PopupActivity.this);
                        return true;
                    }
                }
        );
        mBtnFooterRotateRight.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int mPlanSize = outputPlanList.size();
                        int currentPos = planSpinner.getSelectedItemPosition();
                        if (mPlanSize > 1) {
                            if (currentPos < mPlanSize - 1) {
                                planSpinner.setSelection(currentPos + 1);
                            } else if (currentPos == mPlanSize - 1) {
                                planSpinner.setSelection(0);
                            }
                            //vibarate(Constant.VIBRATE_DURATION);
                            //scrollView.fullScroll(ScrollView.FOCUS_UP);
                        } else {
                            ToastUtil.show( R.string.str_only_one_plan_cant_switch);
                        }
                    }
                }
        );

        mBtnFooterRotateLeft.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int mPlanSize = outputPlanList.size();
                        int currentPos = planSpinner.getSelectedItemPosition();
                        if (mPlanSize > 1) {
                            if (currentPos > 0) {
                                planSpinner.setSelection(currentPos - 1);
                            } else if (currentPos == 0) {
                                planSpinner.setSelection(mPlanSize - 1);
                            }
                            //    vibarate(Constant.VIBRATE_DURATION);
                            //scrollView.fullScroll(ScrollView.FOCUS_UP);
                        } else {
                            ToastUtil.show( R.string.str_only_one_plan_cant_switch);
                        }
                    }
                }
        );

        mBtnFooterSearch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String word = acTextView.getText().toString();
                        if (!word.isEmpty()) {
                            asyncSearch(word);
                            Utils.hideSoftKeyboard(PopupActivity.this);
//                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    }
                }
        );

        mBtnFooterSearch.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(acTextView.getText().toString().equals("")) {
                            selectAllBigbangSelection();
                        } else {
                            clearBigbangSelection();
                            acTextView.setText("");
                        }
                        return true;
                    }
                }
        );

        mBtnFooterCopy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String word = acTextView.getText().toString();
                            if (word.equals("")) return;

                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("plans string", word);
                            clipboard.setPrimaryClip(clip);
                            ToastUtil.show("复制成功");
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.show("复制失败");
                        }
                    }
                }
        );

        mBtnFooterCopy.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        try {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("plans string", mTextToProcess);
                            clipboard.setPrimaryClip(clip);
                            ToastUtil.show("句子复制成功");
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.show("复制失败");
                        }
                        return true;
                    }
                }
        );

        mBtnFooterScrollTop.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (scrollView.getScrollY() > 10) {
                            scrollView.fullScroll(ScrollView.FOCUS_UP);
                        }
                    }
                }
        );

        mBtnFooterScrollBottom.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }
        );

        mBtnFooterShare.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        String text = acTextView.getText().toString().trim();
                        Trace.i("acTextView", text);
                        if(text != null && !text.isEmpty()) {
                            LinearLayout mLayoutRoot;
                            mLayoutRoot = new LinearLayout(view.getContext());

                            //实例化分享窗口
                            SharePopupWindow spw = new SharePopupWindow(PopupActivity.this, text); ;
                            // 显示窗口
                            spw.showAtLocation(mLayoutRoot, Gravity.BOTTOM, 0, 0);
                        }
                    }
                }
        );

        mBtnFooterShare.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
//                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        String text = mTextToProcess;
                        Trace.i("acTextView", text);
                        if(text != null && !text.isEmpty()) {
                            LinearLayout mLayoutRoot;
                            mLayoutRoot = new LinearLayout(view.getContext());

                            //实例化分享窗口
                            SharePopupWindow spw = new SharePopupWindow(PopupActivity.this, text); ;
                            // 显示窗口
                            spw.showAtLocation(mLayoutRoot, Gravity.BOTTOM, 0, 0);
                        }
                        return true;
                    }
                }
        );
    }

    private IDictionary getDictionaryFromOutputPlan(OutputPlanPOJO outputPlan) {
        String dictionaryName = outputPlan.getDictionaryKey();
        for (IDictionary dict : dictionaryList) {
            if (dict.getDictionaryName().equals(dictionaryName)) {
                return dict;
            }
        }
        return null;
    }

    private void processDefinitionList(List<Definition> definitionList) {
        if (definitionList.isEmpty()) {
            viewDefinitionList.removeAllViewsInLayout();
            viewDefinitionList.setMinimumHeight(viewDefinitionList.getMinimumHeight());
            //dw
//            ToastUtil.show(R.string.definition_not_found);
            showSearchButton();
        } else {
//            DefinitionAdapter defAdapter = new DefinitionAdapter(PopupActivity.this, definitionList, mTextSplitter, currentOutputPlan);
//            LinearLayoutManager llm = new LinearLayoutManager(this);
//            //llm.setAutoMeasureEnabled(true);
//            recyclerViewDefinitionList.setLayoutManager(llm);
//            //recyclerViewDefinitionList.getRecycledViewPool().setMaxRecycledViews(0,0);
//            //recyclerViewDefinitionList.setHasFixedSize(true);
//            //recyclerViewDefinitionList.setNestedScrollingEnabled(false);
//            recyclerViewDefinitionList.setAdapter(defAdapter);
            viewDefinitionList.removeAllViewsInLayout();
//            initVideoMediaoPlayer();
            for (int index = 0; index < definitionList.size(); index++) {
                viewDefinitionList.addView(getCardFromDefinition(definitionList.get(index), index));
            }
//            viewDefinitionList.post(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            View childView = scrollView.getChildAt(0);
//                            if(scrollView.getScrollY() > 10) {
//                                scrollView.fullScroll(ScrollView.FOCUS_UP);
//                            }
//                        }
//                    }
//            );
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if(!hasInit) {
        if(isFromFxService || isFromClipboardByWatcherService || isFromClipboardByFloating || isFromClipboard)
            processTextFromFxService();
//            hasInit = true;
//        }
    }

    boolean isFromClipboardByWatcherService = false;
    boolean isFromClipboardByFloating = false;
    boolean isFromFxService = false;
    boolean isFromClipboard = false;

    private void handleIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType() == null ? "": intent.getType();
        Trace.i("type", type);
        Trace.i("action", action);

        if (intent == null) {
            return;
        }

        //getStringExtra() may return null
        if (Intent.ACTION_SEND.equals(action) && type.equals("text/plain")) {
            String base64 = intent.getStringExtra(Constant.INTENT_ANKIHELPER_BASE64);
            mTextToProcess = intent.getStringExtra(Intent.EXTRA_TEXT);

            if (mTextToProcess != null && mTextToProcess.equals(Constant.USE_FX_SERVICE_CB_FLAG)) {
                mTextToProcess = intent.getStringExtra(Constant.FLOATING_GET_CONTENT);
                isFromFxService = true;
            } else if (mTextToProcess != null && mTextToProcess.equals(Constant.USE_CLIPBOARD_CONTENT_FLAG)) {
                mTextToProcess = "";
                isFromClipboardByWatcherService = true;
            } else if (mTextToProcess != null && mTextToProcess.equals(Constant.FLOATING_USE_CLIPBOARD_CONTENT_FLAG)) {
                mTextToProcess = "";
                isFromClipboardByFloating = true;
            } else if (base64 != null && !base64.equals("0")) {
                mTextToProcess = new String(Base64.decode(mTextToProcess, Base64.DEFAULT));
            }

            //mFbReaderBookmarkId = intent.getStringExtra(Constant.INTENT_ANKIHELPER_FBREADER_BOOKMARK_ID);
            String noteEditedByUser = intent.getStringExtra(Constant.INTENT_ANKIHELPER_NOTE);
            if (noteEditedByUser != null) {
                mNoteEditedByUser = noteEditedByUser;
            }
            String updateId = intent.getStringExtra(Constant.INTENT_ANKIHELPER_NOTE_ID);
            mUpdateAction = intent.getStringExtra(Constant.INTENT_ANKIHELPER_UPDATE_ACTION);

            if (updateId != null && !updateId.isEmpty()) {
                try {
                    mUpdateNoteId = Long.parseLong(updateId);
                    mPreNoteId = mUpdateNoteId;
                    if (mUpdateNoteId > 0) {
                        mTagEditedByUser =
                                MyApplication.getAnkiDroid()
                                        .getApi().getNote(mUpdateNoteId)
                                        .getTags();
                        //action is appending.
                        if(mUpdateAction == null)
                            mUpdateAction = MODE_APPEND;
                    }
                } catch (Exception e) {

                }
            }
            mTargetWord = intent.getStringExtra(Constant.INTENT_ANKIHELPER_TARGET_WORD);
        } else if (Intent.ACTION_PROCESS_TEXT.equals(action) && type.equals("text/plain")) {
            mTextToProcess = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT);
            mTargetWord = intent.getStringExtra(Constant.INTENT_ANKIHELPER_TARGET_WORD);
        } else if (Intent.ACTION_PROCESS_TEXT.equals(action) && type.equals("text/*")) {
            mTextToProcess = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT);
            mTargetWord = intent.getStringExtra(Constant.INTENT_ANKIHELPER_TARGET_WORD);
        } else if(Constant.COLORDICT_INTENT_ACTION_SEARCH.equals(action) ||
                Constant.MDICT_INTENT_ACTION_SEARCH.equals(action)) {
            mTextToProcess = intent.getStringExtra("EXTRA_QUERY");
//            mTargetWord = intent.getStringExtra("EXTRA_QUERY");
        } else if(Intent.ACTION_VIEW.equals(action)) {
            mTextToProcess = intent.getData().getQueryParameter("content");
            mTargetWord = intent.getData().getQueryParameter("key");
            String updateId = intent.getData().getQueryParameter("id");
            mUpdateAction = intent.getData().getQueryParameter("action");

            if (updateId != null && !updateId.isEmpty()) {
                try {
                    mUpdateNoteId = Long.parseLong(updateId);
                    mPreNoteId = mUpdateNoteId;
                    if (mUpdateNoteId > 0) {
                        mTagEditedByUser =
                                MyApplication.getAnkiDroid()
                                        .getApi().getNote(mUpdateNoteId)
                                        .getTags();
                        //action is appending.
                        if(mUpdateAction == null)
                            mUpdateAction = MODE_APPEND;
                    }
                } catch (Exception e) {

                }
            }
        } else {
            isFromClipboard = true;
        }

        if (mUpdateAction == null || mUpdateAction.equals(""))
            mUpdateAction = MODE_CREATE;
        switch (mUpdateAction) {
            case MODE_REPLACE:
                settings.put(Settings.NOTE_MODE, Constant.NoteMode.REPLACE_MODE.ordinal());
                break;
            case MODE_APPEND:
                settings.put(Settings.NOTE_MODE, Constant.NoteMode.APPEND_MODE.ordinal());
                break;
            case MODE_CREATE:
            default:
                settings.put(Settings.NOTE_MODE, Constant.NoteMode.NEW_MODE.ordinal());
                break;
        }

//        if (mUpdateAction.equals(MODE_CREATE))
//            settings.put(Settings.NOTE_MODE, Constant.NoteMode.NEW_MODE.ordinal());
//        else if (mUpdateAction.equals(MODE_REPLACE))
//            settings.put(Settings.NOTE_MODE, Constant.NoteMode.REPLACE_MODE.ordinal());
//        else if (mUpdateAction.equals(MODE_APPEND))
//            settings.put(Settings.NOTE_MODE, Constant.NoteMode.APPEND_MODE.ordinal());

        if (mTextToProcess == null) {
            mTextToProcess = "";
        }

        mTextToProcess = PunctuationUtil.chinesePunctuationToEnglish(mTextToProcess.trim());
//        mTextToProcess = StringUtil.htmlTagFilter(mTextToProcess);

        //判断分享内容是否带有分享Url协议
        if(RegexUtil.isScrollingToTextFragment(mTextToProcess))
        {
            String text = mTextToProcess;
//            mTextToProcess = text.substring(1, text.lastIndexOf("\n")-1);
//            mTargetWord = intent.getStringExtra(Constant.INTENT_ANKIHELPER_TARGET_WORD);//无用语句，保留以后可能用得上
//            mUrl = text.substring(text.lastIndexOf("\n")+1, text.length()).trim();

            mTextToProcess =  RegexUtil.getTextOfFragment(text);
            mUrl = RegexUtil.getLinkOfFragment(text);
        } else {
//            mTargetWord = intent.getStringExtra(Constant.INTENT_ANKIHELPER_TARGET_WORD);//无用语句，保留以后可能用得上
            String url = intent.getStringExtra(Constant. INTENT_ANKIHELPER_TARGET_URL);
            if(url != null)
                mUrl = url;
        }

        populateWordSelectBox();

        // 是否复制文本
//        if(settings.get(Settings.COPY_MARKED_TEXT, false) && !mTextToProcess.isEmpty()) {
//            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipData clip = ClipData.newPlainText("plans string", mTextToProcess);
//            clipboard.setPrimaryClip(clip);
//        }

        HistoryUtil.savePopupOpen(mTextToProcess);

        bigBangLayout.post(new Runnable() {
            @Override
            public void run() {
//                if(!mTextToProcess.isEmpty() && isTransparent())
//                    setTransparent(1.0f);
                setTargetWord();
                if (Utils.containsTranslationField(currentOutputPlan, mUpdateAction) && settings.getPopupToolbarAutomaticTranslationEnable()) {
                    asyncTranslate(mTextToProcess);
                }
            }
        });
    }

    private void processTextFromFxService() {
        if(TextUtils.isEmpty(mTextToProcess) && hasWindowFocus() && (isFromFxService || isFromClipboardByWatcherService || isFromClipboardByFloating || isFromClipboard)) {
            String text = "";
            mTextToProcess = "";
            isFromFxService = false;
            isFromClipboardByWatcherService = false;
            isFromClipboardByFloating = false;
            isFromClipboard = false;
            text = ClipboardUtils.getText(getApplicationContext());
            if (!text.isEmpty()) {
                mTextToProcess = text;
            }
            //窗口未销毁，从失焦到得焦时，会调用onWindowFocusChanged，所以必须通过mTextToProcess识别
//                if (mTextToProcess.isEmpty())
////                finish();
//                    moveTaskToBack(true);

            if (mTextToProcess.isEmpty()) {
                isRefreshedBigBang = true;
                mEditTextTranslation.setText("");
                mEditTextTranslation.setVisibility(View.GONE);
                bigBangLayoutWrapper.setVisibility(View.GONE);
                return;
            } else {
                mEditTextTranslation.setVisibility(View.VISIBLE);
                bigBangLayoutWrapper.setVisibility(View.VISIBLE);
                //判断分享内容是否带有分享Url协议
                if(RegexUtil.isScrollingToTextFragment(text))
                {
//                        mTextToProcess = text.substring(1, mTextToProcess.lastIndexOf("\n")-1);
//                        mUrl = text.substring(mTextToProcess.lastIndexOf("\n")+1, mTextToProcess.length()).trim();
                    mTextToProcess =  RegexUtil.getTextOfFragment(text);
                    mUrl = RegexUtil.getLinkOfFragment(text);
                }
            }

            if (settings.getClearSearchedEnable()) {
                ClipboardUtils.clearFirstClipboard(getApplicationContext());
            }

            populateWordSelectBox();
            bigBangLayout.post(new Runnable() {
                @Override
                public void run() {
//                        if (!mTextToProcess.isEmpty() && isTransparent())
//                            setTransparent(1.0f);
                    setTargetWord();
                    if (Utils.containsTranslationField(currentOutputPlan, mUpdateAction) && settings.getPopupToolbarAutomaticTranslationEnable()) {
                        asyncTranslate(mTextToProcess);
                    }
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    //ed
    private void populateWordSelectBox() {
        List<String> localSegments = TextSplitter.getLocalSegments(mTextToProcess);
        bigBangLayout.removeAllViews();
        for (String localSegment : localSegments) {
            bigBangLayout.addTextItem(localSegment);
        }
        bigBangLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        String currentWord = FieldUtil.getSelectedText(bigBangLayout.getLines());
                        if (!currentWord.isEmpty() && !currentWord.equals(acTextView.getText().toString())) {
                            mCurrentKeyWord = currentWord;
                            acTextView.setText(currentWord);
                            asyncSearch(currentWord);
                        } else {
                            acTextView.setText("");
                        }
                    }
                }
        );
    }


    private void asyncSearch(final String word) {
        if (word.length() == 0) {
//            showPronounce(false);
            return;
        }
        if (currentDictionary == null || currentOutputPlan == null) {
            return;
        }

        showProgressBar();
        progressBar.invalidate();
        showPronounce(true);
        if (mPreThread != null && mPreThread.getState() == Thread.State.RUNNABLE)
            mPreThread.interrupt();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Your code goes here
                    List<Definition> d = currentDictionary.wordLookup(word);
                    Message message = mHandler.obtainMessage();
                    message.obj = d;
                    message.what = PROCESS_DEFINITION_LIST;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    String error = e.getMessage();
                    Message message = mHandler.obtainMessage();
                    message.obj = error;
                    message.what = ASYNC_SEARCH_FAILED;
                    mHandler.sendMessage(message);
                }
            }
        });
        thread.start();
        mPreThread = thread;
        HistoryUtil.saveWordlookup(mTextToProcess, word);
    }

    private void asyncTranslate(final String text) {
        if (text == null) return;
        if (text.trim().isEmpty()) return;
        showTranslateLoading();
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String result;
                            result = new TranslateBuilder(settings.getTranslatorCheckedIndex()).translate(text, currentDictionary.getLanguageType());
                            Message message = mHandler.obtainMessage();
                            message.obj = result;
                            message.what = TRANSLATION_DONE;
                            mHandler.sendMessage(message);
                        } catch (Exception e) {
                            String error = e.getMessage();
                            Message message = mHandler.obtainMessage();
                            message.obj = error;
                            message.what = TRANSLATIOn_FAILED;
                            mHandler.sendMessage(message);
                        }
                    }
                }
        );
        thread.start();
    }

    private void setActAdapter(IDictionary dict) {
        Object adapter = dict.getAutoCompleteAdapter(PopupActivity.this,
                android.R.layout.simple_spinner_dropdown_item);
        //自动补全提示，若当前词典没有补全适配器方法，按语言类型指定带有原型数据的离线词典
        if (adapter == null) {
            switch (dict.getLanguageType()) {
                case DictLanguageType.ENG:
                case DictLanguageType.ALL:
                default:
                    adapter = new Ode2(MyApplication.getContext()).getAutoCompleteAdapter(
                            PopupActivity.this,
                            android.R.layout.simple_spinner_dropdown_item);
            }
        }
        //
        if (adapter != null) {
            if (adapter instanceof SimpleCursorAdapter) {
                acTextView.setAdapter((SimpleCursorAdapter) adapter);
            } else if (adapter instanceof UrbanAutoCompleteAdapter) {
                acTextView.setAdapter((UrbanAutoCompleteAdapter) adapter);
            }
        }
        acTextView.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            if (acTextView.getText().toString().trim().isEmpty()) {
                                return;
                            }
                            acTextView.showDropDown();
                        }
                    }
                }
        );
    }

    //plan B
    private View getCardFromDefinition(final Definition def, int index) {
//        int index = viewDefinitionList.getChildCount();
//        Trace.e("index", String.valueOf(index));

        View view;
        if (settings.getLeftHandModeQ()) {
            view = LayoutInflater.from(PopupActivity.this)
                    .inflate(R.layout.definition_item_left, null);
        } else {
            view = LayoutInflater.from(PopupActivity.this)
                    .inflate(R.layout.definition_item, null);
        }
        //toggle fab with clicks
//        view.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(mFab.getVisibility() == View.VISIBLE){
//                            mFab.hide();
//                        }else{
//                            mFab.show();
//                        }
//                    }
//                }
//        );
        final TextView textViewDefinition = (TextView) view.findViewById(R.id.textview_definition);
        final MdxCustomActionWebView webViewDefinition = (MdxCustomActionWebView) view.findViewById(R.id.webview_definition);
        webViewDefinition.setmCustomMenuList(Arrays.stream(currentDictionary.getExportElementsList()).collect(Collectors.toList()));
        HashMap<String, String> mdxEleSelectedMap = new HashMap<>();
        final ImageButton btnAddDefinition = (ImageButton) view.findViewById(R.id.btn_add_definition);
        final LinearLayout btnDefinitionLargeSide = (LinearLayout) view.findViewById(R.id.btn_pronounce_large);
        final LinearLayout btnAddDefinitionLarge = (LinearLayout) view.findViewById(R.id.btn_add_definition_large);
//        final ImageView btnPronounce = (ImageView) view.findViewById(R.id.btn_pronounce);
        final ImageView defImage = view.findViewById(R.id.def_img);

        btnAddDefinitionLarge.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnAddDefinition.callOnClick();
                    }
                }
        );

        btnDefinitionLargeSide.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        btnAddDefinition.callOnClick();
//                        btnPronounce.callOnClick();
                        textViewDefinition.callOnClick();
                    }
                }
        );

        btnDefinitionLargeSide.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        btnAddDefinition.callOnClick();
                        return true;
                    }
                }
        );
//        btnPronounce.setVisibility(View.GONE);
//        btnPronounce.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String word = def.getExportElement(Constant.DICT_FIELD_KEYWORD);
//                        int lastPronounceLanguageIndex = Settings.getInstance(PopupActivity.this).getLastPronounceLanguage();
//                        PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(lastPronounceLanguageIndex);
//
//                        StopPlayingAll();
//                        mMediaProgress.setVisibility(View.VISIBLE);
//                        if (infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_TTS)
//                            playTts(word);
//                        else {
//                            String finalAudioUrl = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, word, currentDictionary.getAudioUrl());
//                            String cacheAudioUrl = getCacheAudioUrl(finalAudioUrl);
//                            PlayAudioManager.playPronounceSound(PopupActivity.this, cacheAudioUrl);
//                            Trace.i("speaks p", word);
//                        }
//                        isClicked = false;
//                        mMediaProgress.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                mMediaProgress.setVisibility(View.GONE);
//                            }
//                        }, 500);
//                    }
//                }
//        );

        //final Definition def = mDefinitionList.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textViewDefinition.setText(Html.fromHtml(def.getDisplayHtml(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            textViewDefinition.setText(Html.fromHtml(def.getDisplayHtml()));
        }

        if (currentDictionary instanceof Mdict) {
            textViewDefinition.setVisibility(View.GONE);

            webViewDefinition.setVisibility(View.VISIBLE);
            webViewDefinition.setVerticalScrollBarEnabled(false);
            WebSettings ws = webViewDefinition.getSettings();
            ws.setAllowFileAccess(true);
            ws.setAllowContentAccess(true);
////            ws.setAllowFileAccessFromFileURLs(true);
////            ws.setAllowUniversalAccessFromFileURLs(true);
            ws.setJavaScriptEnabled(true); // 启用JavaScript
            ws.setDomStorageEnabled(true); // 启用DOM存储
            ws.setLoadsImagesAutomatically(true); // 自动加载图片
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); // 允许混合内容
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    DarkModeUtils.isDarkMode(MyApplication.getContext())) {
                //设置成暗黑模式
                ws.setForceDark(WebSettings.FORCE_DARK_ON);
                //黑色改成主题背景色
                TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(android.R.attr.windowBackground, typedValue, true);
                webViewDefinition.setBackgroundColor(typedValue.data);
            }
            File htmlPath = new File(StorageUtils.getIndividualCacheDirectory(getApplicationContext()), index + Constant.CACHE_TEMP_HTML);

            webViewDefinition.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                    webViewDefinition.setVisibility(View.VISIBLE);
                    super.onPageStarted(view, url, favicon);
//                        view.loadUrl("javascript:"+finalJsStr);
//                        view.evaluateJavascript(finalJsStr, new ValueCallback<String>() {
//                            @Override public void onReceiveValue(String value) {//js与native交互的回调函数
//                                Trace.i("popup","value=" + value);
//                            }
//                        });
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Trace.i("webview_mdx_soul_url", url);
                    if(!url.contains(":///")) {
//                        view.stopLoading();
                        return true;
                    } else {
                        return super.shouldOverrideUrlLoading(view, url);
                    }
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    Trace.i("webview_mdx_soul_request", request.getUrl().toString());
                    if(!request.getUrl().toString().contains(":///")) {
//                        view.stopLoading();
                        return true;
                    } else {
                        return super.shouldOverrideUrlLoading(view, request);
                    }
                }

                @Override
                public void onLoadResource(WebView view, String url) {
                    Trace.i("webview_mdx_olr_url", url);
                    if(!url.contains(":///")) {
//                        view.stopLoading();
                    } else {
                        super.onLoadResource(view, url);
                    }
                }
            });

            webViewDefinition.setActionSelectListener(
                    new ActionSelectListener() {
                        @Override
                        public void onClick(String title, String parentText, String selectText) {

                        }

                        @Override
                        public void onClick(String title, String selectText) {
                            if(!TextUtils.isEmpty(selectText)) {
//                                if(title.equals(getResources().getString(R.string.str_add_rich_text)) ||
//                                        title.equals(getResources().getString(R.string.str_add_plain_text))) {
//                                    selectedTextMdx = selectText;
//                                    btnAddDefinition.callOnClick();
//
//                                } else if(title.equals(getResources().getString(R.string.str_share))) {
//                                    LinearLayout mLayoutRoot;
//                                    mLayoutRoot = new LinearLayout(view.getContext());
//                                    //实例化分享窗口
//                                    SharePopupWindow spw = new SharePopupWindow(PopupActivity.this, selectText);
//                                    ;
//                                    // 显示窗口
//                                    spw.showAtLocation(mLayoutRoot, Gravity.BOTTOM, 0, 0);
//                                }
                                if(def.hasElement(title)) {
                                    String refinedText = selectText;
                                    switch(title) {
                                        case Constant.DICT_FILED_SENSE:
                                            refinedText = RegexUtil.colorizeSense(refinedText);
                                    }
                                    mdxEleSelectedMap.put(title, refinedText);
                                    if (title.contains(Constant.MDX_ADD_TAG)) {
                                        hasPlusItemBeenClicked = true;
                                        btnAddDefinition.callOnClick();
                                    } else
                                        ToastUtil.show(String.format("%s: %s", title, selectText.length()<=20?selectText:selectText.substring(0,20)+"..."));
                                } else if(title.equals(getResources().getString(R.string.str_share))) {
                                    LinearLayout mLayoutRoot;
                                    mLayoutRoot = new LinearLayout(view.getContext());
                                    //实例化分享窗口
                                    SharePopupWindow spw = new SharePopupWindow(PopupActivity.this, selectText);
                                    ;
                                    // 显示窗口
                                    spw.showAtLocation(mLayoutRoot, Gravity.BOTTOM, 0, 0);
                                }
                            }
                        }
                    }
            );
//            webViewDefinition.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            isClicked = true;
//                            break;
////                            case MotionEvent.ACTION_MOVE:
////                                isClicked = false;
////                                break;
//                        case MotionEvent.ACTION_UP:
//                            if (!isClicked) break;
//                            String word = acTextView.getText().toString();
//                            int lastPronounceLanguageIndex = Settings.getInstance(PopupActivity.this).getLastPronounceLanguage();
//                            PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(lastPronounceLanguageIndex);
//
//                            StopPlayingAll();
//                            mMediaProgress.setVisibility(View.VISIBLE);
//                            if (infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_TTS)
//                                playTts(word);
//                            else {
//                                String finalAudioUrl = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, word, currentDictionary.getAudioUrl());
//                                String cacheAudioUrl = getCacheAudioUrl(finalAudioUrl);
//                                PlayAudioManager.playPronounceSound(PopupActivity.this, cacheAudioUrl);
//                                Trace.i("speaks p", word);
//                            }
//                            isClicked = false;
//                            mMediaProgress.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mMediaProgress.setVisibility(View.GONE);
//                                }
//                            }, 500);
//                    }
//                    return true;
//                }
//            });

//            String finalJsStr = "javascript:";
            WebChromeClient webChromeClient = new WebChromeClient();
/*            {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if(newProgress == 100) {
                        view.evaluateJavascript(finalJsStr, new ValueCallback<String>() {
                            @Override public void onReceiveValue(String value) {//js与native交互的回调函数
                                Trace.i("popup","value=" + value);
                            }
                        });
                    }
                }
            };*/
            webViewDefinition.setWebChromeClient(webChromeClient);
            webViewDefinition.loadUrl(FileUtils.getUri(htmlPath).getPath());

        } else {
            webViewDefinition.setVisibility(View.GONE);
            if (def.getDisplayHtml().isEmpty()) {
                textViewDefinition.setVisibility(View.GONE);
            } else {
                textViewDefinition.setVisibility(View.VISIBLE);
            }
        }

        if (def.getImageUrl() != null && !def.getImageUrl().isEmpty()) {
            Glide.with(this).
                    load(def.getImageUrl()).
                    diskCacheStrategy(DiskCacheStrategy.RESOURCE).
                    override(800).
                    into(defImage);
            defImage.setVisibility(View.VISIBLE);
            defImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    defImage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//                    if (defImage.getMeasuredHeight() > defImage.getMeasuredWidth()) {
                        int inHeight = ScreenUtils.dp2px(getApplicationContext(), 150);
                        int height;
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) defImage.getLayoutParams();
                        if (params.height == LinearLayout.LayoutParams.MATCH_PARENT) {
                            height = inHeight;
                        } else {
                            height = LinearLayout.LayoutParams.MATCH_PARENT;
                        }
                        defImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
                    }
//                }
            });

//            defImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Drawable drawable = defImage.getDrawable();
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(FileUtils.getImageUri(getApplicationContext(), drawable), "image/*");
//                    startActivity(intent);
//                }
//            });
        }

        //play audio or video
        if(currentDictionary instanceof Mdict ||
            currentDictionary instanceof DictTango)
            textViewDefinition.setTextIsSelectable(true);
        else
            textViewDefinition.setTextIsSelectable(false);

        textViewDefinition.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        StopPlayingAll();
                        if (!(currentDictionary instanceof Mdict ||
                                currentDictionary instanceof DictTango)) {
                            try {
                                String readContent =cleanKey(def, mdxEleSelectedMap,true); // = def.getExportElement("摘取例句").replaceAll("</?[\\w\\W].*?>", "");
                                String finalAudioUrl = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, readContent, def.getAudioUrl());

                                int lastPronounceLanguageIndex = Settings.getInstance(MyApplication.getContext()).getLastPronounceLanguage();
                                PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(lastPronounceLanguageIndex);

                                // SOUND_SOURCE_ONLINE
                                if (infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_ONLINE) {
                                    if (finalAudioUrl.isEmpty())
                                        ToastUtil.show( R.string.play_no_audio);
                                        // mp4，缓存
                                    else if (def.getAudioSuffix().equals(Constant.MP4_SUFFIX)) {
                                        //在线媒体缓存
                                        String cacheAudioUrl = getCacheAudioUrl(finalAudioUrl);
//                                        Trace.e("cacheAudioUrl", cacheAudioUrl);

                                        //移动mSufaceView
                                        LinearLayout ly = (LinearLayout) textViewDefinition.getParent();
                                        LinearLayout preLy = (LinearLayout) mSurfaceView.getParent();
                                        if (mVideoMediaPlayer == null) {
                                            ly.addView(mSurfaceView);
                                            initVideoMediaPlayer();
                                        } else {
                                            if (mSurfaceView != null) {
                                                if (mSurfaceView.getVisibility() == View.VISIBLE) {
                                                    if (!ly.equals(preLy)) {
                                                        preLy.removeView(mSurfaceView);
                                                        ly.addView(mSurfaceView);
                                                        initVideoMediaPlayer();
                                                    }
                                                } else if (mSurfaceView.getVisibility() == View.GONE) {
                                                    if (preLy != null) {
                                                        preLy.removeView(mSurfaceView);
                                                    }
                                                    ly.addView(mSurfaceView);
                                                    initVideoMediaPlayer();
                                                }
                                            }
                                        }

                                        mMediaProgress.setVisibility(View.VISIBLE);
                                        mVideoMediaPlayer.setDataSource(PopupActivity.this, Uri.parse(cacheAudioUrl));
                                        mVideoMediaPlayer.prepareAsync();
                                    }
                                    //mp3，不缓存
                                    else if (def.getAudioSuffix().equals(Constant.MP3_SUFFIX)) {
                                        // 隐藏mSurfaceView
                                        if (mSurfaceView != null && mSurfaceView.getVisibility() == View.VISIBLE)
                                            mSurfaceView.setVisibility(View.GONE);

                                        mMediaProgress.setVisibility(View.VISIBLE);
                                        PlayAudioManager.playPronounceSound(PopupActivity.this, finalAudioUrl);
                                        mMediaProgress.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mMediaProgress.setVisibility(View.GONE);
                                            }
                                        }, 500);
                                    }
                                }
                                // SOUND_SOURCE_TTS
                                else if (infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_TTS) {
                                    // 隐藏mSurfaceView
                                    if (mSurfaceView != null && mSurfaceView.getVisibility() == View.VISIBLE)
                                        mSurfaceView.setVisibility(View.GONE);

                                    mMediaProgress.setVisibility(View.VISIBLE);
                                    playTts(readContent);
                                    mMediaProgress.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mMediaProgress.setVisibility(View.GONE);
                                        }
                                    }, 500);
                                }
                                // SOUND_SOURCE_YOUDAO
                                // SOUND_SOURCE_EUDICT
                                // SOUND_SOURCE_BD
                                else if (infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_YOUDAO ||
                                        infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_EUDICT) {
//                                        infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_BD) {
                                    // 隐藏mSurfaceView
                                    if (mSurfaceView != null && mSurfaceView.getVisibility() == View.VISIBLE)
                                        mSurfaceView.setVisibility(View.GONE);

                                    mMediaProgress.setVisibility(View.VISIBLE);
                                    PlayAudioManager.playPronounceSound(PopupActivity.this, finalAudioUrl);
                                    mMediaProgress.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mMediaProgress.setVisibility(View.GONE);
                                        }
                                    }, 500);
                                }
                                currentDictionary.setAudioUrl(def.getAudioUrl());

                            } catch (IOException e) {
                                e.printStackTrace();
                                ToastUtil.show( e.getMessage());
                            } catch (IllegalStateException ise) {

                            } catch (Exception e) {

                            }
                        }
                        return true;
                    }
                }
        );
        textViewDefinition.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        StopPlayingAll();
                        try {
                            String readContent = cleanKey(def, mdxEleSelectedMap);
                            Trace.i("speaks", readContent);
                            String finalAudioUrl = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, readContent, def.getAudioUrl());

                            int lastPronounceLanguageIndex = Settings.getInstance(MyApplication.getContext()).getLastPronounceLanguage();
                            PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(lastPronounceLanguageIndex);

                            // SOUND_SOURCE_ONLINE
                            if (infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_ONLINE) {
                                if (finalAudioUrl.isEmpty())
                                    ToastUtil.show( R.string.play_no_audio);
                                    // mp4，缓存
                                else if (def.getAudioSuffix().equals(Constant.MP4_SUFFIX)) {
                                    //在线媒体缓存
                                    String cacheAudioUrl = getCacheAudioUrl(finalAudioUrl);
//                                    Trace.e("cacheAudioUrl", cacheAudioUrl);

                                    //移动mSufaceView
                                    LinearLayout ly = (LinearLayout) textViewDefinition.getParent();
                                    LinearLayout preLy = (LinearLayout) mSurfaceView.getParent();
                                    if (mVideoMediaPlayer == null) {
                                        ly.addView(mSurfaceView);
                                        initVideoMediaPlayer();
                                    } else {
                                        if (mSurfaceView != null) {
                                            if (mSurfaceView.getVisibility() == View.VISIBLE) {
                                                if (!ly.equals(preLy)) {
                                                    preLy.removeView(mSurfaceView);
                                                    ly.addView(mSurfaceView);
                                                    initVideoMediaPlayer();
                                                }
                                            } else if (mSurfaceView.getVisibility() == View.GONE) {
                                                if (preLy != null) {
                                                    preLy.removeView(mSurfaceView);
                                                }
                                                ly.addView(mSurfaceView);
                                                initVideoMediaPlayer();

                                            }
                                        }
                                    }

                                    mMediaProgress.setVisibility(View.VISIBLE);
                                    mVideoMediaPlayer.setDataSource(PopupActivity.this, Uri.parse(cacheAudioUrl));
                                    mVideoMediaPlayer.prepareAsync();
                                }
                                //mp3，不缓存
                                else if (def.getAudioSuffix().equals(Constant.MP3_SUFFIX)) {
                                    // 隐藏mSurfaceView
                                    if (mSurfaceView != null && mSurfaceView.getVisibility() == View.VISIBLE)
                                        mSurfaceView.setVisibility(View.GONE);

                                    mMediaProgress.setVisibility(View.VISIBLE);
                                    PlayAudioManager.playPronounceSound(PopupActivity.this, finalAudioUrl);
                                    mMediaProgress.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mMediaProgress.setVisibility(View.GONE);
                                        }
                                    }, 500);
                                }
                            }
                            // SOUND_SOURCE_TTS
                            else if (infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_TTS) {
                                // 隐藏mSurfaceView
                                if (mSurfaceView != null && mSurfaceView.getVisibility() == View.VISIBLE)
                                    mSurfaceView.setVisibility(View.GONE);

                                mMediaProgress.setVisibility(View.VISIBLE);
                                playTts(readContent);
                                mMediaProgress.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mMediaProgress.setVisibility(View.GONE);
                                    }
                                }, 500);
                            }
                            // SOUND_SOURCE_YOUDAO
                            // SOUND_SOURCE_EUDICT
                            // SOUND_SOURCE_BD
                            else if (infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_YOUDAO ||
                                    infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_EUDICT) {
//                                    infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_BD) {
                                // 隐藏mSurfaceView
                                if (mSurfaceView != null && mSurfaceView.getVisibility() == View.VISIBLE)
                                    mSurfaceView.setVisibility(View.GONE);

                                mMediaProgress.setVisibility(View.VISIBLE);
                                PlayAudioManager.playPronounceSound(PopupActivity.this, finalAudioUrl);
                                mMediaProgress.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mMediaProgress.setVisibility(View.GONE);
                                    }
                                }, 500);
                            }
                            currentDictionary.setAudioUrl(def.getAudioUrl());

                        } catch (IOException e) {
                            e.printStackTrace();
                            ToastUtil.show( e.getMessage());
                        } catch (IllegalStateException ise) {

                        } catch(Exception e) {

                        }

                    }
                }
        );

        // mSurfaceView点击事件-播放视频
        mSurfaceView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            StopPlayingAll();
                            String finalAudioUrl = currentDictionary.getAudioUrl();
                            String cacheAudioUrl = getCacheAudioUrl(finalAudioUrl);
                            mMediaProgress.setVisibility(View.VISIBLE);
                            mSurfaceView.setVisibility(View.VISIBLE);
                            mVideoMediaPlayer.setDataSource(PopupActivity.this, Uri.parse(cacheAudioUrl));
                            mVideoMediaPlayer.prepareAsync();
                        } catch (IOException e) {
                            e.printStackTrace();
                            ToastUtil.show( e.getMessage());
                        } catch (IllegalStateException e) {

                        }
                    }
                }
        );


        //set custom action for the textView
        makeTextViewSelectAndSearch(textViewDefinition);
        //holder.itemView.setAnimation(AnimationUtils.loadAnimation(mActivity, android.R.anim.fade_in));
        //holder.textViewDefinition.setTextColor(Color.BLACK);
        btnAddDefinition.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //vibarate(Constant.VIBRATE_DURATION);
                        //before add, check if this note is already added by check the attached tag
                        try {
                            int currentNoteModeIndex = settings.get(Settings.NOTE_MODE, 0);
                            switch (Constant.NoteMode.values()[currentNoteModeIndex]) {
                                case NEW_MODE:
                                    mUpdateAction = MODE_CREATE;
                                    mUpdateNoteId = 0L;
                                    break;
                                case REPLACE_MODE:
                                    mUpdateAction = MODE_REPLACE;
                                    mUpdateNoteId = mPreNoteId;
                                    break;
                                case APPEND_MODE:
                                    mUpdateAction = MODE_APPEND;
                                    mUpdateNoteId = mPreNoteId;
                                    break;
                            }

                            AnkiDroidHelper mAnkiDroid = MyApplication.getAnkiDroid();
                            Long noteIdAdded = hasPlusItemBeenClicked ? null : (Long) btnAddDefinition.getTag(R.id.TAG_NOTE_ID);
                            hasPlusItemBeenClicked = false;

                            if (noteIdAdded != null) {
                                if (mUpdateNoteId == 0) {
                                    if (Utils.deleteNote(PopupActivity.this, noteIdAdded.longValue())) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            btnAddDefinition.setBackground(ContextCompat.getDrawable(
                                                    PopupActivity.this,
                                                    Utils.getResIdFromAttribute(PopupActivity.this, R.attr.icon_add)));
                                        }
                                        btnAddDefinition.setTag(R.id.TAG_NOTE_ID, null);
                                        mPreNoteId = mUpdateNoteId;
                                        ToastUtil.show( R.string.str_cancel_note_add);

                                    } else {
                                        ToastUtil.show( R.string.error_note_cancel);
                                    }
                                } else {
                                    ToastUtil.show( R.string.str_not_cancelable_append_mode);
                                }
                                return;
                            }
                            ///////////////////////////////////


                            //save css
                            if (def.getCssUrl() != null && !def.getCssUrl().isEmpty()) {
                                String saveFile = settings.getAnkiDroidDir() + def.getCssName();
                                if(currentDictionary instanceof DictTango) {
                                    final Request request = new Request(def.getCssUrl(), saveFile);
//                                Trace.e("cssUrl", def.getCssUrl());
//                                Trace.e("cssName", saveFile);
                                    download(request, saveFile);
                                } else if(currentDictionary instanceof Mdict && !FileUtils.isFileExists(saveFile)) {
                                    try{
                                        FileOutputStream fos = new FileOutputStream(saveFile);
                                        InputStream is = new FileInputStream(def.getCssUrl());
                                        byte[] buffer = new byte[1024];
                                        int byteCount=0;
                                        while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                                            fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                                        }
                                        fos.flush();
                                        is.close();
                                        fos.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            //save js
                            if (def.getJsUrl() != null && !def.getJsUrl().isEmpty()) {
                                String saveFile = settings.getAnkiDroidDir() + def.getJsName();
                                if(currentDictionary instanceof DictTango) {
                                    final Request request = new Request(def.getJsUrl(), saveFile);
//                                Trace.e("jsUrl", def.getJsUrl());
//                                Trace.e("jsName", saveFile);
                                    download(request, saveFile);
                                } else if(currentDictionary instanceof Mdict && !FileUtils.isFileExists(saveFile)) {
                                    try{
                                        FileOutputStream fos = new FileOutputStream(saveFile);
                                        InputStream is = new FileInputStream(def.getJsUrl());
                                        byte[] buffer = new byte[1024];
                                        int byteCount=0;
                                        while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                                            fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                                        }
                                        fos.flush();
                                        is.close();
                                        fos.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            //save image
                            if (def.getImageUrl() != null && !def.getImageUrl().isEmpty()) {
//                                String saveFile = Constant.AUDIO_MEDIA_DIRECTORY + def.getImageName();
//                                final Request request = new Request(def.getImageUrl(), saveFile);
//                                download(request, saveFile);

                                if (defImage.getDrawable() != null && true) {
                                    BitmapDrawable drawable = (BitmapDrawable) defImage.getDrawable();
                                    Bitmap bm = drawable.getBitmap();

                                    OutputStream fOut = null;
                                    //Uri outputFileUri;
                                    try {
                                        File root = new File(settings.getAnkiDroidDir());
                                        if (!root.exists()) {
                                            root.mkdirs();
                                        }

                                        File sdImageMainDirectory = new File(root, def.getImageName());
                                        //outputFileUri = Uri.fromFile(sdImageMainDirectory);
                                        fOut = new FileOutputStream(sdImageMainDirectory);
                                    } catch (Exception e) {

                                    }
                                    try {
                                        bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                        fOut.flush();
                                        fOut.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }


                            // mp3 下载 start

                            //end

//                            String[] sharedExportElements = Constant.getSharedExportElements();
                            String[] exportFields = new String[currentOutputPlan.getFieldsMap().size()];
                            int i = 0;
//                            Map<String, String> map = currentOutputPlan.getFieldsMap();
                            for (String exportedFieldKeys : currentOutputPlan.getFieldsMap().values()) {
                                ComplexElement ce = new ComplexElement(exportedFieldKeys);
                                if(mUpdateNoteId > 0 && mUpdateAction.equals(MODE_APPEND))
                                    exportedFieldKeys = ce.getElementAppending();
                                else
                                    exportedFieldKeys = ce.getElementNormal();
                                Trace.i("exported", String.valueOf(exportedFieldKeys));

                                String[] exportedFieldKeyArr = exportedFieldKeys.split(MultiSpinner.DELIMITER);
                                int keyArrLength = exportedFieldKeyArr.length;
                                exportFields[i] = "";
                                for(String exportedFieldKey : exportedFieldKeyArr) {
                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_EMPTY)) {
//                                        exportFields[i] += "";

                                        continue;
                                    }

                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED)) {
                                        String text = getNormalSentence(bigBangLayout.getLines());
                                        exportFields[i] += keyArrLength > 1 ? renderText(text, exportedFieldKey, currentDictionary) : text;

                                        continue;
                                    }

                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_BOLD)) {
                                        String text = getBoldSentence(bigBangLayout.getLines());
                                        exportFields[i] += keyArrLength > 1 ? renderText(text, exportedFieldKey, currentDictionary) : text;

                                        continue;
                                    }
                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_CLOZE_DELETION)) {
                                        String text = getBlankSentence(bigBangLayout.getLines(), true);
                                        exportFields[i] += keyArrLength > 1 ? renderText(text, exportedFieldKey, currentDictionary) : text;

                                        continue;
                                    }
                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_CLOZE_DELETION_C1)) {
                                        String text = getBlankSentence(bigBangLayout.getLines(), false);
                                        exportFields[i] += keyArrLength > 1 ? renderText(text, exportedFieldKey, currentDictionary) : text;

                                        continue;
                                    }
                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_NOTE)) {
                                        String text = mNoteEditedByUser;
                                        exportFields[i] += keyArrLength > 1 ? renderText(text, exportedFieldKey, currentDictionary) : text;

                                        continue;
                                    }
                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_URL)) {
                                        String text = mUrl;
                                        exportFields[i] += keyArrLength > 1 ? renderText(text, exportedFieldKey, currentDictionary) : text;

                                        continue;
                                    }
                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_FULL_DEFINITIONS)) {
                                        String text = Utils.getAllHtmlFromDefinitionList(mDefinitionList);
                                        exportFields[i] += keyArrLength > 1 ? renderText(text, exportedFieldKey, currentDictionary) : text;

                                        continue;
                                    }
                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_TRANSLATION)) {
                                        String text = mEditTextTranslation.getText().toString().replace("\n", "<br/>");
                                        exportFields[i] += keyArrLength > 1 ? renderText(text, exportedFieldKey, currentDictionary) : text;

                                        continue;
                                    }
                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_ONLINE_LINK)) {
                                        currentDictionary.setAudioUrl(def.getAudioUrl());
                                        String readContent = cleanKey(def, mdxEleSelectedMap);
                                        String tempUrl = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, readContent, currentDictionary.getAudioUrl());

                                        if(!TextUtils.isEmpty(tempUrl)) {
                                            exportFields[i] += keyArrLength > 1 ? renderText(tempUrl, exportedFieldKey, currentDictionary) : tempUrl;
                                        } else if (!readContent.isEmpty()) {
                                            exportFields[i] += getTtsTag(readContent);
                                        }

                                        continue;
                                    }
                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_ONLINE_VIDEOTAG)
//                                            || exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_ONLINE_AUDIOTAG)
//                                            || exportedFieldKey.equals(Constant.DICT_FIELD_COMPLEX_ONLINE)
                                    ) {
                                        currentDictionary.setAudioUrl(def.getAudioUrl());
                                        String readContent = cleanKey(def, mdxEleSelectedMap);
//                                        final int lastPronounceLanguageIndex = settings.getLastPronounceLanguage();
//                                        PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(lastPronounceLanguageIndex);
//                                        String TPL_HTML_MEDIA_TAG;
//                                        if (infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_ONLINE && def.getAudioSuffix().equals(Constant.MP4_SUFFIX)) {
//                                            TPL_HTML_MEDIA_TAG = Constant.TPL_HTML_VIDEO_TAG;
//                                        } else {
//                                            TPL_HTML_MEDIA_TAG = Constant.TPL_HTML_AUDIO_TAG;
//                                        }
//                                        final String finalMediaUrlOnline = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, readContent, currentDictionary.getAudioUrl());
//
////                                        if (exportFields.equals(Constant.DICT_FIELD_KEY_DAV_ONLINE_VIDEOTAG))
//                                        if (!finalMediaUrlOnline.isEmpty()) {
//                                            exportFields[i] += String.format(TPL_HTML_MEDIA_TAG, finalMediaUrlOnline);
//                                        }
                                        final String finalMediaUrlOnline = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, readContent, currentDictionary.getAudioUrl());
                                        if (!TextUtils.isEmpty(finalMediaUrlOnline)) {
                                            exportFields[i] += renderText(finalMediaUrlOnline, exportedFieldKey, currentDictionary);
                                        }


//                                            //Constant.DICT_FIELD_COMPLEX_ONLINE
//                                        else if (exportedFieldKey.equals(Constant.DICT_FIELD_COMPLEX_ONLINE)) {
//                                            exportFields[i] += def.getExportElement(Constant.DICT_FIELD_COMPLEX_ONLINE).
//                                                    replace(Constant._TPL_HTML_NOTE_TAG_LOCATION_,
//                                                            String.format(Constant.TPL_HTML_NOTE_TAG, mNoteEditedByUser)).
//                                                    replace(Constant._TPL_HTML_MEDIA_TAG_LOCATION_,
//                                                            String.format(TPL_HTML_MEDIA_TAG, finalMediaUrlOnline));
//                                        }

                                        continue;
                                    }
                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_ONLINE_SOUNDTAG)) {
                                        currentDictionary.setAudioUrl(def.getAudioUrl());
                                        String readContent = cleanKey(def, mdxEleSelectedMap);
                                        final String finalMediaUrlOnline = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, readContent, currentDictionary.getAudioUrl());
                                        if (!finalMediaUrlOnline.isEmpty()) {
                                            exportFields[i] += PlayAudioManager.getSoundTag(PopupActivity.this, finalMediaUrlOnline);
                                        } else if (!readContent.isEmpty()) {
                                            exportFields[i] += getTtsTag(readContent);
                                        }
                                        continue;
                                    }

                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_KEY_TTS_SOUNDTAG)) {
                                        String readContent = cleanKey(def, mdxEleSelectedMap);
                                        if (!readContent.isEmpty()) {
                                            exportFields[i] += getTtsTag(readContent);
                                        }

                                        continue;
                                    }

                                    // Download mp3, mp4 files at all and save offline-audio in mobile.
                                    //下载字段11"🔊|🎞💾▶️" 和 "苹果专用下载字段12 🔊|🎞💾▶️ (Needs Field: Remarks)"
                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_OFFLINE_LINK) ||
//                                            exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_OFFLINE_AUDIOTAG) ||
                                            exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_OFFLINE_VIDEOTAG) ||
                                            exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_OFFLINE_SOUNDTAG)
//                                            || exportedFieldKey.equals(Constant.DICT_FIELD_COMPLEX_OFFLINE)
                                    ) {

                                        currentDictionary.setAudioUrl(def.getAudioUrl());

                                        String readContent = cleanKey(def, mdxEleSelectedMap);

                                        final int lastPronounceLanguageIndex = settings.getLastPronounceLanguage();
                                        PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(lastPronounceLanguageIndex);

                                        String SavedAudioOrVideoFileName;
//                                        String TPL_HTML_MEDIA_TAG;
                                        if (infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_ONLINE && def.getAudioSuffix().equals(Constant.MP4_SUFFIX)) {
                                            SavedAudioOrVideoFileName = def.getAudioName() + Constant.MP4_SUFFIX;
//                                            TPL_HTML_MEDIA_TAG = Constant.TPL_HTML_VIDEO_TAG;
                                        } else {
                                            SavedAudioOrVideoFileName = def.getAudioName() + Constant.MP3_SUFFIX;
//                                            TPL_HTML_MEDIA_TAG = Constant.TPL_HTML_AUDIO_TAG;
                                        }

                                        final String finalMediaUrlOnline = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, readContent, currentDictionary.getAudioUrl());
                                        final Request request = new Request(finalMediaUrlOnline, settings.getAnkiDroidDir() + SavedAudioOrVideoFileName);

                                        //判断媒体是否有缓存，有则改名
                                        if (mProxy.isCached(finalMediaUrlOnline)) {
                                            File file = new File(URI.create(mProxy.getProxyUrl(finalMediaUrlOnline)));
                                            String.valueOf(file.renameTo(new File(settings.getAnkiDroidDir() + SavedAudioOrVideoFileName)));
                                            ToastUtil.show( R.string.downlaod_completed);
                                        }
                                        //在线语音下载
                                        else if (!finalMediaUrlOnline.isEmpty() && !infor.isSoundSourceType(PronounceManager.SOUND_SOURCE_TTS)) {
                                            Trace.i("url", "download");
                                            download(request, settings.getAnkiDroidDir() + SavedAudioOrVideoFileName);
                                        }

                                        //tts语音下载
                                        if (infor.isSoundSourceType(PronounceManager.SOUND_SOURCE_TTS)) {
                                            File f = new File(settings.getAnkiDroidDir() + SavedAudioOrVideoFileName);
                                            if (!f.exists()) {
                                                restoreTtsVoice(readContent, f.getPath());
                                                ToastUtil.show( R.string.downlaod_completed);
                                            }
                                        }

                                        //音频字段
                                        //url，无tag
                                        if (exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_OFFLINE_LINK)) {
                                            exportFields[i] += keyArrLength > 1 ? renderText(SavedAudioOrVideoFileName, exportedFieldKey, currentDictionary) : SavedAudioOrVideoFileName;
//                                            exportFields[i] += SavedAudioOrVideoFileName;
                                        }
//                                        //"全audio tag
//                                        else if (exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_OFFLINE_AUDIOTAG)) {
//                                            exportFields[i] += renderText(SavedAudioOrVideoFileName, exportedFieldKey);
//                                        }
                                        //"全video tag
                                        else if (exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_OFFLINE_VIDEOTAG)) {
//                                            exportFields[i] += String.format(TPL_HTML_MEDIA_TAG, SavedAudioOrVideoFileName);
                                            exportFields[i] += renderText(SavedAudioOrVideoFileName, exportedFieldKey, currentDictionary);
                                        }
                                        //"全sound tag，苹果专用下载字段 Remarks字段"
                                        else if (exportedFieldKey.equals(Constant.DICT_FIELD_KEY_DAV_OFFLINE_SOUNDTAG)) {
                                            exportFields[i] += PlayAudioManager.getSoundTag(PopupActivity.this, SavedAudioOrVideoFileName);
                                        }
//                                        //Constant.DICT_FIELD_COMPLEX_OFFLINE
//                                        else if (exportedFieldKey.equals(Constant.DICT_FIELD_COMPLEX_OFFLINE)) {
//                                            exportFields[i] += def.getExportElement(Constant.DICT_FIELD_COMPLEX_OFFLINE).
//                                                    replace(Constant._TPL_HTML_NOTE_TAG_LOCATION_,
//                                                            String.format(Constant.TPL_HTML_NOTE_TAG, mNoteEditedByUser)).
//                                                    replace(Constant._TPL_HTML_MEDIA_TAG_LOCATION_,
//                                                            String.format(TPL_HTML_MEDIA_TAG, SavedAudioOrVideoFileName));
//                                        }

                                        continue;
                                    }

                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_DAV_ONLINE_LINK)) {
                                        String readContent = StringUtil.htmlTagFilter(mTextToProcess);
                                        String tempUrl = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, readContent, PlayAudioManager.YOUDAO_AUDIO, pickedSentencePronouceLanguageIndex);
                                        if (!TextUtils.isEmpty(tempUrl)) {
                                            exportFields[i] += keyArrLength > 1 ? renderText(tempUrl, exportedFieldKey, currentDictionary) : tempUrl;
                                        } else if (!readContent.isEmpty()) {
                                            exportFields[i] += getTtsTag(readContent);
                                        }
//                                        exportFields[i] += !tempUrl.isEmpty() ? tempUrl : "";

                                        continue;
                                    }
                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_DAV_ONLINE_SOUNDTAG)
//                                    || exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_DAV_ONLINE_AUDIOTAG)
                                    ) {
                                        String readContent = StringUtil.htmlTagFilter(mTextToProcess);
                                        final String finalMediaUrlOnline = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, readContent, PlayAudioManager.YOUDAO_AUDIO, pickedSentencePronouceLanguageIndex);
                                        if (!finalMediaUrlOnline.isEmpty()) {
//                                            if (exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_DAV_ONLINE_AUDIOTAG)) {
//                                                exportFields[i] += renderText(finalMediaUrlOnline, exportedFieldKey);
//                                            }
//                                            else if (exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_DAV_ONLINE_SOUNDTAG)) {
                                                exportFields[i] += PlayAudioManager.getSoundTag(PopupActivity.this, finalMediaUrlOnline);
//                                            }
                                        } else if (!readContent.isEmpty()) {
                                            exportFields[i] += getTtsTag(readContent);
                                        }

                                        continue;
                                    }

                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_LINK) ||
//                                            exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_AUDIOTAG) ||
                                            exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_SOUNDTAG)) {
                                        String text = StringUtil.htmlTagFilter(mTextToProcess);
                                        PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(pickedSentencePronouceLanguageIndex);
                                        String SavedAudioFileName = def.getAudioName() + "_ps" + Constant.MP3_SUFFIX;
                                        final String finalMediaUrlOnline = PlayAudioManager.getSoundUrlOnline(PopupActivity.this, text, PlayAudioManager.YOUDAO_AUDIO, pickedSentencePronouceLanguageIndex);
                                        final Request request = new Request(finalMediaUrlOnline, settings.getAnkiDroidDir() + SavedAudioFileName);

                                        if (!finalMediaUrlOnline.isEmpty() && !infor.isSoundSourceType(PronounceManager.SOUND_SOURCE_TTS)) {
                                            Trace.i("url", "download");
                                            download(request, settings.getAnkiDroidDir() + SavedAudioFileName);
                                        }

                                        //tts语音下载
                                        if (infor.isSoundSourceType(PronounceManager.SOUND_SOURCE_TTS)) {
                                            File f = new File(settings.getAnkiDroidDir() + SavedAudioFileName);
                                            if (!f.exists()) {
                                                restoreTtsVoice(Settings.ACTION_LONGCLICK, text, f.getPath());
                                                ToastUtil.show( R.string.downlaod_completed);
                                            }
                                        }

                                        //音频字段
                                        //uri，无tag
                                        if (exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_LINK)) {
                                            exportFields[i] += keyArrLength > 1 ? renderText(SavedAudioFileName, exportedFieldKey, currentDictionary) : SavedAudioFileName;
//                                            exportFields[i] += SavedAudioFileName;
                                        }
//                                        else if (exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_AUDIOTAG)) {
//                                            exportFields[i] += renderText(SavedAudioFileName, exportedFieldKey);
//                                        }
                                        //"全sound tag，苹果专用下载字段 Remarks字段"
                                        else if (exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_SOUNDTAG)) {
                                            exportFields[i] += PlayAudioManager.getSoundTag(PopupActivity.this, SavedAudioFileName);
                                        }


                                        continue;
                                    }

                                    if (exportedFieldKey.equals(Constant.DICT_FIELD_SENTENCE_PICKED_TTS_SOUNDTAG)) {
                                        String readContent = StringUtil.htmlTagFilter(mTextToProcess);
                                        if (!readContent.isEmpty()) {
                                            exportFields[i] += getTtsTag(readContent);
                                        }

                                        continue;
                                    }

                                    //其他字段
                                    if (def.hasElement(exportedFieldKey)) {
                                        //Constant.DICT_FIELD_DEFINITION 释义
//                                        if (exportedFieldKey.equals(Constant.DICT_FIELD_DEFINITION)) {
//                                            //                                            if(currentDictionary instanceof DictTango) {
//                                            //                                                int min = 0;
//                                            //                                                int max = textViewDefinition.getText().length();
//                                            //                                                if (textViewDefinition.isFocused()) {
//                                            //                                                    final int selStart = textViewDefinition.getSelectionStart();
//                                            //                                                    final int selEnd = textViewDefinition.getSelectionEnd();
//                                            //
//                                            //                                                    min = Math.max(0, Math.min(selStart, selEnd));
//                                            //                                                    max = Math.max(0, Math.max(selStart, selEnd));
//                                            //
//                                            //                                                    exportFields[i] += textViewDefinition.getText().subSequence(min, max).toString();
//                                            //                                                } else {
//                                            //                                                    exportFields[i] += def.getExportElement(exportedFieldKey);
//                                            //                                                }
//                                            //                                            }
//                                            if(currentDictionary instanceof Mdict && !TextUtils.isEmpty(selectedTextMdx)) {
//                                                exportFields[i] += keyArrLength > 1 ? renderText(selectedTextMdx, exportedFieldKey, currentDictionary) : selectedTextMdx;
//                                                selectedTextMdx = "";
//                                            }
//                                            // Perform your definition lookup with the selected text
//                                            else {
//                                                String text = def.getExportElement(exportedFieldKey);
//                                                exportFields[i] += keyArrLength > 1 ? renderText(text, exportedFieldKey, currentDictionary) : text;
//                                            }
//                                        }
                                        String text = mdxEleSelectedMap.get(exportedFieldKey);
                                        if(currentDictionary instanceof Mdict && !TextUtils.isEmpty(text)) {
                                            exportFields[i] += keyArrLength > 1 ? renderText(text, exportedFieldKey, currentDictionary) : text;
                                        }
                                        else if(keyArrLength> 1) {
                                            text = def.getExportElement(exportedFieldKey);
                                            exportFields[i] += renderText(text, exportedFieldKey, currentDictionary);
                                        } else {
                                            exportFields[i] += def.getExportElement(exportedFieldKey).replaceAll("\\s{2,}", " ");
                                        }

                                        continue;
                                    }
//                                    exportFields[i] += "";
                                }
                                if(keyArrLength > 1 &&
                                        !(exportFields[i].startsWith("[") && exportFields[i].endsWith("]")) &&
                                        !StringUtils.isEmpty(exportFields[i])) {
                                    String begin = "<div class=\"complex\">";
                                    String end = "</div>";

                                    if(currentDictionary instanceof Mdict && exportedFieldKeys.contains(Constant.MDX_ADD_TAG)) {
                                        if (!TextUtils.isEmpty(def.getCssName())) {
                                            begin += String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\"/>", def.getCssName());
                                        }
                                        if (!TextUtils.isEmpty(def.getJsName())) {
                                            begin += String.format("<script type=\"text/javascript\" src=\"%s\"></script>", def.getJsName());
                                        }
                                    }
                                    exportFields[i] = begin + exportFields[i] + end;
                                }
                                i++;
                            }

                            mdxEleSelectedMap.clear();

                            if (mUpdateNoteId == 0) {
                                /////////////////
                                long deckId = currentOutputPlan.getOutputDeckId();
                                long modelId = currentOutputPlan.getOutputModelId();
                                Trace.i("popup_mTagEditedByUser", mTagEditedByUser.toString());
                                Long result = mAnkiDroid.getApi().addNote(modelId, deckId, exportFields, mTagEditedByUser);

                                if (result != null) {
                                    ToastUtil.show( R.string.str_added);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        btnAddDefinition.setBackground(ContextCompat.getDrawable(
                                                PopupActivity.this, Utils.getResIdFromAttribute(PopupActivity.this, R.attr.icon_add_done)));
                                    }
//                                    clearBigbangSelection();
                                    mNoteEditedByUser = "";
                                    //attach the noteid to the button
                                    btnAddDefinition.setTag(R.id.TAG_NOTE_ID, result);
                                    mPreNoteId = result;
                                    //if there is a note id field in the model, update the note
                                    int count = 0;
                                    for (String field : currentOutputPlan.getFieldsMap().keySet()) {
                                        if (field.replace(" ", "").toLowerCase().equals(MODE_NOTEID)) {
                                            exportFields[count] = result.toString();
                                            boolean success = mAnkiDroid.getApi().updateNoteFields(
                                                    result.longValue(),
                                                    exportFields
                                            );
                                            if (!success) {
                                                ToastUtil.show( R.string.str_error_noteid);
                                            }
                                            break;
                                        }
                                        count++;
                                    }
                                    //save note add
                                    HistoryUtil.saveNoteAdd("", getBoldSentence(bigBangLayout.getLines()),
                                            currentDictionary.getDictionaryName(),
                                            textViewDefinition.getText().toString(),
                                            mTranslatedResult,
                                            mNoteEditedByUser,
                                            mTagEditedByUser.toString()
                                    );
                                } else {
                                    ToastUtil.show( R.string.str_failed_add);
                                }
                            } else {//there's note id, so we need to retrieve note first
                                NoteInfo note = mAnkiDroid.getApi().getNote(mUpdateNoteId);
                                String[] original = note.getFields();
                                Set<String> tags = note.getTags();
//                                if (original == null || original.length != exportFields.length) {
                                if (original == null) {
                                    ToastUtil.show( R.string.str_error_notetype_noncompatible);
                                    return;
                                }

                                String[] exportFieldsCurrentPlan = mAnkiDroid.getApi().getFieldList(currentOutputPlan.getOutputModelId());
                                String[] exportFieldsUpdateNote = mAnkiDroid.getApi().getFieldList(note.getMid());
                                String[] temp = new String[exportFieldsUpdateNote.length];
                                String[] finalExportFields = exportFields;
                                IntStream.range(0, exportFieldsUpdateNote.length)
                                        .forEach(ii -> {
                                            boolean assigned = false;
                                            for (int jj = 0; jj < exportFieldsCurrentPlan.length; jj++) {
                                                if (exportFieldsUpdateNote[ii].equals(exportFieldsCurrentPlan[jj])) {
                                                    temp[ii] = finalExportFields[jj];
                                                    assigned = true;
                                                    break;
                                                }
                                            }
                                            if (!assigned) {
                                                temp[ii] = "";
                                            }
                                        });
                                exportFields = temp;

                                if (mUpdateAction != null && mUpdateAction.equals(MODE_REPLACE)) {
                                    //replace
                                    for (int j = 0; j < original.length; j++) {
                                        if (exportFields[j].isEmpty()) {
                                            exportFields[j] = original[j];
                                        }
                                    }

                                } else {
                                    //append
                                    for (int j = 0; j < original.length; j++) {
//                                        if (original[j].trim().isEmpty() || exportFields[j].trim().isEmpty()) {
//                                            exportFields[j] = original[j] + exportFields[j];
//                                        } else {
//                                            exportFields[j] = original[j] + "<br/>" + exportFields[j];
//                                        }
                                        exportFields[j] = original[j] + exportFields[j];
                                    }
                                }
                                //we need to check the tag used by user is already in the tags, if not, add it
                                tags.addAll(mTagEditedByUser);
                                boolean success = mAnkiDroid.getApi().updateNoteFields(mUpdateNoteId, exportFields);
                                boolean successTag = mAnkiDroid.getApi().updateNoteTags(mUpdateNoteId, tags);
                                if (success && successTag) {
                                    ToastUtil.show( R.string.str_note_updated);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        btnAddDefinition.setBackground(ContextCompat.getDrawable(
                                                PopupActivity.this, Utils.getResIdFromAttribute(PopupActivity.this, R.attr.icon_add_done)));
                                    }
                                    //btnAddDefinition.setEnabled(false);
                                } else {
                                    ToastUtil.show( R.string.str_error_note_update);
                                }
                            }
                            if (settings.getAutoCancelPopupQ()) {
                                if (fetch == null) {
                                    finish();
                                } else {
                                    if (!isFetchDownloading) {
                                        finish();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            ToastUtil.show( e.getLocalizedMessage());
                        }
                    }
                });
        return view;
    }

    private  void setupHtmlTagButton(final EditText edt, final Button btn, String front, String behind) {
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editable edit = edt.getText();
                        int start = edt.getSelectionStart();
                        int end = edt.getSelectionEnd();
                        Trace.i("start : end", String.format("%d : %d", start, end));
                        if(start < end) {
                            edt.setText(StringUtil.appendTagAll(edit.toString(), edit.toString().substring(start, end), front, behind));
                            Spannable spanText = (Spannable) edt.getText();
                            Selection.setSelection(spanText, end + front.length() + behind.length());
                        } else {
                            edt.setText(edit.toString().substring(0, start) + front + behind + edit.toString().substring(start));
                            Spannable spanText = (Spannable) edt.getText();
                            Selection.setSelection(spanText, start + front.length());
                        }
                        edt.setFocusable(true);
                        edt.setFocusableInTouchMode(true);
                        edt.requestFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) edt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(edt, 0);
                    }
                }
        );
    }
    private void setupEditNoteDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PopupActivity.this);
        LayoutInflater inflater = PopupActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_note, null);
        dialogBuilder.setView(dialogView);

        MathxView mathPreview;
        NoteEditText result;
        RelativeLayout resultRL;
//        TextView tilHint;
        LinearLayout tagArrAWLL, performEditArrLL, containerLl, viewerOnContainerLl;//, editorOnContainerLl;

        result = (NoteEditText) dialogView.findViewById(R.id.result_net);
        result.setBackgroundResource(R.drawable.backgroup_cursor);
        result.setHorizontallyScrolling(false);
        result.setText(mNoteEditedByUser);
        result.setSelection(mNoteEditedByUser.length());
        resultRL = (RelativeLayout) dialogView.findViewById(R.id.result_rl);
        containerLl = (LinearLayout) dialogView.findViewById(R.id.layout_container);
        viewerOnContainerLl = (LinearLayout) dialogView.findViewById(R.id.layout_container_view);
//        editorOnContainerLl = (LinearLayout) dialogView.findViewById(R.id.layout_container_edit);
//        tilHint = (TextView) dialogView.findViewById(R.id.ocr_result_key);
        //MathView
        mathPreview = (MathxView) dialogView.findViewById(R.id.mathPreview);
        mathPreview.setInitialScale(160);
        mathPreview.setClickable(true);
        mathPreview.setVerticalScrollBarEnabled(true);
        mathPreview.setHorizontalScrollBarEnabled(false);
        mathPreview.getSettings().setDisplayZoomControls(true);
        mathPreview.getSettings().setBuiltInZoomControls(false);
        mathPreview.getSettings().setSupportZoom(true);

        if(!result.getText().toString().equals(""))
            mathPreview.setText(result.getText().toString());
        //获取屏幕的宽高
        heightDpChanged = ScreenUtils.getAvailableScreenHeight(PopupActivity.this);
        widthDpChanged = ScreenUtils.getScreenWidth(PopupActivity.this);

        // viewerOnContainerLl containerLl布局
        //根据屏幕旋转状态设置布局参数
        //横屏状态
        if (getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            heightDpChanged = heightDpChanged * 25 / 100 * heightDpChanged / NORMAL_HEIGHT;
            result.setMaxHeight(heightDpChanged * 2);
//            ocrResult.setMaxHeight(heightDpChanged * 97 / 200);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewerOnContainerLl.getLayoutParams();
//            params.width = widthDpChanged / 4;
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.CENTER_HORIZONTAL;
            viewerOnContainerLl.setLayoutParams(params);

            containerLl.setOrientation(LinearLayout.HORIZONTAL);
            //竖屏状态
        } else {
            heightDpChanged = heightDpChanged * 10 / 100 * heightDpChanged / NORMAL_HEIGHT;
            result.setMaxHeight(heightDpChanged * 2);

            containerLl.setOrientation(LinearLayout.VERTICAL);
        }

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localDisplayMetrics);


        // mathPreview resultRL布局
        if (true) {
//            heightDpChanged = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDpChanged, getResources().getDisplayMetrics());

            viewerOnContainerLl.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) resultRL.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            resultRL.setLayoutParams(layoutParams);

            mathPreview.setMaxHeight(heightDpChanged * 2 + ScreenUtils.dp2px(getApplicationContext(), 20));
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mathPreview.getLayoutParams();
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            mathPreview.setMaxHeight(heightDpChanged * 2 + ScreenUtils.dp2px(getApplicationContext(), 20));
            mathPreview.setLayoutParams(layoutParams);
        }

        //start--标签按钮和和撤回按钮
        tagArrAWLL = dialogView.findViewById(R.id.layout_tag_buttons);
        MLLabel[] mixTagArr = ArrayUtils.concat(Constant.latexTagArr, Constant.htmlTagArr);
        for (MLLabel tag : mixTagArr) {
            final MLLabelButton btn = new MLLabelButton(PopupActivity.this);
            btn.setupHtmlTag(result, tag);
            Paint p = btn.getPaint();
            p.setTypeface(btn.getTypeface());
            p.setTextSize(btn.getTextSize());
            float needWidth = btn.getPaddingLeft() + btn.getPaddingRight() + p.measureText(btn.getText().toString());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.leftMargin = ScreenUtils.dp2px(getApplicationContext(), 2);
            params.rightMargin = ScreenUtils.dp2px(getApplicationContext(), 2);
            params.height = ScreenUtils.dp2px(getApplicationContext(), 28);
            params.width = ScreenUtils.dp2px(getApplicationContext(), 28);
            if (params.width < needWidth)
                params.width = (int) (needWidth + 10);
            tagArrAWLL.addView(btn, params);
        }

        performEditArrLL = dialogView.findViewById(R.id.layout_perform_edit_buttons);
        PerformEdit performEdit = new PerformEdit(result);
        for (PerformEditButton.ActionEnum action : PerformEditButton.ActionEnum.values()) {
            if(action == PerformEditButton.ActionEnum.format) continue;

            final PerformEditButton btn = new PerformEditButton(PopupActivity.this);
            btn.setupPerformEditAction(action, performEdit);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.leftMargin = ScreenUtils.dp2px(getApplicationContext(), 2);
            params.rightMargin = ScreenUtils.dp2px(getApplicationContext(), 2);
            params.height = ScreenUtils.dp2px(getApplicationContext(), 24);
            params.width = ScreenUtils.dp2px(getApplicationContext(), 24);
            performEditArrLL.addView(btn, params);
        }
        //end

        result.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                UrlCountUtil.onEvent(UrlCountUtil.CLICK_CAPTURERESULT_OCRRESULT);
//                if (!TextUtils.isEmpty(ocrResult.getText())) {
//                    Intent intent = new Intent(CaptureResultActivity.this, BigBangActivity.class);
//                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra(BigBangActivity.TO_SPLIT_STR, ocrResult.getText().toString());
//                    startActivity(intent);
//                    finish();
//                }
                return true;
            }

        });

        result.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String latex = s.toString().trim();
//                if(!latex.equals("")) {
//                    mathPreview.setVisibility(View.VISIBLE);
                String change = convertInline(latex);
                if (!mathPreview.getText().equals(change)) {
                    latex = change;
//                        ToastUtil.show(mathPreview.getmScrollY() +"");
                    mathPreview.setText(latex);
                }
//					latex = String.format("$$%s$$", latex);

//                }
//                else
//                    mathPreview.setVisibility(View.GONE);
            }
        });

        result.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.hasFocus()) {
//                    mathPreview.setMaxHeight(heightDpChanged * 2);
//                    if(mathPreview.getContentHeight()>=mathPreview.getScrollY()+heightDpChanged)
//                        mathPreview.setmScrollY(mathPreview.getScrollY()+heightDpChanged);
                } else {
//                    mathPreview.setMaxHeight(heightDpChanged * 2);
                    if (mathPreview.getScrollY() - heightDpChanged >= 0)
                        mathPreview.setmScrollY(mathPreview.getScrollY() - heightDpChanged);
                }
            }
        });
        mathPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        ((MathxView) v).setmScrollY(v.getScrollY());

                        float percent = (((float) v.getScrollY()) / (mathPreview.getContentHeight() * mathPreview.getScale() - v.getHeight()));
                        percent = percent >= 1 ? 1 : percent;
//                        ToastUtil.show(String.format(
//                                "v.getScrollY() %d\nmathPreview.getContentHeight() %f\npercent %f",
//                                v.getScrollY(),
//                                mathPreview.getContentHeight() * mathPreview.getScale() - v.getHeight(),
//                                percent));
                        result.scrollTo(0, (int) (percent * result.getLineCount() * result.getLineHeight()));
                }
                return false;
            }
        });

        dialogBuilder.setTitle(null);
        //dialogBuilder.setMessage("输入笔记");
        dialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mNoteEditedByUser = result.getText().toString().replaceAll("\\n|\\r|\\t", "");
            }
        });
//                        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                //pass
//                            }
//                        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    private void setupEditSentenceDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PopupActivity.this);
        LayoutInflater inflater = PopupActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_sentence, null);
        dialogBuilder.setView(dialogView);

        NoteEditText result;
//        TextView tilHint;
        LinearLayout performEditArrLL; //tagArrAWLL, containerLl, viewerOnContainerLl;//, editorOnContainerLl;

        result = (NoteEditText) dialogView.findViewById(R.id.result_net);
        result.setBackgroundResource(R.drawable.backgroup_cursor);
        result.setHorizontallyScrolling(false);
        result.setTextSize(settings.getPopupFontSize());
        result.setLines(320/settings.getPopupFontSize());
        result.setText(mTextToProcess);
        result.setSelection(mTextToProcess.length());
        result.requestFocus();
        result.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(result, InputMethodManager.SHOW_IMPLICIT);
            }
        });
//        containerLl = (LinearLayout) dialogView.findViewById(R.id.layout_container);
//        editorOnContainerLl = (LinearLayout) dialogView.findViewById(R.id.layout_container_edit);
//        tilHint = (TextView) dialogView.findViewById(R.id.ocr_result_key);

        //获取屏幕的宽高
        heightDpChanged = ScreenUtils.getAvailableScreenHeight(PopupActivity.this);
        widthDpChanged = ScreenUtils.getScreenWidth(PopupActivity.this);

        // viewerOnContainerLl containerLl布局
        //根据屏幕旋转状态设置布局参数
        //横屏状态
//        if (getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            heightDpChanged = heightDpChanged * 25 / 100 * heightDpChanged / NORMAL_HEIGHT;
//            result.setMaxHeight(heightDpChanged * 2);
//
//            containerLl.setOrientation(LinearLayout.HORIZONTAL);
//            //竖屏状态
//        } else {
//            heightDpChanged = heightDpChanged * 10 / 100 * heightDpChanged / NORMAL_HEIGHT;
//            result.setMaxHeight(heightDpChanged * 2);
//
//            containerLl.setOrientation(LinearLayout.VERTICAL);
//        }

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localDisplayMetrics);

        //start--标签按钮和和撤回按钮
//        MLLabel[] mixTagArr = ArrayUtils.concat(Constant.latexTagArr, Constant.htmlTagArr);
//        for (MLLabel tag : mixTagArr) {
//            final MLLabelButton btn = new MLLabelButton(PopupActivity.this);
//            btn.setupHtmlTag(result, tag);
//            Paint p = btn.getPaint();
//            p.setTypeface(btn.getTypeface());
//            p.setTextSize(btn.getTextSize());
//            float needWidth = btn.getPaddingLeft() + btn.getPaddingRight() + p.measureText(btn.getText().toString());
//        }

        performEditArrLL = dialogView.findViewById(R.id.layout_perform_edit_buttons);
        PerformEdit performEdit = new PerformEdit(result);
        for (PerformEditButton.ActionEnum action : PerformEditButton.ActionEnum.values()) {
            if(action == PerformEditButton.ActionEnum.format) continue;

            final PerformEditButton btn = new PerformEditButton(PopupActivity.this);
            btn.setupPerformEditAction(action, performEdit);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.leftMargin = ScreenUtils.dp2px(getApplicationContext(), 2);
            params.rightMargin = ScreenUtils.dp2px(getApplicationContext(), 2);
            params.height = ScreenUtils.dp2px(getApplicationContext(), 24);
            params.width = ScreenUtils.dp2px(getApplicationContext(), 24);
            performEditArrLL.addView(btn, params);
        }
        //end

        result.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                UrlCountUtil.onEvent(UrlCountUtil.CLICK_CAPTURERESULT_OCRRESULT);
//                if (!TextUtils.isEmpty(ocrResult.getText())) {
//                    Intent intent = new Intent(CaptureResultActivity.this, BigBangActivity.class);
//                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra(BigBangActivity.TO_SPLIT_STR, ocrResult.getText().toString());
//                    startActivity(intent);
//                    finish();
//                }
                return true;
            }

        });

        dialogBuilder.setTitle(null);
        //dialogBuilder.setMessage("输入笔记");
        dialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String text = result.getText().toString().trim();//replaceAll("\\n|\\r|\\t", "");
                if(!TextUtils.isEmpty(text)) {
                    mTextToProcess = text;
                    populateWordSelectBox();
                }
            }
        });
//                        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                //pass
//                            }
//                        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void setupEditTagDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PopupActivity.this);
        LayoutInflater inflater = PopupActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_tag, null);
        dialogBuilder.setView(dialogView);
        final AutoCompleteTextView editTag = (AutoCompleteTextView) dialogView.findViewById(R.id.edit_tag);
        final CheckBox checkBoxSetAsDefaultTag = (CheckBox) dialogView.findViewById(R.id.checkbox_as_default_tag);
        final ChipGroup tagChipGroup = (ChipGroup) dialogView.findViewById(R.id.tag_chip_list);
        editTag.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if (mTagEditedByUser.size() > 0) {
            String text = Utils.fromTagSetToString(mTagEditedByUser);
            editTag.setText(text);
            editTag.setSelection(text.length());
        }
        tagChipGroup.setSingleSelection(false);
        final List<UserTag> userTags = DataSupport.findAll(UserTag.class);
        for (UserTag userTag : userTags) {
            final Chip chip = (Chip) inflater.inflate(R.layout.tag_chip_item, null);
            chip.setText(userTag.getTag());
            chip.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                mTagEditedByUser.add(chip.getText().toString());
                            } else {
                                mTagEditedByUser.remove(chip.getText().toString());
                            }
                            //tag1,tag2,tag3
                            String text = Utils.fromTagSetToString(mTagEditedByUser);
                            editTag.setText(text);
                            editTag.setSelection(text.length());
                        }
                    }
            );
            if (mTagEditedByUser.contains(chip.getText().toString())) {
                chip.setChecked(true);
            }
            tagChipGroup.addView(chip);
        }
//        String[] arr = new String[userTags.size()];
//        for (int i = 0; i < userTags.size(); i++) {
//            arr[i] = userTags.get(i).getTag();
//        }
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PopupActivity.this,
//                R.layout.support_simple_spinner_dropdown_item, arr);
//        editTag.setAdapter(arrayAdapter);
//        editTag.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (editTag.getText().toString().isEmpty()) {
//                    editTag.showDropDown();
//                }
//                return false;
//            }
//        });
        boolean setDefaultQ = settings.getSetAsDefaultTag();
        checkBoxSetAsDefaultTag.setChecked(setDefaultQ);
        dialogBuilder.setTitle(R.string.dialog_tag);
        //dialogBuilder.setMessage("输入笔记");
        dialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String tag = editTag.getText().toString().trim();
                if (tag.isEmpty()) {
                    if (checkBoxSetAsDefaultTag.isChecked()) {
                        mTagEditedByUser.clear();
                        ToastUtil.show( R.string.tag_cant_be_blank);
                    } else {
                        settings.setSetAsDefaultTag(false);
                        mTagEditedByUser.clear();
                    }
                    return;
                } else {
                    mTagEditedByUser = Utils.fromStringToTagSet(editTag.getText().toString());
                    settings.setSetAsDefaultTag(checkBoxSetAsDefaultTag.isChecked());
                    settings.setDefaultTag(editTag.getText().toString());
                    for (String t : mTagEditedByUser) {
                        if (!userTags.contains(t)) { //add new tag
                            UserTag userTag = new UserTag(t);
                            userTag.save();
                        }
                    }
                }

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void initFetch() {
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(3)
                .build();
        fetch = Fetch.Impl.getInstance(fetchConfiguration);

        fetchListener = new FetchListener() {
            @Override
            public void onAdded(@NotNull Download download) {

            }

            @Override
            public void onQueued(@NotNull Download download, boolean b) {
            }

            @Override
            public void onWaitingNetwork(@NotNull Download download) {
            }

            @Override
            public void onCompleted(@NotNull Download download) {
                if (BuildConfig.isDebug)
                    ToastUtil.show( download.getFile() + "下载成功");
                else
                    ToastUtil.show( R.string.downlaod_completed);
                mMediaProgress.setVisibility(View.GONE);

                isFetchDownloading = false;
                if (settings.getAutoCancelPopupQ()) {
                    finish();
                }
            }

            @Override
            public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {
                ToastUtil.show( "Download Failed!");
                mMediaProgress.setVisibility(View.GONE);
                isFetchDownloading = false;

                if (settings.getAutoCancelPopupQ()) {
                    finish();
                }
            }

            @Override
            public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {
            }

            @Override
            public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {
            }

            @Override
            public void onProgress(@NotNull Download download, long l, long l1) {
            }

            @Override
            public void onPaused(@NotNull Download download) {

            }

            @Override
            public void onResumed(@NotNull Download download) {

            }

            @Override
            public void onCancelled(@NotNull Download download) {

            }

            @Override
            public void onRemoved(@NotNull Download download) {

            }

            @Override
            public void onDeleted(@NotNull Download download) {

            }
        };
        fetch.addListener(fetchListener);
    }

    //cancel auto completetextview focus
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof AutoCompleteTextView) {
            View currentFocus = getCurrentFocus();
            int screenCoords[] = new int[2];
            currentFocus.getLocationOnScreen(screenCoords);
            float x = event.getRawX() + currentFocus.getLeft() - screenCoords[0];
            float y = event.getRawY() + currentFocus.getTop() - screenCoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < currentFocus.getLeft() ||
                    x >= currentFocus.getRight() ||
                    y < currentFocus.getTop() ||
                    y > currentFocus.getBottom())) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                v.clearFocus();
            }
        }
        return ret;
    }

    private void initVideoMediaPlayer() {

//        if (mVideoMediaPlayer == null) {
        mVideoMediaPlayer = new MediaPlayer();
//        Trace.e("new MediaPlayer", String.valueOf(mSurfaceView.getVisibility()));


        //安卓6.0以后
        if (Build.VERSION.SDK_INT >= 23) {
            //配置播放器
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build();
            mVideoMediaPlayer.setAudioAttributes(aa);

        } else {
            mVideoMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

        mVideoMediaPlayer.setOnVideoSizeChangedListener(
                new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        changeVideoSize();
                    }
                }
        );

        mVideoMediaPlayer.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        surfaceHolder = mSurfaceView.getHolder();
                        surfaceHolder.addCallback(
                                new SurfaceHolder.Callback() {
                                    int currentPosition = 0;

                                    @Override
                                    public void surfaceCreated(SurfaceHolder holder) {
                                        mVideoMediaPlayer.setDisplay(holder);
                                    }

                                    @Override
                                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                                            mVideoMediaPlayer.setDisplay(holder);
                                    }

                                    @Override
                                    public void surfaceDestroyed(SurfaceHolder holder) {
//                    mVideoMediaPlayer.release();
                                        if (mVideoMediaPlayer != null && mVideoMediaPlayer.isPlaying()) {
                                            currentPosition = mVideoMediaPlayer.getCurrentPosition();
                                            mVideoMediaPlayer.stop();
                                        }
                                    }

                                });
                        mSurfaceView.setVisibility(View.VISIBLE);
//                            mVideoMediaPlayer.setDisplay(surfaceHolder);
                        mMediaProgress.setVisibility(View.GONE);
                        mVideoMediaPlayer.start();
//                        Trace.e("start", String.valueOf(mVideoMediaPlayer.isPlaying()));
                    }
                }
        );

        mVideoMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mSurfaceView.setVisibility(View.VISIBLE);
                mMediaProgress.setVisibility(View.GONE);
            }
        });

        mVideoMediaPlayer.setOnErrorListener(
                new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        mp.reset();
                        ToastUtil.show( "Failed to play audio, check your connection.");
                        mSurfaceView.setVisibility(View.GONE);
                        mMediaProgress.setVisibility(View.GONE);
                        return false;
                    }
                }
        );
//        }

        try {
            mMediaProgress.setVisibility(View.GONE);
            mVideoMediaPlayer.reset();
        } catch (IllegalStateException e) {
        }
    }

    private void killVideoMediaPlayer() {
        if (mVideoMediaPlayer != null) {
            try {
                mVideoMediaPlayer.reset();
                mVideoMediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //change surfaceView size by lisenting mplayer
    private void changeVideoSize() {
        // 首先取得video的宽和高
        int videoWidth = mVideoMediaPlayer.getVideoWidth();
        int videoHeight = mVideoMediaPlayer.getVideoHeight();

        float max;
        Display dis = getWindowManager().getDefaultDisplay();

        Point point = new Point();
        dis.getSize(point);
        if (getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            max = (float) 0.8 * point.x / (float) videoWidth / 2;
        } else {
            max = (float) 0.8 * point.x / (float) videoWidth;
//            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
        int nVideoWidth = (int) Math.ceil((float) videoWidth * max);
        int nVideoHeight = (int) Math.ceil((float) videoHeight * max);

        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
        mSurfaceView.setLayoutParams(new LinearLayout.LayoutParams(nVideoWidth, nVideoHeight));

        //surfaceView始终在屏幕内完整显示
        int[] svPos = new int[2];
        int[] cvToolobarPos = new int[2];
        mSurfaceView.getLocationOnScreen(svPos);
        int svY = svPos[1];
        mCvPopupToolbar.getLocationOnScreen(cvToolobarPos);
        int cvY = cvToolobarPos[1];

        int deviation =svY + nVideoHeight - cvY;

        if (deviation > 0) {
            scrollView.scrollTo(mSurfaceView.getScrollX(), scrollView.getScrollY() + deviation);
        }
    }

    private void StopPlayingAll() {
        PlayAudioManager.stop();
        if (mVideoMediaPlayer != null)
            mVideoMediaPlayer.reset();
//        if (mTts != null & mTts.isSpeaking())
//            mTts.stop();
        if (mTts != null)
            mTts.speak("",
                    TextToSpeech.QUEUE_FLUSH,
                    null, "null");
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        isRedrawing = true;
    }

    @Override
    public void onDestroy() {
        PlayAudioManager.killAll();
        killVideoMediaPlayer();
//        killTts();
//        if(mTts != null) {
//            mTts.stop();
//            mTts.shutdown();
//        }
        mTts.speak("",
                TextToSpeech.QUEUE_FLUSH,
                null, "null");
        fetch.removeListener(fetchListener);
        try {
            File preImg = new File(StorageUtils.getIndividualCacheDirectory(PopupActivity.this), settings.get(Settings.SCREENSHOT_NAME, ""));
            if (preImg.exists() && preImg.isFile() && !isRedrawing)
                Utils.deleteFile(preImg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        settings.put(Settings.KEYBOARD_STATE, false);
        settings.put(Settings.POPUP_DISPLAY_STATE, false);
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    private void startCBService() {
        Intent intent = new Intent(this, CBWatcherService.class);
        startService(intent);
    }

    private void stopCBService() {
        Intent intent = new Intent(this, CBWatcherService.class);
        stopService(intent);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        btnSearch.setVisibility(View.GONE);
    }

    private void showSearchButton() {
        progressBar.setVisibility(View.GONE);
        btnSearch.setVisibility(View.VISIBLE);
    }

    private void showSurfaceView(boolean show) {

        mSurfaceView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showPronounce(boolean shouldShow) {
        btnPronounce.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
    }

    private void showTranslateNormal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBtnTranslation.setImageResource(Utils.getResIdFromAttribute(this, R.attr.icon_translate_normal));
        }
    }

    private void showTranslateLoading() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBtnTranslation.setImageResource(Utils.getResIdFromAttribute(this, R.attr.icon_translate_wait));
        }
    }

    private void showTranslateDone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBtnTranslation.setImageResource(Utils.getResIdFromAttribute(this, R.attr.icon_translate_done));
        }
    }

    private void showTranslationCardView(boolean show) {
        mCardViewTranslation.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSelected(String text) {
        String currentWord = FieldUtil.getSelectedText(bigBangLayout.getLines());
        if (!currentWord.equals(acTextView.getText().toString())) {
            mCurrentKeyWord = currentWord;
            acTextView.setText(currentWord);
            if (settings.get(Settings.POPUP_SWITCH_AUTO_SEARCH, true)) {
                asyncSearch(currentWord);
            }
        }
    }

    @Override
    public void onSearch(String text) {

    }

    @Override
    public void onShare(String text) {

    }

    @Override
    public void onCopy(String text) {

    }

    @Override
    public void onTrans(String text) {

    }

    @Override
    public void onDrag() {

    }

    @Override
    public void onSwitchType(boolean isLocal) {

    }

    @Override
    public void onSwitchSymbol(boolean isShow) {

    }

    @Override
    public void onSwitchSection(boolean isShow) {

    }

    @Override
    public void onDragSelection() {

    }

    @Override
    public void onCancel() {
        acTextView.setText("");
        asyncSearch("");
    }

    void vibarate(int ms) {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(ms);
    }

    void clearBigbangSelection() {
        for (BigBangLayout.Item item : bigBangLayout.getItemAll()) {
//            if (item.getText().equals(mTargetWord)) {
                item.setSelected(false);
//            }
        }
    }

    void selectAllBigbangSelection() {
        for (BigBangLayout.Item item : bigBangLayout.getItemAll()) {
//            if (item.getText().equals(mTargetWord)) {
//            if(!item.getText().equals("\n"))
                item.setSelected(true);
//            }
        }
        acTextView.setText(mTextToProcess);
        if(settings.get(Settings.POPUP_SWITCH_AUTO_SEARCH, true))
            asyncSearch(mTextToProcess);
    }

    private void makeTextViewSelectAndSearch(final TextView textView) {
        textView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Remove the "copy all" option
                menu.removeItem(android.R.id.cut);
                //menu.removeItem(android.R.id.copy);
                return true;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Called when action mode is first created. The menu supplied
                // will be used to generate action buttons for the action mode

                // Here is an example MenuItem
                menu.add(0, 1, 0, "Definition").setIcon(R.drawable.ic_ali_search);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Called when an action mode is about to be exited and
                // destroyed
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        int min = 0;
                        int max = textView.getText().length();
                        if (textView.isFocused()) {
                            final int selStart = textView.getSelectionStart();
                            final int selEnd = textView.getSelectionEnd();

                            min = Math.max(0, Math.min(selStart, selEnd));
                            max = Math.max(0, Math.max(selStart, selEnd));
                        }
                        // Perform your definition lookup with the selected text
                        final String selectedText = textView.getText().subSequence(min, max).toString();
                        // Finish and close the ActionMode
                        mode.finish();
                        acTextView.setText(selectedText);
                        asyncSearch(selectedText);
                        //重新定位焦点，避免补全提示遮挡
                        acTextView.clearFocus();
                        return true;
                    default:
                        break;
                }
                return false;
            }

        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

//            List<String> ls = mTts.getVoices().stream().map(voice -> {return voice.getName();}).sorted().collect(Collectors.toList());
//            Set<Voice> ls = mTts.getVoices();
//            for (Voice v : ls) {
//                Trace.i("Voice", v.toString());
//            }
            PronounceManager.initTtsVoiceNames(mTts.getVoices());

//            populateLanguageSpinner();
//            changePronounceToSpinner(acTextView.getText().toString());
            mTtsStatus = status;
            mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onBeginSynthesis(String utteranceId, int sampleRateInHz, int audioFormat, int channelCount) {
                    super.onBeginSynthesis(utteranceId, sampleRateInHz, audioFormat, channelCount);

                }

                @Override
                public void onDone(String utteranceId) {
                }

                @Override
                public void onError(String utteranceId) {
                    ToastUtil.show( R.string.play_pronounciation_failed);

                }

                @Override
                public void onStop(String utteranceId, boolean interrupted) {
                    super.onStop(utteranceId, interrupted);
                    String text = recordsMap.get(utteranceId);
                    if(!TextUtils.isEmpty(text)) {
                        Trace.i("onStop", text);
                        mTts.synthesizeToFile(text, null, new File(utteranceId), utteranceId);
                    }
//                    mTts.stop();
//                    mTts.shutdown();
                }
            });
        } else {
            Trace.e("tts status OnInit", "TextToSpeech is failed!");
        }

        populatePlanSpinner();
        if(!hasInit) {
            handleIntent();
            processTextFromFxService();
            hasInit = true;
        }
    }

    private void playTts(String text, int pronounceIndex) {
        //TextToSpeech的speak方法有两个重载。
        // 执行朗读的方法
        //speak(CharSequence text,int queueMode,Bundle params,String utteranceId);
        // 将朗读的的声音记录成音频文件
        //synthesizeToFile(CharSequence text,Bundle params,File file,String utteranceId);
        //第二个参数queueMode用于指定发音队列模式，两种模式选择
        //（1）TextToSpeech.QUEUE_FLUSH：该模式下在有新任务时候会清除当前语音任务，执行新的语音任务
        //（2）TextToSpeech.QUEUE_ADD：该模式下会把新的语音任务放到语音任务之后，
        //等前面的语音任务执行完了才会执行新的语音任务
        try {
            PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(pronounceIndex);
            Voice voice = infor.getVoice();
            int result;
            if (voice != null) {
                result = mTts.setVoice(voice);
            } else {
                String[] temp = infor.getLangAndCountry();
                Locale locale = new Locale(temp[0], temp[1]);
                result = mTts.setLanguage(locale);
            }
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Trace.e("error", "This Language is not supported");
//                Trace.e("current Locale", infor.getLocale().toString());
                ToastUtil.show( "暂不支持该语言" + infor.getLocale().toString());
            } else {
                mTts.setPitch(1.0f);
                mTts.setSpeechRate(1.0f);
//                Voice voice = new Voice("", locale, 1, 1, true, null);
//                mTts.setVoice(voice);
                mTts.speak(text,
                        TextToSpeech.QUEUE_FLUSH,
                        null);
            }
        } catch (Exception e) {
            Trace.e("tts speak()", e.getMessage());
        }
    }

    private void playTts(String text) {
        int lastPronounceLanguageIndex = Settings.getInstance(MyApplication.getContext()).getLastPronounceLanguage();
        playTts(text, lastPronounceLanguageIndex);
    }

    private void killTts() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
    }

    private String getCacheAudioUrl(String url) {
        try {
//            Trace.e("download url", url);
            //判断是否存在download临时缓存文件，有则删除
            File file = getCacheFile(url);
//            Trace.e("Existing cached File", file.getAbsolutePath() + file.getNameId());
            if (file.exists()) {
                file.delete();
                Trace.e("mProxy", "is deleted.");
            }

            return mProxy.getProxyUrl(url, true);
        } catch (Exception e) {
            return url;
        }
    }

    private File getCacheFile(String url) {
        File cacheDir = StorageUtils.getIndividualCacheDirectory(PopupActivity.this);
        String fileName = new Md5FileNameGenerator().generate(url) + ".download";
        return new File(cacheDir, fileName);
    }

    private void restoreTtsVoice(String text, String pathOfFile) {
        int lastPronounceLanguageIndex = Settings.getInstance(MyApplication.getContext()).getLastPronounceLanguage();
        restoreTtsVoice(text, pathOfFile, lastPronounceLanguageIndex);
    }

    private void restoreTtsVoice(String action, String text, String pathOfFile) {
        int type = getTypeCurrentDictionary();
        int pickedSentenceLastPronouceLanguageIndex = settings.getRestorePronounceSpinnerIndex(action, type, currentDictionary.isExistAudioUrl(), toLoadTTS);
        restoreTtsVoice(text, pathOfFile, pickedSentenceLastPronouceLanguageIndex);
    }

    private void restoreTtsVoice(String text, String pathOfFile, int pronounceIndex) {
        if(text.isEmpty() || pathOfFile.isEmpty()) return;

        try {
            PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(pronounceIndex);
            Voice voice = infor.getVoice();
            int result;
            if (voice != null) {
                result = mTts.setVoice(voice);
            } else {
                String[] temp = infor.getLangAndCountry();
                Locale locale = new Locale(temp[0], temp[1]);
                result = mTts.setLanguage(locale);
            }
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Trace.e("error", "This Language is not supported");
//                Trace.e("current Locale", infor.getLocale().toString());
                ToastUtil.show( "暂不支持该语言" + infor.getLocale().toString());
            } else {
                HashMap<String, String> map = new HashMap<>();
                recordsMap.put(pathOfFile, text);
                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "record");
                mTts.synthesizeToFile(text, null, new File(pathOfFile), pathOfFile);
//            Trace.w("tts", "restored mp3 file.");
            }
        } catch (Exception e) {
            Trace.e("tts speak()", e.getMessage());
        }
    }

    private void download(Request request, String saveFilePath) {
        Trace.e("url", request.getUrl());
        Trace.e("downloading", saveFilePath);
        if(!isConnect()) {
            ToastUtil.show("无网络，下载失败");
            return;
        }
        if (!(new File(saveFilePath).exists())) {
            request.setPriority(Priority.HIGH);
            request.setNetworkType(NetworkType.ALL);

            isFetchDownloading = true;
            fetch.enqueue(request,
                    new Func<Request>() {
                        @Override
                        public void call(@NotNull Request result) {
                            mMediaProgress.setVisibility(View.VISIBLE);
                            isFetchDownloading = true;
                        }
                    },
                    new Func<Error>() {
                        @Override
                        public void call(@NotNull Error result) {
                            isFetchDownloading = false;
                        }
                    }
            );
        }
    }

    private void scrollTo(View v) {
        int[] svPos = new int[2];
        v.getLocationInWindow(svPos);
        int tvTop = svPos[1];
        int tvBottom = tvTop + v.getHeight();
        int scrollY = scrollView.getScrollY();
        int[] bangPos = new int[2];
        bigBangLayout.getLocationInWindow(bangPos);
        int bangTop = bangPos[1];
        int bangBottom =bangTop + bigBangLayout.getHeight();
        int statusBarY = ScreenUtils.getStatusBarHeight(PopupActivity.this);
        int tvRelativeTop = tvTop - scrollY;
        int tvRelativeBottom = tvBottom - 600;

        Trace.i("TvTop", "tvtop: "+tvTop);
        Trace.i("ScrollY", "scrollY: "+scrollY);
        Trace.i("bangBottom", "bangBottom: "+bangBottom);
        Trace.i("tvRelativeTop", "tvRelativeTop: "+tvRelativeTop);
        Trace.i("viewDefinitionList", "viewDefinitionList length:" + viewDefinitionList.getHeight());

//        if (bangBottom > 600 || bangBottom < 0)
//            scrollView.scrollBy(v.getScrollX(), tvRelativeBottom);
        if (bangBottom < 0)
            scrollView.scrollBy(v.getScrollX(), tvRelativeBottom);
    }

    private String getTtsTag(final String text) {
        int pronounceIndex = Settings.getInstance(MyApplication.getContext()).getLastPronounceLanguage();
        PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(pronounceIndex);

        if(infor.getSoundSourceType() != PronounceManager.SOUND_SOURCE_TTS) return "";

        String[] temp = infor.getLangAndCountry();
        Locale locale = new Locale(temp[0], temp[1]);
        String lang = locale.toString();
        return TextUtils.isEmpty(lang) ? "" :String.format("[anki:tts lang=%s]%s[/anki:tts]", lang, text);

    }

    private String cleanKey(final Definition def, HashMap<String, String> mdxEleSelectedMap) {
        return cleanKey(def, mdxEleSelectedMap, false);
    }

    private String cleanKey(final Definition def, HashMap<String, String> mdxEleSelectedMap, boolean isTranslationIncluded) {
        String keyword = mdxEleSelectedMap.get(Constant.DICT_FIELD_KEYWORD);
        if (currentDictionary instanceof Mdict) {
            return !TextUtils.isEmpty(keyword) ? keyword : def.getExportElement(Constant.DICT_FIELD_KEYWORD);
        }
        String content = def.getSpell();
        if(content.isEmpty()) {
            if (currentDictionary instanceof EudicSentence ||
                    currentDictionary instanceof Getyarn ||
                    currentDictionary instanceof RenRenCiDianSentence ||
                    currentDictionary instanceof Dub91Sentence ||
                    currentDictionary instanceof EnglishSentenceSet ||
                    currentDictionary instanceof BatchClip) {
                content = def.getExportElement(Constant.DICT_FILED_SENTENCE);
            } else if (currentDictionary instanceof Handian) {
                content = !TextUtils.isEmpty(keyword) ? keyword : def.getExportElement(Constant.DICT_FIELD_KEYWORD);
            } else if (currentDictionary instanceof Cloze) {
                content = def.getExportElement(Constant.DICT_FIELD_CLOZE_CONTENT);
            } else {
                content = !TextUtils.isEmpty(keyword) ? keyword : def.getExportElement(Constant.DICT_FIELD_KEYWORD);
//            return def.getExportElement("单词").replaceAll("</?[\\w\\W].*?>", "") + ". " + translation;
            }
        }

        if(isTranslationIncluded) {
            content += ". \n";
            if (currentDictionary instanceof EudicSentence ||
                    currentDictionary instanceof RenRenCiDianSentence ||
                    currentDictionary instanceof Dub91Sentence ||
                    currentDictionary instanceof EnglishSentenceSet) {
                content += def.getExportElement(Constant.DICT_FILED_CHINESE_SENTENCE);
            } else if(currentDictionary instanceof VocabCom ||
                    currentDictionary instanceof Mnemonic ||
                    currentDictionary instanceof CollinsEnEn ||
                    currentDictionary instanceof DictionaryDotCom ||
                    currentDictionary instanceof BingOxford ||
                    currentDictionary instanceof Collins ||
                    currentDictionary instanceof IdiomDict ||
                    currentDictionary instanceof Ode2 ||
                    currentDictionary instanceof WebsterLearners ||
                    currentDictionary instanceof CustomDictionary)
                content += def.getExportElement(Constant.DICT_FIELD_DEFINITION);
        }
        return StringUtil.htmlTagFilter(content);
    }

//    private void changePronounceToSpinner(String word) {
//        if(word.isEmpty() || currentDictionary == null)
//            return;
//
//        int type = getTypeCurrentDictionary();
//        int index = settings.getRestorePronounceSpinnerIndex(Settings.ACTION_CLICK, type, currentDictionary.isExistAudioUrl(), toLoadTTS);
//        String[] languages = PronounceManager.getAvailablePronounceLanguage(currentDictionary, toLoadTTS);
//        //根据文字判断语言并切换发音spinner
//        if (!(currentDictionary.isExistAudioUrl() &&
//                (pronounceLanguageSpinner.getSelectedItemPosition() == PronounceManager.getDictInformationSize() - 1))) {
//
//            if(index == -1 || index >= languages.length -1) {
//                index = PronounceManager.getSoundInforIndexByList(type);
//            }
//            pronounceLanguageSpinner.setSelection(index);
//            pickedSentencePronouceLanguageIndex = settings.getRestorePronounceSpinnerIndex(Settings.ACTION_LONGCLICK, type, currentDictionary.isExistAudioUrl(), toLoadTTS);;
//            if(pickedSentencePronouceLanguageIndex == -1 || pickedSentencePronouceLanguageIndex >= languages.length -1) {
//                pickedSentencePronouceLanguageIndex = PronounceManager.getSoundInforIndexByList(type) != -1 ? PronounceManager.getSoundInforIndexByList(type) : 0;
//                settings.setRestorePronounceSpinnerIndex(Settings.ACTION_LONGCLICK, pickedSentencePronouceLanguageIndex, type, currentDictionary.isExistAudioUrl(), toLoadTTS);
//            }
//        }
//    }

    private int getTypeCurrentDictionary() {
        if (currentDictionary == null) return DictLanguageType.NAN;

        int type = currentDictionary.getLanguageType();

        if(acTextView.getText().toString().isEmpty()) {
            return type;
        }
        if(currentDictionary instanceof DictTango) {
            HashMap<String, Boolean> tangoDictCheckerMap = settings.getTangoDictChckerMap(currentOutputPlan.getPlanName());
            if(!tangoDictCheckerMap.isEmpty()) {
                int resultType =  DictLanguageType.NAN;
                for(String dictName : tangoDictCheckerMap.keySet()) {
                    if(tangoDictCheckerMap.get(dictName) && OutputLocatorPOJO.getOutputlocatorMap().containsKey(dictName)) {
                        int checkedType = (int) Math.pow(2, OutputLocatorPOJO.getOutputlocatorMap().get(dictName).getLangIndex());
                        int wordType = DictLanguageType.getLTIdByWord(acTextView.getText().toString());
                        resultType = checkedType;
                        if(checkedType == wordType) {
                            break;
                        }
                    }
                }
                type = resultType;
            } else {
                type = DictLanguageType.getLTIdByWord(acTextView.getText().toString());
            }
        } else if(currentDictionary.getLanguageType() == DictLanguageType.ALL)
            type = DictLanguageType.getLTIdByWord(acTextView.getText().toString());

        return currentDictionary.getLanguageType() | type;
    }

    private String renderText(String string, final String exportedFieldKey, IDictionary dictionary) {
        if(settings.get(Settings.WEBVIEW_CONTEXT_MENU_RICH_TO_PLAIN, false)
        && BuildConfig.isDebug) {
            return Utils.addSimpleHtmlTag(string, exportedFieldKey, dictionary);
        } else {
            return Utils.addHtmlTag(string, exportedFieldKey, dictionary);
        }
    }

    private boolean isConnect() {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null&& info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        // TODO: handle exception
            Trace.e("isConnect",e.toString());
        }
        return false;
    }

    private String convertInline(String text) {
        String regex = "(\\${1,2})(.+?)(\\${1,2})";
        String latex = text.replaceAll(regex, "\\\\($2\\\\)");
        return latex;
    }
}
