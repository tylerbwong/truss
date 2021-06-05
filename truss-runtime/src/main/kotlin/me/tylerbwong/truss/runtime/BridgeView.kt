package me.tylerbwong.truss.runtime

/**
 * Applying this annotation will generate an associated Android View to be used as a bridge to
 * Composable functions.
 */
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class BridgeView
