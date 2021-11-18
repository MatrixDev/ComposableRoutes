package dev.matrix.compose_routes.compiler.model.nav_type

import com.squareup.kotlinpoet.CodeBlock
import dev.matrix.compose_routes.compiler.AnnotationProcessorState

object StringNavType : NavType() {
    override fun getNavType(state: AnnotationProcessorState): CodeBlock {
        return CodeBlock.of("%T.StringType", state.navType)
    }

    override fun serialize(state: AnnotationProcessorState, valName: String): CodeBlock {
        return CodeBlock.of("%L", valName)
    }

    override fun deserialize(state: AnnotationProcessorState, valName: String): CodeBlock {
        return CodeBlock.of("%L", valName)
    }
}
