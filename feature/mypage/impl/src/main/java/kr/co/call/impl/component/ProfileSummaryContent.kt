package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 프로필 정보 요약 : 아바타 + 이름 + 등급 뱃지
 */
@Composable
fun ProfileSummaryContent(
    profileImageUrl:String,
    nickname: String,
    tier:String,
    onClick:()->Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 25.dp, end = 25.dp, top = 24.dp, bottom = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        // 프로필 이미지
        AsyncImage(
            model = profileImageUrl,
            contentDescription = if (profileImageUrl.isBlank()) null else "프로필 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape),
            placeholder = painterResource(id = R.drawable.img_mypage_profile_default),
            error = painterResource(id = R.drawable.img_mypage_profile_default)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 사용자 이름
        Text(
            text = nickname,
            style = CallTheme.typography.bodyLargeBold,
            color = CallTheme.colors.black,
        )

        Spacer(modifier = Modifier.width(10.dp))

        // 티어 뱃지
        TierBadge(tier = tier)

        Spacer(modifier = Modifier.weight(1f))

        // 이동 아이콘(>)
        Icon(
            painter = painterResource(id = R.drawable.ic_mypage_arrow_right),
            contentDescription = "이동",
            tint = CallTheme.colors.gray400,
        )
    }
}

// 티어 뱃지 컴포넌트
@Composable
private fun TierBadge(
    tier: String,
    modifier: Modifier= Modifier
){
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(CallTheme.colors.mainVariant2)
            .padding(horizontal = 13.dp, vertical = 3.dp)

    ){
        Text(
            text=tier,
            style = CallTheme.typography.bodySmallBold,
            color = CallTheme.colors.mainVariant1
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileSummaryCardPreview(){
    CallFromAiTheme {
        ProfileSummaryContent(
            profileImageUrl = "",
            nickname = "김수현",
            tier = "Basic",
            onClick = {}
        )
    }
}