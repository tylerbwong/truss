package me.tylerbwong.truss.utils

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal val KSFunctionDeclaration.annotationNames: Sequence<String>
    get() = this.annotations.mapNotNull {
        it.annotationType.resolve().declaration.qualifiedName?.asString()
    }

internal fun KSFunctionDeclaration.checkAndReportDefaultParameters(logger: KSPLogger): Boolean {
    var hasDefaultParameters = false
    parameters.forEach { param ->
        if (param.hasDefault) {
            logger.error(
                message = "Default values are currently not supported",
                symbol = param,
            )
            hasDefaultParameters = true
        }
    }
    return hasDefaultParameters
}
