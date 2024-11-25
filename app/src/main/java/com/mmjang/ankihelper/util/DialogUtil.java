package com.mmjang.ankihelper.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadList;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.plan.OutputPlanPOJO;
import com.mmjang.ankihelper.ui.intelligence.tess.ApiResponse;
import com.mmjang.ankihelper.ui.intelligence.tess.TesseractDataInfo;
import com.mmjang.ankihelper.ui.translation.TranslateBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
* 工具类，包含一些对话框相关的工具方法
*
* @author ss
* @date 2024-01-4
*/
public class DialogUtil {

    /**
    * 显示启动Anki对话框
    *
    * @param activityContext 活动上下文
    */
    public static void showStartAnkiDialog(Context activityContext) {
        AlertDialog dialog = new AlertDialog.Builder(activityContext)
                .setMessage(activityContext.getString(R.string.plan_anki_not_started))
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                MyApplication.getAnkiDroid().startAnkiDroid();
                            }
                        })
                .create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    /**
    * 显示Tesseract OCR设置对话框
    *
    * @param activityContext 活动上下文
    */
    public static void showTesseractSettingDialog(Context activityContext) {
        Settings settings = Settings.getInstance(MyApplication.getContext());
        HashMap<String, Boolean> dataCheckerMap = settings.getTesseractOcrTraineddataCheckBoxMap();
//                        HashMap<String, Boolean> dataCheckerMap = new HashMap<>();
        File[] files = new File(StorageUtils.getIndividualTesseractDirectory(),
                Constant.EXTERNAL_STORAGE_TESSDATA_SUBDIRECTORY).listFiles();
        List<File> list;

        list = new ArrayList<>();
        if(files != null) {
            Arrays.asList(files).stream().map(a->a.getName().endsWith(Constant.TRAINEDDATA_SUFFIX)?list.add(a):null).collect(Collectors.toList());
        }

//        if(dataCheckerMap.size() != files.length) {
//            for(int i=0; i<list.size(); i++) {
//                dataCheckerMap.put(list.get(i).getNameId(), false);
//            }
//        }
        for(File file : list) {
            if(!dataCheckerMap.containsKey(file.getName()))
                dataCheckerMap.put(file.getName(), false);
        }


        String[] dataNameArr = new String[list.size()];
        boolean[] isCheckedArr = new boolean[list.size()];

        for(int i=0; i<list.size(); i++) {
            dataNameArr[i] = String.format("%s", Constant.getNameByTraineddataName(list.get(i).getName().split("\\.")[0].toLowerCase()));
            Boolean value = dataCheckerMap.get(list.get(i).getName());
            if(value != null) {
                isCheckedArr[i] = value.booleanValue();
            }else
                isCheckedArr[i] = false;
        }

        AlertDialog.Builder multiChoiceDialog = new AlertDialog.Builder(activityContext);
        multiChoiceDialog
                .setTitle(R.string.tv_choose_language_name);
//                                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
//
//                                });

        multiChoiceDialog.setMultiChoiceItems(
                dataNameArr,
                isCheckedArr,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        File file = list.get(which);
                        dataCheckerMap.replace(file.getName(), Boolean.valueOf(isChecked));
                        isCheckedArr[which] = isChecked;

                        List<String> selectedDataList = new ArrayList<>();
                        for(String key : dataCheckerMap.keySet()) {
                            if(dataCheckerMap.get(key)) {
//                                                Trace.e("selectedData", );
                                selectedDataList.add(key.split("\\.")[0]);
                            }
                        }

                        if(selectedDataList.size()<3) {
                            String lang = "";
                            switch (selectedDataList.size()) {
                                case 1:
                                    lang = selectedDataList.get(0);
                                    break;
                                case 2:
                                    lang = TextUtils.join("+", selectedDataList.toArray());
                                    break;
                            }
                            settings.setTesseractOcrTraineddataCheckBoxMap(dataCheckerMap);
                            settings.setOrcSelectedLang(lang);
                        } else {
                            ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                            dataCheckerMap.replace(file.getName(), Boolean.valueOf(false));
                            isCheckedArr[which] = false;
                        }
                    }
                });
        multiChoiceDialog.show();
    }

    /**
    * 显示MlKit OCR设置对话框
    *
    * @param activityContext 活动上下文
    */
    public static void showMlKitOcrSettingDialog(Context activityContext) {
        Settings settings = Settings.getInstance(MyApplication.getContext());
        int checkedIndex = settings.getMlKitOcrLangCheckedIndex();
        String[] dataNameArr = Constant.MLKIT_TEXT_RECONGNITION_LANGS;
        boolean[] isCheckedArr = new boolean[Constant.MLKIT_TEXT_RECONGNITION_LANGS.length];

        for(int i = 0; i < isCheckedArr.length; i++) {
            if(i == checkedIndex)
                isCheckedArr[i] = true;
            else
                isCheckedArr[i] = false;
        }

        AlertDialog.Builder multiChoiceDialog = new AlertDialog.Builder(activityContext);
        multiChoiceDialog
                .setTitle(R.string.tv_choose_script_charactor);
//                                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
//
//                                });

        multiChoiceDialog.setSingleChoiceItems(dataNameArr, checkedIndex,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settings.setMlKitOcrCheckedIndex(which);
                    }
                });
        multiChoiceDialog.show();
    }

    /**
    * 显示选择翻译器设置对话框
    *
    * @param activityContext 活动上下文
    */
    public static void selectTranslatorSettingDialog(Context activityContext) {
        Settings settings = Settings.getInstance(MyApplication.getContext());
        int checkedIndex = settings.getTranslatorCheckedIndex();
        String[] dataNameArr = TranslateBuilder.getNameArr();
        boolean[] isCheckedArr = new boolean[dataNameArr.length];

        for(int i = 0; i < isCheckedArr.length; i++) {
            if(i == checkedIndex)
                isCheckedArr[i] = true;
            else
                isCheckedArr[i] = false;
        }

        AlertDialog.Builder multiChoiceDialog = new AlertDialog.Builder(activityContext);
        multiChoiceDialog.setTitle(R.string.tv_translation_engine);
        multiChoiceDialog.setSingleChoiceItems(dataNameArr, checkedIndex,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settings.setTranslatorCheckedIndex(which);
                    }
                });
        multiChoiceDialog.show();
    }

    /**
    * 显示捕获编辑模式设置对话框
    *
    * @param activityContext 活动上下文
    */
    public static void captureEditModeSettingDialog(Context activityContext) {
        Settings settings = Settings.getInstance(MyApplication.getContext());
        int checkedIndex = settings.get(Settings.CAPTURE_RESULT_EDIT_MODE, 0);
        String[] modeNameArr = new String[Constant.Mode.values().length];
        for(int index=0; index < Constant.Mode.values().length; index++) {
            modeNameArr[index] = Constant.Mode.values()[index].getName();
        }

        boolean[] isCheckedArr = new boolean[modeNameArr.length];

        for(int i = 0; i < isCheckedArr.length; i++) {
            if(i == checkedIndex)
                isCheckedArr[i] = true;
            else
                isCheckedArr[i] = false;
        }

        AlertDialog.Builder multiChoiceDialog = new AlertDialog.Builder(activityContext);
        multiChoiceDialog.setTitle(R.string.tv_capture_result_edit_mode);
        multiChoiceDialog.setSingleChoiceItems(modeNameArr, checkedIndex,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settings.put(Settings.CAPTURE_RESULT_EDIT_MODE, which);
                    }
                });
        multiChoiceDialog.show();
    }

    /**
    * 显示笔记模式设置对话框
    *
    * @param activityContext 活动上下文
    */
    public static void noteModeSettingDialog(Context activityContext) {
        Settings settings = Settings.getInstance(MyApplication.getContext());
        int checkedIndex = settings.get(Settings.NOTE_MODE, 0);
        String[] modeNameArr = new String[Constant.NoteMode.values().length];
        for(int index=0; index < Constant.NoteMode.values().length; index++) {
            modeNameArr[index] = activityContext.getResources().getString(Constant.NoteMode.values()[index].getNameResId());
        }

        boolean[] isCheckedArr = new boolean[modeNameArr.length];

        for(int i = 0; i < isCheckedArr.length; i++) {
            if(i == checkedIndex)
                isCheckedArr[i] = true;
            else
                isCheckedArr[i] = false;
        }

        AlertDialog.Builder multiChoiceDialog = new AlertDialog.Builder(activityContext);
        multiChoiceDialog.setTitle(R.string.tv_note_mode);
        multiChoiceDialog.setSingleChoiceItems(modeNameArr, checkedIndex,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settings.put(Settings.NOTE_MODE, which);
                    }
                });
        multiChoiceDialog.show();
    }

    /**
    * 显示选择牌组对话框
    *
    * @param activityContext   活动上下文
    * @param ankiDroid        AnkiDroidHelper 实例
    * @return 选择牌组对话框
    */
    public static Dialog showSelectingDecksDialog(Context activityContext, AnkiDroidHelper ankiDroid) {
        Settings settings = Settings.getInstance(MyApplication.getContext());
        HashMap<Long, Boolean> dataCheckerMap = settings.getDeckSelectedLinkedHashMap();
        LinkedHashMap<Long, String> deckList = Utils.hashMap2LinkedHashMap(ankiDroid.getApi().getDeckList());

        if(deckList.isEmpty()) {
            ToastUtil.show(R.string.toast_failed_to_get_deck_list);
            return null;
        }

        if(!dataCheckerMap.values().contains(true)) {
            Long id = deckList.keySet().toArray(new Long[0])[0];
            dataCheckerMap.put(id, true);
        }

        List<Long> idList = deckList.keySet().stream().collect(Collectors.toList());
        String[] dataNameArr = new String[deckList.size()];
        boolean[] isCheckedArr = new boolean[deckList.size()];

        for(Long id : idList) {
            if(!dataCheckerMap.containsKey(id))
                dataCheckerMap.put(id, false);
        }


        for(int i=0; i<deckList.size(); i++) {
            dataNameArr[i] = deckList.get(idList.get(i));
            Boolean value = dataCheckerMap.get(idList.get(i));
            if(value != null) {
                isCheckedArr[i] = value.booleanValue();
            }else
                isCheckedArr[i] = false;
        }

        AlertDialog.Builder multiChoiceDialog = new AlertDialog.Builder(activityContext);
        multiChoiceDialog
                .setTitle(R.string.tv_select_deck);
        multiChoiceDialog.setMultiChoiceItems(
                dataNameArr,
                isCheckedArr,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        if(isChecked || (!isChecked && dataCheckerMap.values().stream().mapToInt(s->s?1:0).sum() > 1)) {
                            Long deckId = idList.get(which);
                            dataCheckerMap.replace(deckId, Boolean.valueOf(isChecked));
                            isCheckedArr[which] = isChecked;
                            settings.setDeckSelectedLinkedHashMap(dataCheckerMap);
//                        } else {
//                            ((AlertDialog) dialog).getListView().setItemChecked(which, true);
//                            isCheckedArr[which] =true;
//                            ToastUtil.show(R.string.toast_keep_at_least_one_deck);
//                        }

                        if (dataCheckerMap.values().stream().mapToInt(s->s?1:0).sum() == 0)
                            ToastUtil.show(R.string.toast_keep_at_least_one_deck);
                    }
                });
        return multiChoiceDialog.show();
    }


    /**
     * 显示选择方案对话框
     *
     * @param activityContext   活动上下文
     * @param planList        AnkiDroidHelper 实例
     * @return 选择方案对话框
     */
    public static Dialog showSelectingPlansDialog(Context activityContext, List<OutputPlanPOJO>  planList) {
        Settings settings = Settings.getInstance(MyApplication.getContext());
        HashMap<String, Boolean> dataCheckerMap = settings.getPlanSelectedLinkedHashMap();
        List<String> nameList = planList.stream().map(OutputPlanPOJO::getPlanName).collect(Collectors.toList());

        if(nameList.size()==0) {
            ToastUtil.show(R.string.toast_failed_to_get_plan_list);
            return null;
        }

        if(!dataCheckerMap.values().contains(true)) {
            String name = nameList.get(0);
            dataCheckerMap.put(name, true);
        }

        String[] dataNameArr =  nameList.toArray(new String[0]);
        boolean[] isCheckedArr = new boolean[nameList.size()];

        for(String name : nameList) {
            if(!dataCheckerMap.containsKey(name))
                dataCheckerMap.put(name, false);
        }


        for(int i=0; i<nameList.size(); i++) {
            dataNameArr[i] = nameList.get(i);
            Boolean value = dataCheckerMap.get(nameList.get(i));
            if(value != null) {
                isCheckedArr[i] = value.booleanValue();
            }else
                isCheckedArr[i] = false;
        }

        AlertDialog.Builder multiChoiceDialog = new AlertDialog.Builder(activityContext);
        multiChoiceDialog
                .setTitle(R.string.tv_select_plan);
        multiChoiceDialog.setMultiChoiceItems(
                dataNameArr,
                isCheckedArr,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        if(isChecked || (!isChecked && dataCheckerMap.values().stream().mapToInt(s->s?1:0).sum() > 1)) {
                            dataCheckerMap.replace(dataNameArr[which], Boolean.valueOf(isChecked));
                            isCheckedArr[which] = isChecked;
                            settings.setPlanSelectedLinkedHashMap(dataCheckerMap);
//                        } else {
//                            ((AlertDialog) dialog).getListView().setItemChecked(which, true);
//                            isCheckedArr[which] =true;
//                            ToastUtil.show(R.string.toast_keep_at_least_one_deck);
//                        }

                        if (dataCheckerMap.values().stream().mapToInt(s->s?1:0).sum() == 0)
                            ToastUtil.show(R.string.toast_keep_at_least_one_plan);
                    }
                });
        return multiChoiceDialog.show();
    }
