package de.damien.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import de.damien.frontend.ui.theme.FunnyPlacesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FunnyPlacesTheme {
                Surface(color = MaterialTheme.colors.background) {

                }
            }
        }
    }
}