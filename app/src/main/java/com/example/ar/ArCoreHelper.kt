package com.example.ar

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException

object ArCoreHelper {
    private const val TAG = "ArCoreHelper"
    private const val AR_CORE_PACKAGE = "com.google.ar.core"

    enum class ArAvailability {
        UNKNOWN,
        SUPPORTED_INSTALLED,
        SUPPORTED_NOT_INSTALLED,
        UNSUPPORTED_DEVICE_NOT_CAPABLE
    }

    /**
     * Check if Google Play Services for AR (ARCore) package is installed.
     */
    private fun isArCorePackageInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(AR_CORE_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        } catch (e: Throwable) {
            false
        }
    }

    /**
     * Check if ARCore is supported on this device.
     */
    fun checkArCoreAvailability(context: Context, onResult: (ArAvailability) -> Unit) {
        try {
            if (!isArCorePackageInstalled(context)) {
                onResult(ArAvailability.SUPPORTED_NOT_INSTALLED)
                return
            }

            val availability = ArCoreApk.getInstance().checkAvailability(context)
            if (availability.isTransient) {
                onResult(ArAvailability.UNKNOWN)
            } else if (availability.isSupported) {
                if (availability == ArCoreApk.Availability.SUPPORTED_INSTALLED) {
                    onResult(ArAvailability.SUPPORTED_INSTALLED)
                } else {
                    onResult(ArAvailability.SUPPORTED_NOT_INSTALLED)
                }
            } else {
                onResult(ArAvailability.UNSUPPORTED_DEVICE_NOT_CAPABLE)
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to check ARCore availability: ${e.message}")
            onResult(ArAvailability.UNSUPPORTED_DEVICE_NOT_CAPABLE)
        }
    }

    /**
     * Create an ARCore Session configured for object scanning and plane detection.
     * Returns null if ARCore is unavailable or cannot be initialized.
     */
    fun createArSession(activity: Activity): Session? {
        return try {
            if (!isArCorePackageInstalled(activity)) {
                Log.i(TAG, "ARCore package not installed; utilizing fallback CameraX AR pipeline")
                return null
            }

            val availability = ArCoreApk.getInstance().checkAvailability(activity)
            if (availability != ArCoreApk.Availability.SUPPORTED_INSTALLED) {
                Log.i(TAG, "ARCore is not installed or not supported; skipping Play Store binding")
                return null
            }

            val session = Session(activity)
            val config = Config(session).apply {
                focusMode = Config.FocusMode.AUTO
                lightEstimationMode = Config.LightEstimationMode.AMBIENT_INTENSITY
                planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            }
            session.configure(config)
            session
        } catch (e: UnavailableArcoreNotInstalledException) {
            Log.w(TAG, "ARCore not installed: ${e.message}")
            null
        } catch (e: UnavailableApkTooOldException) {
            Log.w(TAG, "ARCore APK too old: ${e.message}")
            null
        } catch (e: UnavailableSdkTooOldException) {
            Log.w(TAG, "ARCore SDK too old: ${e.message}")
            null
        } catch (e: UnavailableDeviceNotCompatibleException) {
            Log.w(TAG, "Device not compatible with ARCore: ${e.message}")
            null
        } catch (e: UnavailableUserDeclinedInstallationException) {
            Log.w(TAG, "User declined ARCore installation: ${e.message}")
            null
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to create ARCore session: ${e.message}")
            null
        }
    }
}
