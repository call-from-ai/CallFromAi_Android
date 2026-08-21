package kr.co.call.impl.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kr.co.call.designsystem.R
import kr.co.call.designsystem.component.bottomsheet.ProfileImagePickerBottomSheet
import kr.co.call.designsystem.component.button.SecondaryButton
import kr.co.call.designsystem.component.popup.OneButtonPopup
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.AgeInputField
import kr.co.call.impl.component.CommonTopAppBar
import kr.co.call.impl.component.EditProfileNameFields
import kr.co.call.impl.component.KeywordChoice
import kr.co.call.impl.component.MemberChoice
import kr.co.call.impl.component.TempControl
import kr.co.call.impl.viewmodel.EditCharacterIntent
import kr.co.call.impl.viewmodel.EditCharacterJob
import kr.co.call.impl.viewmodel.EditCharacterSideEffect
import kr.co.call.impl.viewmodel.EditCharacterState
import kr.co.call.impl.viewmodel.EditCharacterViewModel
import kr.co.call.impl.viewmodel.model.Mbti
import kr.co.call.impl.viewmodel.model.Trait
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

private val traitRows = listOf(
    listOf(Trait.HUMOROUS, Trait.PLAYFUL),
    listOf(Trait.AFFECTIONATE, Trait.JEALOUS, Trait.TALKATIVE),
    listOf(Trait.DAD_JOKE_LOVER, Trait.HOMEBODY),
    listOf(Trait.TEASING, Trait.POSSESSIVE, Trait.TSUNDERE),
    listOf(Trait.EXPRESSIVE, Trait.PET_NAME_LOVER),
    listOf(Trait.EXCLUSIVE, Trait.QUIRKY, Trait.LAID_BACK),
    listOf(Trait.OPENLY_JEALOUS, Trait.SHY),
    listOf(Trait.SMOOTH_TALKER, Trait.FREQUENT_CHECKER),
    listOf(Trait.GOOD_LISTENER, Trait.COMPLIMENTER),
)

@Composable
fun EditCharacterScreen(
    characterId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditCharacterViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    var showEditLimit by remember { mutableStateOf(false) }

    LaunchedEffect(characterId) {
        viewModel.initialize(characterId)
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is EditCharacterSideEffect.NavigateBack -> onBackClick()
            is EditCharacterSideEffect.ShowMessage -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
            is EditCharacterSideEffect.ShowEditLimitExceeded -> {
                showEditLimit = true
            }
        }
    }

    EditCharacterScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
        modifier = modifier,
    )

    if (showEditLimit) {
        OneButtonPopup(
            label = "이상형 정보 수정",
            title = "정보 수정 횟수를 초과했어요.",
            description = androidx.compose.ui.text.buildAnnotatedString {
                append("이상형 정보 수정은 최대 1회만 가능합니다.")
            },
            buttonText = "확인",
            onButtonClick = {
                showEditLimit = false
                onBackClick()
            },
            onDismissRequest = {
                showEditLimit = false
                onBackClick()
            },
        )
    }
}

