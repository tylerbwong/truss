package me.tylerbwong.truss.runtime

/**
 * Applying this annotation will generate an associated @Composable function to be used as a bridge
 * to Android Views.
 *
 * @param generateFunctionsAsParameters Will cause functions that are not overridden to be generated
 * as function parameters.
 */
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class BridgeComposable(val generateFunctionsAsParameters: Boolean = true)
