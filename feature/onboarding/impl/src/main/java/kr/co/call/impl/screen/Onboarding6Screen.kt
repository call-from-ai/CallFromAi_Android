package kr.co.call.impl.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.component.button.PrimaryButton
import kr.co.call.designsystem.component.popup.TwoButtonPopup
import kr.co.call.designsystem.theme.Black
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.ChatGradient
import kr.co.call.designsystem.theme.Gray200
import kr.co.call.designsystem.theme.Gray400
import kr.co.call.designsystem.theme.Gray600
import kr.co.call.designsystem.theme.Gray800
import kr.co.call.designsystem.theme.MainVariant1
import kr.co.call.designsystem.theme.MainVariant2
import kr.co.call.designsystem.theme.SubPressed
import kr.co.call.designsystem.theme.White
import kr.co.call.onboarding.impl.R

@Composable
fun Onboarding6Screen(
    characterName: String,
    isLoading: Boolean,
    isCallDialogVisible: Boolean,
    onShowCallDialog:()->Unit,
    onDismissCallDialog:()->Unit,
    onCallNowClick:()->Unit,
    onCallLaterClick:()->Unit,
    modifier: Modifier =Modifier,
){
    var isCallNowPressed by remember {mutableStateOf(false)}
    var isCallLaterPressed by remember {mutableStateOf(false)}

    Column(
        modifier=modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 16.dp),
        horizontalAlignment =Alignment.CenterHorizontally,
    ){
        Spacer(modifier=Modifier.height(188.dp))
        Image(
            painter=painterResource(R.drawable.onboarding_check),
            contentDescription = null,
            modifier=Modifier.size(117.dp),
        )
        Spacer(modifier=Modifier.height(22.dp))
        Text(
            text="나의 이상형 등록 완료",
            style=CallTheme.typography.titleMediumBold,
            color= MainVariant1
        )
        Spacer(modifier=Modifier.height(16.dp))
        Text(
            text="준비가 되었다면,\n지금 바로 첫 통화를 시작해볼까요?",
            style= CallTheme.typography.bodyMedium,
            color= Gray800,
            textAlign=TextAlign.Center
        )
        Spacer(modifier=Modifier.height(30.dp))
        PrimaryButton(
            modifier=Modifier
                .size(width = 280.dp, height = 49.dp)
                .trackPressState {
                    isCallNowPressed = it
                },
            text="지금 전화할래",
            onClick= { if (!isLoading){
                     onShowCallDialog()}
                     },
            containerColor=CallTheme.colors.mainVariant3,
            contentColor=if(isCallNowPressed){
                Black
            }else{White},
            pressedContainerColor = SubPressed,
        )
        Spacer(modifier=Modifier.height(17.dp))
        PrimaryButton(
            modifier=Modifier
                .size(width = 280.dp, height = 49.dp)
                .trackPressState {
                    isCallLaterPressed = it
                },
            text="조금 이따할래",
            onClick={
                if (!isLoading){
                    onCallLaterClick()
                }
            },
            containerColor= Gray200,
            contentColor=if(isCallLaterPressed){
                Gray800 }else{ Gray600},
            pressedContainerColor = Gray400,
        )
        Spacer(modifier=Modifier.height(62.dp))
        Box(
            modifier= Modifier
                .fillMaxWidth()
                .height(71.dp)
                .background(
                    color = MainVariant2,
                    shape = RoundedCornerShape(10.dp),
                ),
            contentAlignment=Alignment.CenterStart,
        ){
            Row(
                modifier=Modifier
                    .fillMaxSize()
                    .padding(
                        start =16.dp,
                        end=16.dp
                    ),
                verticalAlignment=Alignment.CenterVertically,
                ){
                Box(
                    modifier = Modifier.width(25.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "\uD83D\uDCA1")
                }
                Spacer(modifier=Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "채팅을 많이 할수록",
                        style = CallTheme.typography.captionBold.copy(
                            brush=ChatGradient,
                        ),
                        color=Color.Unspecified,
                    )
                    Text(
                        text = "상대가 당신의 취향과 일상을 기억하고, 실제 연인처럼 대화를 이어가요.",
                        style = CallTheme.typography.captionBold,
                        color = Gray600,
                    )
                }
            }
        }
    }
    if (isCallDialogVisible){
        TwoButtonPopup(
            label="통화 연결",
            title="${characterName}에게 바로\n통화를 연결할까요?",
            positiveText = "연결",
            negativeText = "취소",
            onPositiveClick = {
                if (!isLoading) {
                    onDismissCallDialog()
                    onCallNowClick()
                }
            },
            onNegativeClick = {
                if (!isLoading) {
                    onDismissCallDialog()
                }
            },
            onDismissRequest = {
                if (!isLoading){
                    onDismissCallDialog()
                }
            },
        )
    }
}

private fun Modifier.trackPressState(
        onPressedChange:(Boolean)->Unit,
):Modifier=pointerInput(Unit){
    awaitEachGesture{
        awaitFirstDown(
            requireUnconsumed=false,
            pass=PointerEventPass.Initial,
        )
        onPressedChange(true)
        try{
            do{
                val event=awaitPointerEvent(PointerEventPass.Initial)
            }while (event.changes.any{it.pressed})
        }finally {
            onPressedChange(false)
        }
    }
}
