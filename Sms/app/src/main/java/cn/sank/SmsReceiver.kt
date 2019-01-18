package cn.sank

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.telephony.SmsMessage


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SmsReceiver : BroadcastReceiver() {
    private var SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED"
    private var fileName = "SmsLog.txt"
    private var path = "${Environment.getExternalStorageDirectory()}/$fileName"

    @SuppressLint("SimpleDateFormat")
    override fun onReceive(context: Context, intent: Intent) {
        val simpleDateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
                .format(Date(System.currentTimeMillis()))
        val action = intent.action
        if(SMS_RECEIVED_ACTION == action){
            if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val pdus = intent.extras.get("pdus") as Array<*>
                for (pdu in pdus) {
                    val sms = SmsMessage.createFromPdu(pdu as ByteArray)
                    val body = sms.messageBody
                    val file = File(path)
                    if(!file.exists()){
                        file.createNewFile()
                    }
                    val fos = FileOutputStream(file,true)
                    fos.write("$simpleDateFormat-->$body\r\n".toByteArray())
                    fos.close()
                }
            }else{
                Toast.makeText(context,"sd卡不可用",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
