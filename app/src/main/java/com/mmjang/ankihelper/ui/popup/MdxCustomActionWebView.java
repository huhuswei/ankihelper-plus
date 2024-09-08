package com.mmjang.ankihelper.ui.popup;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class MdxCustomActionWebView  extends WebView {
    private static final String CUSTOM_MENU_JS_INTERFACE = "MdxCustomMenuJSInterface";
//    private ActionMode mActionMode;
    private ActionSelectListener mActionSelectListener;
    private List<String> mCustomMenuList; //自定义添加的选项
    private List<String> mStayMenuList; //需要保留的系统选项

    public MdxCustomActionWebView(Context context) {
        super(context);
        init();
    }

    public MdxCustomActionWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MdxCustomActionWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        addJavascriptInterface(new ActionSelectInterface(this), CUSTOM_MENU_JS_INTERFACE);
        Settings settings = Settings.getInstance(MyApplication.getContext());
        mStayMenuList = new ArrayList<>();
        if(settings.get(Settings.WEBVIEW_CONTEXT_MENU_COPY, true))
            mStayMenuList.add(getResources().getString(android.R.string.copy));
        if(settings.get(Settings.WEBVIEW_CONTEXT_MENU_SELECT_ALL, true))
            mStayMenuList.add(getResources().getString(android.R.string.selectAll));
        mCustomMenuList = new ArrayList<>();

//        if(settings.get(Settings.WEBVIEW_CONTEXT_MENU_SHARE_TEXT, true))
//            mCustomMenuList.add(getResources().getString(R.string.str_share));
//        mCustomMenuList.add(getResources().getString(R.string.str_add_rich_text));
//        if(settings.get(Settings.WEBVIEW_CONTEXT_MENU_ADD_PLAIN, true))
//            mCustomMenuList.add(getResources().getString(R.string.str_add_plain_text));
    }


    public void setmCustomMenuList(List<String> list) {
        if (list != null) {
            this.mCustomMenuList.clear();
            Settings settings = Settings.getInstance(MyApplication.getContext());
            if(settings.get(Settings.WEBVIEW_CONTEXT_MENU_SHARE_TEXT, true))
                mCustomMenuList.add(getResources().getString(R.string.str_share));
            this.mCustomMenuList.addAll(list);
        }
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return super.startActionMode(buildCustomCallback(callback));
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        return super.startActionMode(buildCustomCallback(callback), type);
    }

    /**
     * 自定义callback，用于菜单过滤
     *
     * @param callback
     * @return
     */
    private ActionMode.Callback buildCustomCallback(final ActionMode.Callback callback) {
        ActionMode.Callback customCallback;
        customCallback = new ActionMode.Callback2() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return callback.onCreateActionMode(mode, menu);
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                callback.onPrepareActionMode(mode, menu);
                addCustomMenu(mode);
                boolean hasShareItem = false;
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem menuItem = menu.getItem(i);
                    if (!isMenuStay(menuItem)) {
                        menuItem.setVisible(false);
                    } else {
                        //隐藏多余的分享项
                        if(menuItem.getTitle().equals(getResources().getString(R.string.str_share))) {
                            if(hasShareItem) {
                                menuItem.setVisible(false);
                                continue;
                            } else {
                                hasShareItem = true;
                            }
                        }//end

                        if (menuItem.getItemId() == 0) {
                            //自定义或是通过PROCESS_TEXT方案加入到菜单中的选项，item都为0
                            Intent intent = menuItem.getIntent();
                            ComponentName componentName = intent == null ? null : intent.getComponent();
                            //根据包名比较菜单中的选项是否是本app加入的
                            if (componentName != null && getContext().getPackageName().equals(componentName.getPackageName())) {
                                menuItem.setVisible(true);
                            } else {
                                menuItem.setVisible(false);
                            }
                        } else {
                            menuItem.setVisible(true);
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item == null || TextUtils.isEmpty(item.getTitle())) {
                    return callback.onActionItemClicked(mode, item);
                }
                String title = item.getTitle().toString();
                if (mCustomMenuList != null && mCustomMenuList.contains(title)) {
                    try {
//                            if(title.equals(getResources().getString(R.string.str_add_rich_text))) {
//                                getSelectedRichData(title);
//                            } else
//                                getSelectedPlainData(title);

                        if(mCustomMenuList.contains(title) &&
                                !title.equals(getResources().getString(R.string.str_share)) &&
                                !Settings.getInstance(MyApplication.getContext()).
                                        get(Settings.WEBVIEW_CONTEXT_MENU_RICH_TO_PLAIN, false)) {
                            getSelectedRichData(title);
                        } else {
                            getSelectedPlainData(title);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (mActionSelectListener != null) {
                    mActionSelectListener.onClick(title, "");
                }
                return callback.onActionItemClicked(mode, item);
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                callback.onDestroyActionMode(mode);
            }

            @Override
            public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
                if (callback instanceof ActionMode.Callback2) {
                    ActionMode.Callback2 tempCallback2 = (ActionMode.Callback2) callback;
                    tempCallback2.onGetContentRect(mode, view, outRect);
                } else {
                    super.onGetContentRect(mode, view, outRect);
                }
            }
        };
        return customCallback;
    }

    /**
     * 添加自定义菜单
     *
     * @param actionMode
     */
    private ActionMode addCustomMenu(ActionMode actionMode) {
        if (actionMode != null && mCustomMenuList != null) {
            Menu menu = actionMode.getMenu();
            int groupId = 0;
            //查找"复制"选项的信息
            for (int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);
//                if ("复制".equals(menuItem.getTitle())) {
                if (menuItem.getTitle().equals(getResources().getString(android.R.string.copy))) {
                    groupId = menuItem.getGroupId();
                }
            }
            //添加自定义选项
            int size = mCustomMenuList.size();
            for (int i = 0; i < size; i++) {
                //intent主要用于过滤菜单时使用
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(getContext().getPackageName(), ""));
                String title = mCustomMenuList.get(i);
                //非系统选项，itemId只能为0，否则会崩溃（ Unable to find resource ID）
                //order可以自己选择控制，但是有些rom不行
                menu.add(groupId, 0, 0, title).setIntent(intent);
            }
//            mActionMode = actionMode;
        }
        return actionMode;
    }

    /**
     * 是否为保留的菜单选项
     *
     * @param menuItem
     * @return
     */
    private boolean isMenuStay(MenuItem menuItem) {
        CharSequence title = menuItem == null ? "" : menuItem.getTitle();
        return (mStayMenuList != null && mStayMenuList.contains(title)) || (mCustomMenuList != null && mCustomMenuList.contains(title));
    }

    private boolean isMenuStayingMenuList(MenuItem menuItem) {
        CharSequence title = menuItem == null ? "" : menuItem.getTitle();
        return mStayMenuList != null && mStayMenuList.contains(title);
    }

    private boolean isMenuStayingCutomMenuList(MenuItem menuItem) {
        CharSequence title = menuItem == null ? "" : menuItem.getTitle();
        return mCustomMenuList != null && mCustomMenuList.contains(title);
    }

    /**
     * This method releases the ActionMode when a user cancels an action or switches to another action.
     * The ActionMode is ended by calling this method to finish the current ActionMode.
     */
