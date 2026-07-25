package kr.co.call.impl.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.onboarding.impl.R
import androidx.compose.runtime.getValue


@Composable
fun ProfileChoice(
    modifier: Modifier,
    imageUrl: String?,
    onClick: ()->Unit,
    size: Dp =99.dp
){
    val interactionSource=remember{MutableInteractionSource()}
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier=modifier
            .size(size)
            .clickable(
                interactionSource=interactionSource,
                indication = null,
                onClick=onClick, )
    ) {
        if (imageUrl.isNullOrEmpty()){
            Image(
                imageVector= ImageVector.vectorResource(
                    id= if(isPressed){
                        R.drawable.onboarding_profile2
                    }else {R.drawable.onboarding_profile},
                ),
                contentDescription="기본 프로필 이미지",
                modifier=Modifier.fillMaxSize(),
                contentScale= ContentScale.Fit
            )
        } else{
            AsyncImage(
                model=imageUrl,
                contentDescription="선택된 프로필 사진",
                modifier=Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale=ContentScale.Crop
            )
        }
    }
}

@Preview(
    name = "기본 프로필 이미지",
    showBackground = true,
)
@Composable
private fun ProfileChoicePreview() {
    CallFromAiTheme {
        ProfileChoice(
            modifier = Modifier.padding(20.dp),
            imageUrl = null,
            onClick = {},
        )
    }
}