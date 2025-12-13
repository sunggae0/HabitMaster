package com.example.habitmaster.feature.habitEdit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmaster.core.designsystem.PretendardFamily
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.draw.scale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HabitEditScreen(onFinish: () -> Unit) {

    //TODO: ui 구현

    var habitName by remember { mutableStateOf("") }
    var targetCount by remember { mutableStateOf("") }

    var periodValue by remember { mutableStateOf("1") }
    var periodUnit by remember { mutableStateOf("일마다") }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var isActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(onBackClick = onFinish)
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start= 24.dp, end= 24.dp, bottom = 20.dp)
            ) {
                FilledEditButton(
                    onClick = { /* TODO */ },
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
            PageTitle(text = "습관 수정")

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
                onDateSelected = {startDate = it}
            )

            Spacer(modifier = Modifier.height(25.dp))

            ActivationSwitch(
                isChecked = isActive,
                onCheckedChange = {isActive= it}
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
                    enabled = false, // 직접 입력 못하게
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .clickable { expanded = true }, // 클릭되게
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
                    shape = RoundedCornerShape(15.dp)
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

        OutlinedTextField(
            value = formattedDate,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .clickable { showDialog = true },
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
            readOnly = true
        )

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
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

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

