package dev.matrix.compose_routes.compiler.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock

private val urlEncoderClassName = ClassName("java.net", "URLEncoder")
private val urlDecoderClassName = ClassName("java.net", "URLDecoder")

fun encodeUrl(expression: CodeBlock): CodeBlock {
    return CodeBlock.of("%T.encode(%L, %S)", urlEncoderClassName, expression, "UTF-8")
}

fun decodeUrl(expression: CodeBlock): CodeBlock {
    return CodeBlock.of("%T.decode(%L, %S)", urlDecoderClassName, expression, "UTF-8")
}
