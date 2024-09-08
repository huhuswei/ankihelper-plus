package com.mmjang.ankihelper.data.dict.JPDeinflector;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import com.mmjang.ankihelper.util.Trace;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.data.dict.JPDeinflector
 * @ClassName: MarkArea
 * @Description: java类作用描述
 * @Author: 唐朝
 * @CreateDate: 2022/7/21 8:51 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/7/21 8:51 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MarkedArea implements Parcelable {
    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;

    protected MarkedArea(Parcel in) {
        mLeft = in.readInt();
        mTop = in.readInt();
        mRight = in.readInt();
        mBottom = in.readInt();
        Trace.i("read l t r b: ", String.format("%d %d %d %d", mLeft, mTop, mRight, mBottom));
    }

    public MarkedArea(int left, int top, int right, int bottom) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
        Trace.i("constructor l t r b: ", String.format("%d %d %d %d", mLeft, mTop, mRight, mBottom));

    }
    public static final Creator<MarkedArea> CREATOR = new Creator<MarkedArea>() {
        @Override
        public MarkedArea createFromParcel(Parcel in) {
            return new MarkedArea(in);
        }

        @Override
        public MarkedArea[] newArray(int size) {
            return new MarkedArea[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mLeft);
        dest.writeInt(mTop);
        dest.writeInt(mRight);
        dest.writeInt(mBottom);
        Trace.i("write l t r b: ", String.format("%d %d %d %d", mLeft, mTop, mRight, mBottom));

    }

    public Rect getRect() {
        Trace.i("getRect l t r b: ", String.format("%d %d %d %d", mLeft, mTop, mRight, mBottom));
        Rect rect = new Rect(mLeft, mTop, mRight, mBottom);
        return rect;
    }


    public void getInfor() {
        Trace.i("infor l t r b:", String.format("l t r b: %d %d %d %d", mLeft, mTop, mRight, mBottom));
    }
}
