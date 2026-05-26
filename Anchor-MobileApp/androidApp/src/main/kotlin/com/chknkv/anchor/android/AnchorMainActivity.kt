package com.chknkv.anchor.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.chknkv.anchor.root.AnchorApp

/**
 * Главная Activity приложения для платформы Android.
 * 
 * Наследуется от [FragmentActivity] для обеспечения работы биометрии 
 * (требование библиотеки androidx.biometric).
 */
class AnchorMainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AnchorApp() }
    }
}
