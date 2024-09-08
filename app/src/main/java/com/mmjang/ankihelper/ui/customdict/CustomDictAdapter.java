package com.mmjang.ankihelper.ui.customdict;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.dict.customdict.CustomDictionaryInformation;
import com.mmjang.ankihelper.ui.customdict.helper.ItemTouchHelperAdapter;
import com.mmjang.ankihelper.ui.customdict.helper.ItemTouchHelperViewHolder;

import java.util.List;


/**
 * Created by liao on 2017/4/27.
 */

public class CustomDictAdapter extends RecyclerView.Adapter<CustomDictAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private List<CustomDictionaryInformation> mDictInfoList;
    private Activity mActivity;

    static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        RelativeLayout countainer;
        TextView dictName;
        TextView lang;
        LinearLayout layoutDelete;

        public ViewHolder(View view) {
            super(view);
            countainer = view.findViewById(R.id.customdict_item);
            dictName = (TextView) view.findViewById(R.id.customdict_name);
            lang = (TextView) view.findViewById(R.id.customdict_lang);
            layoutDelete = (LinearLayout) view.findViewById(R.id.layout_customdict_delete);
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

    public CustomDictAdapter(
            Activity activity,
            List<CustomDictionaryInformation> dictInfoList) {
        mDictInfoList = dictInfoList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_custom_dict, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CustomDictionaryInformation dictInfo = mDictInfoList.get(position);
        holder.dictName.setText(dictInfo.getDictName());
        holder.lang.setText(dictInfo.getDictLang());
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
                                        //mPlansList.get(pos).delete();
//                                        ExternalDatabase.getInstance().deleteCustomDictById(mDictInfoList.get(pos).getId());
                                        ((CustomDictionaryActivity) mActivity).clearCustomDict(mDictInfoList.get(pos).getId());
                                        mDictInfoList.remove(pos);
                                        notifyItemRemoved(pos);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                }
        );
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        CustomDictionaryInformation from = mDictInfoList.get(fromPosition);
        mDictInfoList.remove(fromPosition);
        mDictInfoList.add(toPosition, from);
        notifyItemMoved(fromPosition, toPosition);

//        ((CustomDictionaryActivity) mActivity).updateId(fromPosition, toPosition);
    }


    @Override
    public void onMoveFinished() {
        ((CustomDictionaryActivity) mActivity).updateOrder();

//        ExternalDatabase.getInstance().refreshPlanWith(mDictInfoList);
//        List<OutputPlan> plansInDatabase = DataSupport.findAll(OutputPlan.class);
//        for(int i = 0; i < plansInDatabase.size(); i ++){
//            OutputPlan oldPlan = plansInDatabase.get(i);
//            OutputPlan newPlan = mPlansList.get(i);
//            if(oldPlan.getPlanName() == newPlan.getPlanName()){
//                continue;
//            }else{
//                oldPlan.setPlanName(newPlan.getPlanName());
//                oldPlan.setDictionaryKey(newPlan.getDictionaryKey());
//                oldPlan.setOutputDeckId(newPlan.getOutputDeckId());
//                oldPlan.setOutputModelId(newPlan.getOutputModelId());
//                oldPlan.setFieldsMap(newPlan.getFieldsMap());
//                oldPlan.save();
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return mDictInfoList.size();
    }
}
