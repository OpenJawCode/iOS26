package dev.ios26.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Launcher app model. */
data class AppInfo(
    val packageName: String,
    val label: String,
    val icon: Drawable?,
)

/** Loads launchable apps off the main thread (single source for all surfaces). */
object AppRepository {

    suspend fun loadLaunchableApps(context: Context): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolved = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        resolved
            .filter { it.activityInfo != null && it.activityInfo.packageName != context.packageName }
            .distinctBy { it.activityInfo.packageName }
            .mapNotNull { ri ->
                runCatching {
                    AppInfo(
                        packageName = ri.activityInfo.packageName,
                        label = ri.loadLabel(pm).toString(),
                        icon = ri.loadIcon(pm),
                    )
                }.getOrNull()
            }
            .sortedBy { it.label.lowercase() }
    }

    fun launch(context: Context, app: AppInfo) {
        runCatching {
            context.startActivity(
                context.packageManager.getLaunchIntentForPackage(app.packageName)
                    ?: Intent(Intent.ACTION_MAIN).apply { setPackage(app.packageName) },
            )
        }
    }
}
