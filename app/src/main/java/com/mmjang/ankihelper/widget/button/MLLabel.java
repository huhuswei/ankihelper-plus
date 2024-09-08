package com.mmjang.ankihelper.widget.button;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.widget.MLLabelButton
 * @ClassName: MLLabel
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/10/15 5:53 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/10/15 5:53 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MLLabel {
    private String name;
    private String front;
    private String behind;
    private int reId;

    public MLLabel(String name, String front, String behind) {
        this.name = name;
        this.front = front;
        this.behind = behind;
    }

    public MLLabel(String name, String front, String behind, int resId) {
        this.name = name;
        this.front = front;
        this.behind = behind;
        this.reId = resId;
    }

    public String getName() {
        return name;
    }

    public String getFront() {
        return front;
    }

    public String getBehind() {
        return behind;
    }

    public int getReId() { return  reId; }
}
