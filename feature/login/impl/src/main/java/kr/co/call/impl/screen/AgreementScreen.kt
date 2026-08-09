package kr.co.call.impl.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.call.designsystem.component.popup.TwoButtonPopup
import kr.co.call.designsystem.theme.Black
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme.typography
import kr.co.call.designsystem.theme.Gray100
import kr.co.call.designsystem.theme.Gray600
import kr.co.call.designsystem.theme.Gray900
import kr.co.call.designsystem.theme.MainVariant1
import kr.co.call.designsystem.theme.White
import kr.co.call.domain.model.login.AgreementTerm
import kr.co.call.impl.component.AgreementItem
import kr.co.call.impl.component.CheckBox
import kr.co.call.impl.component.NextButton
import kr.co.call.impl.viewmodel.AgreementSideEffect
import kr.co.call.impl.viewmodel.AgreementViewModel
import kr.co.call.impl.viewmodel.state.AgreementUiState
import kr.co.call.login.impl.R
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

@Composable
fun AgreementScreen(
    onNavigateNext: () -> Unit,
    onAgreementViewClick: (AgreementTerm) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AgreementViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value

    var isPopupDismissed by rememberSaveable {
        mutableStateOf(false)
    }

    val showNotificationPermissionPopup =
        !isPopupDismissed &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) {
        isPopupDismissed = true
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            AgreementSideEffect.NavigateToNext -> onNavigateNext()
            is AgreementSideEffect.ShowError -> {
                Toast.makeText(
                    context,
                    sideEffect.message,
                    Toast.LENGTH_SHORT,
                ).show()
                Timber.e(sideEffect.message)
            }
        }
    }

    AgreementContent(
        modifier = modifier,
        uiState = uiState,
        onNextClick = viewModel::submitAgreements,
        onAgreementViewClick = onAgreementViewClick,
        onAgreementToggle = viewModel::toggleAgreement,
        onAllAgreementsCheckedChange = viewModel::toggleAllAgreements,
    )

    if (showNotificationPermissionPopup) {
        TwoButtonPopup(
            label = "",
            title = "‘전화왔어’에서 알림을\n보내고자 합니다.",
            description = AnnotatedString(
                "경고, 사운드 및 아이콘 배지가 알림에\n" +
                    "포함될 수 있습니다.\n" +
                    "설정에서 이를 구성할 수 있습니다.",
            ),
            positiveText = "허용",
            negativeText = "허용 안 함",
            onPositiveClick = {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS,
                )
            },
            onNegativeClick = {
                isPopupDismissed = true
            },
            onDismissRequest = {
                isPopupDismissed = true
            },
        )
    }
}

@Composable
private fun AgreementContent(
    modifier: Modifier = Modifier,
    uiState: AgreementUiState,
    onNextClick:()->Unit,
    onAgreementViewClick:(AgreementTerm)->Unit,
    onAgreementToggle: (Long)->Unit,
    onAllAgreementsCheckedChange: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 23.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(136.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "로고",
                    modifier = Modifier.size(70.dp),
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "전화왔어",
                    color = MainVariant1,
                    style = typography.titleExtraLargeBold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "전화왔어를 이용하려면\n약관에 동의가 필요해요",
                color = Gray900,
                style = typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(80.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                color = White,
                border = BorderStroke(
                    width = 1.dp,
                    color = Gray100,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 30.dp,
                    ),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CheckBox(
                            checked = uiState.isAllChecked,
                            onCheckedChange = onAllAgreementsCheckedChange,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "모두 동의",
                                color = Black,
                                style = typography.bodyMediumMedium
                            )
                            Text(
                                text = "서비스 이용을 위한 아래 약관에 모두 동의합니다",
                                color = Gray600,
                                style = typography.bodySmall
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Gray100,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    uiState.terms.forEach { term->
                        AgreementItem(
                            title = term.title,
                            isRequired = term.isRequired,
                            isChecked = term.termId in uiState.checkedTermIds,
                            onCheckedChange = {
                                onAgreementToggle(term.termId)
                            },
                            onViewClick = {
                                onAgreementViewClick(term)
                            },
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .navigationBarsPadding()
                .padding(
                    start = 23.dp,
                    end = 23.dp,
                    bottom = 10.dp,
                ),
        ) {
            NextButton(
                modifier=Modifier
                    .fillMaxWidth(),
                text="동의하고 프로필 설정하기",
                onClick = onNextClick,
                enabled = uiState.isRequiredChecked,
            )
        }
    }
}

@Preview(
    name = "Agreement Screen Preview",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun AgreementScreenPreview() {
    CallFromAiTheme {
        AgreementContent(
            modifier = Modifier,
            uiState = AgreementUiState(
                terms = listOf(
                    AgreementTerm(
                        termId = 1L,
                        title = "서비스 이용약관 동의",
                        content = "# 서비스 이용약관",
                        isRequired = true,
                    ),
                    AgreementTerm(
                        termId = 2L,
                        title = "개인정보 수집/이용 동의",
                        content = "# 개인정보 수집 및 이용",
                        isRequired = true,
                    ),
                    AgreementTerm(
                        termId = 3L,
                        title = "마케팅 정보 수신 동의",
                        content = "# 마케팅 정보 수신",
                        isRequired = false,
                    ),
                ),
            ),
            onAgreementToggle = {},
            onAllAgreementsCheckedChange = {},
            onAgreementViewClick = {},
            onNextClick = {},
        )
    }
}
