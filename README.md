# truss
[![Build](https://github.com/tylerbwong/truss/workflows/Build/badge.svg)](https://github.com/tylerbwong/truss/actions/workflows/build.yml)

Truss is a [KSP](https://github.com/google/ksp) processor that automatically generates Android Views for your `@Composable` functions and vice versa.

### `@BridgeView`
```kotlin
@BridgeView
@Composable
fun Test(
    isVisible: Boolean,
    footer: String,
) {
    if (isVisible) {
        Text(text = footer)
    }
}
```

Applying the `@BridgeView` annotation to any `@Composable` function will generate the following code

```kotlin
public class TestBridgeView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = 0
) : AbstractComposeView(context, attrs, defStyle) {
  public var isVisible: Boolean by mutableStateOf(false)

  public var footer: String by mutableStateOf("")

  @Composable
  public override fun Content(): Unit {
    Test(
      isVisible = isVisible,
      footer = footer
    )
  }
}
```

### `@BridgeComposable`
```kotlin
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
```

Similarly, applying the `@BridgeComposable` to any Android View will generate the following code

```kotlin
@Composable
public fun Test(
  @DrawableRes resId: Int,
  text: String,
  modifier: Modifier = Modifier,
  viewConfig: TestView.() -> Unit
): Unit {
  AndroidView(
    factory = { context ->
      TestView(context).apply {
        setDrawable(resId)
        setText(text)
        viewConfig()
      }
    },
    modifier = modifier,
  )
}
```
