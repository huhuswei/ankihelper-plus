package com.mmjang.ankihelper.domain;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Base64;
import android.widget.Toast;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.dict.DictLanguageType;
import com.mmjang.ankihelper.util.StringUtil;
import com.mmjang.ankihelper.util.Trace;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;







public class PlayAudioManager {
    private static MediaPlayer mediaPlayer;
    public static String YOUDAO_AUDIO = "youdao_audio";
    public static String EUDICT_AUDIO = "eudict_audio";
//    public static String BD_AUDIO = "bd_audio";

    //销毁
    public static void killAll() {
        killMediaPlayer();
//        Log.i("PlayAudioManager", "PlayAudioManager is Destroyed");
    }

    private static void playAudio(final Context context, final String url) throws Exception {

        if(url == null || url.isEmpty()) {
            Toast.makeText(context, R.string.play_no_audio, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
//            Log.e("mediaPlayer", "new MediaPlayer()");
            mediaPlayer.setOnPreparedListener(
                    new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    }
            );
        }
        try {
            if(mediaPlayer != null) {
                mediaPlayer.reset();
                //mMediaPlayer.release();
            }
        }catch(IllegalStateException e){
        }

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(context, Uri.parse(url));
        mediaPlayer.prepareAsync();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
            }
        });

        mediaPlayer.setOnErrorListener(
                new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        mp.reset();
                        return false;
                    }
                }
        );
    }

    private static void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void playPronounceSound(final Context context, String audioUrl) {
        try {
            PlayAudioManager.playAudio(context, audioUrl);
        } catch (Exception e) {
            Toast.makeText(context, "获取发音失败,请检查网络设置或单词拼写。", Toast.LENGTH_SHORT).show();
        }
    }
//    public static void playPronounceSound(final Context context, String word, String audioUrl) {
//        try {
//            String soundUrl = PlayAudioManager.getSoundUrlOnline(context, word, audioUrl);
//            PlayAudioManager.playAudio(context, soundUrl);
//        } catch (Exception e) {
//            Toast.makeText(context, "获取发音失败,请检查网络设置或单词拼写。", Toast.LENGTH_SHORT).show();
//        }
//    }

    public static String getSoundUrlOnline(final Context context, String word, String audioUrl, int pronounceIndex) {
        PronounceManager.SoundInformation infor = PronounceManager.getDictInformationByIndex(pronounceIndex);

        if((infor.getDictLanguageType() == DictLanguageType.JPN) && StringUtil.isJapanese(word)) {
            Tokenizer tokenizer = new Tokenizer();
            List<Token> tokens = tokenizer.tokenize(word);
            StringBuilder readings = new StringBuilder();
            for(Token token : tokens) {
                readings.append(token.getReading());
            }
            word = readings.toString();
        }

        try {
            switch (infor.getSoundSourceType()) {
                case PronounceManager.SOUND_SOURCE_YOUDAO:
                    return PlayAudioManager.getYoudaoSound(word, infor.getLangAndCountry());
                case PronounceManager.SOUND_SOURCE_EUDICT:
                    return PlayAudioManager.getEudictSound(word, infor.getLangAndCountry());
//                case PronounceManager.SOUND_SOURCE_BD:
//                    return PlayAudioManager.getBdSound(word, infor.getLangAndCountry());
                case PronounceManager.SOUND_SOURCE_ONLINE:
                    if(audioUrl.equals(YOUDAO_AUDIO))
                        return PlayAudioManager.getYoudaoSound(word, infor.getLangAndCountry());
                    else if(audioUrl.equals(EUDICT_AUDIO))
                        return PlayAudioManager.getEudictSound(word, infor.getLangAndCountry());
//                    else if(audioUrl.equals(BD_AUDIO))
//                        return PlayAudioManager.getBdSound(word, infor.getLangAndCountry());
                    else
                        return audioUrl;
                default:
                    return "";
            }
        } catch (Exception e) {
            Toast.makeText(context, "获取语音URL失败,请检查网络设置或单词拼写。", Toast.LENGTH_SHORT).show();
            return "";
        }
    }
    public static String getSoundUrlOnline(final Context context, String word, String audioUrl) {
        int lastPronounceLanguageIndex = Settings.getInstance(MyApplication.getContext()).getLastPronounceLanguage();
        return getSoundUrlOnline(context, word, audioUrl, lastPronounceLanguageIndex);
    }

    public static String getSoundTag(final Context context, String word, String audioUrl) {
        return "[sound:" + PlayAudioManager.getSoundUrlOnline(context, word, audioUrl) +"]";
    }
    public static String getSoundTag(final Context context, String uri) {
        return getSoundTag(uri);
    }

    public static String getSoundTag(final String uri) {
        return "[sound:" + uri +"]";
    }

    public static String getYoudaoSound(String content, String[] langAndContry) throws UnsupportedEncodingException {
//        String newContent = PunctuationUtil.chinesePunctuationToEnglish(content.trim());
//        Trace.i("newContent", newContent);
        String wordEncodedStr = URLEncoder.encode(content, "UTF-8");
        Trace.i("newContent", wordEncodedStr);
        String urlStr = null;

        if(langAndContry.length == 1) {
            urlStr = String.format("https://dict.youdao.com/dictvoice?le=%s&audio=%s", langAndContry[0], wordEncodedStr);
        } else {
            urlStr = String.format("https://dict.youdao.com/dictvoice?le=%s&type=%s&audio=%s", langAndContry[0], langAndContry[1], wordEncodedStr);
        }
       if (urlExit(urlStr)) {
            return urlStr;
        } else {
           return "";
       }
    }

    public static String getEudictSound(String word, String[] langAndContry) throws UnsupportedEncodingException {

        String wordEncodedStr = URLEncoder.encode(new String(Base64.encode(word.trim().getBytes(StandardCharsets.UTF_8), Base64.DEFAULT), StandardCharsets.UTF_8), "UTF-8");
        String urlStr = null;
        if(langAndContry.length == 1) {
            urlStr = String.format("https://api.frdic.com/api/v2/speech/speakweb?langid=%s&txt=QYN%s", langAndContry[0], wordEncodedStr);
        }
        else {
                urlStr = String.format("https://api.frdic.com/api/v2/speech/speakweb?langid=%s&voicename=%s&txt=QYN%s", langAndContry[0], langAndContry[1], wordEncodedStr);
        }
        if (urlExit(urlStr)) {
            return urlStr;
        } else {
            return "";
//            return PlayAudioManager.getYoudaoSound(word, langAndContry); //如果欧路没有语音则返回有道语音
        }
    }

//    public static String getBdSound(String content, String[] langAndContry) throws UnsupportedEncodingException {
////        String newContent = PunctuationUtil.chinesePunctuationToEnglish(content.trim());
////        Trace.i("newContent", newContent);
//        String wordEncodedStr = URLEncoder.encode(content, "UTF-8");
//        String urlStr = null;
//        if(langAndContry.length == 1) {
//            urlStr = String.format("https://fanyi.baidu.com/gettts?lan=%s&text=%s&spd=4", langAndContry[0], wordEncodedStr);
//        }
//        if (urlExit(urlStr)) {
//            return urlStr;
//        } else {
//            return "";
//        }
//    }

    private static boolean urlExit(String url) {
        try {
//            URL u = new URL(url);
//            HttpURLConnection huc = (HttpURLConnection) u.openConnection();
//            huc.setRequestMethod("HEAD");
//            huc.setConnectTimeout(1000);
//            huc.connect();
//            return huc.getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}