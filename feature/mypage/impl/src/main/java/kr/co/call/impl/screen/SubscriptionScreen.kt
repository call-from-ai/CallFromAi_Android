package kr.co.call.impl.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.call.designsystem.component.button.SecondaryButton
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.component.CommonTopAppBar
import kr.co.call.impl.component.SubscriptionPlanCard
import kr.co.call.impl.model.SubscriptionPlanMock
import kr.co.call.impl.viewmodel.SubscriptionIntent
import kr.co.call.impl.viewmodel.SubscriptionSideEffect
import kr.co.call.impl.viewmodel.SubscriptionState
import kr.co.call.impl.viewmodel.SubscriptionViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SubscriptionScreen(
    modifier: Modifier,
    onBackClick: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    var showComingSoon by remember { mutableStateOf(false) }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SubscriptionSideEffect.NavigateBack -> onBackClick()
            is SubscriptionSideEffect.ShowMessage -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
            is SubscriptionSideEffect.ConfirmPlan -> {
                showComingSoon = true
            }
        }
    }

    SubscriptionScreenContent(
        modifier = modifier,
        state = state,
        onIntent = viewModel::handleIntent,
        onBackClick = onBackClick,
    )

    if (showComingSoon) {
        ComingSoonScreen(
            modifier = Modifier,
            onBackClick = { showComingSoon = false },
        )
    }
}

@Composable
private fun SubscriptionScreenContent(
    modifier: Modifier,
    state: SubscriptionState,
    onIntent: (SubscriptionIntent) -> Unit,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.background)
            .statusBarsPadding(),
    ) {
        // 상단 앱바
        CommonTopAppBar(
            title = "구독",
            onBackClick = onBackClick,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "구독 유형을\n선택해 주세요.",
                style = CallTheme.typography.titleSmallBold,
                color = CallTheme.colors.black,
            )

            Spacer(modifier = Modifier.height(25.dp))

            // 구독 플랜 카드(무료/베이직/프리미엄)
            Column(verticalArrangement = Arrangement.spacedBy(25.dp)) {
                state.plans.forEach { plan ->
                    SubscriptionPlanCard(
                        plan = plan,
                        isSelected = plan.id == state.selectedPlanId,
                        isCurrent = plan.id == state.currentPlanId,
                        onClick = { onIntent(SubscriptionIntent.SelectPlan(plan.id)) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 하단 '선택하기' 버튼
        SecondaryButton(
            text = "선택하기",
            onClick = { onIntent(SubscriptionIntent.ClickConfirm) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SubscriptionScreenPreview() {
    CallFromAiTheme {
        SubscriptionScreenContent(
            modifier = Modifier,
            state = SubscriptionState(
                plans = SubscriptionPlanMock.plans,
                currentPlanId = SubscriptionPlanMock.DEFAULT_CURRENT_PLAN_ID,
                selectedPlanId = SubscriptionPlanMock.DEFAULT_CURRENT_PLAN_ID,
            ),
            onIntent = {},
            onBackClick = {},
        )
    }
}
