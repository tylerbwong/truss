# truss
[![CI](https://github.com/tylerbwong/truss/workflows/CI/badge.svg)](https://github.com/tylerbwong/truss/actions?query=workflow%3ACI)

Truss is a KSP processor to automatically generate Android Views from your Composable functions.

### Example Usage
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
    if (listOf(textValues).all { it != null }) {
      Test(
        isVisible = isVisible,
        footer = footer
      )
    }
  }
}
```
