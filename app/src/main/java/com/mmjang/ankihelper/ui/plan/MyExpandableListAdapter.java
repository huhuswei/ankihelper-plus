package com.mmjang.ankihelper.ui.plan;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mmjang.ankihelper.R;

import java.util.List;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> groupList;
    private List<List<String>> childList;
    private SparseBooleanArray selectedItems;

    public MyExpandableListAdapter(Context context, List<String> groupList, List<List<String>> childList) {
        this.context = context;
        this.groupList = groupList;
        this.childList = childList;
        this.selectedItems = new SparseBooleanArray();
        int count = childList.stream().map(List::size).reduce((a, b)-> a + b).get();
        for(int i=0; i< count; i++) {
            selectedItems.put(i, false);
        }
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_default_plan_list_item_group, parent, false);
        }
        TextView titleView = convertView.findViewById(R.id.group_title);
        String title = groupList.get(groupPosition);
        titleView.setText(title);
        titleView.setGravity(Gravity.CENTER);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_default_plan_list_item_child, parent, false);
        }
        TextView titleView = convertView.findViewById(R.id.child_title);
        CheckBox checkbox = convertView.findViewById(R.id.child_checkbox);
        checkbox.setOnClickListener(v -> toggleSelection(groupPosition, childPosition));
        String title = childList.get(groupPosition).get(childPosition);
        titleView.setText(title);

        // 根据选中状态更新复选框
        int position = 0;
        for (int i=0; i < groupPosition; i++)
            position += childList.get(i).size();
        position +=  childPosition;
        boolean isSelected = selectedItems.get(position);
        checkbox.setChecked(isSelected);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    // 更新选中状态，并返回当前选中的项目的位置和状态
    public SparseBooleanArray getSelectedItems() {
        return selectedItems;
    }

    public void toggleSelection(int groupPosition, int childPosition) {
        int position = 0;
        for (int i=0; i < groupPosition; i++)
            position += childList.get(i).size();
        position +=  childPosition;
        boolean isSelected = selectedItems.get(position);
        selectedItems.put(position, !isSelected);
        notifyDataSetChanged();
    }
}

