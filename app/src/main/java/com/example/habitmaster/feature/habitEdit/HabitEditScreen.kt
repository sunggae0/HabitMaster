package com.example.habitmaster.feature.habitEdit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HabitEditScreen(
    habitId: String,
    onFinish: (String) -> Unit // 완료 시 profileId를 전달
) {
    val repository = remember { FirebaseProfileRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // 현재 프로필 및 습관 데이터 로드
    var currentProfileId by remember { mutableStateOf<String?>(null) }
    var currentHabit by remember { mutableStateOf<Habit?>(null) }
    
    // UI 상태
    var habitName by remember { mutableStateOf("") }
    var targetCount by remember { mutableStateOf("") }
    var periodValue by remember { mutableStateOf("1") }
    var periodUnit by remember { mutableStateOf("일마다") }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var isActive by remember { mutableStateOf(true) } 

    // 데이터 로딩
    LaunchedEffect(habitId) {
        try {
            val profiles = repository.observeProfiles().firstOrNull() ?: emptyList()
            // 모든 프로필을 뒤져서 해당 habitId를 가진 프로필과 습관을 찾음
            for (profile in profiles) {
                val foundHabit = profile.habits.find { it.id == habitId }
                if (foundHabit != null) {
                    currentProfileId = profile.id
                    currentHabit = foundHabit
                    
                    habitName = foundHabit.title
                    targetCount = foundHabit.targetCount.toString()
                    periodValue = foundHabit.periodValue.toString()
                    periodUnit = foundHabit.periodUnit
                    startDate = foundHabit.startDate
                    isActive = foundHabit.isActive
                    break // 찾았으면 종료
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopBar(onBackClick = {
                // 뒤로가기 시에도 profileId가 있다면 전달, 없다면(로딩 실패 등) 그냥 빈 문자열이나 에러 처리
                // 여기서는 로딩된 profileId가 있으면 메인으로 이동
                currentProfileId?.let { onFinish(it) }
            })
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // 저장 버튼
                FilledEditButton(
                    onClick = { 
                        scope.launch {
                            try {
                                val profileId = currentProfileId ?: return@launch
                                val habitToUpdate = currentHabit ?: return@launch

                                val updatedHabit = habitToUpdate.copy(
                                    title = habitName,
                                    targetCount = targetCount.toIntOrNull() ?: 0,
                                    periodValue = periodValue.toIntOrNull() ?: 1,
                                    periodUnit = periodUnit,
                                    startDate = startDate ?: 0L,
                                    isActive = isActive
                                )

                                repository.updateHabit(profileId, updatedHabit)
                                onFinish(profileId) // 저장 완료 후 해당 프로필 메인으로 이동
                            } catch (e: Exception) {
                                e.printStackTrace()
                                snackbarHostState.showSnackbar("저장 중 오류가 발생했습니다: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        if (currentHabit == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            ) {
                PageTitle(text = "습관 수정")

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val profileId = currentProfileId ?: return@launch
                                val habitToUpdate = currentHabit ?: return@launch
                                
                                val now = System.currentTimeMillis()
                                val lastSuccess = habitToUpdate.lastSuccessDate
                                
                                val isSameDay = if (lastSuccess != null) {
                                    val cal1 = Calendar.getInstance().apply { timeInMillis = lastSuccess }
                                    val cal2 = Calendar.getInstance().apply { timeInMillis = now }
                                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
                                } else {
                                    false
                                }

                                if (isSameDay) {
                                    snackbarHostState.showSnackbar("다음 날 자정 이후에 성공버튼을 다시 누를 수 있습니다.")
                                    return@launch
                                }

                                val updatedList = habitToUpdate.completeList.toMutableList()
                                if (updatedList.size < 7) { 
                                    updatedList.add(true)
                                } else {
                                    updatedList.removeAt(0)
                                    updatedList.add(true)
                                }
                                
                                val successCount = updatedList.count { it == true }
                                val target = habitToUpdate.targetCount
                                
                                val newRate = if (target > 0) {
                                    (successCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
                                } else 0f

                                val updatedHabit = habitToUpdate.copy(
                                    completeList = updatedList,
                                    achievementRate = newRate,
                                    lastSuccessDate = now
                                )
                                
                                currentHabit = updatedHabit 
                                repository.updateHabit(profileId, updatedHabit)
                                snackbarHostState.showSnackbar("오늘 습관 달성 완료!")
                            } catch (e: Exception) {
                                e.printStackTrace()
                                snackbarHostState.showSnackbar("오류 발생: ${e.message}")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A86F7)),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("오늘 습관 성공!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

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

                StartDateEdit(
                    selectedDate = startDate,
                    onDateSelected = { startDate = it }
                )

                Spacer(modifier = Modifier.height(25.dp))

                ActivationSwitch(
                    isChecked = isActive,
                    onCheckedChange = { isActive = it }
                )
            }
        }
    }
}

// ... (하위 컴포저블은 변경 없음, 생략 가능하지만 파일 전체를 덮어쓰므로 포함)
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
            text = "목표 횟수 수정",
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
                    text = "예: 30회",
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
            text="주기 변경",
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

                // 투명한 Box를 위에 덮어서 클릭 이벤트 가로채기
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
fun StartDateEdit(
    selectedDate: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

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
                enabled = false, 
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = Color.Gray
                )
            )
            
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDialog = true }
            )
        }

        if (showDialog) {
            DatePickerModalInput2(
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
fun DatePickerModalInput2(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)

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
fun ActivationSwitch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp), 
        shape = RoundedCornerShape(15.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "활성화 여부",
                fontFamily = PretendardFamily,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.scale(0.8f),

                )
        }
    }

}

@Composable
fun FilledEditButton(
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
            text = "저장",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}
