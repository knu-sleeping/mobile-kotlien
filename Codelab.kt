package com.dacslab.android.sleeping

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable


@Composable
fun OutlinedTextFieldBackground(
    color: Color,
    shape: RoundedCornerShape,
    content: @Composable () -> Unit,
) {
    Box {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 8.dp)
                .background(
                    color,
                    shape = shape
                )
        )
        content()
    }
}


@Composable
fun MyTextField(
    value: String?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    label: String = "default_label",
    shape: RoundedCornerShape = CircleShape
) {
    OutlinedTextFieldBackground(MaterialTheme.colorScheme.background, shape) {
        OutlinedTextField(
            value = value ?: "",
            onValueChange = { onValueChange(it) },  // 상위에서 전달된 onValueChange 호출
            shape = shape,
            label = {
                Text(text = label)
            },
            trailingIcon = {
                IconButton(onClick = { onValueChange("") }) {  // Clear 아이콘 클릭 시 값 초기화
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null,
                    )
                }
            },
            modifier = modifier  // fillMaxWidth 적용
        )
    }
}

@Composable
fun MyVisibleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    label: String = "default_label",
    shape: RoundedCornerShape = CircleShape
) {
    var isVisiblePassword by rememberSaveable { mutableStateOf(false) }

    OutlinedTextFieldBackground(MaterialTheme.colorScheme.background, shape) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },  // 입력값 변경 시 상위 컴포저블에 전달
            modifier = modifier,  // fillMaxWidth 적용
            trailingIcon = {
                val icon = if (isVisiblePassword) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }

                IconButton(onClick = { isVisiblePassword = !isVisiblePassword }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (isVisiblePassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            textStyle = TextStyle(
                fontSize = 20.sp
            ),
            shape = shape,
            label = {
                Text(text = label)
            }
        )
    }
}


