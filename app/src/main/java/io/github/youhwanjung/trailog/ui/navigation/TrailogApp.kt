package io.github.youhwanjung.trailog.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.youhwanjung.trailog.ui.home.HomeRoute
import io.github.youhwanjung.trailog.ui.login.LoginRoute
import io.github.youhwanjung.trailog.ui.trip.TripDetailRoute

/**
 * 앱 최상위 Composable, 네비게이션도 정의해준다.
 *
 * 로그인 여부를 여기서 판단해서, 로그인 화면과 메인 백스택을 갈아끼운다.
 * 로그인 화면을 백스택에 넣지 않기 때문에
 * - 로그인 성공 후 뒤로가기로 로그인 화면에 돌아갈 일이 없고
 * - 로그아웃은 인증 상태만 되돌리면 되며 스택 정리가 필요 없다.
 *
 * 이전과 달라진 점: isLoggedIn 이 rememberSaveable 지역 상태가 아니라
 * AuthRepository 에서 흘러온다. 그래서 로그인/로그아웃/토큰 만료가
 * 어디서 일어나든 같은 경로로 화면에 반영된다.
 */
@Composable
/** 로그인 상태에 따라 무엇을 보여줄지 결정. */
fun TrailogApp(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        AppUiState.Loading -> {
            Box(modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }

        AppUiState.LoggedOut -> {
            LoginRoute(
                modifier = modifier,
                onSignUpClick = { /* TODO: 회원가입 플로우 */ },
            )
        }

        AppUiState.LoggedIn -> {
            MainNavDisplay(modifier = modifier)
        }
    }
}

@Composable
/** 로그인 이후의 네비게이션을 담당하게된다. */
private fun MainNavDisplay(modifier: Modifier = Modifier) {
    // 백스택은 그냥 리스트다. add/remove 로 화면을 이동한다.
    val backStack = rememberNavBackStack(Route.Home)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        // 데코레이터를 직접 지정하면 기본값을 덮어쓰므로, 기본 데코레이터도 같이 나열해야 한다.
        // ViewModelStore 데코레이터가 NavEntry 마다 ViewModelStore 를 만들어주고,
        // NavEntry.contentKey 로 구분하므로 TripDetail(1) 과 TripDetail(2) 가
        // 서로 다른 ViewModel 을 받는다. 화면이 백스택에서 빠질 때 onCleared 도 불린다.
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Route.Home> {
                HomeRoute(
                    onTripClick = { tripId -> backStack.add(Route.TripDetail(tripId)) },
                )
            }
            entry<Route.TripDetail> { key ->
                TripDetailRoute(
                    tripId = key.tripId,
                    onBackClick = dropUnlessResumed { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
