package com.testfcm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.hermes.reactexecutor.HermesExecutorFactory
import com.facebook.react.PackageList
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactPackage
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.soloader.SoLoader


class BubbleActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler {

    private lateinit var reactRootView: ReactRootView
    private lateinit var reactInstanceManager: ReactInstanceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        SoLoader.init(this, false)

        // Pass the channelData to your React Native module if needed
        val initialProps = Bundle()
        initialProps.putString("title", "title")

        reactRootView = ReactRootView(this)
        val packages: List<ReactPackage> = PackageList(application).packages
        // Packages that cannot be autolinked yet can be added manually here, for example:
        // packages.add(MyReactNativePackage())
        // Remember to include them in `settings.gradle` and `app/build.gradle` too.
            reactInstanceManager = ReactInstanceManager.builder()
                .setApplication(application)
                .setCurrentActivity(this)
                .setBundleAssetName("indexBubble.android.bundle")
                .setJSMainModulePath("indexBubble")
                .addPackages(packages)
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setJavaScriptExecutorFactory(HermesExecutorFactory())
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build()

        reactRootView.startReactApplication(reactInstanceManager, "bubble", initialProps)
        setContentView(reactRootView)
    }

    override fun invokeDefaultOnBackPressed() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        reactInstanceManager.onHostPause(this)
    }

    override fun onResume() {
        super.onResume()
        reactInstanceManager.onHostResume(this, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        reactInstanceManager.onHostDestroy(this)
        reactRootView.unmountReactApplication()
    }

    override fun onBackPressed() {
        reactInstanceManager.onBackPressed()
        onBackPressedDispatcher.onBackPressed()
    }
}