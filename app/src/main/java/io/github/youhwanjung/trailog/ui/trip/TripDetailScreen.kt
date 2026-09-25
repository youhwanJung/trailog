package io.github.youhwanjung.trailog.ui.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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

/**
 * tripId 를 ViewModel 생성 시점에 넘겨야 하므로 hiltViewModel 에 creationCallback 을 준다.
 *
 * 이 Route 가 매번 새 ViewModel 을 받는 것은 NavDisplay 에 걸어둔
 * rememberViewModelStoreNavEntryDecorator 덕분이다(TrailogApp 참고).
 * 그게 없으면 기록 1번을 보고 나온 뒤 2번을 열어도 1번 ViewModel 이 재사용된다.
 */
@Composable
fun TripDetailRoute(
    tripId: Long,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    viewModel: TripDetailViewModel = hiltViewModel<TripDetailViewModel, TripDetailViewModel.Factory>(
        creationCallback = { factory -> factory.create(tripId) },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TripDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

/**
 * 기록 상세.
 *
 * 네비게이션도 ViewModel 도 모른다. Preview 와 테스트에서 단독으로 띄울 수 있다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    uiState: TripDetailUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(uiState.trip?.title ?: "") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) { Text("뒤로") }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            val trip = uiState.trip
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                trip == null -> {
                    Text(
                        text = "기록을 찾을 수 없습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = trip.place,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "${trip.startDate.format(DATE_FORMATTER)} – " +
                                trip.endDate.format(DATE_FORMATTER),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (trip.memo.isNotBlank()) {
                            Text(
                                text = trip.memo,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

@Preview(showBackground = true)
@Composable
private fun TripDetailScreenPreview() {
    TrailogTheme {
        TripDetailScreen(
            uiState = TripDetailUiState(
                isLoading = false,
                trip = Trip(
                    id = 1L,
                    title = "제주 한달살기",
                    place = "제주특별자치도",
                    startDate = LocalDate.of(2026, 4, 1),
                    endDate = LocalDate.of(2026, 4, 30),
                    memo = "우도에서 본 일출",
                ),
            ),
        )
    }
}
