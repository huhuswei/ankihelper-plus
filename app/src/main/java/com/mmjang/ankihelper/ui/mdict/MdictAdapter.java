package com.mmjang.ankihelper.ui.mdict;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.dict.DictLanguageType;
import com.mmjang.ankihelper.data.dict.mdict.MdictInformation;
import com.mmjang.ankihelper.ui.mdict.helper.ItemTouchHelperAdapter;
import com.mmjang.ankihelper.ui.mdict.helper.ItemTouchHelperViewHolder;
import com.mmjang.ankihelper.util.Constant;

import java.io.File;
import java.util.List;


/**
 * Created by liao on 2017/4/27.
 */

public class MdictAdapter extends RecyclerView.Adapter<MdictAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private List<MdictInformation> mDictInfoList;
    private Activity mActivity;

    static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        RelativeLayout countainer;
        TextView dictName;
        TextView lang;
        LinearLayout layoutEdit;
        LinearLayout layoutDelete;

        public ViewHolder(View view) {
            super(view);
            countainer = view.findViewById(R.id.mdict_item);
            dictName = (TextView) view.findViewById(R.id.mdict_name);
            lang = (TextView) view.findViewById(R.id.mdict_lang);
            layoutEdit = (LinearLayout) view.findViewById(R.id.layout_mdict_edit);
            layoutDelete = (LinearLayout) view.findViewById(R.id.layout_mdict_delete);
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

    public MdictAdapter(
            Activity activity,
            List<MdictInformation> dictInfoList) {
        mDictInfoList = dictInfoList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mdict, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MdictInformation dictInfo = mDictInfoList.get(position);
        String name = dictInfo.getDictName().substring(dictInfo.getDictName().lastIndexOf(File.separatorChar)+1);
        holder.dictName.setText(name);
        holder.lang.setText(DictLanguageType.getLanguageNameList()[dictInfo.getDictLangNameIndex()]);

        holder.layoutEdit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int order = holder.getAdapterPosition();
                        Intent intent = new Intent(mActivity, MdictEditorActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Constant.INTENT_ANKIHELPER_MDICT_ORDER, order);
                        MyApplication.getContext().startActivity(intent);
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
                                        //mPlansList.get(pos).delete();
//                                        ExternalDatabase.getInstance().deletemdictById(mDictInfoList.get(pos).getId());
                                        ((MdictActivity) mActivity).clearMdict(mDictInfoList.get(pos).getId());
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
        MdictInformation from = mDictInfoList.get(fromPosition);
        mDictInfoList.remove(fromPosition);
        mDictInfoList.add(toPosition, from);
        notifyItemMoved(fromPosition, toPosition);
    }


    @Override
    public void onMoveFinished() {
        ((MdictActivity) mActivity).updateOrder();

    }

    @Override
    public int getItemCount() {
        return mDictInfoList.size();
    }
}
