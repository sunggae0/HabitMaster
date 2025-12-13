package com.example.habitmaster.feature.habitCreate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmaster.core.data.Habit
import com.example.habitmaster.core.data.firebase.FirebaseProfileRepository
import com.example.habitmaster.core.designsystem.PretendardFamily
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun HabitCreateScreen(onFinish: () -> Unit) {
    val repository = remember { FirebaseProfileRepository() }
    val scope = rememberCoroutineScope()
    var currentProfileId by remember { mutableStateOf<String?>(null) }

    // LaunchedEffect는 컴포지션 시점에 한 번만 실행됨.
    // 하지만 observeProfiles()가 값을 방출하기까지 시간이 걸릴 수 있음.
    LaunchedEffect(Unit) {
        // 첫 번째 값을 가져오거나 flow collection을 유지
        repository.observeProfiles().collect { profiles ->
            if (profiles.isNotEmpty()) {
                currentProfileId = profiles.first().id
            }
        }
    }

    var habitName by remember { mutableStateOf("") }
    var targetCount by remember { mutableStateOf("") }
    var periodValue by remember { mutableStateOf("1") }
    var periodUnit by remember { mutableStateOf("일마다") }
    var startDate by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }

    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(onBackClick = onFinish)
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 20.dp)
            ) {

                FilledRegisterButton(
                    onClick = {
                        // 유효성 검사 및 저장
                        if (habitName.isNotBlank() && !isSaving) {
                            isSaving = true
                            scope.launch {
                                try {
                                    // currentProfileId가 아직 로드되지 않았을 경우를 대비해 다시 한번 가져오기 시도
                                    val profileIdToUse = currentProfileId ?: repository.observeProfiles().firstOrNull()?.firstOrNull()?.id

                                    if (profileIdToUse != null) {
                                        val newHabit = Habit(
                                            id = UUID.randomUUID().toString(),
                                            title = habitName,
                                            achievementRate = 0f,
                                            completeList = mutableListOf(),
                                            targetCount = targetCount.toIntOrNull() ?: 0,
                                            periodValue = periodValue.toIntOrNull() ?: 1,
                                            periodUnit = periodUnit,
                                            startDate = startDate ?: System.currentTimeMillis()
                                        )
                                        // 저장 작업 실행
                                        repository.addHabitToProfile(profileIdToUse, newHabit)

                                        // 저장 완료 후, UI가 아직 로딩 중이라면 바로 onFinish() 호출
                                        // 굳이 추가적인 delay를 줄 필요 없음.
                                        // Firestore 업데이트는 비동기이지만 await()를 사용했으므로 완료된 상태임.

                                        onFinish()
                                    } else {
                                        // 프로필 ID를 찾지 못함
                                        isSaving = false
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    isSaving = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        ) {
            PageTitle(text = "습관 등록")

            HabitNameInput(
                habitName = habitName,
                onValueChange = { habitName = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            FrequencySelector(
                periodValue = periodValue,
                onPeriodValueChange = { periodValue = it },
                periodUnit = periodUnit,
                onPeriodUnitChange = { periodUnit = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TargetCount(
                target = targetCount,
                onValueChange = { targetCount = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            StartDateSelector(
                selectedDate = startDate,
                onDateSelected = { startDate = it }
            )
        }
    }
}

@Composable
fun TopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ){
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        ){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun PageTitle(
    text: String,
    modifier: Modifier = Modifier
){
    Text(
        text = text,
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 20.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )

}
@Composable
fun HabitNameInput(
    habitName: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "습관 이름",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = habitName,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            placeholder = {
                Text(
                    text = "예: 매일 물 2L 마시기",
                    fontFamily = PretendardFamily,
                    fontSize = 14.sp
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontFamily = PretendardFamily,
                fontSize = 14.sp
            ),
            singleLine = true
        )
    }
}

@Composable
fun TargetCount(
    target: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "목표 횟수",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = target,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            placeholder = {
                Text(
                    text = "예: 30",
                    fontFamily = PretendardFamily,
                    fontSize = 14.sp
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontFamily = PretendardFamily,
                fontSize = 14.sp
            ),
            singleLine = true
        )
    }
}




@Composable
fun FrequencySelector(
    periodValue: String,
    onPeriodValueChange: (String) -> Unit,
    periodUnit: String,
    onPeriodUnitChange: (String) -> Unit
){
    Column(modifier =Modifier.fillMaxWidth()) {
        Text(
            text="주기 선택",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = periodValue,
                onValueChange = onPeriodValueChange,
                modifier = Modifier
                    .height(55.dp)
                    .weight(1f),
                singleLine = true,
                placeholder = {
                    Text(
                        "1",
                        fontFamily = PretendardFamily,
                        fontSize = 14.sp
                    )
                },
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = PretendardFamily,
                    fontSize = 14.sp
                ),
                shape = RoundedCornerShape(15.dp)
            )

            var expanded by remember { mutableStateOf(false) }
            val items = listOf("일마다", "주마다", "개월마다")

            Box(modifier = Modifier.weight(2f)) {
                // Read-only TextField with Dropdown
                OutlinedTextField(
                    value = periodUnit,
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .clickable { expanded = true },
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = PretendardFamily,
                        fontSize = 14.sp
                    ),
                    shape = RoundedCornerShape(15.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        // For material3, containers might need tweaking, but default is usually fine
                    )
                )

                // 투명한 Box를 위에 덮어서 클릭 이벤트 가로채기 (enabled=false인 경우 클릭 안먹힘 방지)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { expanded = true }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    items.forEach{ item ->
                        DropdownMenuItem(
                            text = {
                                Text(item, fontFamily = PretendardFamily)
                            },
                            onClick = {
                                onPeriodUnitChange(item)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartDateSelector(
    selectedDate: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    // selectedDate prop을 사용하여 포맷팅
    val formattedDate = selectedDate?.let {
        SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(it))
    } ?: ""

    Column(modifier = modifier.fillMaxWidth()) {

        // 라벨
        Text(
            text = "시작일 선택",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box {
             OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                placeholder = {
                    Text(
                        text = "연도.월.일",
                        fontFamily = PretendardFamily,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                },
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = PretendardFamily,
                    fontSize = 16.sp
                ),
                shape = RoundedCornerShape(15.dp),
                trailingIcon = {
                    IconButton(onClick = { showDialog = true }){
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                singleLine = true,
                readOnly = true,
                enabled = false, // 비활성화하여 키보드 안 올라오게 함
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = Color.Gray
                )
            )

            // 클릭 영역 확보
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDialog = true }
            )
        }


        if (showDialog) {
            DatePickerModalInput(
                onDateSelected = {
                    onDateSelected(it)
                },
                onDismiss = { showDialog = false}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker) // Input 보다는 Picker가 모바일에서 더 직관적일 수 있음

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}



@Composable
fun FilledRegisterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF5865F2),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(15.dp)
    ) {
        Text(
            text = "등록",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}