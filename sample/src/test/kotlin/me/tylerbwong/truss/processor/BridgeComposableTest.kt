package me.tylerbwong.truss.processor

import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class BridgeComposableTest(private val incremental: Boolean) {
    @Test
    fun `test processor returns OK on valid use`() {
        val result = compile(
            kotlin(
                name = "source.kt",
                contents = """
                   package test
    
                   import android.content.Context
                   import android.util.AttributeSet
                   import androidx.annotation.DrawableRes
                   import androidx.appcompat.widget.AppCompatTextView
                   import me.tylerbwong.truss.runtime.BridgeComposable
                   
                   @BridgeComposable
                   class TestView @JvmOverloads constructor(
                      context: Context,
                      attrs: AttributeSet? = null,
                      defStyle: Int = 0
                   ) : AppCompatTextView(context, attrs, defStyle)
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
    }

    @Test
    fun `test processor returns COMPILATION_ERROR without extending View`() {
        val result = compile(
            kotlin(
                name = "source.kt",
                contents = """
                   package test
    
                   import android.content.Context
                   import android.util.AttributeSet
                   import androidx.annotation.DrawableRes
                   import androidx.appcompat.widget.AppCompatTextView
                   import me.tylerbwong.truss.runtime.BridgeComposable
                   
                   @BridgeComposable
                   class TestView @JvmOverloads constructor(
                      context: Context,
                      attrs: AttributeSet? = null,
                      defStyle: Int = 0
                   ) {
                      fun setDrawable(@DrawableRes resId: Int) {
                         // No-op
                      }
                    
                      fun setText(text: String) {
                         // No-op
                      }
                   }
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        assertThat(result.messages).contains("@BridgeComposable should only be applied to sub-classes of android.view.View")
    }

    @Test
    fun `test processor returns OK with valid functions`() {
        val result = compile(
            kotlin(
                name = "source.kt",
                contents = """
                   package test
    
                   import android.content.Context
                   import android.util.AttributeSet
                   import androidx.annotation.DrawableRes
                   import androidx.appcompat.widget.AppCompatTextView
                   import me.tylerbwong.truss.runtime.BridgeComposable
                   
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
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
    }

    @Test
    fun `test processor returns OK with generateFunctionsAsParameters as false`() {
        val result = compile(
            kotlin(
                name = "source.kt",
                contents = """
                   package test
    
                   import android.content.Context
                   import android.util.AttributeSet
                   import androidx.annotation.DrawableRes
                   import androidx.appcompat.widget.AppCompatTextView
                   import me.tylerbwong.truss.runtime.BridgeComposable
                   
                   @BridgeComposable(generateFunctionsAsParameters = false)
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
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
    }

    @Test
    fun `test processor returns OK with generateFunctionsAsParameters as true`() {
        val result = compile(
            kotlin(
                name = "source.kt",
                contents = """
                   package test
    
                   import android.content.Context
                   import android.util.AttributeSet
                   import androidx.annotation.DrawableRes
                   import androidx.appcompat.widget.AppCompatTextView
                   import me.tylerbwong.truss.runtime.BridgeComposable
                   
                   @BridgeComposable(generateFunctionsAsParameters = true)
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
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
    }

    private fun compile(
        vararg sourceFiles: SourceFile,
        inheritClassPath: Boolean = true,
    ): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            this.inheritClassPath = inheritClassPath
            symbolProcessorProviders = listOf(TrussProcessorProvider())
            sources = sourceFiles.toList()
            kspIncremental = incremental
        }.compile()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "incremental={0}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(true),
                arrayOf(false)
            )
        }
    }
}
