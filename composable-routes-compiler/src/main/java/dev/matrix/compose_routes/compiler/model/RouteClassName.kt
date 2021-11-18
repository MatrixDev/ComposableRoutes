package dev.matrix.compose_routes.compiler.model

import com.squareup.kotlinpoet.ClassName

data class RouteClassName(
    val classPackage: String,
    val className: String,
) {
    fun toClassName(): ClassName {
        return ClassName(classPackage, className)
    }
}
