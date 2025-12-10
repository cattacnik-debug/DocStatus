package com.example.docstatus.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.docstatus.data.local.VerificationEntity
import com.example.docstatus.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composable-функция, которая отображает историю верификаций документов.
 *
 * Этот экран содержит верхнюю панель с кнопкой "назад" и прокручиваемый список
 * записей о верификациях. Каждая запись отображается в виде карточки [HistoryItem].
 *
 * @param onBackClick Лямбда-функция, которая будет вызвана при нажатии на кнопку "назад".
 * @param historyViewModel Экземпляр [HistoryViewModel], который предоставляет список записей истории.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBackClick: () -> Unit, historyViewModel: HistoryViewModel = viewModel()) {
    val history by historyViewModel.history.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("История проверок") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = AppColors.Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(history) {
                HistoryItem(it)
            }
        }
    }
}

/**
 * Composable-функция, которая отображает один элемент в истории верификаций.
 *
 * Каждый элемент рендерится как `Card`, содержащая ID документа, детали верификации,
 * временную метку и цветовой индикатор статуса.
 *
 * @param item [VerificationEntity], который необходимо отобразить.
 */
@Composable
private fun HistoryItem(item: VerificationEntity) {
    val statusColor = when (item.status) {
        "VALID" -> AppColors.StatusGreen
        "WARNING" -> AppColors.StatusYellow
        else -> AppColors.StatusRed
    }
    val formatter = remember {
        SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = AppColors.LogoBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.docId, fontWeight = FontWeight.Bold, color = Color.White)
                Text(formatter.format(Date(item.timestampUnix)), fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.details, color = Color.LightGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Статус: ${item.status}", color = statusColor, fontWeight = FontWeight.Bold)
        }
    }
}
