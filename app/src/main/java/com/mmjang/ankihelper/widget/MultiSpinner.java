package com.mmjang.ankihelper.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.util.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MultiSpinner extends AppCompatSpinner implements DialogInterface.OnMultiChoiceClickListener {

    public static String DELIMITER = ", ";
    private String mDefaultText = "";
    private String mOriginalText = "";
    private List<String> mListItems;
    private boolean[] mSelected;
    private List<String> mSelectedItems = new ArrayList<>();

    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        mSelected[which] = isChecked;
        if (isChecked) {
            mSelectedItems.add(mListItems.get(which));
        } else {
            mSelectedItems.remove(mListItems.get(which));
        }
        AlertDialog alertDialog = (AlertDialog) dialog;
        MathxView titleText = alertDialog.findViewById(R.id.title);
        titleText.setText(getmDefaultText().replaceAll(DELIMITER, "<br/>"));
    }

    @Override
    public boolean performClick() {
        restoreItems();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(mListItems.toArray(new CharSequence[0]), mSelected, this);
        View titleView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_title, null);
        ((MathxView) titleView.findViewById(R.id.title)).setText(getmDefaultText().replaceAll(DELIMITER, "<br/>"));
        builder.setCustomTitle(titleView);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            if (mSelectedItems.size() > 0) {
                setSpinnerAdapter(TextUtils.join(DELIMITER, mSelectedItems), mSelectedItems);
            } else {
                mDefaultText = Constant.DICT_FIELD_EMPTY;
                setSpinnerAdapter(mDefaultText, new ArrayList<>());
            }
            mOriginalText = mDefaultText;
        });
        builder.show();
        return true;
    }

    private void setSpinnerAdapter(String text, List<String> selectedItems) {
        if(text.equals("")) {
            text = Constant.DICT_FIELD_EMPTY;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{text}
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setAutoSizeTextTypeUniformWithConfiguration(12, 24, TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM, TypedValue.COMPLEX_UNIT_DIP);
//                textView.setGravity(Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK); // Set the text alignment to center vertical
                textView.setPadding(0, (int) getResources().getDimension(R.dimen.text_padding), 0, (int) getResources().getDimension(R.dimen.text_padding));
                textView.setSingleLine(false);
                textView.setLines(1);
//                textView.setMaxLines(2); // Set the maximum number of lines to 2
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(adapter);
    }

    /**
     * 恢复下拉菜单到默认状态
     *
     * @param
     *
     * @return void
     */
    private void restoreItems() {
        mSelectedItems = new ArrayList<>();
        Arrays.fill(mSelected, false);

        if (mOriginalText != null && !mOriginalText.isEmpty()) {
            mDefaultText = mOriginalText;
        }

        setSelectedItems(Arrays.stream(mOriginalText.split(DELIMITER)).collect(Collectors.toList()));
        setSpinnerAdapter(mDefaultText, mSelectedItems);
    }

    public void setItems(List<String> items, String allText) {
        mListItems = items;
        mOriginalText = allText;

        mSelected = new boolean[mListItems.size()];
        Arrays.fill(mSelected, false);

        if (allText != null && !allText.isEmpty()) {
            mDefaultText = allText;
        }

        setSelectedItems(Arrays.stream(allText.split(DELIMITER)).collect(Collectors.toList()));
        setSpinnerAdapter(mDefaultText, mSelectedItems);
    }

    public void setItems(List<String> items, String allText, ArrayAdapter<String> adapter) {
        mListItems = items;
        mOriginalText = allText;

        mSelected = new boolean[mListItems.size()];
        Arrays.fill(mSelected, false);

        if (allText != null && !allText.isEmpty()) {
            mDefaultText = allText;
        }

        setSelectedItems(Arrays.stream(allText.split(DELIMITER)).collect(Collectors.toList()));
        setAdapter(adapter);
    }

    public void setSelectedItems(List<String> items) {
        List<String> selectedItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            int index = mListItems.indexOf(items.get(i));
            if (index > -1) {
                mSelected[index] = true;
                selectedItems.add(items.get(i));
            }
        }

        if (selectedItems.size() > 0) {
            mSelectedItems = selectedItems;
//            setSpinnerAdapter(TextUtils.join(DELIMITER, mSelectedItems), mSelectedItems);
        } else {
            mDefaultText = Constant.DICT_FIELD_EMPTY;
//            setSpinnerAdapter(mDefaultText, new ArrayList<>());
        }
    }

    public List<String> getSelectedItems() {
        return mSelectedItems;
    }

    public String getmDefaultText() {
//        mDefaultText = TextUtils.join(DELIMITER, getSelectedItems());
//        return mDefaultText;
        if (mSelectedItems.size() > 0) {
            mDefaultText = TextUtils.join(DELIMITER, mSelectedItems);
        } else {
            mDefaultText = Constant.DICT_FIELD_EMPTY;
        }
        return mDefaultText;
    }

    public void setDelimiter(String delimiter) {
        DELIMITER = delimiter;
    }
}
