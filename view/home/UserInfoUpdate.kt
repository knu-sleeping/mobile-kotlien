package com.dacslab.android.sleeping.view.home

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
import com.dacslab.android.sleeping.ShapeBox
import com.dacslab.android.sleeping.viewmodel.UserViewModel
import com.dacslab.android.sleeping.UserInputField
import com.dacslab.android.sleeping.GenderDropdown
import com.dacslab.android.sleeping.ComplicationDropdown
import com.dacslab.android.sleeping.MainRoutes
import com.dacslab.android.sleeping.MySmallTopAppBar
import com.dacslab.android.sleeping.model.User
import kotlinx.coroutines.launch


@Composable
fun UserInfoUpdateScreen(
    bottomNavController : NavController,
    mainNavController: NavController,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val isLoading by userViewModel.isLoading.collectAsState()
    val error by userViewModel.error.collectAsState()
    val updateResult by userViewModel.updateResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()

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
                    title = "정보 수정",
                    navController = bottomNavController,
                ) { innerPadding ->
                    UserInfoUpdateForm(
                        bottomNavController = bottomNavController,
                        innerPadding = innerPadding,
                        userViewModel = userViewModel
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
                userViewModel.clearError()
            }
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.getUserInfo()
    }

    // 업데이트 성공 시 다른 화면으로 이동
    LaunchedEffect(updateResult) {
        if (updateResult == true) {
            bottomNavController.popBackStack() // 예: 프로필 화면으로 이동
            userViewModel.clearUpdateResult()
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.navigateToLogin.collect {
            mainNavController.navigate(MainRoutes.AUTH_NAV) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}

@Composable
fun UserInfoUpdateForm(
    bottomNavController: NavController,
    innerPadding: PaddingValues,
    userViewModel: UserViewModel
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
        UserInfoUpdateBox(userViewModel)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "내 정보로 돌아가기",
            modifier = Modifier.clickable { bottomNavController.popBackStack() },
            color = MaterialTheme.colorScheme.primary,
            fontSize = 20.sp
        )
    }
}

@Composable
fun UserInfoUpdateBox(
    userViewModel: UserViewModel,
) {
    val userInfo by userViewModel.userInfo.collectAsState()
    // 최초 userInfo 값을 기반으로 userState 초기화
    var userState by remember {
        mutableStateOf(userInfo ?: User(
                userId = "", userPw = "", userName = "",
                userGender = null, userAge = null, userHeight = null,
                userWeight = null, userComp = null
            )
        )
    }

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
                UserInputField("키 (cm생략, ex:170)", userState.userHeight?.toString())
                { userState = userState.copy(userHeight = it.toIntOrNull()) }
                UserInputField("몸무게 (kg생략, ex:60)", userState.userWeight?.toString())
                { userState = userState.copy(userWeight = it.toIntOrNull()) }
                UserInputField("나이 (ex:25)", userState.userAge?.toString())
                { userState = userState.copy(userAge = it.toIntOrNull()) }
                ComplicationDropdown(userState.userComp)
                { userState = userState.copy(userComp = it) }

                Spacer(modifier = Modifier.height(8.dp))

                UpdateButton(userViewModel, userState)
            }
        }
    }
    // userInfo 변경 시 userState 업데이트
    LaunchedEffect(userInfo) {
        userInfo?.let {
            userState = it
        }
    }
}

@Composable
fun UpdateButton(
    userViewModel: UserViewModel,
    user: User
) {
    Button(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        onClick = {
            // 입력 검증 로직
            when {
                user.userName.isBlank() -> userViewModel.setError("이름을 입력하세요.")
                user.userGender.isNullOrBlank() -> userViewModel.setError("성별을 선택하세요.")
                user.userAge == null -> userViewModel.setError("나이를 입력하세요.")
                user.userHeight == null -> userViewModel.setError("키를 입력하세요.")
                user.userWeight == null -> userViewModel.setError("몸무게를 입력하세요.")
                user.userComp == null -> userViewModel.setError("합병증 여부를 선택하세요.")
                else -> userViewModel.updateUserInfo(user)
            }
        }
    ) {
        Text(text = "수정", fontSize = 20.sp)
    }
}

