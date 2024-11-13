package com.dacslab.android.sleeping.view.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dacslab.android.sleeping.HomeNavGraph
import com.dacslab.android.sleeping.HomeRoutes
import com.dacslab.android.sleeping.MyBottomBar
import com.dacslab.android.sleeping.TopLevelRoute

@Composable
fun HomeScreen(content: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            content + "!!",
            fontWeight = FontWeight.Bold,
            fontSize = 70.sp
        )
    }
}

@Composable
fun GuideScreen() {
    HomeScreen(content = HomeRoutes.GUIDE)
}

@Composable
fun MeasurementScreen() {
    HomeScreen(content = HomeRoutes.MEASUREMENT)
}

@Composable
fun ResultShowScreen() {
    HomeScreen(content = HomeRoutes.RESULT_SHOW)
}


val homeTopLevelRoutes = listOf(
    TopLevelRoute("가이드", HomeRoutes.GUIDE, Icons.Filled.Person),
    TopLevelRoute("수면 측정", HomeRoutes.MEASUREMENT, Icons.Outlined.Person),
    TopLevelRoute("결과 조회", HomeRoutes.RESULT_SHOW, Icons.Outlined.Person),
    TopLevelRoute("내 정보", HomeRoutes.PROFILE_NAV, Icons.Filled.Person)
)

@Composable
fun Home(
    mainNavController: NavHostController,
) {
    // BottomBar용 네비게이션
    val bottomNavController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        MyBottomBar(
            bottomNavController = bottomNavController,
            topLevelRoutes = homeTopLevelRoutes
        ) { innerPadding ->
            HomeNavGraph(
                mainNavController = mainNavController,
                bottomNavController = bottomNavController,
                innerPadding = innerPadding,
            )
        }
    }
}