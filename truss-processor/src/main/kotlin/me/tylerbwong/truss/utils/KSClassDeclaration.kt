package me.tylerbwong.truss.utils

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSClassDeclaration

fun KSClassDeclaration.containsSuperType(
    superType: String
): Boolean = superType in getAllSuperTypes().mapNotNull { it.declaration.qualifiedName?.asString() }
