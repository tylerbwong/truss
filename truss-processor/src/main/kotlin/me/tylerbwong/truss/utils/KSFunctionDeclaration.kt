package me.tylerbwong.truss.utils

import com.google.devtools.ksp.symbol.KSFunctionDeclaration

val KSFunctionDeclaration.annotationNames: Sequence<String>
    get() = this.annotations.mapNotNull {
        it.annotationType.resolve().declaration.qualifiedName?.asString()
    }
