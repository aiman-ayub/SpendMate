package com.example.spendsensei.ui.navigation
import kotlinx.serialization.Serializable
sealed interface Screen {
    @Serializable
    data object Dashboard : Screen
    @Serializable
    data object AddTransaction : Screen
    @Serializable
    data object Analytics : Screen

    @Serializable
    data object Budget : Screen
    @Serializable
    data object Settings : Screen
    @Serializable
    data object Splash : Screen
    @Serializable
    data object Login : Screen
    @Serializable
    data object SignUp : Screen

    @Serializable
    data object History : Screen

    @Serializable
    object UserList : Screen

}