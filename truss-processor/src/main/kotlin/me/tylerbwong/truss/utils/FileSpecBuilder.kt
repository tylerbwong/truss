package me.tylerbwong.truss.utils

import com.squareup.kotlinpoet.FileSpec
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset

fun FileSpec.Builder.writeTo(
    file: OutputStream,
    charset: Charset = Charset.defaultCharset(),
) = OutputStreamWriter(file, charset).use { build().writeTo(it) }
