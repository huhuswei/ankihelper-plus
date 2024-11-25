package com.mmjang.ankihelper.ui.floating;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.floating.assist.AssistService;
import com.mmjang.ankihelper.util.BuildConfig;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Trace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rikka.shizuku.Shizuku;

public class UserService extends IUserService.Stub {

    //shizuku
    private IUserService iUserService;
    private final static int PERMISSION_CODE = 10001;
    private boolean shizukuServiceState = false;

    @Override
    public void destroy() throws RemoteException {
        System.exit(0);
    }

    @Override
    public void exit() throws RemoteException {
        destroy();
    }

    @Override
    public String execLine(String command) throws RemoteException {
        try {
            // 执行shell命令
            Process process = Runtime.getRuntime().exec(command);
            // 读取执行结果
            return readResult(process);
        } catch (IOException | InterruptedException e) {
            throw new RemoteException();
        }
    }

    @Override
    public String execArr(String[] command) throws RemoteException {
        try {
            // 执行shell命令
            Process process = Runtime.getRuntime().exec(command);
            // 读取执行结果
            return readResult(process);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取执行结果，如果有异常会向上抛
     */
    public String readResult(Process process) throws IOException, InterruptedException {
        StringBuilder stringBuilder = new StringBuilder();
        // 读取执行结果
        InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        inputStreamReader.close();
        process.waitFor();
        return stringBuilder.toString();
    }

    //----------------
    public void addListeners() {
        // 添加权限申请监听
        Shizuku.addRequestPermissionResultListener(onRequestPermissionResultListener);

        // Shiziku服务启动时调用该监听
        Shizuku.addBinderReceivedListenerSticky(onBinderReceivedListener);

        // Shiziku服务终止时调用该监听
        Shizuku.addBinderDeadListener(onBinderDeadListener);
    }

    public void removeListeners() {
        // 移除权限申请监听
        Shizuku.removeRequestPermissionResultListener(onRequestPermissionResultListener);

        Shizuku.removeBinderReceivedListener(onBinderReceivedListener);

        Shizuku.removeBinderDeadListener(onBinderDeadListener);

        Shizuku.unbindUserService(userServiceArgs, serviceConnection, true);
    }

    public void connectShizuku() {
        if (checkShizukuServiceState() && checkPermission()) {
            if (iUserService == null) {
                // 绑定shizuku服务
                Shizuku.bindUserService(userServiceArgs, serviceConnection);
            }
        }
    }

    public boolean checkShizukuServiceState() {
        return shizukuServiceState;
    }

    private final Shizuku.OnBinderReceivedListener onBinderReceivedListener = () -> {
        shizukuServiceState = true;
        Trace.i("Shizuku服务已启动");
    };

    private final Shizuku.OnBinderDeadListener onBinderDeadListener = () -> {
        shizukuServiceState = false;
        iUserService = null;
        Trace.i("Shizuku服务被终止");
    };

    private String exec(String command) throws RemoteException {
        // 检查是否存在包含任意内容的双引号
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(command);

        // 下面展示了两种不同的命令执行方法
        if (matcher.find()) {
            ArrayList<String> list = new ArrayList<>();
            Pattern pattern2 = Pattern.compile("\"([^\"]*)\"|(\\S+)");
            Matcher matcher2 = pattern2.matcher(command);

            while (matcher2.find()) {
                if (matcher2.group(1) != null) {
                    // 如果是引号包裹的内容，取group(1)
                    list.add(matcher2.group(1));
                } else {
                    // 否则取group(2)，即普通的单词
                    list.add(matcher2.group(2));
                }
            }

            // 这种方法可用于执行路径中带空格的命令，例如 ls /storage/0/emulated/temp dir/
            // 当然也可以执行不带空格的命令，实际上是要强于另一种执行方式的
            return iUserService.execArr(list.toArray(new String[0]));
        } else {
            // 这种方法仅用于执行路径中不包含空格的命令，例如 ls /storage/0/emulated/
            return iUserService.execLine(command);
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Trace.i("Shizuku服务连接成功");

            if (iBinder != null && iBinder.pingBinder()) {
                iUserService = IUserService.Stub.asInterface(iBinder);
            }
            Settings appSettings = Settings.getInstance(MyApplication.getApplication());
            if (appSettings.getFloatBallEnable()) {
                enabledAccessibilityService();
            } else {
                disabledAccessibilityService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Trace.i("Shizuku服务连接断开");
            iUserService = null;
        }
    };

    private final Shizuku.UserServiceArgs userServiceArgs =
            new Shizuku.UserServiceArgs(new ComponentName(BuildConfig.APPLICATION_ID, UserService.class.getName()))
                    .daemon(false)
                    .processNameSuffix("adb_service")
                    .debuggable(BuildConfig.DEBUG)
                    .version(BuildConfig.VERSION_CODE);

    /**
     * 动态申请Shizuku adb shell权限
     */
    public void requestShizukuPermission() {
        if (Shizuku.isPreV11()) {
            Trace.i("当前shizuku版本不支持动态申请权限");
            return;
        }

        if (checkPermission()) {
            Trace.i("已拥有Shizuku权限");
            return;
        }

        // 动态申请权限
        Shizuku.requestPermission(UserService.PERMISSION_CODE);
    }

    private final Shizuku.OnRequestPermissionResultListener onRequestPermissionResultListener = new Shizuku.OnRequestPermissionResultListener() {
        @Override
        public void onRequestPermissionResult(int requestCode, int grantResult) {
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            if (granted) {
                Trace.i("Shizuku授权成功");
            } else {
                Trace.i("Shizuku授权失败");
            }
        }
    };

    /**
     * 判断是否拥有shizuku adb shell权限
     */
    public boolean checkPermission() {
        return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
    }

    public boolean enabledAccessibilityService() {
        Context context = MyApplication.getApplication();
        ComponentName componentName = new ComponentName(Constant.ANKIHELPER_PACKAGE_NAME, AssistService.class.getName());
        String enabledServices = "";
        String serviceName = componentName.flattenToShortString();

        try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                ContentResolver contentResolver = context.getContentResolver();
                // 如果有 WRITE_SECURE_SETTINGS 权限，直接开启辅助功能
                android.provider.Settings.Secure.putInt(contentResolver, android.provider.Settings.Secure.ACCESSIBILITY_ENABLED, 1);
                enabledServices = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if (TextUtils.isEmpty(enabledServices) || !enabledServices.contains(serviceName)) {
                    String modified = enabledServices + ":" + serviceName;
                    return android.provider.Settings.Secure.putString(contentResolver, android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, modified);
                }
            } else if (iUserService != null) {
                enabledServices = exec("settings get secure enabled_accessibility_services");
                String cmd = "settings put secure enabled_accessibility_services "
                        + serviceName + ":" + enabledServices;
                exec(cmd);
                return true;
            }
        } catch (Exception e) {
            Trace.e("enable accessibility failed:", e.getMessage());
        }
        return false;
    }

    public boolean disabledAccessibilityService() {
        Context context = MyApplication.getApplication();
        ComponentName componentName = new ComponentName(Constant.ANKIHELPER_PACKAGE_NAME, AssistService.class.getName());
        String enabledServices = "";
        String serviceName = componentName.flattenToShortString();

        try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                ContentResolver contentResolver = context.getContentResolver();
                // 如果有 WRITE_SECURE_SETTINGS 权限，直接开启辅助功能
                android.provider.Settings.Secure.putInt(contentResolver, android.provider.Settings.Secure.ACCESSIBILITY_ENABLED, 1);
                enabledServices = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if (!TextUtils.isEmpty(enabledServices) && enabledServices.contains(serviceName)) {
                    String regex = String.format("(%s:|:%s|%s)", serviceName, serviceName, serviceName);
                    String modified = enabledServices.replaceFirst(regex, "");
                    return android.provider.Settings.Secure.putString(contentResolver, android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, modified);
                }
            } else if (iUserService != null) {
                enabledServices = exec("settings get secure enabled_accessibility_services");
                String regex = String.format("(%s:|:%s|%s)", serviceName, serviceName, serviceName);
                String modified = enabledServices.replaceFirst(regex, "");
                String cmd = "settings put secure enabled_accessibility_services "
                        + modified;
                exec(cmd);
                return true;
            }
        } catch (Exception e) {
            Trace.e("enable accessibility failed:", e.getMessage());
        }
        return false;
    }

}