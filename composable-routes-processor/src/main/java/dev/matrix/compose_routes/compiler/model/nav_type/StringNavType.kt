package dev.matrix.compose_routes.compiler.model.nav_type

import com.squareup.kotlinpoet.CodeBlock
import dev.matrix.compose_routes.compiler.AnnotationProcessorState
import dev.matrix.compose_routes.compiler.utils.decodeUrl
import dev.matrix.compose_routes.compiler.utils.encodeUrl

object StringNavType : NavType() {
    override fun getNavType(state: AnnotationProcessorState): CodeBlock {
        return CodeBlock.of("%T.StringType", state.navType)
    }

    override fun toNavValue(state: AnnotationProcessorState, expression: CodeBlock): CodeBlock {
        return encodeUrl(expression)
    }

    override fun fromNavValue(state: AnnotationProcessorState, expression: CodeBlock): CodeBlock {
        return decodeUrl(expression)
    }
}