//    public void releaseAction() {
//        if (mActionMode != null) {
//            mActionMode.finish();
//            mActionMode = null;
//        }
//    }
//
//    public void restartActionMode() {
//        if(mActionMode != null) {
//            mActionMode.finish();
//        }
//    }

    /**
     * 点击的时候，获取网页中选择的文本，回掉到原生中的js接口
     *
     * @param title 传入点击的item文本，一起通过js返回给原生接口
     */
    private void getSelectedPlainData(String title) {

        String js = "javascript:(function getSelectedText() {" +
                "var txt;" +
                "var title = \"" + title + "\";" +
                "if (window.getSelection) {" +
                "txt = window.getSelection().getRangeAt(0).toString();" +
                "} else if (window.document.getSelection) {" +
                "txt = window.document.getSelection().getRangeAt(0)" + ".toString();" +
                "} else if (window.document.selection) {" +
                "txt = window.document.selection.createRange().text;" +
                "window.getSelection().removeAllRanges();" +
                "}" +
                CUSTOM_MENU_JS_INTERFACE + ".callback(txt,title);" + "})()";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(js, null);
        } else {
            loadUrl(js);
        }
    }

    private void getSelectedRichData(String title) {

        String js = "javascript:(function getSelectedText() {" +
                "var txt;" +
                "var title = \"" + title + "\";" +
                "if (window.getSelection) {" +
                "var rangeObj = window.getSelection().getRangeAt(0);" +
//                "var ancestor = rangeObj.commonAncestorContainer;" +
//                "var parent = ancestor.parentNode;" +
//                "var txt = ancestor.outerHTML;" +
                "var content = rangeObj.cloneContents();" +
                "var defTag = document.createElement(\"div\");" +
                "defTag.appendChild(content);" +
                "txt = defTag.innerHTML;" +
                "window.getSelection().removeAllRanges();" +
                "}" +
                CUSTOM_MENU_JS_INTERFACE + ".callback(txt,title);" + "})()";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(js, null);
        } else {
            loadUrl(js);
        }
    }

    /**
     * 设置点击回调
     *
     * @param actionSelectListener
     */
    public void setActionSelectListener(ActionSelectListener actionSelectListener) {
        this.mActionSelectListener = actionSelectListener;
    }

    /**
     * js选中的回掉接口
     */
    private class ActionSelectInterface {

        MdxCustomActionWebView mContext;

        ActionSelectInterface(MdxCustomActionWebView context) {
            mContext = context;
        }

        @JavascriptInterface
        public void callback(final String value, final String title) {
            if (mActionSelectListener != null) {
                mActionSelectListener.onClick(title, value);
            }
        }
    }
}