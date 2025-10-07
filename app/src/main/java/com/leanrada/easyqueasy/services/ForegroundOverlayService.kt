package com.leanrada.easyqueasy.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.leanrada.easyqueasy.AppDataClient
import com.leanrada.easyqueasy.MainActivity
import com.leanrada.easyqueasy.R
import com.leanrada.easyqueasy.ui.Overlay
import kotlinx.coroutines.runBlocking

class ForegroundOverlayService : Service(), SavedStateRegistryOwner {
    companion object {
        var isActive = false

        fun start(context: Context) {
            Log.i(ForegroundOverlayService::class.simpleName, "Intending to start foreground overlay service...")
            if (isActive) {
                Log.w(ForegroundOverlayService::class.simpleName, "Service already active, not starting again")
                return
            }
            val intent = Intent(context, ForegroundOverlayService::class.java)
            try {
                ContextCompat.startForegroundService(context, intent)
            } catch (e: Exception) {
                Log.e(ForegroundOverlayService::class.simpleName, "Failed to start foreground service", e)
            }
        }

        fun stop(context: Context) {
            Log.i(ForegroundOverlayService::class.simpleName, "Intending to stop foreground overlay service...")
            if (!isActive) {
                Log.w(ForegroundOverlayService::class.simpleName, "Service not active, nothing to stop")
                return
            }
            val intent = Intent(context, ForegroundOverlayService::class.java)
            try {
                context.stopService(intent)
            } catch (e: Exception) {
                Log.e(ForegroundOverlayService::class.simpleName, "Failed to stop service", e)
            }
        }
    }

    private val lifecycleDispatcher = ServiceLifecycleDispatcher(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private lateinit var appData: AppDataClient
    private lateinit var contentView: View

    override fun onCreate() {
        lifecycleDispatcher.onServicePreSuperOnCreate()
        super.onCreate()
        startForegroundNotification()
        savedStateRegistryController.performRestore(null)

        appData = AppDataClient(this, lifecycleScope)

        contentView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@ForegroundOverlayService)
            setViewTreeSavedStateRegistryOwner(this@ForegroundOverlayService)
            setContent {
                Overlay(appData = appData)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, startFlags: Int, startId: Int): Int {
        if (isActive) {
            Log.w(ForegroundOverlayService::class.simpleName, "Service already active, ignoring start command")
            return START_STICKY
        }
        startOverlay()
        return START_STICKY
    }

    override fun onDestroy() {
        lifecycleDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
        stopOverlay()
        cleanup()
    }
    
    private fun cleanup() {
        try {
            if (::contentView.isInitialized) {
                // Clear any references to prevent memory leaks
                val composeView = contentView as? ComposeView
                composeView?.disposeComposition()
            }
        } catch (e: Exception) {
            Log.e(ForegroundOverlayService::class.simpleName, "Failed to cleanup resources", e)
        }
    }

    @Deprecated("Deprecated in super")
    override fun onStart(intent: Intent?, startId: Int) {
        lifecycleDispatcher.onServicePreSuperOnStart()
        @Suppress("DEPRECATION")
        super.onStart(intent, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        lifecycleDispatcher.onServicePreSuperOnBind()
        return null
    }

    private fun startOverlay() {
        Log.i(ForegroundOverlayService::class.simpleName, "Starting foreground overlay service...")
        try {
            if (!contentView.isAttachedToWindow) {
                addOverlayView(this, contentView, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            }

            isActive = true
            runBlocking {
                try {
                    appData.dataStore.updateData {
                        it.toBuilder().setForegroundOverlayStartTime(System.currentTimeMillis()).build()
                    }
                } catch (e: Exception) {
                    Log.e(ForegroundOverlayService::class.simpleName, "Failed to update overlay start time", e)
                }
            }

            try {
                ForegroundOverlayTileService.requestListeningState(this)
            } catch (e: Exception) {
                Log.e(ForegroundOverlayService::class.simpleName, "Failed to request tile listening state", e)
            }
        } catch (e: Exception) {
            Log.e(ForegroundOverlayService::class.simpleName, "Adding overlay root view failed!", e)
            // Stop the service if overlay fails to start
            try {
                stopSelf()
            } catch (stopException: Exception) {
                Log.e(ForegroundOverlayService::class.simpleName, "Failed to stop service", stopException)
            }
        }
    }

    private fun stopOverlay() {
        Log.i(ForegroundOverlayService::class.simpleName, "Stopping foreground overlay service...")

        try {
            if (::contentView.isInitialized && contentView.isAttachedToWindow) {
                val windowManager = getSystemService(WINDOW_SERVICE) as? WindowManager
                windowManager?.removeView(contentView)
            }
        } catch (e: Exception) {
            Log.e(ForegroundOverlayService::class.simpleName, "Failed to remove overlay view", e)
        }

        isActive = false
        runBlocking {
            try {
                appData.dataStore.updateData {
                    it.toBuilder().setForegroundOverlayStopTime(System.currentTimeMillis()).build()
                }
            } catch (e: Exception) {
                Log.e(ForegroundOverlayService::class.simpleName, "Failed to update overlay stop time", e)
            }
        }

        try {
            ForegroundOverlayTileService.requestListeningState(this)
        } catch (e: Exception) {
            Log.e(ForegroundOverlayService::class.simpleName, "Failed to request tile listening state on stop", e)
        }
    }

    private fun startForegroundNotification() {
        val channelID = "overlay"
        val channel = NotificationChannel(
            channelID,
            "Overlay notification",
            NotificationManager.IMPORTANCE_LOW
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("Motion Relief running")
            .setContentText("Tap to open")
            .setSmallIcon(R.mipmap.monochrome_logo)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

        ServiceCompat.startForeground(
            /* service = */ this,
            /* id = */ 1,
            /* notification = */ notification,
            /* foregroundServiceType = */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            else
                0
        )
    }

    override val lifecycle: Lifecycle
        get() = lifecycleDispatcher.lifecycle

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry
}

