package com.minimal.notes.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.minimal.notes.R
import com.minimal.notes.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    lateinit var binding: ActivityAboutBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val versionName = getVersionName()
        val versionCode = getVersionCode()

        binding.versiontxt.text = "Version: $versionName($versionCode)"

        binding.back.setOnClickListener {
            finish()
        }
        binding.noteGithub.setOnClickListener{
            val url = "https://github.com/dhanushcodev/Notes-App"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        binding.developerInfo.setOnClickListener{
            val url = "https://dhanushcodev.github.io/cv/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

    private fun getVersionName(): String {
        return try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName // Get version name
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "Unknown" // Return default value in case of error
        }
    }

    // Method to get the version code (deprecated in API 29 and above)
    private fun getVersionCode(): Int {
        return try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                // For API 29 (Android 10) and above, use getLongVersionCode
                packageInfo.longVersionCode.toInt()  // For API 29+ (returns long, cast to int)
            } else {
                packageInfo.versionCode  // For older API levels, use versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1 // Return default value in case of error
        }
    }


}