package com.dacslab.android.sleeping.view.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dacslab.android.sleeping.ShapeBox
import com.dacslab.android.sleeping.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import com.dacslab.android.sleeping.MainRoutes
import com.dacslab.android.sleeping.ProfileRoutes
import com.dacslab.android.sleeping.model.User
import com.dacslab.android.sleeping.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    mainNavController: NavHostController,
    bottomNavController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val userInfo by userViewModel.userInfo.collectAsState()
    val userIsLoading by userViewModel.isLoading.collectAsState()
    val userError by userViewModel.error.collectAsState()
    val authIsLoading by authViewModel.isLoading.collectAsState()
    val authError by authViewModel.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        userViewModel.getUserInfo()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        ProfileContent(
            padding = padding,
            userInfo = userInfo,
            isLoading = authIsLoading || userIsLoading,
            onLogout = {
                coroutineScope.launch {
                    authViewModel.logout()
                    mainNavController.navigate(MainRoutes.AUTH_NAV) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            },
            onPwChange = {
                bottomNavController.navigate(ProfileRoutes.PASSWORD_CHANGE)
            },
            onUserUpdate = {
                bottomNavController.navigate(ProfileRoutes.UPDATE_USERINFO)
            },
        )

        HandleErrors(
            authError = authError,
            userError = userError,
            authViewModel = authViewModel,
            userViewModel = userViewModel,
            snackbarHostState = snackbarHostState
        )

        LaunchedEffect(Unit) {
            userViewModel.navigateToLogin.collect {
                mainNavController.navigate(MainRoutes.AUTH_NAV) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    padding: PaddingValues,
    userInfo: User?,
    isLoading: Boolean,
    onLogout: () -> Unit,
    onPwChange: () -> Unit,
    onUserUpdate: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeader(userInfo)
            Spacer(modifier = Modifier.height(24.dp))
            ProfileDetails(userInfo)
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(text = "비밀번호 변경", doWhat = onPwChange)
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(text = "내 정보 수정", doWhat = onUserUpdate)
            Spacer(modifier = Modifier.height(24.dp))
            LogoutButton(onLogout)
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun ProfileHeader(userInfo: User?) {
    ShapeBox(shape = RoundedCornerShape(20.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            userInfo?.let { user ->
                Text(text = user.userName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = user.userId, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ProfileDetails(userInfo: User?) {
    ShapeBox(shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            userInfo?.let { user ->
                ProfileInfoItem(title = "아이디", value = user.userId)
                ProfileInfoDivider()
                ProfileInfoItem(title = "이름", value = user.userName)
                ProfileInfoDivider()
                ProfileInfoItem(title = "성별", value = user.userGender ?: "N/A")
                ProfileInfoDivider()
                ProfileInfoItem(title = "나이", value = user.userAge?.toString() ?: "N/A")
                ProfileInfoDivider()
                ProfileInfoItem(title = "키", value = user.userHeight?.toString() ?: "N/A")
                ProfileInfoDivider()
                ProfileInfoItem(title = "몸무게", value = user.userWeight?.toString() ?: "N/A")
                ProfileInfoDivider()
                ProfileInfoItem(title = "합병증 여부", value = user.userComp?.let { if (it) "있음" else "없음" } ?: "N/A")
            }
        }
    }
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        onClick = onLogout
    ) {
        Text("로그아웃", fontSize = 18.sp)
    }
}

@Composable
private fun PrimaryButton(
    text : String,
    doWhat: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        onClick = doWhat
    ) {
        Text(text, fontSize = 18.sp)
    }
}

@Composable
private fun ProfileInfoItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ProfileInfoDivider() {
    Divider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun HandleErrors(
    authError: String?,
    userError: String?,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    authError?.let {
        Log.d("ProfileScreen", "authError?.let { ...")
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message = it, actionLabel = "확인", duration = SnackbarDuration.Short)
            authViewModel.clearError()
        }
    }

    userError?.let {
        Log.d("ProfileScreen", "userError?.let { ...")
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message = it, actionLabel = "확인", duration = SnackbarDuration.Short)
            userViewModel.clearError()
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ProfileContentPreview() {
    // 예시 데이터 생성
    val sampleUser = User(
        userId = "sampleUser",
        userPw = null,
        userName = "홍길동",
        userGender = "M",
        userAge = 25,
        userHeight = 175,
        userWeight = 70,
        userComp = false
    )

    ProfileContent(
        padding = PaddingValues(16.dp),
        userInfo = sampleUser,
        isLoading = false,
        onLogout = {}, // 로그아웃 버튼이 눌렸을 때의 동작 정의
        onPwChange = {},
        onUserUpdate = {},
    )
}