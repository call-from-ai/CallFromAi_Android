package kr.co.call.impl.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import kr.co.call.login.impl.R
import kr.co.call.designsystem.theme.*

@Composable
fun LandingScreen(
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Image(
            painter= painterResource(id= R.drawable.landing),
            contentDescription = null,
            modifier=Modifier.fillMaxSize(),
            contentScale= ContentScale.Crop
        )
        Column(
            modifier=Modifier
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier=Modifier.height(266.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement=Arrangement.Center
            ){
                Image(
                    painter=painterResource(id=R.drawable.logo),
                    contentDescription = "로고",
                    modifier=Modifier.size(97.dp),
                    contentScale=ContentScale.Fit
                )
                Spacer(modifier=Modifier.width(20.dp))

                Text(
                    text="전화왔어",
                    style= CallTheme.typography.titleExtraLargeBold.copy(
                        brush=CallTheme.colors.chatGradient,
                    ),
                )
            }
            Spacer(modifier= Modifier.height(22.dp))

            Text(
                text="오늘, 기다리던 사람에게\n전화가 옵니다.",
                color=Gray200,
                style= CallTheme.typography.titleExtraSmall,
                textAlign= TextAlign.Center,
                modifier=Modifier.wrapContentWidth()
            )
        }
    }
}

@Preview
@Composable
private fun LandingScreenPreview() {
    LandingScreen(
        modifier = Modifier,
    )
}