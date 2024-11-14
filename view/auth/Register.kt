package com.dacslab.android.sleeping.view.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dacslab.android.sleeping.AuthRoutes
import com.dacslab.android.sleeping.ComplicationDropdown
import com.dacslab.android.sleeping.GenderDropdown
import com.dacslab.android.sleeping.MySmallTopAppBar
import com.dacslab.android.sleeping.PasswordField
import com.dacslab.android.sleeping.ShapeBox
import com.dacslab.android.sleeping.UserInputField
import com.dacslab.android.sleeping.model.User
import com.dacslab.android.sleeping.viewmodel.AuthViewModel
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        error?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    actionLabel = "확인",
                    duration = SnackbarDuration.Short
                )
                authViewModel.clearError()
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

        Text(
            text = "이미 계정이 있으신가요? 로그인",
            modifier = Modifier.clickable { navController?.popBackStack() },
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp
        )
    }
}
@Composable
fun RegisterBox(authViewModel: AuthViewModel) {
    var userState by remember {
        mutableStateOf(
            User(
                userId = "", userPw = "", userName = "",
                userGender = null, userAge = null, userHeight = null,
                userWeight = null, userComp = null
            )
        )
    }
    var confirmUserPw by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ShapeBox(shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UserInputField("이름 *", userState.userName)
                { userState = userState.copy(userName = it) }
                GenderDropdown(userState.userGender)
                { userState = userState.copy(userGender = it) }
                UserInputField("사용할 아이디 *", userState.userId)
                { userState = userState.copy(userId = it) }
                PasswordField("비밀번호 *", userState.userPw ?: "")
                { userState = userState.copy(userPw = it) }
                PasswordField("비밀번호 확인 *", confirmUserPw)
                { confirmUserPw = it }
                UserInputField("키 (cm생략, ex:170)", userState.userHeight?.toString())
                { userState = userState.copy(userHeight = it.toIntOrNull()) }
                UserInputField("몸무게 (kg생략, ex:60)", userState.userWeight?.toString())
                { userState = userState.copy(userWeight = it.toIntOrNull()) }
                UserInputField("나이 (ex:25)", userState.userAge?.toString())
                { userState = userState.copy(userAge = it.toIntOrNull()) }
                ComplicationDropdown(userState.userComp)
                { userState = userState.copy(userComp = it) }

                Spacer(modifier = Modifier.height(8.dp))

                RegisterButton(authViewModel, userState, confirmUserPw)
            }
        }
    }
}



@Composable
fun RegisterButton(
    authViewModel: AuthViewModel,
    user: User,
    confirmUserPw: String,
) {
    Button(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        onClick = {
            if (user.userPw == confirmUserPw) {
                authViewModel.register(user)
            } else {
                authViewModel.clearError()
                authViewModel.setError("비밀번호가 일치하지 않습니다.")
            }
        }
    ) {
        Text(text = "회원가입", fontSize = 20.sp)
    }
}



//@Preview(showBackground = true)
//@Composable
//fun RegisterPreview() {
//    // No navController for preview, use a stub
//    RegisterScreen(navController = null)
//}