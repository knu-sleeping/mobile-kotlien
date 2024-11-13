package com.dacslab.android.sleeping.view.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.dacslab.android.sleeping.MainNavGraph
import com.dacslab.android.sleeping.MainRoutes
import com.dacslab.android.sleeping.model.source.local.AuthLocalDataSource
import com.dacslab.android.sleeping.model.source.remote.AuthRemoteDataSource
import com.dacslab.android.sleeping.viewmodel.AuthViewModel
import com.dacslab.android.sleeping.viewmodel.SharedSessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var authRemoteDataSource: AuthRemoteDataSource
    @Inject lateinit var authLocalDataSource: AuthLocalDataSource
    @Inject lateinit var sessionManager: SharedSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val authViewModel: AuthViewModel = hiltViewModel()
            // isAuthenticated 값을 상태로 유지
            var isAuthenticated by remember { mutableStateOf<Boolean?>(null) }  // 초기값을 null로 설정
            var checkTokenCompleted by remember { mutableStateOf(false) }

            // checkToken() 실행
            LaunchedEffect(Unit) {
                authViewModel.checkToken()
                checkTokenCompleted = true // checkToken 완료 표시
            }

            // checkToken 완료 후 실행
            LaunchedEffect(checkTokenCompleted) {
                if (checkTokenCompleted) {
                    isAuthenticated = authViewModel.isAuthenticated()
                    Log.d("MainActivity", "isAuthenticated: $isAuthenticated after checkToken")
                }
            }

            isAuthenticated?.let { isAuthed ->
                MyApp(isUserAuthenticated = isAuthed)
            }
        }
    }
}

@Composable
fun MyApp(
    isUserAuthenticated: Boolean,
) {
    val mainNavController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        MainNavGraph(
            mainNavController = mainNavController,
            startDestination = if (isUserAuthenticated) MainRoutes.HOME_NAV else MainRoutes.AUTH_NAV,
        )
    }
}
