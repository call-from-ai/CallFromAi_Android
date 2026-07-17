package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kr.co.call.designsystem.R
import kr.co.call.designsystem.component.button.SecondaryButton
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.mypage.AiCharacter

/** AI 캐릭터 관리 목록 아이템 카드 */
@Composable
fun CharacterCard(
    aiCharacter: AiCharacter,
    onChatHistoryClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(CallTheme.colors.white)
            .border(width = 1.dp, color = CallTheme.colors.gray200, shape =cardShape )
            .padding(horizontal = 20.dp)
            .padding(top = 26.dp, bottom = 21.dp),
    ) {
        Row{
            Box {
                // 프로필 이미지
                AsyncImage(
                    model = aiCharacter.profileImageUrl,
                    contentDescription = aiCharacter.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp).clip(CircleShape),
                    placeholder = painterResource(id = R.drawable.img_mypage_profile_default),
                    error = painterResource(id = R.drawable.img_mypage_profile_default),
                )
                // 프로필 편집하기 아이콘
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(23.dp)
                        .clip(CircleShape)
                        .background(CallTheme.colors.white),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mypage_edit),
                        contentDescription = "편집하기 버튼",
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.width(11.dp))

            Column{
                Row{
                    Text(text = aiCharacter.name, style = CallTheme.typography.bodyMedium, color = CallTheme.colors.black)

                    // 메인 캐릭터 뱃지
                    if (aiCharacter.isMain) {
                        Spacer(modifier = Modifier.width(7.dp))
                        MainBadge()
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 채팅 기록 보기 버튼
                    Text(
                        text = "채팅 기록 보기",
                        style = CallTheme.typography.caption,
                        color = CallTheme.colors.gray400,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable(onClick = onChatHistoryClick),
                    )
                }
                Spacer(modifier= Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoColumn(label = "생성일", value = aiCharacter.createdAtLabel, modifier = Modifier.weight(1f))
                    VerticalDivider(
                        thickness = 1.dp,
                        color = CallTheme.colors.gray100,
                        modifier = Modifier.height(30.dp)
                    )
                    InfoColumn(label = "함께한 지", value = aiCharacter.daysTogetherLabel, modifier = Modifier.weight(1f))
                    VerticalDivider(
                        thickness = 1.dp,
                        color = CallTheme.colors.gray100,
                        modifier = Modifier.height(30.dp)
                    )
                    InfoColumn(label = "마지막 대화", value = aiCharacter.lastConversationLabel, modifier = Modifier.weight(1f))
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        SecondaryButton(
            text = "캐릭터 영구 삭제",
            onClick = onDeleteClick,
            iconRes = R.drawable.ic_mypage_trash,
            containerColor = CallTheme.colors.mainVariant1,
            modifier = Modifier.height(38.dp)
        )

    }
}

@Composable
private fun InfoColumn(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = CallTheme.typography.caption, color = CallTheme.colors.gray400)
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = value, style = CallTheme.typography.captionBold, color = CallTheme.colors.black)
    }
}

@Composable
private fun MainBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(CallTheme.colors.mainVariant5Chat)
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Text(text = "메인", style = CallTheme.typography.captionBold, color = CallTheme.colors.mainVariant1)
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterCardPreview() {
    CallFromAiTheme {
        CharacterCard(
            aiCharacter = AiCharacter("1", "김민준", "", true, "2025.05.26", "35일 째", "오늘 21:42", "요약 텍스트"),
            onChatHistoryClick = {},
            onDeleteClick = {},
        )
    }
}