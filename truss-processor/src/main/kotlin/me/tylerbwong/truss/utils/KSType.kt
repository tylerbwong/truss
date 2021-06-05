package me.tylerbwong.truss.utils

import com.google.devtools.ksp.symbol.KSValueParameter

internal val primitiveDefaults = mapOf(
    Byte::class to "0",
    Short::class to "0",
    Int::class to "0",
    Long::class to "0",
    Float::class to "0f",
    Double::class to "0.0",
    Boolean::class to "false",
    Char::class to "\' \'",
    String::class to "\"\"",
)

internal fun KSValueParameter.defaultValue(): String? {
    val key = primitiveDefaults.keys.find {
        it.simpleName == this.type.resolve().declaration.simpleName.asString()
    }
    return primitiveDefaults[key]
}

internal val KSValueParameter.isPrimitive: Boolean
    get() = this.type.resolve().declaration.simpleName.asString() in primitiveDefaults.keys.map { kClass ->
        kClass.simpleName
    }
