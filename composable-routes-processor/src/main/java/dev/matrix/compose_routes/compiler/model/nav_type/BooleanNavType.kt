package dev.matrix.compose_routes.compiler.model.nav_type

import com.squareup.kotlinpoet.CodeBlock
import dev.matrix.compose_routes.compiler.AnnotationProcessorState
import dev.matrix.compose_routes.compiler.model.RouteDestinationArg

object BooleanNavType : NavType() {
    override fun getNavType(state: AnnotationProcessorState): CodeBlock {
        return CodeBlock.of("%T.BoolType", state.navType)
    }

    override fun toNavValue(state: AnnotationProcessorState, expression: CodeBlock): CodeBlock {
        return expression
    }

    override fun fromNavValue(state: AnnotationProcessorState, expression: CodeBlock): CodeBlock {
        return expression
    }

    override fun getFromBundle(
        state: AnnotationProcessorState,
        argument: RouteDestinationArg,
        bundleValName: String,
    ): CodeBlock {
        return CodeBlock.of("%L.getBoolean(%S)", bundleValName, argument.name)
    }
}
