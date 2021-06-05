package me.tylerbwong.truss.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TestBridgeView>(R.id.test_bridge_view).apply {
            textValues = TextValues(
                primaryText = "Hello World!",
                secondaryText = "This is some secondary text!",
            )
            isVisible = true
            footer = "Here is a footer."
        }
    }
}
