package dev.matrix.compose_routes.compiler.model.nav_type

import com.squareup.kotlinpoet.CodeBlock
import dev.matrix.compose_routes.compiler.AnnotationProcessorState

object NavControllerNavType : NavType() {
    override fun toNavValue(state: AnnotationProcessorState, expression: CodeBlock): CodeBlock {
        return expression
    }

    override fun fromNavValue(state: AnnotationProcessorState, expression: CodeBlock): CodeBlock {
        return expression
    }
}
