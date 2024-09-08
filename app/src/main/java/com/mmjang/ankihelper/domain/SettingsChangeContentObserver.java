package com.mmjang.ankihelper.domain;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.domain
 * @ClassName: SettingsValueChangeContentObserver
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/9/5 8:32 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/9/5 8:32 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */

public class SettingsChangeContentObserver extends ContentObserver {

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SettingsChangeContentObserver(Handler handler) {
        super(handler);
    }


}
