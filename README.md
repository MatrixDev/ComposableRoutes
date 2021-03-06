# ComposableRoutes

[![Release](https://jitpack.io/v/MatrixDev/ComposableRoutes.svg)](https://jitpack.io/#MatrixDev/ComposableRoutes)

This library is a helper for the Android Compose Navigation routes:
- generates routes for the annotated screens
- provides helpers to register routes in the `NavController`
- reduces boiler-plate code for arguments parsing
- and more...

# How does it work?

Add annotation to the Compose screen:

```kotlin
@Composable
@ComposableRoute
fun HomeScreen() {
    // screen without arguments
}

@Composable
@ComposableRoute
fun ContactScreen(id: String) {
    // screen with required parameter
}

@Composable
@ComposableRoute
fun AboutScreen(overrideTitle: String?) {
    // screen with optional arguments
}

@Composable
@ComposableRoute
fun SettingsScreen(navController: NavController) {
    // in case you want to pass navController directly
}
```

Build your project and it will automatically generate `NavRoutes` object.

The next step is to register this route:

```kotlin
val navController = rememberNavController()
NavHost(navController = navController, startDestination = NavRoutes.HomeScreen.PATH) {

    NavRoutes.HomeScreen.register(this)
    NavRoutes.ContactScreen.register(this)
    NavRoutes.AboutScreen.register(this)
    NavRoutes.SettingsScreen.register(this, navController = navController)

}
```

`NavRoutes` also provides a helper to register all generated routes with one call:

```kotlin
val navController = rememberNavController()
NavHost(navController = navController, startDestination = NavRoutes.HomeScreen.PATH) {

    NavRoutes.registerAll(this, navController = navController)

}
```

There are few ways to navigate to the specified route:

```kotlin
// NavController.navigate
navController.navigate(NavRoutes.HomeScreen())

// generated navigateToXXX helpers
navController.navigateToHomeScreen()

// required arguments
navController.navigateToContactScreen(id = "1")

// optional arguments
navController.navigateToAboutScreen(overrideTitle = "Ny About Title")
navController.navigateToAboutScreen()
```

Library supports following type of arguments:
- primitives (`Boolean`, `Int`, `Float`, etc.)
- `String`s
- `Parcelable` objects
- `Serializable` objects

# How to pass NavController to the Screen?

There are few ways to pass `NavController` to the screen:

## Solution #1: By passing it directly to the Screen

All related function will require `NavController` as an argument if at least one Screen takes `NavController` as argument. For example if we have a screen:

```kotlin
@Composable
@ComposableRoute
fun ScreenWithNavController(navController: NavController) {
    // ...
}
```

Than all related `register` and `registerAll` functions will start to require `NavController` as the argument:

```kotlin
NavRoutes.registerAll(this, navController = navController)
// or
NavRoutes.ScreenWithNavController.register(this, navController = navController)
```

## Solution #2: By using CompositionLocalProvider

On the other hand `CompositionLocalProvider` can be used if you don't want to pass `NavController` directly.

The first step is to declare our `CompositionLocal` key for the provider:
```kotlin
val LocalNavController = compositionLocalOf<NavHostController> {
    error("NavController was not provided")
}
```

Then we need to add this key to the provider:
```kotlin
val navController = rememberNavController()
CompositionLocalProvider(LocalNavController provides navController) {
    NavHost(navController = navController, startDestination = NavRoutes.JoinAsGuestScreen()) {
        NavRoutes.registerAll(this)
    }
}
```

That's it. Now you can get `NavController` in any descendant `@Composable` function like this:
```kotlin
LocalNavController.current.navigateToHomeScreen()
```

# Add to your project

To add this library into your project:

Step 1. Add a JitPack repository to your root build.gradle:

```kotlin
allprojects {
    repositories {
        maven(url = "https://jitpack.io")
    }
}
```

Step 2. Add library and compiler dependencies:

```kotlin
dependencies {
    implementation("com.github.MatrixDev.ComposableRoutes:composable-routes-lib:{latest}")
    kapt("com.github.MatrixDev.ComposableRoutes:composable-routes-processor:{latest}")
}
```

More info can be found at https://jitpack.io/#MatrixDev/ComposableRoutes

# TODO

These are just few more nice things to have in the future:
- Migrate from KAPT to KSP

# License

```
MIT License

Copyright (c) 2018 Rostyslav Lesovyi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
