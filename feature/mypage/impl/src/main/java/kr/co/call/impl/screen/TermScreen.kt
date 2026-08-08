package kr.co.call.impl.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.login.AgreementTerm
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.CommonTopAppBar
import kr.co.call.impl.component.TermMarkdownText
import kr.co.call.impl.viewmodel.TermIntent
import kr.co.call.impl.viewmodel.TermSideEffect
import kr.co.call.impl.viewmodel.TermState
import kr.co.call.impl.viewmodel.TermViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun TermScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    viewModel: TermViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is TermSideEffect.ShowMessage -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    TermScreenContent(
        state = state,
        onBackClick = onBackClick,
        onRetry = { viewModel.handleIntent(TermIntent.Retry) },
        modifier = modifier,
    )
}

@Composable
private fun TermScreenContent(
    state: TermState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.background)
            .statusBarsPadding(),
    ) {
        // 상단 앱바
        CommonTopAppBar(
            onBackClick = onBackClick,
            title = "약관/개인정보 처리 방침",
        )

        when (val loadStatus = state.loadStatus) {
            LoadStatus.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = CallTheme.colors.mainVariant1)
                }
            }

            is LoadStatus.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = loadStatus.message,
                            style = CallTheme.typography.bodyMedium,
                            color = CallTheme.colors.gray400,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "다시 시도",
                            style = CallTheme.typography.bodySmallBold,
                            color = CallTheme.colors.mainVariant1,
                            modifier = Modifier
                                .clickable(onClick = onRetry)
                                .padding(8.dp),
                        )
                    }
                }
            }

            LoadStatus.Idle -> {
                if (state.terms.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "표시할 약관이 없습니다.",
                            style = CallTheme.typography.bodyMedium,
                            color = CallTheme.colors.gray400,
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 40.dp),
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        state.terms.forEachIndexed { index, term ->
                            TermItem(term = term)
                            if (index != state.terms.lastIndex) {
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(29.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TermItem(
    term: AgreementTerm,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // content에 # 제목이 있으면 마크다운만, 없으면 title + 본문 (동일 bodySmall 스타일)
        val contentHasHeading = term.content.lineSequence()
            .map { it.trim() }
            .any { it.startsWith("#") }

        if (!contentHasHeading && term.title.isNotBlank()) {
            Text(
                text = term.title,
                style = CallTheme.typography.bodySmall,
                color = CallTheme.colors.gray400,
            )
            Spacer(modifier = Modifier.height(22.dp))
        }

        TermMarkdownText(markdown = term.content)
    }
}

@Preview(showBackground = true)
@Composable
private fun TermScreenPreview() {
    CallFromAiTheme {
        TermScreenContent(
            state = TermState(
                terms = listOf(
                    AgreementTerm(
                        termId = 1L,
                        title = "전화왔어 서비스 이용약관",
                        content = """
                            # 전화왔어 서비스 이용약관

                            ## 제1조 목적
                            본 약관은 서비스의 이용조건 및 절차를 규정합니다.

                            ## 제2조 용어의 정의
                            1. 서비스란 AI 캐릭터와 대화할 수 있는 기능을 말합니다.
                            2. 회원이란 본 약관에 동의한 이용자를 말합니다.
                        """.trimIndent(),
                        isRequired = true,
                    ),
                    AgreementTerm(
                        termId = 2L,
                        title = "개인정보 처리방침",
                        content = """
                            # 개인정보 처리방침

                            ## 수집 항목
                            수집하는 개인정보 항목과 이용 목적을 안내합니다.
                        """.trimIndent(),
                        isRequired = true,
                    ),
                ),
                loadStatus = LoadStatus.Idle,
            ),
            onBackClick = {},
            onRetry = {},
        )
    }
}
