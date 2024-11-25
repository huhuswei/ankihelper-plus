package com.mmjang.ankihelper.ui.floating

import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.mmjang.ankihelper.MyApplication
import com.mmjang.ankihelper.R
import com.mmjang.ankihelper.data.Settings
import com.mmjang.ankihelper.ui.floating.assist.AssistFloatWindow
import com.mmjang.ankihelper.util.Constant
import com.mmjang.ankihelper.util.ToastUtil
import com.mmjang.ankihelper.util.Trace


class QuickStartTileService : TileService(), TileServiceLongClick {

    private var setting = Settings.getInstance(MyApplication.getContext())
    private var callbackChangeState =  SharedPreferences.OnSharedPreferenceChangeListener {
        sharePreferences, key ->
        if(this.qsTile != null) {
            if (setting.floatBallEnable == true) {
                this.qsTile.state = Tile.STATE_ACTIVE
            } else {
                this.qsTile.state = Tile.STATE_INACTIVE
            }
            var icon: Icon = Icon.createWithResource(applicationContext, R.drawable.icon_light)
            qsTile.icon = icon//设置图标
            qsTile.updateTile()//更新Tile
        } else
            Trace.e("quick tile", "null")
    }

    companion object {
        const val TAG = "QuickStartTileService"
//        var isStarted = true
    }

    override fun onCreate() {
        super.onCreate()
        setting.sharedPreferences.registerOnSharedPreferenceChangeListener(callbackChangeState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    init {
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requestListeningState(this,
            ComponentName(this, QuickStartTileService::class.java))
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        requestListeningState(this,
            ComponentName(this, QuickStartTileService::class.java))

        val userService = MyApplication.getShizukuService()
        userService.addListeners()
        userService.connectShizuku()

        if(setting.floatBallEnable && MyApplication.getShizukuService().checkShizukuServiceState()) {
            MyApplication.getShizukuService().enabledAccessibilityService()
        }
        return super.onBind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!isTileServiceRunning()) {
            enableTileService()
        }
        super.onTaskRemoved(rootIntent)
    }

    private fun isTileServiceRunning(): Boolean {
        val packageManager = packageManager
        val componentName = ComponentName(this, Constant.QUICKSTARTTILE_SERVICE_ID)
        val state = packageManager.getComponentEnabledSetting(componentName)
        return state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
    }

    private fun enableTileService() {
        val packageManager = packageManager
        val componentName = ComponentName(this, Constant.QUICKSTARTTILE_SERVICE_ID)
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        val i = Intent(this, QuickStartTileService::class.java)
        startService(i)
    }
    //当用户从Edit栏添加到快速设置中调用
    override fun onTileAdded() {
        super.onTileAdded()
        ToastUtil.show(applicationContext, "onTileAdded")
        Trace.d("onTileAdded", TAG)
        requestListeningState(this,
            ComponentName(this, QuickStartTileService::class.java)
        )
        refresh()
    }

    //当用户从快速设置栏中移除的时候调用
//    override fun onTileRemoved() {
//        super.onTileRemoved()
//        Trace.d("onTileRemoved", TAG)
//    }
    // 点击的时候
    /*  我们可以通过getQsTile来获得Tile对象，通过getState() 来获得Tile当前状态。
      - STATE_ACTIVE 开启状态
      - STATE_INACTIVE 关闭状态
      - STATE_UNAVAILABLE 非可点击状态
      最后必须调用updateTile() 来触发刷新*/

    override fun onClick() {
        super.onClick()
        Trace.d("onClick", TAG)
        refresh()
    }

    private fun refresh() {
        val state = qsTile.state
        Trace.d("onClick state = " + qsTile.state.toString(), TAG)
        when (state) {
            Tile.STATE_INACTIVE -> {
                qsTile.state = Tile.STATE_ACTIVE// 更改成活跃状态
                startOrShowNotification()
            }
            Tile.STATE_ACTIVE -> {
                qsTile.state = Tile.STATE_INACTIVE//更改成非活跃状态
                cancelNotification()
            }
            Tile.STATE_UNAVAILABLE -> {
            }
        }
        var icon: Icon = Icon.createWithResource(applicationContext, R.drawable.icon_light)
        qsTile.icon = icon//设置图标
        qsTile.updateTile()//更新Tile
    }
    private fun cancelNotification() {
        setting.floatBallEnable = false
        AssistFloatWindow.instance.hide()
        MyApplication.getShizukuService().disabledAccessibilityService()
    }

    private fun startOrShowNotification() {
        setting.floatBallEnable = true
        AssistFloatWindow.instance.show()
        MyApplication.getShizukuService().enabledAccessibilityService()
    }

    override fun onLongClick() {
    }

    // 打开下拉菜单的时候调用,当快速设置按钮并没有在编辑栏拖到设置栏中不会调用
    //在TleAdded之后会调用一次
//    override fun onStartListening() {
//        super.onStartListening()
//        Trace.d("onStartListening", TAG)
//
//        if(setting.floatBallEnable)
//            qsTile.state = Tile.STATE_ACTIVE
//        else
//            qsTile.state = Tile.STATE_INACTIVE
//        var icon: Icon = Icon.createWithResource(applicationContext, R.drawable.ic_ali_search)
//        qsTile.icon = icon//设置图标
//        qsTile.updateTile()//更新Tile
//    }

    // 关闭下拉菜单的时候调用,当快速设置按钮并没有在编辑栏拖到设置栏中不会调用
    // 在onTileRemoved移除之前也会调用移除
//    override fun onStopListening() {
//        super.onStopListening()
//        Trace.d("onStopListening", TAG)
//
//        if(setting.floatBallEnable)
//            qsTile.state = Tile.STATE_ACTIVE
//        else
//            qsTile.state = Tile.STATE_INACTIVE
//        var icon: Icon = Icon.createWithResource(applicationContext, R.drawable.ic_ali_search)
//        qsTile.icon = icon//设置图标
//        qsTile.updateTile()//更新Tile
//    }

}

interface TileServiceLongClick{
    fun onLongClick()
}