package dev.matrix.compose_routes

import android.graphics.PorterDuff
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
class ParcelableArg : Parcelable
class SerializableArg : Serializable
enum class EnumArg { A }

@Composable
@ComposableRoute
fun ScreenNoArgs() {
}

@Composable
@ComposableRoute
fun ScreenWithString1(arg0: String) {
}

@Composable
@ComposableRoute
fun ScreenWithString2(arg0: String?) {
}

@Composable
@ComposableRoute
fun ScreenWithInt(arg0: Int) {
}

@Composable
@ComposableRoute
fun ScreenWithSimpleArgs1(
    arg0: Boolean,
    arg1: Byte,
    arg2: Short,
    arg3: Int,
    arg4: Long,
    arg5: Float?,
    arg6: Double?,
) {
}

@Composable
@ComposableRoute
fun ScreenWithSimpleArgs2(
    arg0: Boolean?,
    arg1: Byte?,
    arg2: Short?,
    arg3: Int?,
    arg4: Long?,
    arg5: Float,
    arg6: Double,
) {
}

@Composable
@ComposableRoute
fun ScreenWithParcelableArgs1(
    arg0: ParcelableArg,
) {
}

@Composable
@ComposableRoute
fun ScreenWithParcelableArgs2(
    arg0: ParcelableArg?,
) {
}

@Composable
@ComposableRoute
fun ScreenWithEnumArgs(
    arg1: EnumArg,
    arg2: PorterDuff.Mode,
) {
}

@Composable
@ComposableRoute
fun ScreenWithSerializableArgs(
    arg0: SerializableArg,
) {
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoutes.ScreenNoArgs.PATH) {
        NavRoutes.registerAll(this)
    }
    navController.navigateToScreenNoArgs()
    navController.navigateToScreenWithInt(10)
}
