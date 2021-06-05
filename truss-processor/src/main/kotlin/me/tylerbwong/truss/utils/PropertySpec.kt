package me.tylerbwong.truss.utils

import com.squareup.kotlinpoet.PropertySpec

internal val PropertySpec.isPrimitive: Boolean
    get() = this.type.toString() in primitiveDefaults.keys.map { kClass -> kClass.qualifiedName }