@Composable
private fun EditCharacterScreenContent(
    state: EditCharacterState,
    onIntent: (EditCharacterIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 상단 앱바
            CommonTopAppBar(
                title = "이상형 정보 수정",
                onBackClick = { onIntent(EditCharacterIntent.ClickBack) },
            )

            when (val load = state.loadStatus) {
                is LoadStatus.Loading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = CallTheme.colors.mainVariant1)
                    }
                }
                is LoadStatus.Error -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = load.message,
                            style = CallTheme.typography.bodyMedium,
                            color = CallTheme.colors.gray600,
                        )
                    }
                }
                LoadStatus.Idle -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // 기본 정보
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Spacer(modifier = Modifier.height(24.dp))
                            // 캐릭터 정보 (프로필+이름)
                            EditCharacterHeader(
                                imageUrl = state.imageUrl,
                                name = state.displayName,
                                onEditPhotoClick = { onIntent(EditCharacterIntent.ClickEditPhoto) },
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            // 이름 입력 필드
                            EditProfileNameFields(
                                lastName = state.lastName,
                                firstName = state.firstName,
                                onLastNameChange = { onIntent(EditCharacterIntent.ChangeLastName(it)) },
                                onFirstNameChange = { onIntent(EditCharacterIntent.ChangeFirstName(it)) },
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            // 나이 입력 필드
                            AgeInputField(
                                age = state.age,
                                onAgeChange = { onIntent(EditCharacterIntent.ChangeAge(it)) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            // 직업 입력 필드
                            MemberChoice(
                                modifier = Modifier.fillMaxWidth(),
                                label = "직업",
                                selectedOption = state.jobLabel,
                                placeholder = "직업을 선택해주세요",
                                options = EditCharacterJob.entries.map { it.label },
                                onOptionSelected = { onIntent(EditCharacterIntent.SelectJob(it)) },
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            // mbti 입력 필드
                            MemberChoice(
                                modifier = Modifier.fillMaxWidth(),
                                label = "MBTI",
                                selectedOption = state.mbti,
                                placeholder = "MBTI를 선택해주세요",
                                options = Mbti.entries.map { it.name },
                                onOptionSelected = { onIntent(EditCharacterIntent.SelectMbti(it)) },
                            )
                            Spacer(modifier = Modifier.height(28.dp))
                        }

                        EditCharacterSectionDivider()

                        // 연애 온도
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        ) {
                            Spacer(modifier = Modifier.height(28.dp))
                            TempControl(
                                initialTemperature = state.spiceLevel,
                                onTemperatureChange = {
                                    onIntent(EditCharacterIntent.ChangeSpiceLevel(it))
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(modifier = Modifier.height(28.dp))
                        }

                        // 연애 온도 / 매력 키워드 구분
                        EditCharacterSectionDivider()

                        // 매력 키워드
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        ) {
                            Spacer(modifier = Modifier.height(28.dp))
                            traitRows.forEach { rowTraits ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                ) {
                                    rowTraits.forEach { trait ->
                                        val selectedOrder = state.selectedTraitKeywords
                                            .indexOf(trait.keyword)
                                            .takeIf { it >= 0 }
                                            ?.plus(1)
                                        KeywordChoice(
                                            emoji = trait.emoji,
                                            text = trait.label,
                                            selectedOrder = selectedOrder,
                                            onClick = {
                                                onIntent(EditCharacterIntent.ToggleTrait(trait.keyword))
                                            },
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(23.dp))

                            Text(
                                text = "이상형 정보 수정은 최초 1회만 가능해요!",
                                style = CallTheme.typography.caption,
                                color = CallTheme.colors.gray400,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 8.dp, bottom = 12.dp),
                            )
                        }
                    }
                    // 확인 버튼
                    SecondaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 18.dp),
                        text = if (state.isSaving) "저장 중..." else "완료",
                        enabled = state.canComplete,
                        onClick = { onIntent(EditCharacterIntent.ClickComplete) },
                    )
                }
            }
        }

        if (state.showImagePicker) {
            ProfileImagePickerBottomSheet(
                images = state.pickerImages,
                selectedGender = state.pickerGender,
                selectedImageId = state.selectedImageId,
                isLoading = state.isPickerLoading,
                onGenderChange = { onIntent(EditCharacterIntent.ChangePickerGender(it)) },
                onImageSelected = { onIntent(EditCharacterIntent.SelectPickerImage(it)) },
                onSaveClick = { onIntent(EditCharacterIntent.ConfirmPickerImage) },
                onDismissRequest = { onIntent(EditCharacterIntent.DismissImagePicker) },
            )
        }
    }
}

// 섹션 구분 바
@Composable
private fun EditCharacterSectionDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(7.dp)
            .background(CallTheme.colors.gray100),
    )
}

// 기본 프로필 (프로필 사진+이름)
@Composable
private fun EditCharacterHeader(
    imageUrl: String,
    name: String,
    onEditPhotoClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 프로필 사진
        Box(modifier = Modifier.size(140.dp)) {
            AsyncImage(
                model = imageUrl.takeIf { it.isNotBlank() },
                contentDescription = "이상형 프로필",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape),
                placeholder = painterResource(id = R.drawable.img_mypage_profile_default),
                error = painterResource(id = R.drawable.img_mypage_profile_default),
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_mypage_edit),
                contentDescription = "프로필 사진 수정",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .clickable(onClick = onEditPhotoClick),
                tint = Color.Unspecified,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // 캐릭터 이름
        Text(
            text = name.ifBlank { " " },
            style = CallTheme.typography.titleSmallBold,
            color = CallTheme.colors.black,
        )
    }
}

@Preview(showBackground = true, heightDp = 1550)
@Composable
private fun EditCharacterScreenPreview() {
    CallFromAiTheme {
        EditCharacterScreenContent(
            state = EditCharacterState(
                lastName = "김",
                firstName = "민준",
                age = "25",
                jobLabel = "직장인",
                mbti = "ENFJ",
                spiceLevel = 80,
                selectedTraitKeywords = listOf("AFFECTIONATE", "HUMOROUS"),
            ),
            onIntent = {},
        )
    }
}