//    public static void addDefaultPlanDialog(Context activityContext) {
//        Settings settings = Settings.getInstance(activityContext);
//
//        HashMap<String, Boolean> dataCheckerMap = new HashMap<>();
//        List<IDictionary> list = DictionaryRegister.getDictionaryObjectList();
//
//        String[] dataNameArr = new String[list.size()];
//        boolean[] isCheckedArr = new boolean[list.size()];
//
//        for(int i=0; i<list.size(); i++) {
//            dataNameArr[i] = String.format("%s", list.get(i).getDictionaryName());
//            isCheckedArr[i] = false;
//        }
//
//        AlertDialog.Builder multiChoiceDialog = new AlertDialog.Builder(activityContext);
//        multiChoiceDialog
//                .setTitle(R.string.tv_plan_name)
//                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String[] elements = new String[]{
//                                Constant.DICT_FIELD_EMPTY + "\t" + Constant.DICT_FIELD_EMPTY,
//                                Constant.DICT_FIELD_SENTENCE_PICKED_BOLD + "\t" + Constant.DICT_FIELD_EMPTY,
//                                Constant.DICT_FIELD_TRANSLATION + "\t" + Constant.DICT_FIELD_EMPTY,
//                                Constant.DICT_FIELD_EMPTY + "\t" + Constant.DICT_FIELD_EMPTY,
//                                Constant.DICT_FIELD_EMPTY + "\t" + Constant.DICT_FIELD_EMPTY,
//                                Constant.DICT_FIELD_COMPLEX_OFFLINE + "\t" + Constant.DICT_FIELD_COMPLEX_OFFLINE,
//                                Constant.DICT_FIELD_EMPTY + "\t" + Constant.DICT_FIELD_EMPTY,
//                                Constant.DICT_FIELD_EMPTY + "\t" + Constant.DICT_FIELD_EMPTY,
//                                Constant.DICT_FIELD_KEY_DAV_OFFLINE_SOUNDTAG + "\t" + Constant.DICT_FIELD_KEY_DAV_OFFLINE_SOUNDTAG,
//                                Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_SOUNDTAG + "\t" + Constant.DICT_FIELD_EMPTY,
//                                Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_LINK + "\t" + Constant.DICT_FIELD_EMPTY,
//                                Constant.DICT_FIELD_URL + "\t" + Constant.DICT_FIELD_EMPTY
//                        };
//                        String[] FILEDS = {
//                                "Note ID",
//                                "摘取例句（加粗）",
//                                "句子翻译",
//                                "例句",
//                                "笔记",
//                                "释义",
//                                "单词",
//                                "音标",
//                                "自动发音",
//                                "手动发音",
//                                "发音链接",
//                                "来源"
//                        };
//                        LinkedHashMap<String, String> fieldMap = new LinkedHashMap<>();
//                        for (int j = 0; j < FILEDS.length; j++) {
//                            fieldMap.put(FILEDS[i], elements[i]);
//                        }
//
//                        for (int k=0; k<list.size(); k++) {
//                            if(!isCheckedArr[k])
//                                continue;
//
//                            IDictionary dict = list.get(i);
//
//                            OutputPlanPOJO defaultPlan = new OutputPlanPOJO();
//                            defaultPlan.setPlanName(DEFAULT_PLAN_NAME);
//                            defaultPlan.setOutputModelId(getDefaultModelId());
//                            defaultPlan.setOutputDeckId(getDefaultDeckId());
//                            defaultPlan.setDictionaryKey(dict.getDictionaryName());
//                            defaultPlan.setFieldsMap(fieldMap);
//                            ExternalDatabase.getInstance().insertPlan(defaultPlan);
//                        }
//                    }
//                })
//                .setPositiveButton(android.R.string.ok,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                            }
//                        })
//                .setMultiChoiceItems(
//                dataNameArr,
//                isCheckedArr,
//                new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                    }
//                });
//        multiChoiceDialog.show();
//    }

