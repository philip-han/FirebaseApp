package net.cequals.firebaseapp

import android.preference.PreferenceManager
import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    private val TAG = MyFirebaseMessagingService::class.java.simpleName

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
    }

    override fun onNewToken(token: String?) {
        token?.let {
            Log.i(TAG, "received token: $it **********************************************************************************************")
            val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val editor = pref.edit()
            editor.putString("deviceToken", it)
            editor.apply()
            val email: String? = pref.getString("email", "test@mig.org")
            email?.let {
                serverPutToken(it, token)
            }
        }
    }

    private fun serverPutToken(email: String, token: String) {
        Log.i(TAG, "saving token: $email $token **********************************************************************************************")
        val functions = FirebaseFunctions.getInstance()
        val result = functions.getHttpsCallable("storeDeviceIdCallable")
                .call(mutableMapOf<String, String>().apply {
                    put("id", email)
                    put("deviceId", token)
                }).continueWith { it.result?.data }
        result.addOnCompleteListener {
            Log.i(TAG, it.result.toString())
        }
    }

}