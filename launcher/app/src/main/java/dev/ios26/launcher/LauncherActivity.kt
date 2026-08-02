package dev.ios26.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.ios26.design.engines.applyGlassBlur
import dev.ios26.design.engines.blurRadiusFor
import dev.ios26.design.theme.GlassIntensity
import dev.ios26.design.theme.Ios26Theme

/** The launcher home surface — HOME + MAIN (Phase 3 product screen). */
class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.post {
            applyGlassBlur(blurRadiusFor(GlassIntensity.Standard))
        }
        setContent {
            Ios26Theme {
                LauncherRoot()
            }
        }
    }
}
