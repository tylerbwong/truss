package me.tylerbwong.truss.sample

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import me.tylerbwong.truss.runtime.BridgeComposable
import me.tylerbwong.truss.runtime.BridgeView
import org.w3c.dom.Text

data class TextValues(val primaryText: String, val secondaryText: String)

@BridgeView
@Composable
fun Test(
    textValues: TextValues,
    isVisible: Boolean,
    footer: String,
) {
    if (isVisible) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = textValues.primaryText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h1,
            )
            Text(
                text = textValues.secondaryText,
                style = MaterialTheme.typography.body1,
            )
            Text(text = footer)
        }
    }
}

@BridgeComposable
class TestView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {
    fun setDrawable(@DrawableRes resId: Int) {
        // No-op
    }

    fun setText(text: String) {
        // No-op
    }
}
