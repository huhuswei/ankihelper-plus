package com.mmjang.ankihelper.domain;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;

import java.util.HashMap;
import java.util.Locale;


///////////////////////////////////////////////////////////////////////////
// TextToSpeech实例
// 备注：tts对象实现必须是Activity，不然会被系统销毁
///////////////////////////////////////////////////////////////////////////

public class TtsActivity extends Activity implements TextToSpeech.OnInitListener {
    private TextToSpeech mTts;
    private Context mContext;
    private String mText;

    public TtsActivity(Context context) {
        this.mText = "";
        this.mContext = context;
        this.mTts = new TextToSpeech(mContext, this);
    }

    public TtsActivity(Context context, String text) {
        this.mContext = context;
        this.mText = text;
    }

    public void speak() {
        if(mText.equals("")) {
            try {
                int lastPronounceLanguageIndex = Settings.getInstance(MyApplication.getContext()).getLastPronounceLanguage();
                PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(lastPronounceLanguageIndex);
                Log.w("current locale:", infor.getLangAndCountry().toString());

                String[] temp = infor.getLangAndCountry();
                Locale locale = new Locale(temp[0], temp[1]);
                Log.e("speak", locale.toString());
                int result = mTts.setLanguage(locale);
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("error", "This Language is not supported");
                    Log.e("current Locale", infor.getLocale().toString());

                    Toast.makeText(mContext, "暂不支持该语言" + infor.getLocale().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    mTts.speak(mText, TextToSpeech.QUEUE_FLUSH, null);
                }
            } catch (Exception e) {
                Log.e("tts OnInit", e.getMessage());
            }

            mTts.setPitch(1.0f);
            mTts.setSpeechRate(1.0f);
            mTts.speak(mText, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void speak(String text) {
        mText = text;
        if(mText.equals("")) {
            try {
                int lastPronounceLanguageIndex = Settings.getInstance(MyApplication.getContext()).getLastPronounceLanguage();
                PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(lastPronounceLanguageIndex);
                Log.w("current locale:", infor.getLangAndCountry().toString());

                String[] temp = infor.getLangAndCountry();
                Locale locale = new Locale(temp[0], temp[1]);
                Log.e("speak", locale.toString());
                int result = mTts.setLanguage(locale);
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("error", "This Language is not supported");
                    Log.e("current Locale", infor.getLocale().toString());

                    Toast.makeText(mContext, "暂不支持该语言" + infor.getLocale().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    mTts.speak(mText, TextToSpeech.QUEUE_FLUSH, null);
                }
            } catch (Exception e) {
                Log.e("tts OnInit", e.getMessage());
            }

            mTts.setPitch(1.0f);
            mTts.setSpeechRate(1.0f);
            mTts.speak(mText, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void kill() {
        if(mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
    }

    public void restore(String pathOfFile) {
        if (!mText.equals("")) {
            HashMap<String, String> map = new HashMap<>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "record");
            mTts.synthesizeToFile(mText, map, pathOfFile);
            Log.w("tts", "restored mp3 file.");
        }
    }

    public void restore(String text, String pathOfFile) {
        mText = text;
        if (!mText.equals("")) {
            HashMap<String, String> map = new HashMap<>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "record");
            mTts.synthesizeToFile(mText, map, pathOfFile);
            Log.w("tts", "restored mp3 file.");
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mTts = new TextToSpeech(mContext, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {

                }

                @Override
                public void onDone(String utteranceId) {

                }

                @Override
                public void onError(String utteranceId) {
                    Toast.makeText(mContext, R.string.play_pronounciation_failed, Toast.LENGTH_SHORT).show();
                    kill();
                }

                @Override
                public void onStop(String utteranceId, boolean interrupted) {
                    super.onStop(utteranceId, interrupted);
                    mTts.stop();
                    mTts.shutdown();
                }
            });

        } else {
            Log.e("tts status OnInit", "TextToSpeech is failed!");
        }
    }
}
