package cn.sank

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log

class MainActivity : AppCompatActivity() {

    private var openAppDetDialog: AlertDialog? = null

    private var permission = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var smsReceiver:SmsReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions(this@MainActivity,permission,1)
        smsReceiver = SmsReceiver()
        val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsReceiver,intentFilter)
    }

    override fun onDestroy() {
        unregisterReceiver(smsReceiver)
        super.onDestroy()
    }

    private fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        // 先检查是否已经授权
        if (!checkPermissionsGroup(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }
    }

    private fun checkPermissionsGroup(context: Context, permissions: Array<String>): Boolean {
        var result = false
        for (permission in permissions) {
            result = checkPersmission(context, permission)
            Log.e("PermissionUtils", "result$result")
        }
        return result
    }

    private fun checkPersmission(context: Context, permission: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            var isAllGranted = true
            for (grant in grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false
                    break
                }
            }
            if (!isAllGranted) {
                showPermissionDialog()
            }
        }
    }


    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.app_name) + "需要访问 \"信息\" 和 \"外部存储器\",否则会影响绝大部分功能使用, 请到 \"应用信息 -> 权限\" 中设置！")
        builder.setPositiveButton("去设置") { _, _ ->
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:$packageName")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            startActivity(intent)
        }
        builder.setCancelable(false)
        builder.setNegativeButton("暂不设置") { _, _ -> finish() }
        if (null == openAppDetDialog) {
            openAppDetDialog = builder.create()
        }
        if (null != openAppDetDialog && !openAppDetDialog!!.isShowing) {
            openAppDetDialog!!.show()
        }
    }


}
