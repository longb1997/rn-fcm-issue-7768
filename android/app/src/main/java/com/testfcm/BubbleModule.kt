package com.testfcm

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.content.LocusIdCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import java.lang.System.currentTimeMillis

class BubblesModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    companion object {
        private const val BUBBLES_CHANNEL_ID = "base_bubble"
        private const val REQUEST_CONTENT = 1
    }
    override fun getName(): String {
        return "BubbleModule"
    }

    private val notificationManager = NotificationManagerCompat.from(reactContext)
    @RequiresApi(Build.VERSION_CODES.Q)
    fun setupNotificationChannels() {
        if (notificationManager.getNotificationChannel(BUBBLES_CHANNEL_ID) == null) {
            notificationManager.createNotificationChannel(
                    NotificationChannel(
                            BUBBLES_CHANNEL_ID,
                            "Base message",
                            NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Base Bubble Messages"
                        setAllowBubbles(true)
                        enableVibration(false)
//                    enableLights(false)
//                    setSound(null, null)
                    }
            )
        }
    }
    private fun flagUpdateCurrent(mutable: Boolean): Int {
        return if (mutable) {
            if (Build.VERSION.SDK_INT >= 31) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        }
    }



    private fun createBubble(channelId: String, title: String, content: String, icon: IconCompat) {
        val contentUri = "".toUri()

        val person = Person.Builder()
                .setName(title)
                .setImportant(true)
                .setIcon(icon)
                .build()
        val set = HashSet<String>()
        set.add("vn.base.message.android.bubbles.category.TEXT_SHARE_TARGET")
        val shortcutInfo = ShortcutInfoCompat.Builder(reactContext, channelId)
                .setLocusId(LocusIdCompat(channelId))
                .setActivity(ComponentName(reactContext, MainActivity::class.java))
                .setShortLabel(person.name!!)
                .setIcon(icon)
                .setLongLived(true)
                .setCategories(set)
                .setIntent(Intent(Intent.ACTION_VIEW))
                .setPerson(person)
                .build()
        ShortcutManagerCompat.pushDynamicShortcut(reactContext, shortcutInfo)

        val targetIntent = Intent(reactContext, BubbleActivity::class.java)
                .setAction(Intent.ACTION_VIEW)

        val uniqueRequestCode = channelId.hashCode()

        val bubbleIntent = PendingIntent.getActivity(
                reactContext,
                uniqueRequestCode,
                // Launch BubbleActivity as the expanded bubble.
                targetIntent,
                flagUpdateCurrent(mutable = true))

        val messagingStyle = NotificationCompat.MessagingStyle(person)

        val bubbleData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            NotificationCompat.BubbleMetadata.Builder(bubbleIntent, icon)
                    .setDesiredHeight(600)
                    .setSuppressNotification(false)
                    .build()
        } else {
            NotificationCompat.BubbleMetadata.Builder()
                    .setDesiredHeight(600)
                    .setIcon(icon)
                    .setIntent(bubbleIntent)
                    .setSuppressNotification(true)
                    .build()
        }

        val notificationBuilder = NotificationCompat.Builder(reactContext, BUBBLES_CHANNEL_ID)
                .setBubbleMetadata(bubbleData)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_message)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setShortcutId(channelId)
                .setLocusId(LocusIdCompat(channelId))
                .addPerson(person)
                .setShowWhen(true)
                .setContentIntent(PendingIntent.getActivity(
                        reactContext,
                        REQUEST_CONTENT,
                        Intent(reactContext, MainActivity::class.java)
                                .setAction(Intent.ACTION_VIEW)
                                .setData(contentUri),
                        flagUpdateCurrent(mutable = false)
                ))
                .setContentText(content)
                .setStyle(messagingStyle)

        val style = NotificationCompat.MessagingStyle(person)
                .setConversationTitle(title)

        style.addMessage(content, currentTimeMillis(), person)
        notificationBuilder.setStyle(style)

        Log.d("Bubble", "create notify")
        notificationManager.notify(channelId.toInt(), notificationBuilder.build())
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.Q)
    @ReactMethod
    fun showBubble() {
        setupNotificationChannels()
        val defaultIcon = IconCompat.createWithResource(reactContext, R.drawable.ic_message)
        createBubble("0", "Tittle notificaiton", "Content sample", defaultIcon)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @ReactMethod
    fun openBubblePermissionSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_BUBBLE_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, reactContext.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (intent.resolveActivity(reactContext.packageManager) != null) {
            reactContext.startActivity(intent)
        } else {
            // Fallback to app notification settings if bubble settings cannot be directly accessed
            val fallbackIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, reactContext.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            reactContext.startActivity(fallbackIntent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @ReactMethod
    fun getBubbleSetting(promise: Promise) {
        val notificationManager = reactContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Get the bubble preference (All, Selected, or None)
            val bubblePreference = notificationManager.bubblePreference
            promise.resolve(bubblePreference)
        } else {
            // If the API level is below Q, bubbles are not supported
            promise.reject("ERROR", "Bubbles are not supported on this Android version.")
        }
    }
}
