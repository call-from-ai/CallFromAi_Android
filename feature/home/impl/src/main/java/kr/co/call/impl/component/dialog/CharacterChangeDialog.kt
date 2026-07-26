package kr.co.call.impl.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.viewmodel.model.CharacterOptionUiModel

/**
 * 메인 연인 교체하기 다이얼로그
 * @param characters 메인 연인 리스트
 * @param onCharacterClick 메인 연인 선택 시 이벤트
 */
@Composable
fun CharacterChangeDialog(
    characters: List<CharacterOptionUiModel>,
    onCharacterClick: (characterId: Long, nickname: String) -> Unit,
    onAddCharacterClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier
                .width(321.dp)
                .shadow(
                    elevation = 15.dp,
                    shape = RoundedCornerShape(30.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.08f),
                )
                .background(
                    color = CallTheme.colors.background,
                    shape = RoundedCornerShape(30.dp),
                )
                .padding(top = 26.dp, bottom = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "메인 연인 교체하기",
                style = CallTheme.typography.bodyMediumMedium,
                color = CallTheme.colors.mainVariant1,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(13.dp))
            Text(
                text = "아래 중에 교체하실\n연인을 선택해주세요.",
                style = CallTheme.typography.bodyLargeBold,
                color = CallTheme.colors.black,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                characters.forEach { character ->
                    CharacterOption(
                        character = character,
                        onClick = {
                            onCharacterClick(character.characterId, character.name)
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            val addCharacterInteractionSource = remember { MutableInteractionSource() }
            val isAddCharacterPressed by
                addCharacterInteractionSource.collectIsPressedAsState()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(
                        color = if (isAddCharacterPressed) {
                            CallTheme.colors.subGray
                        } else {
                            CallTheme.colors.background
                        },
                        shape = RoundedCornerShape(percent = 50),
                    )
                    .clickable(
                        interactionSource = addCharacterInteractionSource,
                        indication = ripple(),
                        onClick = onAddCharacterClick,
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(CallTheme.colors.mainVariant1, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "+",
                        color = CallTheme.colors.white,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                    )
                }
                Spacer(modifier = Modifier.width(11.dp))
                Text(
                    text = "캐릭터 추가하기",
                    style = CallTheme.typography.bodySmallBold,
                    color = CallTheme.colors.black,
                )
            }
        }
    }
}

@Composable
private fun CharacterOption(
    character: CharacterOptionUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(82.dp)
            .background(
                color = if (isPressed) {
                    CallTheme.colors.mainVariant2
                } else {
                    CallTheme.colors.background
                },
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick,
            )
            .padding(start = 54.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val defaultProfile = painterResource(
            id = R.drawable.img_profile_url_default,
        )
        AsyncImage(
            model = character.imageUrl?.takeIf { it.isNotBlank() },
            contentDescription = "${character.name} 프로필 이미지",
            modifier = Modifier
                .size(width = 57.dp, height = 60.dp)
                .clip(RoundedCornerShape(20.dp)),
            placeholder = defaultProfile,
            error = defaultProfile,
            fallback = defaultProfile,
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(17.dp))
        Text(
            text = character.name,
            style = CallTheme.typography.bodyMediumMedium,
            color = CallTheme.colors.black,
        )
        Spacer(modifier = Modifier.width(11.dp))
        Text(
            text = "D+ ${character.day}",
            modifier = Modifier
                .background(
                    color = CallTheme.colors.mainVariant1,
                    shape = RoundedCornerShape(5.dp),
                )
                .padding(horizontal = 6.dp),
            style = CallTheme.typography.captionBold,
            color = CallTheme.colors.white,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterChangeDialogPreview() {
    CallFromAiTheme {
        CharacterChangeDialog(
            characters = listOf(
                CharacterOptionUiModel(characterId = 1L, name = "민준", day = 35),
                CharacterOptionUiModel(characterId = 2L, name = "동휘", day = 11),
                CharacterOptionUiModel(characterId = 3L, name = "유나", day = 5),
            ),
            onCharacterClick = { _, _ -> },
            onAddCharacterClick = {},
            onDismissRequest = {},
        )
    }
}
