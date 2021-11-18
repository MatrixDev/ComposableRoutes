package dev.matrix.compose_routes.compiler.serializers

import com.squareup.kotlinpoet.*
import dev.matrix.compose_routes.compiler.AnnotationProcessorState

class ParcelableSerializer(private val state: AnnotationProcessorState) {
    private val base64ClassName = ClassName("android.util", "Base64")
    private val parcelClassName = ClassName("android.os", "Parcel")

    fun serialize(valName: String): CodeBlock {
        return CodeBlock.of("%L.serializeToString()", valName)
    }

    fun deserialize(valName: String): CodeBlock {
        return CodeBlock.of("%L.deserializeToParcelable()", valName)
    }

    fun generateSerializer(): FunSpec {
        return FunSpec.builder("serializeToString")
            .receiver(state.parcelableClassName)
            .returns(STRING)
            .addModifiers(KModifier.PRIVATE)
            .addStatement("val parcel = %T.obtain()", parcelClassName)
            .beginControlFlow("try")
            .addStatement("parcel.writeParcelable(this, 0)")
            .addStatement("parcel.setDataPosition(0)")
            .addStatement(
                "return %T.encodeToString(parcel.marshall(), %T.URL_SAFE)",
                base64ClassName,
                base64ClassName,
            )
            .nextControlFlow("finally")
            .addStatement("parcel.recycle()")
            .endControlFlow()
            .build()
    }

    fun generateDeserializer(): FunSpec {
        val type = TypeVariableName("T", state.parcelableClassName)
        return FunSpec.builder("deserializeToParcelable")
            .addTypeVariable(type.copy(reified = true))
            .addModifiers(KModifier.INLINE)
            .addModifiers(KModifier.PRIVATE)
            .receiver(STRING)
            .returns(type)
            .addStatement("val bytes = Base64.decode(this, Base64.URL_SAFE)", base64ClassName)
            .addStatement("val parcel = %T.obtain()", parcelClassName)
            .beginControlFlow("try")
            .addStatement("parcel.unmarshall(bytes, 0, bytes.size)")
            .addStatement("parcel.setDataPosition(0)")
            .addStatement("return parcel.readParcelable(this.javaClass.classLoader)!!")
            .nextControlFlow("finally")
            .addStatement("parcel.recycle()")
            .endControlFlow()
            .build()
    }
}
