package io.github.youhwanjung.trailog.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.youhwanjung.trailog.domain.model.Trip
import io.github.youhwanjung.trailog.ui.theme.TrailogTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onTripClick: (tripId: Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onTripClick = onTripClick,
        onLogoutClick = viewModel::logout,
        modifier = modifier,
    )
}

/**
 * 기록 목록.
 *
 * 로그아웃 버튼이 화면 상태를 직접 바꾸지 않는다는 점이 이전과 다르다.
 * viewModel.logout() -> AuthRepository.isLoggedIn 이 false -> AppViewModel 이 로그인 화면으로 교체.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onTripClick: (tripId: Long) -> Unit = {},
    onLogoutClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("기록") },
                actions = {
                    TextButton(onClick = onLogoutClick) { Text("로그아웃") }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                uiState.trips.isEmpty() -> {
                    Text(
                        text = "아직 기록이 없습니다.\n첫 여행을 남겨보세요.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(items = uiState.trips, key = { it.id }) { trip ->
                            TripRow(trip = trip, onClick = { onTripClick(trip.id) })
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TripRow(
    trip: Trip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = trip.title,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "${trip.place} · ${trip.startDate.format(DATE_FORMATTER)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TrailogTheme {
        HomeScreen(
            uiState = HomeUiState(
                isLoading = false,
                trips = listOf(
                    Trip(
                        id = 1L,
                        title = "제주 한달살기",
                        place = "제주특별자치도",
                        startDate = LocalDate.of(2026, 4, 1),
                        endDate = LocalDate.of(2026, 4, 30),
                    ),
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() {
    TrailogTheme {
        HomeScreen(uiState = HomeUiState(isLoading = false))
    }
}
