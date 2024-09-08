package com.mmjang.ankihelper.ui.plan;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.widget.MultiSpinner;
import com.mmjang.ankihelper.util.Constant;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by liao on 2017/4/28.
 */

public class FieldMapSpinnerListAdapter
        extends RecyclerView.Adapter<FieldMapSpinnerListAdapter.ViewHolder> {
    private List<FieldsMapItem> mFieldsMapItemList;
    private Activity mActivity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exportElementName;
        MultiSpinner fieldsSpinner, fieldsSpinnerAppending;

        public ViewHolder(View view) {
            super(view);
            exportElementName = (TextView) view.findViewById(R.id.tv_export_element);
            fieldsSpinner = (MultiSpinner) view.findViewById(R.id.spinner_fields);
            fieldsSpinnerAppending = (MultiSpinner) view.findViewById(R.id.spinner_fields_appending);
        }
    }

    public FieldMapSpinnerListAdapter(Activity activity, List<FieldsMapItem> itemList) {
        mFieldsMapItemList = itemList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.field_map_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final FieldsMapItem item = mFieldsMapItemList.get(position);
        holder.exportElementName.setText(item.getField());
        holder.fieldsSpinner.setItems(
//                Arrays.stream(item.getExportedElementNames()).filter(n->!n.equals(Constant.DICT_FIELD_EMPTY)).collect(Collectors.toList()),
                Arrays.stream(item.getExportedElementNames()).collect(Collectors.toList()),
                item.getMultiSel());
        holder.fieldsSpinner.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    item.setMultiSel(holder.fieldsSpinner.getmDefaultText());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            }
        );

        holder.fieldsSpinnerAppending.setItems(
//                Arrays.stream(item.getExportedElementNames()).filter(n->!n.equals(Constant.DICT_FIELD_EMPTY)).collect(Collectors.toList()),
                Arrays.stream(item.getExportedElementNames()).collect(Collectors.toList()),
                item.getMultiSelAppending());
        holder.fieldsSpinnerAppending.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        item.setMultiSelAppending(holder.fieldsSpinnerAppending.getmDefaultText());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return mFieldsMapItemList.size();
    }
}
