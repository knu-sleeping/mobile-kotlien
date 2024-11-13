package com.dacslab.android.sleeping

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.dacslab.android.sleeping.view.auth.LoginScreen
import com.dacslab.android.sleeping.view.auth.RegisterScreen
import com.dacslab.android.sleeping.view.home.GuideScreen
import com.dacslab.android.sleeping.view.home.Home
import com.dacslab.android.sleeping.view.home.MeasurementScreen
import com.dacslab.android.sleeping.view.home.PasswordChangeScreen
import com.dacslab.android.sleeping.view.home.ProfileScreen
import com.dacslab.android.sleeping.view.home.ResultShowScreen


object MainRoutes {
    const val HOME_NAV = "home_nav"
    const val AUTH_NAV = "auth_nav"
}

object AuthRoutes {
    const val REGISTER = "reg"
    const val LOGIN = "login"
}

object HomeRoutes {
    const val GUIDE = "guide"
    const val MEASUREMENT = "measurement"
    const val RESULT_SHOW = "result_show"
    const val PROFILE_NAV = "profile_nav"
}

object ProfileRoutes {
    const val PROFILE = "profile"
    const val PASSWORD_CHANGE = "pw_change"
}

@Composable
fun MainNavGraph(
    mainNavController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = mainNavController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        // Authentication Navigation Graph
        authNavGraph(navController = mainNavController)

        // Home Screen with Bottom Navigation
        composable(MainRoutes.HOME_NAV) {
            Home(mainNavController = mainNavController)
        }
    }
}

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
) {
    navigation(startDestination = AuthRoutes.LOGIN, route = MainRoutes.AUTH_NAV) {
        composable(AuthRoutes.REGISTER) {
            RegisterScreen(navController = navController)
        }
        composable(AuthRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }
    }
}

@Composable
fun HomeNavGraph(
    bottomNavController: NavHostController,  // 이름 변경
    mainNavController: NavHostController,    // 메인 네비게이션 추가
    innerPadding: PaddingValues? = null,
) {
    NavHost(
        modifier = innerPadding?.let { Modifier.padding(it) } ?: Modifier,
        navController = bottomNavController,
        startDestination = HomeRoutes.GUIDE,
        route = MainRoutes.HOME_NAV,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(HomeRoutes.GUIDE) {
            GuideScreen()
        }
        composable(HomeRoutes.MEASUREMENT) {
            MeasurementScreen()
        }
        composable(HomeRoutes.RESULT_SHOW) {
            ResultShowScreen()
        }

        profileNavGraph(
            mainNavController = mainNavController,
            bottomNavController = bottomNavController
        )
    }
}

fun NavGraphBuilder.profileNavGraph(
    mainNavController: NavHostController,
    bottomNavController: NavHostController,  // 이름 변경

) {
    navigation(startDestination = ProfileRoutes.PROFILE, route = HomeRoutes.PROFILE_NAV) {
        composable(ProfileRoutes.PROFILE) {
            ProfileScreen(
                mainNavController = mainNavController,
                bottomNavController = bottomNavController
            )
        }
        composable(ProfileRoutes.PASSWORD_CHANGE) {
            PasswordChangeScreen(
                bottomNavController = bottomNavController,
                mainNavController = mainNavController
            )
        }
    }
}