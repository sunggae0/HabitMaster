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
import java.util.Date
import java.util.Locale

@Composable
fun HabitEditScreen(
    habitId: String,
    onFinish: () -> Unit
) {
    val repository = remember { FirebaseProfileRepository() }
    val scope = rememberCoroutineScope()
    
    // 현재 프로필 및 습관 데이터 로드
    var currentProfileId by remember { mutableStateOf<String?>(null) }
    var currentHabit by remember { mutableStateOf<Habit?>(null) }
    
    // UI 상태
    var habitName by remember { mutableStateOf("") }
    var targetCount by remember { mutableStateOf("") }
    var periodValue by remember { mutableStateOf("1") }
    var periodUnit by remember { mutableStateOf("일마다") }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var isActive by remember { mutableStateOf(true) } // 활성화 여부 (삭제 대신 비활성화라면)

    LaunchedEffect(Unit) {
        repository.observeProfiles().collect { profiles ->
            if (profiles.isNotEmpty()) {
                val profile = profiles.first()
                currentProfileId = profile.id
                
                // 습관 찾기
                val foundHabit = profile.habits.find { it.id == habitId }
                if (foundHabit != null) {
                    currentHabit = foundHabit
                    // 초기값 세팅
                    habitName = foundHabit.title
                    targetCount = foundHabit.targetCount.toString()
                    periodValue = foundHabit.periodValue.toString()
                    periodUnit = foundHabit.periodUnit
                    startDate = foundHabit.startDate
                    isActive = foundHabit.isActive
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(onBackClick = onFinish)
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
                            onFinish()
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

                // 습관 성공 버튼 (오늘 날짜 기준 체크)
                // 실제로는 날짜 계산 로직이 더 복잡할 수 있음 (오늘이 몇 번째 인덱스인지 등)
                // 여기서는 간단히 "오늘 성공" 버튼을 추가하고, 누르면 achievementRate 등을 업데이트하는 예시
                Button(
                    onClick = {
                        scope.launch {
                            val profileId = currentProfileId ?: return@launch
                            val habitToUpdate = currentHabit ?: return@launch
                            
                            // 오늘에 해당하는 completeList 인덱스를 찾거나 추가해야 함. (임시로 마지막에 추가)
                            val updatedList = habitToUpdate.completeList.toMutableList()
                            if (updatedList.size < 7) { // 7일 제한
                                updatedList.add(true)
                            }
                            
                            // 달성률 계산 (성공 횟수 / 전체 시도 가능 횟수)
                            val successCount = updatedList.count { it == true }
                            val newRate = if (updatedList.isNotEmpty()) {
                                successCount.toFloat() / updatedList.size.toFloat()
                            } else 0f

                            val updatedHabit = habitToUpdate.copy(
                                completeList = updatedList,
                                achievementRate = newRate
                            )
                            
                            currentHabit = updatedHabit // UI 즉시 반영을 위해
                            repository.updateHabit(profileId, updatedHabit)
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

// ... (나머지 Composable 함수들은 기존과 동일하게 유지하거나 필요시 수정)
// 하단에 기존 Composable들 복사
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

        // Read-only TextField with DatePicker dialog trigger
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
            .height(55.dp), // OutlinedTextField와 동일한 높이
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
