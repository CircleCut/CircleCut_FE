import android.content.Context
import android.content.SharedPreferences
import java.util.*
import kotlin.collections.HashMap

class SessionManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("SessionPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        const val KEY_USER_ID = "userId"
        const val KEY_SESSION_TOKEN = "sessionToken"
        const val KEY_ENCRYPTION_ID ="encryptionToken"
        const val KEY_EXPIRATION_TIME = "expirationTime"
    }

    fun createLoginSession(userId: String, sessionToken: String,encryptionToken:String) {
        val expirationTime = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 60) // Set expiration time to 60 minutes from now
        }.timeInMillis

        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_SESSION_TOKEN, sessionToken)
        editor.putLong(KEY_EXPIRATION_TIME, expirationTime)
        editor.putString(KEY_ENCRYPTION_ID,encryptionToken)
        editor.apply()
    }

    fun checkLogin(): Boolean {
        val currentTime = Calendar.getInstance().timeInMillis
        val expirationTime = sharedPreferences.getLong(KEY_EXPIRATION_TIME, 0)
        return currentTime < expirationTime
    }

    fun getUserDetails(): HashMap<String, String> {
        val userDetails = HashMap<String, String>()
        userDetails[KEY_USER_ID] = sharedPreferences.getString(KEY_USER_ID, "")!!
        userDetails[KEY_SESSION_TOKEN] = sharedPreferences.getString(KEY_SESSION_TOKEN, "")!!
        userDetails[KEY_ENCRYPTION_ID] = sharedPreferences.getString(KEY_ENCRYPTION_ID, "")!!

        return userDetails
    }

    fun logoutUser() {
        editor.clear()
        editor.apply()
    }
}
