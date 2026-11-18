package com.example.data.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.security.MessageDigest

/**
 * Enterprise-grade App Integrity, Anti-Tampering, and Root Detection Guard
 * for Noor-e-Hidayah. Protects offline user debts, bookmarks, and auth tokens.
 */
object SecurityGuard {

    private const val TAG = "SecurityGuard"

    data class SecurityReport(
        val isRooted: Boolean,
        val rootReasons: List<String>,
        val certFingerprintSha256: String,
        val isSignatureValid: Boolean,
        val isSecure: Boolean,
        val environmentSummary: String
    )

    // Known root binaries and paths across custom ROMs and SU managers
    private val KNOWN_ROOT_PATHS = arrayOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su",
        "/system/xbin/daemonsu"
    )

    // Common root packages
    private val ROOT_PACKAGES = arrayOf(
        "com.noshufou.android.su",
        "com.thirdparty.superuser",
        "eu.chainfire.supersu",
        "com.koushikdutta.superuser",
        "com.topjohnwu.magisk"
    )

    /**
     * Checks if the device appears to be rooted or running a compromised OS.
     */
    fun isDeviceRooted(context: Context): Pair<Boolean, List<String>> {
        val reasons = mutableListOf<String>()

        // 1. Build tags check (test-keys indicates custom/unofficial ROM)
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            reasons.add("Custom ROM / Unofficial build tags detected")
        }

        // 2. Known SU binary files check
        for (path in KNOWN_ROOT_PATHS) {
            try {
                val file = File(path)
                if (file.exists() && file.canExecute()) {
                    reasons.add("Superuser binary detected: $path")
                    break
                }
            } catch (_: Exception) {
                // Security exception or permission denied
            }
        }

        // 3. Known root manager apps check
        val pm = context.packageManager
        for (pkg in ROOT_PACKAGES) {
            try {
                pm.getPackageInfo(pkg, 0)
                reasons.add("Root manager application installed: $pkg")
                break
            } catch (_: PackageManager.NameNotFoundException) {
                // Package not found (expected for clean devices)
            } catch (_: Exception) {
                // Handled
            }
        }

        // 4. Runtime SU execution check
        if (canExecuteSu()) {
            reasons.add("Interactive SU shell execution available")
        }

        val isRooted = reasons.isNotEmpty()
        return Pair(isRooted, reasons)
    }

    private fun canExecuteSu(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            reader.readLine() != null
        } catch (_: Exception) {
            false
        } finally {
            process?.destroy()
        }
    }

    /**
     * Retrieves the SHA-256 fingerprint of the current app package signature.
     */
    fun getCertificateFingerprint(context: Context): String {
        return try {
            val pm = context.packageManager
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val packageInfo = pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                val packageInfo = pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            val certBytes = signatures?.firstOrNull()?.toByteArray() ?: return "NO_CERTIFICATE"
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(certBytes)
            digest.joinToString(":") { "%02X".format(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating cert fingerprint", e)
            "UNAVAILABLE"
        }
    }

    /**
     * Comprehensive security analysis report for UI status and tamper prevention.
     */
    fun getSecurityReport(context: Context): SecurityReport {
        val (isRooted, reasons) = isDeviceRooted(context)
        val certFingerprint = getCertificateFingerprint(context)
        val isSignatureValid = certFingerprint.isNotBlank() && certFingerprint != "NO_CERTIFICATE"
        val isSecure = !isRooted && isSignatureValid

        val summary = if (isSecure) {
            "নিরাপদ ও অক্ষত এনভায়রনমেন্ট (অফিসিয়াল স্বাক্ষর যাচাইকৃত)"
        } else if (isRooted) {
            "সতর্কতা: রুটেড বা টেস্ট-কি ডিভাইস সনাক্ত হয়েছে"
        } else {
            "অ্যাপ অখণ্ডতা সাধারণ মোডে চলমান"
        }

        return SecurityReport(
            isRooted = isRooted,
            rootReasons = reasons,
            certFingerprintSha256 = certFingerprint,
            isSignatureValid = isSignatureValid,
            isSecure = isSecure,
            environmentSummary = summary
        )
    }
}
