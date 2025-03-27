package nu.milad.motmaenbash.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import nu.milad.motmaenbash.ui.activities.AlertHandlerActivity
import nu.milad.motmaenbash.ui.activities.AlertHandlerActivity.Companion.AlertLevel
import nu.milad.motmaenbash.utils.AlertUtils
import nu.milad.motmaenbash.utils.DatabaseHelper
import nu.milad.motmaenbash.utils.PackageUtils

class AppInstallReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "AppInstallReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {

        Log.d(TAG, "Received action: ${intent.action}")


        val packageName = intent.data?.schemeSpecificPart ?: return

        val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)


        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                // Skip if this is just an update to an existing app
                if (!isReplacing) {
                    checkAppAgainstDatabase(context, packageName)
                }
            }

            Intent.ACTION_PACKAGE_REPLACED -> {
                checkAppAgainstDatabase(context, packageName)
            }

            Intent.ACTION_PACKAGE_FIRST_LAUNCH -> {
                // Check against database on first launch
                checkAppAgainstDatabase(context, packageName)
            }
        }
    }

    private fun checkAppAgainstDatabase(context: Context, packageName: String) {
        Log.d(TAG, "Checking app: $packageName")

        val dbHelper = DatabaseHelper(context)
        val appInfo = PackageUtils.getAppInfo(context, packageName)

        val isAppFlagged = dbHelper.isAppFlagged(packageName, appInfo.sha1, appInfo.sha1)

        if (isAppFlagged) {
            // App found suspicious in the database, alert the user

            AlertUtils.showAlert(
                context,
                AlertHandlerActivity.ALERT_TYPE_APP_FLAGGED,
                AlertLevel.ERROR.toString(),
                packageName,
                appInfo.appName
            )
        }
    }
}