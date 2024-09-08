package com.mmjang.ankihelper.ui.share;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.share
 * @ClassName: SharePopupWindow
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/6/18 5:09 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/6/18 5:09 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */

import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.util.Constant;


public class SharePopupWindow extends PopupWindow {

    //每行显示多少个
    private static final int NUM = 5;

    private View mMenuView;
    private GridView mGridView;
    private TextView mTextViewClose;
    private AppInfoAdapter mAdapter;

    private List<AppInfo> mAppinfoList;

    private String imgpath;
    private String shareTitle;
    private String shareContent;
    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }
    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }
    public void setShareContent(String shareContent) {
        if(shareContent == null)
            shareContent = "";
        this.shareContent = shareContent;
    }

    /**
     * 构造函数
     * @param context
     */
    public SharePopupWindow(final Context context, final String text) {
        super(context);
        shareContent = text;
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mMenuView = inflater.inflate(R.layout.share_dialog, null);
//        //获取控件
//        mGridView=(GridView) mMenuView.findViewById(R.id.sharePopupWindow_gridView);
//        mTextViewClose=(TextView) mMenuView.findViewById(R.id.sharePopupWindow_close);
//        //获取所有的非系统应用
//        mAppinfoList = ShareUtil.getAllApps(context);
//        //适配GridView
//        mAdapter=new AppInfoAdapter(context, mAppinfoList);
//        mGridView.setAdapter(mAdapter);
//
//        //修改GridView
//        changeGridView(context);
//
//        mGridView.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                                    long id) {
//                // TODO Auto-generated method stub
//                //使用其他APP打开文件
////                Intent intent = new Intent();
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
////                intent.setAction(Intent.ACTION_VIEW);
//                //LogUtil.debug("图片地址："+imgpath);
//                //我这里分享的是图片，如果你要分享链接和文本，可以在这里自行发挥
////                Uri uri = FileProvider.getUriForFile(context, "fileprovider", new File(imgpath));
////                intent.setDataAndType(uri, MapTable.getMIMEType(imgpath));
//
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_TEXT, shareContent);
//                context.startActivity(intent);
//            }
//        });
//        //取消按钮
//        mTextViewClose.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                dismiss();
//            }
//        });
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置窗口外也能点击（点击外面时，窗口可以关闭）
        this.setOutsideTouchable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.circleDialog);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        intent.putExtra(Constant.INTENT_ANKIHELPER_ACTION, true);
        context.startActivity(Intent.createChooser(intent, "share"));
    }

    /**
     * 将GridView改成单行横向布局
     */
    private void changeGridView(Context context) {
        // item宽度
        int itemWidth = dip2px(context, 90);
        // item之间的间隔
        int itemPaddingH = dip2px(context, 1);
        //计算一共显示多少行;
        int size = mAppinfoList.size();
        //int row=(size<=NUM) ? 1 :( (size%NUM>0) ? size/NUM+1 : size/NUM );
        //每行真正显示多少个
        int rowitem = (size<NUM)?size:NUM;
        // 计算GridView宽度
        int gridviewWidth = rowitem * (itemWidth + itemPaddingH);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        mGridView.setLayoutParams(params);
        mGridView.setColumnWidth(itemWidth);
        mGridView.setHorizontalSpacing(itemPaddingH);
        mGridView.setStretchMode(GridView.NO_STRETCH);
        mGridView.setNumColumns(rowitem);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @param context   上下文
     * @param dpValue   dp值
     * @return  px值
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}