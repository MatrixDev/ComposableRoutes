package dev.matrix.compose_routes.compiler.model.nav_type

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.asTypeName
import dev.matrix.compose_routes.compiler.AnnotationProcessorState
import dev.matrix.compose_routes.compiler.model.RouteDestinationArg
import javax.lang.model.type.TypeMirror

class EnumNavType(private val type: TypeMirror) : NavType() {
    override fun getNavType(state: AnnotationProcessorState): CodeBlock {
        return CodeBlock.of("%T.IntType", state.navType)
    }

    override fun toNavValue(state: AnnotationProcessorState, expression: CodeBlock): CodeBlock {
        return CodeBlock.of("(%L).ordinal", expression)
    }

    override fun fromNavValue(state: AnnotationProcessorState, expression: CodeBlock): CodeBlock {
        return CodeBlock.of("%T.values()[%L]", type.asTypeName(), expression)
    }

    override fun getFromBundle(
        state: AnnotationProcessorState,
        argument: RouteDestinationArg,
        bundleValName: String,
    ): CodeBlock {
        return CodeBlock.of("%L.getInt(%S)", bundleValName, argument.name)
    }
}
