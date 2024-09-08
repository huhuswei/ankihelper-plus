package com.mmjang.ankihelper.widget.button;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;


import com.mmjang.ankihelper.R;

import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.util.PerformEdit;
import com.mmjang.ankihelper.util.Utils;


/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.widget
 * @ClassName: HtmlButton
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/7/6 9:23 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/7/6 9:23 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PerformEditButton extends AppCompatButton {
    public PerformEditButton(Context context) {
        this(context, null);
    }

    public PerformEditButton(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setText(nameEnum.name());
        init();
    }

    private void init() {
        setAllCaps(false);
        this.setBackgroundResource(R.drawable.no_solid_background);
        setPadding(0, 0, 0, 0);
    }

    public void setupPerformEditAction(ActionEnum name, PerformEdit performEdit) {
        OnClickListener onClickListener = null;
        OnLongClickListener onLongClickListener = null;
        switch (name) {
            case undo:
                setBackgroundResource(R.drawable.ic_undo_write);
                onClickListener = v-> performEdit.undo();
                onLongClickListener = v-> {
                    return true;
                };
                performEdit.setUndoHistoryBackChangedListener(new PerformEdit.UndoHistoryBackChangedListener() {
                    @Override
                    public void undoHistoryBackChanged(PerformEdit p) {
                        if(p.hasUndo())
                            PerformEditButton.this.setBackground(ContextCompat.getDrawable(
                                    getContext(),
                                    Utils.getResIdFromAttribute(getContext(), R.attr.icon_undo)));
                        else
                            PerformEditButton.this.setBackgroundResource(R.drawable.ic_undo_write);
                    }
                });
                break;
            case redo:
                setBackgroundResource(R.drawable.ic_redo_white);
                onClickListener = v-> performEdit.redo();
                onLongClickListener = v-> {
                    return true;
                };
                performEdit.setRedoHistoryChangedListener(new PerformEdit.RedoHistoryChangedListener() {
                    @Override
                    public void redoHistoryChanged(PerformEdit p) {
                        if(p.hasRedo())
                            PerformEditButton.this.setBackground(ContextCompat.getDrawable(
                                    getContext(),
                                    Utils.getResIdFromAttribute(getContext(), R.attr.icon_redo)));
                        else
                            PerformEditButton.this.setBackgroundResource(R.drawable.ic_redo_white);
                    }
                });
                break;
            case format:
                Settings settings = Settings.getInstance(getContext());
                if(!settings.get(Settings.FORMAT_TEXT_ENABLE, true)) {
                    setBackground(ContextCompat.getDrawable(
                        getContext(),
                        Utils.getResIdFromAttribute(getContext(), R.attr.icon_format)));
                } else {
                    setBackgroundResource(R.drawable.ic_format_black);
                }
                onClickListener = v-> {
                    settings.put(Settings.FORMAT_TEXT_ENABLE, !settings.get(Settings.FORMAT_TEXT_ENABLE, true));
                    if(!settings.get(Settings.FORMAT_TEXT_ENABLE, true)) {
                        PerformEditButton.this.setBackground(ContextCompat.getDrawable(
                                getContext(),
                                Utils.getResIdFromAttribute(getContext(), R.attr.icon_format)));
                    } else {
                        PerformEditButton.this.setBackgroundResource(R.drawable.ic_format_black);
                    }
                };
        }
        this.setOnClickListener(onClickListener);
        this.setOnLongClickListener(onLongClickListener);
    }

    public enum ActionEnum {
        undo, redo, format;
    }

}
