package com.dacslab.android.sleeping.view.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dacslab.android.sleeping.AuthRoutes
import com.dacslab.android.sleeping.MainRoutes
import com.dacslab.android.sleeping.MySmallTopAppBar
import com.dacslab.android.sleeping.RegisterBox
import com.dacslab.android.sleeping.viewmodel.AuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch


@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    val registerResult by authViewModel.registerResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // LaunchedEffect를 사용하여 네비게이션 처리
    LaunchedEffect(registerResult) {
        if (registerResult == true) {
            navController.navigate(AuthRoutes.LOGIN)
            authViewModel.clearRegisterResult()
        }
    }



    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MySmallTopAppBar(
                    title = "회원가입",
                    navController = navController,
                ) { innerPadding ->
                    RegisterForm(
                        navController = navController,
                        innerPadding = innerPadding,
                        authViewModel = authViewModel
                    )
                }
            }
            // Display a loading indicator if needed
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        // 에러 메시지가 있을 경우 CoroutineScope를 사용해 Snackbar 표시
        error?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    actionLabel = "확인",
                    duration = SnackbarDuration.Short
                )

                authViewModel.clearError() // 오류 상태 초기화 함수 호출
            }
        }
    }
}

@Composable
fun RegisterForm(
    navController: NavController?,
    innerPadding: PaddingValues,
    authViewModel: AuthViewModel
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RegisterBox(authViewModel)

        Spacer(modifier = Modifier.height(20.dp))

        // Clickable Text to navigate to the login screen
        Text(
            text = "이미 계정이 있으신가요? 로그인",
            modifier = Modifier.clickable { navController?.popBackStack() },
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp
        )


    }
}

//@Preview(showBackground = true)
//@Composable
//fun RegisterPreview() {
//    // No navController for preview, use a stub
//    RegisterScreen(navController = null)
//}