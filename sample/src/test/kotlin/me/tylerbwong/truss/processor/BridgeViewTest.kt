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
class BridgeViewTest(private val incremental: Boolean) {
    @Test
    fun `test processor returns OK on valid use`() {
        val result = compile(
            kotlin(
                name = "source.kt",
                contents = """
                   package test
    
                   import androidx.compose.runtime.Composable
                   import me.tylerbwong.truss.runtime.BridgeView
                   
                   @BridgeView
                   @Composable
                   fun Test() {
                      // No-op
                   }
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
    }

    @Test
    fun `test processor returns COMPILATION_ERROR without @Composable`() {
        val result = compile(
            kotlin(
                name = "source.kt",
                contents = """
                   package test
    
                   import me.tylerbwong.truss.runtime.BridgeView
                   
                   @BridgeView
                   fun Test() {
                      // No-op
                   }
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        assertThat(result.messages).contains("@BridgeView should only be applied to @Composable functions")
    }

    @Test
    fun `test processor returns OK with valid primitive parameters`() {
        val result = compile(
            kotlin(
                name = "source.kt",
                contents = """
                   package test
    
                   import androidx.compose.runtime.Composable
                   import me.tylerbwong.truss.runtime.BridgeView
                   
                   @BridgeView
                   @Composable
                   fun Test(param1: String, param2: String) {
                      // No-op
                   }
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
    }

    @Test
    fun `test processor returns COMPILATION_ERROR with default value parameter`() {
        val result = compile(
            kotlin(
                name = "source.kt",
                contents = """
                   package test
    
                   import androidx.compose.runtime.Composable
                   import me.tylerbwong.truss.runtime.BridgeView
                   
                   @BridgeView
                   @Composable
                   fun Test(param1: String = "test") {
                      // No-op
                   }
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        assertThat(result.messages).contains("Default values are currently not supported")
    }

    @Test
    fun `test processor returns OK with valid parameters`() {
        val result = compile(
            kotlin(
                name = "source.kt",
                contents = """
                   package test
    
                   import androidx.compose.runtime.Composable
                   import me.tylerbwong.truss.runtime.BridgeView

                   data class TestData(val test: String)
                   
                   @BridgeView
                   @Composable
                   fun Test(data: TestData, param1: String) {
                      // No-op
                   }
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
    }

    @Test
    fun `test processor returns COMPILATION_ERROR with invalid classpath`() {
        val result = compile(
            kotlin(
                name = "source.kt",
                contents = """
                   package test
    
                   import androidx.compose.runtime.Composable
                   import me.tylerbwong.truss.runtime.BridgeView

                   data class TestData(val test: String)
                   
                   @BridgeView
                   @Composable
                   fun Test(data: TestData, param1: String) {
                      // No-op
                   }
                """
            ),
            inheritClassPath = false,
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        assertThat(result.messages).contains("Missing processor dependencies on classpath: [android.content.Context, android.util.AttributeSet, androidx.compose.runtime.Composable, androidx.compose.ui.platform.AbstractComposeView]")
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
