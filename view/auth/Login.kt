package com.dacslab.android.sleeping.view.auth

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dacslab.android.sleeping.AuthRoutes
import com.dacslab.android.sleeping.MainRoutes
import com.dacslab.android.sleeping.MyTextField
import com.dacslab.android.sleeping.MyVisibleTextField
import com.dacslab.android.sleeping.ShapeBox
import com.dacslab.android.sleeping.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LoginForm(navController)
    }
}

@Composable
fun LoginForm(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val sharedSessionManager = remember { authViewModel.sharedSessionManager }
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    val loginResult by authViewModel.loginResult.collectAsState()
    val showSessionExpiredAlert by sharedSessionManager.showSessionExpiredAlert.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        LoginContent(
            modifier = Modifier.padding(padding),
            navController = navController,
            isLoading = isLoading,
            authViewModel = authViewModel
        )

        LaunchedEffect(loginResult) {
            if (loginResult == true) {
                navController.navigate(MainRoutes.HOME_NAV) {
                    popUpTo(MainRoutes.AUTH_NAV) { inclusive = true }
                }
                authViewModel.clearLoginResult()
            }
        }

        LaunchedEffect(error) {
            error?.let {
                snackbarHostState.showSnackbar(
                    message = it,
                    actionLabel = "확인",
                    duration = SnackbarDuration.Short
                )
                authViewModel.clearError()
            }
        }

        LaunchedEffect(showSessionExpiredAlert) {
            if (showSessionExpiredAlert) {
                Log.d("LoginForm", "Showing session expired snackbar.")
                snackbarHostState.showSnackbar(
                    message = "세션이 만료되었습니다. 다시 로그인해주세요.",
                    actionLabel = "확인",
                    duration = SnackbarDuration.Short
                )
                sharedSessionManager.resetSessionExpiredAlert()
            }
        }
    }
}

@Composable
private fun LoginContent(
    modifier: Modifier,
    navController: NavController,
    isLoading: Boolean,
    authViewModel: AuthViewModel
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("로그인", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            LoginBox(authViewModel)
            Spacer(modifier = Modifier.height(20.dp))
            SignupText(navController)
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun SignupText(navController: NavController) {
    Text(
        text = "계정이 없으신가요? 회원가입",
        modifier = Modifier.clickable { navController.navigate(AuthRoutes.REGISTER) },
        color = MaterialTheme.colorScheme.primary,
        fontSize = 16.sp
    )
}

@Composable
private fun LoginBox(authViewModel: AuthViewModel) {
    var userId by remember { mutableStateOf("") }
    var userPw by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ShapeBox(shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MyTextField(label = "ID", value = userId, onValueChange = { userId = it })
                MyVisibleTextField(label = "Password", value = userPw, onValueChange = { userPw = it })
                Spacer(modifier = Modifier.height(8.dp))
                LoginButton { authViewModel.login(userId, userPw) }
                Spacer(modifier = Modifier.height(16.dp))
                ForgotPasswordRow()
            }
        }
    }
}

@Composable
private fun LoginButton(onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        onClick = onClick
    ) {
        Text(text = "로그인", fontSize = 20.sp)
    }
}

@Composable
private fun ForgotPasswordRow() {
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text("아이디 찾기")
        VerticalDivider(color = MaterialTheme.colorScheme.secondary)
        Text("비밀번호 찾기")
    }
}