//    public static AlertDialog tessDatadownloadDialog(Context activityContext, ApiResponse apiResponse) {
//        List<DownloadItem> downloadItems = new ArrayList<>();
//        // 初始化你的下载项列表...
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
//        LayoutInflater inflater = (LayoutInflater) activityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogLayout = inflater.inflate(R.layout.dialog_download_tess_data_item, null);
//        LinearLayout downloadItemContainer = dialogLayout.findViewById(R.id.download_item_container);
//        List<TesseractDataInfo> languageModelList = apiResponse.getLanguages();
//        for (TesseractDataInfo languageModel : languageModelList) {
//            View itemView = inflater.inflate(R.layout.item_download, null);
//            downloadItemContainer.addView(itemView);
//            Button downloadButton = itemView.findViewById(R.id.download_button);
//            Button deleteButton = itemView.findViewById(R.id.delete_button);
//            ProgressBar downloadProgress = itemView.findViewById(R.id.download_progress);
//            TextView statusText = itemView.findViewById(R.id.download_status_text);
//            TextView displayName = itemView.findViewById(R.id.display_name_text);
//            String url = String.format("%s%s", apiResponse.getUrl(), languageModel.getDownloadPackage());
//            DownloadItem downloadItem = new DownloadItem(downloadButton, deleteButton, downloadProgress, statusText, displayName, url);
//            downloadItems.add(downloadItem);
//
//            // 关联视图和DownloadItem对象
//            DownloadItem.associateWithView(downloadButton, downloadItem);
//            DownloadItem.associateWithView(downloadProgress, downloadItem);
//            DownloadItem.associateWithView(statusText, downloadItem);
//            DownloadItem.associateWithView(displayName, downloadItem);
//            DownloadItem.associateWithView(deleteButton, downloadItem);
//
//            // 设置初始状态
//            String tessPath = StorageUtils.getIndividualTesseractDirectory().getPath();
//            File dataDir = new File(tessPath, Constant.EXTERNAL_STORAGE_TESSDATA_SUBDIRECTORY);
//            File file = new File(dataDir, languageModel.getPackageName());
//            displayName.setText(languageModel.getLanguageName());
//            if(file.exists()) {
//                statusText.setText("🟢");
//                downloadButton.setVisibility(View.GONE);
//                deleteButton.setVisibility(View.VISIBLE);
//            } else {
//                statusText.setText("🔴");
//                downloadButton.setVisibility(View.VISIBLE);
//                deleteButton.setVisibility(View.GONE);
//            }
//
//            downloadProgress.setVisibility(View.GONE);
//
//            downloadButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // 通过关联的视图找到对应的DownloadItem对象
//                    DownloadItem clickedItem = (DownloadItem) v.getTag();
//                    // 开始下载操作
//                    clickedItem.startDownload();
//                }
//            });
//            deleteButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(file.exists()) {
//                        file.delete();
//                        statusText.setText("🔴");
//                        downloadButton.setText("下载");
//                        downloadButton.setEnabled(true);
//                        downloadButton.setVisibility(View.VISIBLE);
//                        deleteButton.setVisibility(View.GONE);
//                    }
//                }
//            });
//        }
//
//        builder.setView(dialogLayout);
//        AlertDialog dialog = builder.create();
//        return dialog;
//    }


