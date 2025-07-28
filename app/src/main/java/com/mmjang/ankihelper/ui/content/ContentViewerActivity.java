package com.mmjang.ankihelper.ui.content;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.content.ContentEntity;
import com.mmjang.ankihelper.data.content.ExternalContent;
import com.mmjang.ankihelper.data.dict.Cloze;
import com.mmjang.ankihelper.domain.PlayAudioManager;
import com.mmjang.ankihelper.domain.PronounceManager;
import com.mmjang.ankihelper.ui.popup.PopupActivity;
import com.mmjang.ankihelper.util.ActivityUtil;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.Utils;

import java.util.Locale;

public class ContentViewerActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    SwipeRefreshLayout swipeRefreshLayout;
    TextView contentTextView;
    ExternalContent externalContent;
    Settings settings;
    int mIndex;
    FloatingActionButton fabtnSearch, fabtnTts;
    String currentContent = "";
    String note = "";
    private int pickedSentencePronouceLanguageIndex;
    TextToSpeech mTts;
    int mTtsStatus;

    Cloze clozeDict;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Settings settings = Settings.getInstance(this);
//        if(settings.getPinkThemeQ()){
//            setTheme(R.style.AppThemePink);
//        }
        ColorThemeUtils.initColorTheme(ContentViewerActivity.this);
        super.onCreate(savedInstanceState);
        ActivityUtil.checkAndStartAnkiDroid(this);
        setContentView(R.layout.activity_content_viewer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        contentTextView = findViewById(R.id.context_text);
        fabtnSearch = findViewById(R.id.add_content);
        fabtnTts = findViewById(R.id.tts_play);
        externalContent = new ExternalContent(this);
        settings = Settings.getInstance(MyApplication.getContext());
        Intent intent = getIntent();
        mIndex = intent.getIntExtra(Constant.INTENT_ANKIHELPER_CONTENT_INDEX, 0);
        clozeDict = new Cloze(getApplicationContext());
        int type = clozeDict.getLanguageType();
        //tts init
        //初始化语音。这是一个异步操作。初始化完成后调用oninitListener(第二个参数)。
        if(mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        mTts = new TextToSpeech(getApplicationContext(), this);
        pickedSentencePronouceLanguageIndex =
                settings.getRestorePronounceSpinnerIndex(Settings.ACTION_LONGCLICK, type, false, true);
        if(pickedSentencePronouceLanguageIndex == -1) {
            pickedSentencePronouceLanguageIndex = PronounceManager.getSoundInforIndexByList(type) != -1 ? PronounceManager.getSoundInforIndexByList(type) : 0;
        }
        final SwipeRefreshLayout.OnRefreshListener swipeRefreshListener = () -> refreshContent(mIndex);

        swipeRefreshLayout.setOnRefreshListener(
                swipeRefreshListener
        );

        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        swipeRefreshListener.onRefresh();
                    }
                }
        );

        fabtnSearch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PopupActivity.class);
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, currentContent);
                        intent.putExtra(Constant.INTENT_ANKIHELPER_NOTE, note);
                        startActivity(intent);
                    }
                }
        );

        fabtnTts.setOnClickListener(v -> {
            String text = contentTextView.getText().toString();
            PronounceManager.getAvailablePronounceLanguage(clozeDict, true);
            PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(pickedSentencePronouceLanguageIndex);

            StopPlayingAll();
            if (infor.getSoundSourceType() == PronounceManager.SOUND_SOURCE_TTS) {
                playTts(text);
            } else {
                String finalAudioUrl = PlayAudioManager.getSoundUrlOnline(ContentViewerActivity.this, text, "", pickedSentencePronouceLanguageIndex);
                PlayAudioManager.playPronounceSound(ContentViewerActivity.this, finalAudioUrl);
            }
        });

        fabtnTts.setOnLongClickListener(v -> {
            int checkedIndex = pickedSentencePronouceLanguageIndex;
            String[] languages = PronounceManager.getAvailablePronounceLanguage(clozeDict, true);
            boolean[] isCheckedArr = new boolean[languages.length];

            for(int i = 0; i < isCheckedArr.length; i++) {
                if(i == checkedIndex)
                    isCheckedArr[i] = true;
                else
                    isCheckedArr[i] = false;
            }

            androidx.appcompat.app.AlertDialog.Builder multiChoiceDialog = new androidx.appcompat.app.AlertDialog.Builder(ContentViewerActivity.this);
            multiChoiceDialog
                    .setTitle(R.string.tv_choose_language_pronounce_picked_sentence);
//                                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
//
//                                });

            multiChoiceDialog.setSingleChoiceItems(languages, checkedIndex,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int type = clozeDict.getLanguageType();
                            settings.setRestorePronounceSpinnerIndex(Settings.ACTION_LONGCLICK, which, type, false, true);
                            pickedSentencePronouceLanguageIndex = which;
                        }
                    });
            multiChoiceDialog.show();

            return false;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityUtil.checkAndStartAnkiDroid(this);
    }

    private void refreshContent(int index) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Integer, Void, ContentEntity> asyncTask = new AsyncTask<Integer, Void, ContentEntity>() {
            @Override
            protected ContentEntity doInBackground(Integer... integers) {
                return externalContent.getRandomContentAt(integers[0],
                        Settings.getInstance(MyApplication.getContext()).getShowContentAlreadyRead());
            }

            @Override
            protected void onPostExecute(ContentEntity contentEntity){
                swipeRefreshLayout.setRefreshing(false);
                if(contentEntity != null){
                    contentTextView.setText(
                            contentEntity.getText()
                    );
                    currentContent = contentEntity.getText();
                    note = contentEntity.getNote();
                }else{
                    Utils.showMessage(ContentViewerActivity.this, "刷新失败");
                }
            }
        };

        asyncTask.execute(index);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInit(int status) {
        mTtsStatus = status;
        if (status == TextToSpeech.SUCCESS) {

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
                    Toast.makeText(ContentViewerActivity.this, R.string.play_pronounciation_failed, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onStop(String utteranceId, boolean interrupted) {
                    super.onStop(utteranceId, interrupted);
                    mTts.stop();
                    mTts.shutdown();
                }
            });
        } else {
            Trace.i("tts status OnInit", "TextToSpeech is failed!");
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
//            Trace.w("current locale:", Arrays.asList(infor.getLangAndCountry()).toString());

            String[] temp = infor.getLangAndCountry();
            Locale locale = new Locale(temp[0], temp[1]);
//            Trace.i("playTts()", locale.toString());
            int result = mTts.setLanguage(locale);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Trace.e("error", "This Language is not supported");
//                Trace.e("current Locale", infor.getLocale().toString());
                Toast.makeText(ContentViewerActivity.this, "暂不支持该语言" + infor.getLocale().toString(), Toast.LENGTH_SHORT).show();
            } else {
                mTts.setPitch(1.0f);
                mTts.setSpeechRate(1.0f);
                mTts.speak(text,
                        TextToSpeech.QUEUE_FLUSH,
                        null);
            }
        } catch (Exception e) {
            Trace.e("tts speak()", e.getMessage());
        }
    }

    private void playTts(String text) {
//        int lastPronounceLanguageIndex = Settings.getInstance(ContentViewerActivity.this).getLastPronounceLanguage();
        playTts(text, pickedSentencePronouceLanguageIndex);
    }

    private void killTts() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
    }

    private void StopPlayingAll() {
        PlayAudioManager.stop();
        if (mTts != null & mTts.isSpeaking())
            mTts.stop();
    }

    @Override
    protected void onDestroy() {
        killTts();
        super.onDestroy();
    }
}
