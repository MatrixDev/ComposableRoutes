package dev.matrix.compose_routes.compiler

import com.squareup.kotlinpoet.*
import dev.matrix.compose_routes.compiler.serializers.ParcelableSerializer
import dev.matrix.compose_routes.compiler.serializers.SerializableSerializer
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

@Suppress("MemberVisibilityCanBePrivate")
class AnnotationProcessorState(val environment: ProcessingEnvironment) {
    val parcelableSerializer = ParcelableSerializer(this)
    val serializableSerializer = SerializableSerializer(this)

    val jStringClassName = ClassName("java.lang", "String")
    val jStringTypeElement = jStringClassName.toTypeElement()

    val serializableClassName = ClassName("java.io", "Serializable")
    val serializableTypeElement = serializableClassName.toTypeElement()

    val parcelableClassName = ClassName("android.os", "Parcelable")
    val parcelableTypeElement = parcelableClassName.toTypeElement()

    val composableAnnotation = ClassName("androidx.compose.runtime", "Composable")
    val navType = ClassName("androidx.navigation", "NavType")
    val navGraphBuilder = ClassName("androidx.navigation", "NavGraphBuilder")
    val navBackStackEntry = ClassName("androidx.navigation", "NavBackStackEntry")
    val navHostController = ClassName("androidx.navigation", "NavHostController")
    val navComposeFun = MemberName("androidx.navigation.compose", "composable")
    val navArgumentFun = MemberName("androidx.navigation", "navArgument")

    fun error(message: String, element: Element? = null): Nothing {
        environment.messager.printMessage(Diagnostic.Kind.ERROR, message, element)
        throw Exception(message)
    }

    private fun ClassName.toTypeElement(): TypeElement {
        return toTypeElementMaybe() ?: error("$this not found")
    }

    fun ClassName.toTypeElementMaybe(): TypeElement? {
        return environment.elementUtils.getTypeElement(canonicalName)
    }

    fun unbox(type: TypeMirror): TypeMirror {
        runCatching {
            return environment.typeUtils.unboxedType(type)
        }
        return type
    }
}