@Composable
fun ShapeBox(
    shape: Shape,
    content: @Composable BoxScope.() -> Unit
){
    Column(modifier = Modifier.wrapContentSize(Alignment.Center))
    {
        Box(
            modifier = Modifier
                .clip(shape)
                .width(400.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            content = content // Box 내부에 content를 추가
        )
    }
}

@Composable
fun MyDropdown(
    modifier: Modifier = Modifier.fillMaxWidth(),
    shape: RoundedCornerShape = CircleShape,
    options: List<String>,
    label: String,
    selectedOption: String = "",
    onOptionSelected: (String) -> Unit = {}
) {
    val optionsWithLabel = listOf(label) + options
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown
    val focusManager = LocalFocusManager.current

    Column {
        OutlinedTextFieldBackground(MaterialTheme.colorScheme.background, shape) {
            OutlinedTextField(
                value = selectedOption,
                readOnly = true,
                onValueChange = {},
                trailingIcon = {
                    Icon(icon, null)
                },
                modifier = modifier
                    .onFocusChanged { expanded = it.isFocused },
                shape = shape,
                label = {
                    Text(text = label)
                },
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                focusManager.clearFocus()
            },
            modifier = modifier
        ) {
            optionsWithLabel.forEach { s ->
                DropdownMenuItem(
                    enabled = s != label, // label은 비활성화
                    onClick = {
                        onOptionSelected(s) // 선택된 옵션을 상위로 전달
                        expanded = false
                        focusManager.clearFocus()
                    },
                    text = {
                        Text(text = s)
                    }
                )
            }
        }
    }
}




@Composable
fun UserInputField(label: String, value: String?, onValueChange: (String) -> Unit) {
    MyTextField(
        label = label,
        value = value ?: "",
        onValueChange = onValueChange
    )
}

@Composable
fun GenderDropdown(selectedGender: String?, onGenderSelect: (String) -> Unit) {
    MyDropdown(
        label = "성별",
        options = listOf("남", "여"),
        selectedOption = selectedGender ?: "",
        onOptionSelected = { onGenderSelect(it) }
    )
}

@Composable
fun PasswordField(label: String, value: String, onValueChange: (String) -> Unit) {
    MyVisibleTextField(
        label = label,
        value = value,
        onValueChange = onValueChange
    )
}

@Composable
fun ComplicationDropdown(selectedComplication: Boolean?, onComplicationSelect: (Boolean) -> Unit) {
    MyDropdown(
        label = "합병증 유무",
        options = listOf("O", "X"),
        selectedOption = if (selectedComplication == true) "O" else "X",
        onOptionSelected = {
            onComplicationSelect(it == "O")
        }
    )
}


@Composable
fun ScrollContent(innerPadding: PaddingValues) {
    LazyColumn(
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Example items in the scrollable content
        items(20) { index ->
            Text(
                text = "Item #$index",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySmallTopAppBar(
    title: String,
    navController: NavController?,
    snackbarHostState: SnackbarHostState? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        snackbarHost = {
            if (snackbarHostState != null) {
                SnackbarHost(hostState = snackbarHostState)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
//                actions = {
//                    IconButton(onClick = { /* do something */ }) {
//                        Icon(
//                            imageVector = Icons.Filled.Menu,
//                            contentDescription = "Localized description"
//                        )
//                    }
//                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        content(innerPadding)
    }
}
data class TopLevelRoute(val name: String, val route: String, val icon: ImageVector)


object ExampleRoutes {
    const val PROFILE = "profile"
    const val FRIENDS = "friends"
}


val exampleTopLevelRoutes = listOf(
    TopLevelRoute("Profile", ExampleRoutes.PROFILE, Icons.Filled.Person),
    TopLevelRoute("Friends", ExampleRoutes.FRIENDS, Icons.Outlined.Person)
)

@Composable
fun ExampleNavHost(navHostController: NavHostController, innerPadding: PaddingValues) {
    NavHost(
        navController = navHostController,
        startDestination = ExampleRoutes.PROFILE,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(route = ExampleRoutes.PROFILE) {
            ExampleScreen(1)
        }
        composable(route = ExampleRoutes.FRIENDS) {
            ExampleScreen(2)
        }
    }
}

@Composable
fun ExampleScreen(index: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "화면",
            fontWeight = FontWeight.Bold,
            fontSize = 70.sp
        )
        Text(
            "$index",
            fontWeight = FontWeight.Bold,
            fontSize = 70.sp
        )
    }
}

@Composable
fun MyBottomBar(
    bottomNavController: NavController,
    topLevelRoutes: List<TopLevelRoute>,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                topLevelRoutes.forEach { topLevelRoute ->
                    BottomNavigationItem(
                        icon = { Icon(topLevelRoute.icon, contentDescription = topLevelRoute.name) },
                        label = { Text(topLevelRoute.name) },
                        selected = currentDestination?.route == topLevelRoute.route,
                        onClick = {
                            bottomNavController.navigate(topLevelRoute.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
fun WearInputComp(context: Context) {
    var inputValue by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            label = { Text("Enter value") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            sendDataToPhone(context, inputValue)
        }) {
            Text("Send to Phone")
        }
    }
}

fun sendDataToPhone(context: Context, inputValue: String) {
    val dataMapRequest = PutDataMapRequest.create("/inputValue")
    val dataMap = dataMapRequest.dataMap
    dataMap.putString("input", inputValue)
    dataMap.putLong("timestamp", System.currentTimeMillis()) // Unique ID to avoid overwriting data

    val putDataRequest = dataMapRequest.asPutDataRequest().setUrgent()
    Wearable.getDataClient(context).putDataItem(putDataRequest)
        .addOnSuccessListener {
            Log.d("Wearable", "Data sent successfully")
        }
        .addOnFailureListener { e ->
            Log.e("Wearable", "Failed to send data", e)
        }
}

@Preview(showBackground = true, name = "Text preview", widthDp = 400)
@Composable
fun GreetingPreview() {
//    RegisterBox()
}

@Preview(showBackground = true, name = "Wear Input Screen Preview", widthDp = 400)
@Composable
fun WearInputScreenPreview() {
    WearInputComp(context = LocalContext.current)
}
