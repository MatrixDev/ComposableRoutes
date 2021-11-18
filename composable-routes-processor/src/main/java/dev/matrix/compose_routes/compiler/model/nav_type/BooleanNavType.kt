package dev.matrix.compose_routes.compiler.model.nav_type

import com.squareup.kotlinpoet.CodeBlock
import dev.matrix.compose_routes.compiler.AnnotationProcessorState
import dev.matrix.compose_routes.compiler.model.RouteDestinationArg

object BooleanNavType : NavType() {
    override fun getNavType(state: AnnotationProcessorState): CodeBlock {
        return CodeBlock.of("%T.IntType", state.navType)
    }

    override fun serialize(state: AnnotationProcessorState, valName: String): CodeBlock {
        return CodeBlock.of("%L", valName)
    }

    override fun deserialize(state: AnnotationProcessorState, valName: String): CodeBlock {
        return CodeBlock.of("%L", valName)
    }

    override fun getValueFromNavBundle(
        state: AnnotationProcessorState,
        argument: RouteDestinationArg,
        bundleValName: String,
    ): CodeBlock {
        return CodeBlock.of("%L.getBoolean(%S)", bundleValName, argument.name)
    }
}
