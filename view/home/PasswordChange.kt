package com.dacslab.android.sleeping.view.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dacslab.android.sleeping.MainRoutes
import com.dacslab.android.sleeping.MySmallTopAppBar
import com.dacslab.android.sleeping.MyVisibleTextField
import com.dacslab.android.sleeping.ProfileRoutes
import com.dacslab.android.sleeping.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun PasswordChangeScreen(
    mainNavController: NavController,
    bottomNavController: NavController,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val userError by userViewModel.error.collectAsState()
    val result by userViewModel.pwChangeResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    MySmallTopAppBar(
        title = "비밀번호 변경",
        navController = bottomNavController,
        snackbarHostState = snackbarHostState,
        content = { padding ->
            PasswordChangeContent(
                modifier = Modifier.padding(padding),
                userViewModel = userViewModel,
                bottomNavController = bottomNavController
            )
        }
    )

    LaunchedEffect(result) {
        if (result == true) {
            bottomNavController.popBackStack()
            userViewModel.clearPwChangeResult()
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.navigateToLogin.collect {
            mainNavController.navigate(MainRoutes.AUTH_NAV) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    PasswordChangeHandleErrors(
        userViewModel = userViewModel,
        snackbarHostState = snackbarHostState,
        userError = userError
    )
}

@Composable
fun PasswordChangeContent(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    bottomNavController: NavController
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isLoading by userViewModel.isLoading.collectAsState()


    fun handlePasswordChange(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        when {
            newPassword != confirmPassword -> {
                userViewModel.clearError()
                userViewModel.setError("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.")
            }
            oldPassword == newPassword -> {
                userViewModel.clearError()
                userViewModel.setError("새 비밀번호는 이전 비밀번호와 달라야 합니다.")
            }
            else -> {
                userViewModel.passwordChange(oldPassword, newPassword)
            }
        }
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PasswordField(
                label = "현재 비밀번호",
                password = oldPassword,
                onPasswordChange = { oldPassword = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordField(
                label = "새 비밀번호",
                password = newPassword,
                onPasswordChange = { newPassword = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordField(
                label = "새 비밀번호 확인",
                password = confirmPassword,
                onPasswordChange = { confirmPassword = it }
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = {
                    handlePasswordChange(oldPassword, newPassword, confirmPassword)
                },
                enabled = !isLoading
            ) {
                Text("비밀번호 변경", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = {
                    bottomNavController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                enabled = !isLoading
            ) {
                Text("다음에 변경", fontSize = 18.sp)
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}
@Composable
fun PasswordField(
    label: String,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    MyVisibleTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = label
    )
}

@Composable
fun PasswordChangeHandleErrors(
    userError: String?,
    userViewModel: UserViewModel,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    userError?.let {
        Log.d("PasswordChangeScreen", "userError?.let { ...")
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = it,
                actionLabel = "확인",
                duration = SnackbarDuration.Short
            )
            userViewModel.clearError()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordChangeContentPreview() {
}
