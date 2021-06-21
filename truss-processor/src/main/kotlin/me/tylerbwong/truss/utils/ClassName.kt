package me.tylerbwong.truss.utils

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.squareup.kotlinpoet.ClassName

internal val contextClassName = ClassName(
    "android.content",
    "Context"
)

internal val attributeSetClassName = ClassName(
    "android.util",
    "AttributeSet"
)

internal val composableClassName = ClassName(
    "androidx.compose.runtime",
    "Composable",
)

internal val abstractComposeViewClassName = ClassName(
    "androidx.compose.ui.platform",
    "AbstractComposeView",
)

internal val modifierClassName = ClassName(
    "androidx.compose.ui",
    "Modifier",
)

internal val androidViewClassName = ClassName(
    "androidx.compose.ui.viewinterop",
    "AndroidView",
)

internal val platformDependencies = listOf(
    contextClassName,
    attributeSetClassName,
)

internal val composeDependencies = listOf(
    composableClassName,
    abstractComposeViewClassName,
)

internal fun ClassName.isOnClasspath(resolver: Resolver): Boolean {
    return resolver.getClassDeclarationByName(
        resolver.getKSNameFromString(name = canonicalName)
    )?.asType(emptyList()) != null
}

internal fun checkAndReportClasspath(
    resolver: Resolver,
    logger: KSPLogger,
): Boolean {
    val missingDependencies = mutableListOf<ClassName>()
    (platformDependencies + composeDependencies).forEach {
        if (!it.isOnClasspath(resolver)) {
            missingDependencies += it
        }
    }

    if (missingDependencies.isNotEmpty()) {
        logger.error("Missing processor dependencies on classpath: $missingDependencies")
    }

    return missingDependencies.isNotEmpty()
}
