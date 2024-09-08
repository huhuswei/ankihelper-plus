package com.mmjang.ankihelper.ui.tango;

import android.app.Activity;
import android.graphics.Typeface;

import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.util.Constant;

import java.util.List;

/**
 * Created by liao on 2017/4/28.
 */

public class FieldMapListAdapter
        extends RecyclerView.Adapter<FieldMapListAdapter.ViewHolder> {
    private List<com.mmjang.ankihelper.ui.tango.FieldsMapItem> mFieldsMapItemList;
    private Activity mActivity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExportElementName;
        EditText etSelector;
        MyCustomEditTextListener myCustomEditTextListener;

        public ViewHolder(View view, MyCustomEditTextListener myCustomEditTextListener) {
            super(view);
            this.tvExportElementName = (TextView) view.findViewById(R.id.locator_tv_export_element);
            this.etSelector = (EditText) view.findViewById(R.id.locator_et_fields);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.etSelector.addTextChangedListener(myCustomEditTextListener);
        }
    }

    public FieldMapListAdapter(Activity activity, List<com.mmjang.ankihelper.ui.tango.FieldsMapItem> itemList) {
        mFieldsMapItemList = itemList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.locator_field_map_item, parent, false);
        ViewHolder holder = new ViewHolder(view, new MyCustomEditTextListener());
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        String exportElementName = mFieldsMapItemList.get(holder.getAdapterPosition()).getField();
        if(exportElementName.equals(Constant.getTangoDefaultFields()[0])) {
            SpannableString sS = new SpannableString(exportElementName);
            sS.setSpan(new StyleSpan(Typeface.BOLD), 0, Constant.getTangoDefaultFields()[0].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //粗体
            holder.tvExportElementName.setText(sS);
        } else
            holder.tvExportElementName.setText(exportElementName);
        holder.etSelector.setText(mFieldsMapItemList.get(holder.getAdapterPosition()).getSelector());
    }

    @Override
    public int getItemCount() {
        return mFieldsMapItemList.size();
    }

//    @Override
//    public int getItemViewType(int position) {
////        return super.getItemViewType(position);
//        return position;
//    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mFieldsMapItemList.get(position).setSelector(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