//    public static AlertDialog tessDatadownloadDialog(Context activityContext, ApiResponse apiResponse) {
////        List<DownloadItem> downloadItems = new ArrayList<>();
//        // 初始化你的下载项列表...
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
//        LayoutInflater inflater = (LayoutInflater) activityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogLayout = inflater.inflate(R.layout.dialog_download_tess_data_item, null);
//        LinearLayout downloadItemContainer = dialogLayout.findViewById(R.id.download_item_container);
//        List<TesseractDataInfo> tesseractDataInfoList = apiResponse.getLanguages();
//        for (TesseractDataInfo info : tesseractDataInfoList) {
//            String url = String.format("%s%s", apiResponse.getUrl(), info.getDownloadPackage());
//            // 设置初始状态
//            String tessPath = StorageUtils.getIndividualTesseractDirectory().getPath();
//            File dataDir = new File(tessPath, Constant.EXTERNAL_STORAGE_TESSDATA_SUBDIRECTORY);
//            File downFile = new File(dataDir, info.getDownloadPackage());
//            File file = new File(dataDir, info.getPackageName());
//
//            int tid = FileDownloadUtils.generateId(url, downFile.getPath());
//
//            if (FileDownloadList.getImpl().get(tid) != null) {
//                BaseDownloadTask dt = FileDownloadList.getImpl().get(tid).getOrigin();
//                View itemView = ((DownloadItem) dt.getTag()).getView();
//                downloadItemContainer.addView(itemView);
//            } else {
//                View itemView = inflater.inflate(R.layout.item_download, null);
//                downloadItemContainer.addView(itemView);
//                Button downloadButton = itemView.findViewById(R.id.download_button);
//                Button deleteButton = itemView.findViewById(R.id.delete_button);
//                ProgressBar downloadProgress = itemView.findViewById(R.id.download_progress);
//                TextView statusText = itemView.findViewById(R.id.download_status_text);
//                TextView displayName = itemView.findViewById(R.id.display_name_text);
//                displayName.setText(info.getName());
//
//                DownloadItem downloadItem = new DownloadItem(itemView, downloadButton, deleteButton, downloadProgress, statusText, displayName, url, dataDir.getPath(), info);
////            downloadItems.add(downloadItem);
//
//                // 关联视图和DownloadItem对象
//                DownloadItem.associateWithView(itemView, downloadItem);
//                DownloadItem.associateWithView(downloadButton, downloadItem);
//                DownloadItem.associateWithView(downloadProgress, downloadItem);
//                DownloadItem.associateWithView(statusText, downloadItem);
//                DownloadItem.associateWithView(displayName, downloadItem);
//                DownloadItem.associateWithView(deleteButton, downloadItem);
//
//
//                BaseDownloadTask dt = FileDownloader.getImpl().create(url)
//                        .setPath(downFile.getPath(), false)
//                        .setCallbackProgressTimes(300)
//                        .setMinIntervalUpdateSpeed(400)
//                        .setTag(downloadItem)
//                        .setListener(new FileDownloadListener() {
//                            @Override
//                            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                                ((DownloadItem) task.getTag()).getDownloadButton().setText("等待");
//                            }
//
//                            @Override
//                            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
//                                ((DownloadItem) task.getTag()).getStatusText().setText("🔵");
//                                ((DownloadItem) task.getTag()).getDownloadButton().setText("下载中");
////                            downloadButton.setEnabled(false);
//                                ((DownloadItem) task.getTag()).getDownloadProgress().setVisibility(View.VISIBLE);
//
//                            }
//
//                            @Override
//                            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                                ((DownloadItem) task.getTag()).getDownloadProgress().setProgress(soFarBytes * 100 / totalBytes, true);
//                            }
//
//                            @Override
//                            protected void blockComplete(BaseDownloadTask task) {
//                            }
//
//                            @Override
//                            protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
//                            }
//
//                            @Override
//                            protected void completed(BaseDownloadTask task) {
//                                task.reuse();
//                                try {
//
//                                    if (downFile.getName().endsWith(".zip")) {
//                                        //upzip
//                                        ZipFileUtil.upZipFile(downFile, dataDir.getPath());
//                                        ///delete zip
//                                        if (downFile.exists())
//                                            Utils.deleteFile(downFile);
//                                        ToastUtil.showLong(
//                                                ((DownloadItem) task.getTag()).getInfo().getPackageName() +
//                                                activityContext.getResources().getString(R.string.info_downloaded));
//                                    }
//                                    ((DownloadItem) task.getTag()).getStatusText().setText("🟢");
//                                    ((DownloadItem) task.getTag()).getDownloadButton().setVisibility(View.GONE);
//                                    ((DownloadItem) task.getTag()).getDownloadProgress().setVisibility(View.GONE);
//                                    ((DownloadItem) task.getTag()).getDeleteButton().setVisibility(View.VISIBLE);
//                                } catch (Exception e) {
//
//                                }
//                            }
//
//                            @Override
//                            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                                ((DownloadItem) task.getTag()).getStatusText().setText("🔵");
//                                ((DownloadItem) task.getTag()).getDownloadButton().setText("继续");
//                                ((DownloadItem) task.getTag()).getDownloadProgress().setProgress(soFarBytes * 100 / totalBytes, true);
//                                ((DownloadItem) task.getTag()).getDownloadProgress().setVisibility(View.VISIBLE);
//                            }
//
//                            @Override
//                            protected void error(BaseDownloadTask task, Throwable e) {
//                                task.reuse();
//                                ((DownloadItem) task.getTag()).getStatusText().setText("🔴");
//                                ((DownloadItem) task.getTag()).getDownloadButton().setText("下载");
//                                ((DownloadItem) task.getTag()).getDownloadButton().setEnabled(true);
//                                ((DownloadItem) task.getTag()).getDownloadProgress().setVisibility(View.GONE);
//                                ToastUtil.showLong("Network error.");
//                            }
//
//                            @Override
//                            protected void warn(BaseDownloadTask task) {
//                            }
//                        });
//
//                if (file.exists()) {
//                    statusText.setText("🟢");
//                    downloadButton.setVisibility(View.GONE);
//                    downloadProgress.setVisibility(View.GONE);
//                    deleteButton.setVisibility(View.VISIBLE);
//                } else {
//                    statusText.setText("🔴");
//                    downloadButton.setVisibility(View.VISIBLE);
//                    downloadProgress.setVisibility(View.GONE);
//                    deleteButton.setVisibility(View.GONE);
//                }
////
////            downloadProgress.setVisibility(View.GONE);
////
//                downloadButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                    // 通过关联的视图找到对应的DownloadItem对象
////                    DownloadItem clickedItem = (DownloadItem) v.getTag();
////                    // 开始下载操作
////                    clickedItem.startDownload();
//                        switch (dt.getStatus()) {
//                            case FileDownloadStatus.INVALID_STATUS:
//                                dt.start();
//                                break;
//                            case FileDownloadStatus.pending:
//                            case FileDownloadStatus.connected:
//                            case FileDownloadStatus.progress:
//                            case FileDownloadStatus.started:
//                                dt.pause();
//                                downloadButton.setText("继续");
//                                break;
//                            case FileDownloadStatus.paused:
//                                dt.reuse();
//                                dt.start();
//                                break;
//                        }
//                    }
//                });
//                deleteButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (file.exists()) {
//                            file.delete();
//                            statusText.setText("🔴");
//                            downloadButton.setText("下载");
//                            downloadButton.setVisibility(View.VISIBLE);
//                            deleteButton.setVisibility(View.GONE);
//                        }
//                    }
//                });
//            }
//        }
//
//        builder.setView(dialogLayout);
//        AlertDialog dialog = builder.create();
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
////                FileDownloader.getImpl().pauseAll();
////                FileDownloader.getImpl().clearAllTaskData();
//                downloadItemContainer.removeAllViews();
//            }
//        });
//        return dialog;
//    }


    public static AlertDialog tessDatadownloadDialog(Context activityContext, ApiResponse apiResponse) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        LayoutInflater inflater = (LayoutInflater) activityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog_download_tess_data_item, null);
        final LinearLayout downloadItemContainer = dialogLayout.findViewById(R.id.download_item_container);
        List<TesseractDataInfo> tesseractDataInfoList = apiResponse.getLanguages();

        for (TesseractDataInfo info : tesseractDataInfoList) {
            String url = String.format("%s%s", apiResponse.getUrl(), info.getDownloadPackage());
            // 设置初始状态
            String tessPath = StorageUtils.getIndividualTesseractDirectory().getPath();
            File dataDir = new File(tessPath, Constant.EXTERNAL_STORAGE_TESSDATA_SUBDIRECTORY);
            final File downFile = new File(dataDir, info.getDownloadPackage());
            final File file = new File(dataDir, info.getPackageName());

            int tid = FileDownloadUtils.generateId(url, downFile.getPath());

            if (FileDownloadList.getImpl().get(tid) != null) {
                BaseDownloadTask dt = FileDownloadList.getImpl().get(tid).getOrigin();
                View itemView = ((DownloadItem) dt.getTag()).getView();
                if (itemView != null) {
                    downloadItemContainer.addView(itemView);
                }
                continue;
            }

            View itemView = inflater.inflate(R.layout.item_download, null);
            downloadItemContainer.addView(itemView);

            final Button downloadButton = itemView.findViewById(R.id.download_button);
            final Button deleteButton = itemView.findViewById(R.id.delete_button);
            final ProgressBar downloadProgress = itemView.findViewById(R.id.download_progress);
            final TextView statusText = itemView.findViewById(R.id.download_status_text);
            final TextView displayName = itemView.findViewById(R.id.display_name_text);
            displayName.setText(info.getName());

            final DownloadItem downloadItem = new DownloadItem(
                    itemView,
                    downloadButton,
                    deleteButton,
                    downloadProgress,
                    statusText,
                    displayName,
                    url,
                    dataDir.getPath(),
                    info
            );

            final BaseDownloadTask dt = FileDownloader.getImpl().create(url)
                    .setPath(downFile.getPath(), false)
                    .setCallbackProgressTimes(300)
                    .setMinIntervalUpdateSpeed(400)
                    .setTag(downloadItem)
                    .setListener(new FileDownloadListener() {
                        @Override
                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            DownloadItem item = (DownloadItem) task.getTag();
                            if (item != null && item.getDownloadButton() != null) {
                                item.getDownloadButton().setText("等待");
                            }
                        }

                        @Override
                        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                            DownloadItem item = (DownloadItem) task.getTag();
                            if (item != null) {
                                if (item.getStatusText() != null) {
                                    item.getStatusText().setText("🔵");
                                }
                                if (item.getDownloadButton() != null) {
                                    item.getDownloadButton().setText("下载中");
                                }
                                if (item.getDownloadProgress() != null) {
                                    item.getDownloadProgress().setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            DownloadItem item = (DownloadItem) task.getTag();
                            if (item != null && item.getDownloadProgress() != null && totalBytes > 0) {
                                item.getDownloadProgress().setProgress((int) (soFarBytes * 100.0f / totalBytes), true);
                            }
                        }

                        @Override
                        protected void blockComplete(BaseDownloadTask task) {
                        }

                        @Override
                        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        }

                        @Override
                        protected void completed(BaseDownloadTask task) {
                            task.reuse();
                            DownloadItem item = (DownloadItem) task.getTag();
                            if (item == null) return;

                            try {
                                if (downFile.getName().endsWith(".zip")) {
                                    ZipFileUtil.upZipFile(downFile, dataDir.getPath());
                                    if (downFile.exists()) {
                                        Utils.deleteFile(downFile);
                                    }
                                    ToastUtil.showLong(
                                            item.getInfo().getPackageName() +
                                                    activityContext.getResources().getString(R.string.info_downloaded)
                                    );
                                }

                                if (item.getStatusText() != null) {
                                    item.getStatusText().setText("🟢");
                                }
                                if (item.getDownloadButton() != null) {
                                    item.getDownloadButton().setVisibility(View.GONE);
                                }
                                if (item.getDownloadProgress() != null) {
                                    item.getDownloadProgress().setVisibility(View.GONE);
                                }
                                if (item.getDeleteButton() != null) {
                                    item.getDeleteButton().setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            DownloadItem item = (DownloadItem) task.getTag();
                            if (item != null) {
                                if (item.getStatusText() != null) {
                                    item.getStatusText().setText("🔵");
                                }
                                if (item.getDownloadButton() != null) {
                                    item.getDownloadButton().setText("继续");
                                }
                                if (item.getDownloadProgress() != null) {
                                    item.getDownloadProgress().setProgress((int) (soFarBytes * 100.0f / totalBytes), true);
                                    item.getDownloadProgress().setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {
                            task.reuse();
                            DownloadItem item = (DownloadItem) task.getTag();
                            if (item != null) {
                                if (item.getStatusText() != null) {
                                    item.getStatusText().setText("🔴");
                                }
                                if (item.getDownloadButton() != null) {
                                    item.getDownloadButton().setText("下载");
                                    item.getDownloadButton().setEnabled(true);
                                }
                                if (item.getDownloadProgress() != null) {
                                    item.getDownloadProgress().setVisibility(View.GONE);
                                }
                            }
                            ToastUtil.showLong("Network error.");
                        }

                        @Override
                        protected void warn(BaseDownloadTask task) {
                        }
                    });

            // 设置初始UI状态
            if (file.exists()) {
                statusText.setText("🟢");
                downloadButton.setVisibility(View.GONE);
                downloadProgress.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                statusText.setText("🔴");
                downloadButton.setVisibility(View.VISIBLE);
                downloadProgress.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
            }

            // 设置按钮点击事件
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dt != null) {
                        switch (dt.getStatus()) {
                            case FileDownloadStatus.INVALID_STATUS:
                                dt.start();
                                break;
                            case FileDownloadStatus.pending:
                            case FileDownloadStatus.connected:
                            case FileDownloadStatus.progress:
                            case FileDownloadStatus.started:
                                dt.pause();
                                downloadButton.setText("继续");
                                break;
                            case FileDownloadStatus.paused:
                                dt.reuse();
                                dt.start();
                                break;
                        }
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (file.exists() && file.delete()) {
                        statusText.setText("🔴");
                        downloadButton.setText("下载");
                        downloadButton.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.GONE);
                    }
                }
            });
        }

        builder.setView(dialogLayout);
        AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadItemContainer.removeAllViews();
            }
        });

        return dialog;
    }
}
