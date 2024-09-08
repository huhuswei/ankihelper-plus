package com.mmjang.ankihelper.widget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.LauncherActivity;
import com.mmjang.ankihelper.util.ActivationCodeUtils;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.SystemUtils;
import com.mmjang.ankihelper.util.ToastUtil;

public class ActivationDialog extends DialogFragment {
    private EditText etUsername, etRegistrationCode, etActivationCode;
//    private static final int BLOCK_COUNT = 4;
//
//    private LinearLayout mMondrianLayout;
//    private View[] mBlocks;
//    private float[] mBlockAreas;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Settings settings = Settings.getInstance(MyApplication.getContext());
//        ColorThemeUtils.initColorTheme(ActivationDialog.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_activation, null);
        builder.setView(view);

        // 获取输入框和按钮控件
        etUsername = view.findViewById(R.id.et_username);
        etRegistrationCode = view.findViewById(R.id.et_registration_code);
        etActivationCode = view.findViewById(R.id.et_activation_code);
        Button btnActivate = view.findViewById(R.id.btn_activate);

        if (ActivationCodeUtils.verifyActivationCode(
                settings.get(Constant.REG_USERNAME, ""),
                SystemUtils.getDeviceID(requireContext()),
                settings.get(Constant.REG_ACTIVATIONCODE, "")
        )) {
            etUsername.setEnabled(false);
            etRegistrationCode.setEnabled(false);
            etActivationCode.setEnabled(false);
            btnActivate.setEnabled(false);
            btnActivate.setText(R.string.btn_activated);
        }

        etUsername.setText(settings.get(Constant.REG_USERNAME, ""));
//        etRegistrationCode.setEnabled(false);
        etRegistrationCode.setText(SystemUtils.getDeviceID(requireContext()));
        etRegistrationCode.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DEL:
                    case KeyEvent.KEYCODE_FORWARD_DEL:
                    case KeyEvent.KEYCODE_ENTER:
                        return true;
                }
            }
            return false;
        });
        etRegistrationCode.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("text", etRegistrationCode.getText()));
                ToastUtil.show("复制成功");
            }
            return true;
        });

        etActivationCode.setText(settings.get(Constant.REG_ACTIVATIONCODE, ""));

        // 设置按钮点击事件处理程序
        btnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入的用户名、注册码和激活码
                String username = etUsername.getText().toString();
                String deviceId = SystemUtils.getDeviceID(MyApplication.getContext());
                String actCode = etActivationCode.getText().toString();

                if (username.equals("")) {
                    Toast.makeText(requireContext(), "Username is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (deviceId.equals("")) {
                    Toast.makeText(requireContext(), "DeviceId is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (actCode.equals("")) {
                    Toast.makeText(requireContext(), "Activation Code is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }


                boolean isValid = false;
                try {
                    isValid = ActivationCodeUtils.verifyActivationCode(username, deviceId, actCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 显示验证结果，如果激活码有效则跳转到LauncherActivity
                settings.put(Constant.REG_USERNAME, username);
                if (isValid) {
                    settings.put(Constant.REG_ACTIVATIONCODE, actCode);
                    Intent intent = new Intent(getActivity(), LauncherActivity.class);
                    startActivity(intent);
                    dismiss();  // 关闭当前DialogFragment
                    Toast.makeText(requireContext(), "Activate successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    //start 测试代码
//                    String localRegistrationCode = ActivationCodeUtils.generateLocalRegistrationCode(username, deviceId);
//                    LocalDate date = LocalDate.now().plusDays(1);
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//                    String dateStr = date.format(formatter);
//                    etActivationCode.setText(ActivationCodeUtils.generateActivationCode(username, localRegistrationCode, dateStr));
                    //end
                    Toast.makeText(requireContext(), "Invalid activation code.", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        //<!-- Mondrian风格的图形 -->
//        // 获取布局和块对象
//        mMondrianLayout = view.findViewById(R.id.layout_mondrian);
//        mBlocks = new View[BLOCK_COUNT];
//        mBlocks[0] = view.findViewById(R.id.red_block);
//        mBlocks[1] = view.findViewById(R.id.yellow_block);
//        mBlocks[2] = view.findViewById(R.id.blue_block);
//        mBlocks[3] = view.findViewById(R.id.white_block);

        return builder.create();
    }
}