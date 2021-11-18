package dev.matrix.compose_routes.compiler.model.nav_type

import com.squareup.kotlinpoet.CodeBlock
import dev.matrix.compose_routes.compiler.AnnotationProcessorState
import dev.matrix.compose_routes.compiler.serializers.ParcelableSerializer

class ParcelableNavType(private val serializer: ParcelableSerializer) : NavType() {
    override fun toNavValue(state: AnnotationProcessorState, expression: CodeBlock): CodeBlock {
        return serializer.serialize(expression)
    }

    override fun fromNavValue(state: AnnotationProcessorState, expression: CodeBlock): CodeBlock {
        return serializer.deserialize(expression)
    }
}
