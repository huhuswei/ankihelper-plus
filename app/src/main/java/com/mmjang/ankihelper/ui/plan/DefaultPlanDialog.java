package com.mmjang.ankihelper.ui.plan;

import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.plan.CardModel;
import com.mmjang.ankihelper.data.plan.DefaultPlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultPlanDialog {
    private List<String> groupList;
    private List<List<String>> childList;
    private DefaultPlan defaultPlan;
    Context mContext;
    PlansAdapter mPlansAdapter;

    public DefaultPlanDialog(Context context) {
        this(context, null);
    }
    public DefaultPlanDialog(Context context, PlansAdapter plansAdapter) {
        mContext = context;
        mPlansAdapter = plansAdapter;
        defaultPlan = new DefaultPlan(mContext);
        // 初始化数据列表
        groupList = defaultPlan.getModelList().stream().map(CardModel::getName).collect(Collectors.toList());
        childList = new ArrayList<>();
        for(String group : groupList) {
            childList.add(Arrays.asList(defaultPlan.getPlanNamesMap().get(group)));
        }

        // 启动对话框
//        showSelectionDialog();
    }

    public void showSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.adding_default_plan_str);

        // 创建 ExpandableListView 并设置适配器、监听器等
        ExpandableListView listView = new ExpandableListView(mContext);
        final MyExpandableListAdapter adapter = new MyExpandableListAdapter(mContext, groupList, childList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE); // 设置多选模式
//        listView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//        listView.setGroupIndicator(null);

        // 处理 ExpandableListView 中子项的点击事件
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // 在这里处理子项点击事件，并更新多选状态
                adapter.toggleSelection(groupPosition, childPosition);
                return true;
            }
        });

        // 添加 ExpandableListView 到对话框并显示对话框
        builder.setView(listView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 在这里处理用户点击确定按钮后的操作，例如获取选中的项目等
                SparseBooleanArray selectedItems = adapter.getSelectedItems();
                int totalInserted = defaultPlan.insertPlans(defaultPlan.getModelList(), defaultPlan.getPlanStrList(), selectedItems);
                if(totalInserted > 0) {
                    if(mPlansAdapter != null) {
                        mPlansAdapter.refresh();
                    }
                    Toast.makeText(mContext, R.string.default_plan_added, Toast.LENGTH_SHORT).show();
                }
            }
        });
//        builder.setNegativeButton("取消", null);
        builder.setNeutralButton("更新默认模板", null)
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
                        defaultPlan.updateDefaultCardModel();
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
}

