package com.mmjang.ankihelper.ui.tango;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.tango.helper.ItemTouchHelperAdapter;
import com.mmjang.ankihelper.ui.tango.helper.ItemTouchHelperViewHolder;

import java.util.List;


/**
 * Created by liao on 2017/4/27.
 */

public class TangoLocatorsAdapter extends RecyclerView.Adapter<TangoLocatorsAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private List<OutputLocatorPOJO> mLocatorList;
    private Activity mActivity;
    private Settings settings;

    static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        RelativeLayout countainer;
        TextView dictName;
        TextView langName;
        CheckBox cbCheck;
        LinearLayout layoutEdit;
        LinearLayout layoutDelete;

        public ViewHolder(View view) {
            super(view);
            countainer = view.findViewById(R.id.locator_item);
            dictName = (TextView) view.findViewById(R.id.locator_dict_name);
            langName = (TextView) view.findViewById(R.id.locator_lang_name);
            cbCheck = (CheckBox) view.findViewById(R.id.layout_locator_check);
            layoutEdit = (LinearLayout) view.findViewById(R.id.layout_locator_edit);
            layoutDelete = (LinearLayout) view.findViewById(R.id.layout_locator_delete);
        }

        @Override
        public void onItemSelected() {
            countainer.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            countainer.setBackgroundColor(0);
        }
    }

    public TangoLocatorsAdapter(
            Activity activity,
            List<OutputLocatorPOJO> locatorList) {
        mLocatorList = locatorList;
        mActivity = activity;
        settings = Settings.getInstance(MyApplication.getContext());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_locators, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mLocatorList.get(position).setOrderIndex(position);
        OutputLocatorPOJO locator = mLocatorList.get(position);
        holder.cbCheck.setChecked(locator.isChecked());
        holder.dictName.setText(locator.getDictName());
        holder.langName.setText(locator.getlangName());

        holder.cbCheck.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Log.i("checked", String.valueOf(holder.cbCheck.isChecked()));
                        mLocatorList.get(holder.getAdapterPosition()).setChecked(holder.cbCheck.isChecked());
                        mLocatorList.get(holder.getAdapterPosition()).save();
                    }
                }
        );
        holder.layoutDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(mActivity)
                                .setTitle(R.string.confirm_deletion)
                                //.setMessage("Do you really want to whatever?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        int pos = holder.getAdapterPosition();
                                        OutputLocatorPOJO.removeOutputlocatorMap(mLocatorList.get(pos).getDictName());
                                        mLocatorList.remove(pos);
                                        notifyItemRemoved(pos);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                }
        );

        holder.layoutEdit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        String dictName = mLocatorList.get(pos).getDictName();
                        Intent intent = new Intent(mActivity, TangoLocationEditorActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, dictName);
                        MyApplication.getContext().startActivity(intent);
                    }
                }
        );

    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        OutputLocatorPOJO from = mLocatorList.get(fromPosition);
        if(toPosition - fromPosition < 0)
            mLocatorList.get(toPosition).setOrderIndex(toPosition+1);
        else if((toPosition - fromPosition > 0))
            mLocatorList.get(toPosition).setOrderIndex(toPosition-1);

        from.setOrderIndex(toPosition);
        mLocatorList.remove(fromPosition);
        mLocatorList.add(toPosition, from);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onMoveFinished() {
        OutputLocatorPOJO.saveOutputLocatorPOJOListToSettings(mLocatorList);
//        List<Outputlocator> locatorsInDatabase = DataSupport.findAll(Outputlocator.class);
//        for(int i = 0; i < locatorsInDatabase.size(); i ++){
//            Outputlocator oldlocator = locatorsInDatabase.get(i);
//            Outputlocator newlocator = mLocatorList.get(i);
//            if(oldlocator.getdictName() == newlocator.getdictName()){
//                continue;
//            }else{
//                oldlocator.setdictName(newlocator.getdictName());
//                oldlocator.setDictionaryKey(newlocator.getDictionaryKey());
//                oldlocator.setOutputDeckId(newlocator.getOutputDeckId());
//                oldlocator.setOutputModelId(newlocator.getOutputModelId());
//                oldlocator.setFieldsMap(newlocator.getFieldsMap());
//                oldlocator.save();
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return mLocatorList.size();
    }
}
