package com.tagok.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.mapbox.common.MapboxOptions
import com.tagok.app.BuildConfig
import com.tagok.app.ui.navigation.NavGraph
import com.tagok.app.ui.theme.TagOkAppTheme
import io.github.jan.supabase.auth.handleDeeplinks
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
        enableEdgeToEdge()
        setContent {
            TagOkAppTheme {
                NavGraph()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        lifecycleScope.launch {
            supabase.handleDeeplinks(intent = intent)
        }
    }
}
