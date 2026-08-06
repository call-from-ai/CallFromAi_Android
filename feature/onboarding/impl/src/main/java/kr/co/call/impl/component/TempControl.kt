package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.Black
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray400
import kr.co.call.designsystem.theme.MainVariant1
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.DragGradient
import kr.co.call.designsystem.theme.SubFire
import kr.co.call.designsystem.theme.SubSoup
import kr.co.call.designsystem.theme.White
import kr.co.call.onboarding.impl.R
import kotlin.math.roundToInt


@Composable
fun TempControl(
    onTemperatureChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    initialTemperature: Int = 50,
) {
    var temperature by rememberSaveable(initialTemperature) {
        mutableIntStateOf(initialTemperature.coerceIn(0, 100))
    }
    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
                Text(
                    text = "연애 온도 : ",
                    color = Black,
                    style = CallTheme.typography.bodyMediumMedium,
                )
                Text(
                    text = "$temperature ºC",
                    color = MainVariant1,
                    style = CallTheme.typography.bodyMediumMedium,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "드래그해서 나에게 맞는 스타일을 선택해보세요!",
                color = Gray400,
                style = CallTheme.typography.caption,
            )
            Spacer(modifier = Modifier.height(15.dp))
            TempDrag(
                temperature=temperature,
                onTemperatureChange={
                    temperature=it
                    onTemperatureChange(it)
                },
            )
        }
    }

@Composable
private fun TempDrag(
    temperature: Int,
    onTemperatureChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(151.dp),
    ) {
        val density=LocalDensity.current
        // 버튼 중심이 왼쪽 카드의 오른쪽 끝에 위치하는 지점
        val minimumOffsetPx=with(density){
            (98.dp-24.dp).toPx()
        }
        // 버튼 중심이 오른쪽 카드의 왼쪽 끝에 위치하는 지점
        val maximumOffsetPx=constraints.maxWidth.toFloat()-with(density){
            (98.dp+24.dp).toPx()
        }
        val dragDistancePx=maximumOffsetPx-minimumOffsetPx
        var thumbOffsetPx by remember(
            minimumOffsetPx,
            maximumOffsetPx,
        ){
            mutableFloatStateOf(
                minimumOffsetPx+dragDistancePx*(temperature/100f),
            )
        }
        val draggableState=rememberDraggableState{dragAmount ->
            thumbOffsetPx=(
                    thumbOffsetPx+dragAmount
                    ).coerceIn(
                        minimumValue=minimumOffsetPx,
                        maximumValue=maximumOffsetPx,
                    )
            val progress=(
                    (thumbOffsetPx-minimumOffsetPx)/
                    dragDistancePx
                    ).coerceIn(0f,1f)
            val changedTemperature=
                (progress*10f).roundToInt()*10
            if (changedTemperature != temperature){
                onTemperatureChange(changedTemperature)
            }
        }
        Box(
            modifier= Modifier
                .align(Alignment.Center)
                .padding(horizontal = 98.dp)
                .fillMaxWidth()
                .height(13.dp)
                .background(brush= DragGradient),
        )
        TempCard(
            isHot=false,
            modifier= Modifier.align(
                Alignment.CenterStart,
            ),
        )

        TempCard(
            isHot=true,
            modifier=Modifier.align(
                Alignment.CenterEnd,
            ),
        )
        Surface(
            modifier= Modifier
                .align(Alignment.CenterStart)
                .offset {
                    IntOffset(
                        x=thumbOffsetPx.roundToInt(),
                        y=0,
                    )
                }
                .size(47.dp)
                .draggable(
                    state=draggableState,
                    orientation=Orientation.Horizontal,
                    onDragStopped={
                        val progress=(
                                (thumbOffsetPx-minimumOffsetPx)/dragDistancePx
                                ).coerceIn(
                                    minimumValue = 0f,
                                    maximumValue=1f,
                                )
                        val snappedTemperature=(progress*10f).roundToInt()*10
                        thumbOffsetPx=minimumOffsetPx+dragDistancePx*(snappedTemperature/100f)
                        onTemperatureChange(snappedTemperature,)
                    },
                ),
            shape=CircleShape,
            color=White,
            shadowElevation = 10.dp,
        ){
            Row(
                modifier=Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ){
                Icon(
                    painter= painterResource(
                        id=R.drawable.ic_left,
                    ),
                    contentDescription = null,
                    tint=Color.Unspecified,
                    modifier=Modifier.size(14.dp),
                )
                Spacer(modifier=Modifier.width(10.dp))
                Icon(
                    painter= painterResource(
                        id=R.drawable.ic_right,
                    ),
                    contentDescription = null,
                    tint=Color.Unspecified,
                    modifier=Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun TempCard(
    isHot: Boolean, //어떤 카드인지 구분하는 용(T: 불닭맛)
    modifier: Modifier=Modifier,
){
    Surface(
        modifier=modifier
            .width(98.dp)
            .height(151.dp),
        shape=RoundedCornerShape(20.dp),
        color=White,
    ){
        Column(
            modifier= Modifier
                .fillMaxSize()
                .padding(
                    horizontal=12.dp,
                    vertical=9.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Box(
                modifier= Modifier
                    .size(40.dp)
                    .background(
                        color=if(isHot){
                            SubFire
                        }else {
                            SubSoup},
                        shape= CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ){
                Text(
                    text= if(isHot) "\uD83C\uDF36\uFE0F" else "\uD83C\uDF72",
                    style= CallTheme.typography.titleMediumBold,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(
                modifier=Modifier.height(11.dp),
            )
            Text(
                text=if(isHot) {"도파민 뿜뿜"} else {"파워 안정형"},
                color=if(isHot){SubFire} else {SubSoup},
                style= CallTheme.typography.caption,
                textAlign=TextAlign.Center,
                maxLines=1,
            )
            Text(
                text=if(isHot){"불닭 맛"} else {"능이백숙 맛"},
                color=Black,
                style= CallTheme.typography.bodySmallBold,
                textAlign = TextAlign.Center,
                maxLines=1,
            )
            Spacer(modifier=Modifier.height(14.dp))
            Text(
                text=if(isHot){"과감하고 표현이 많은 편\n화끈하고 짜릿한 스타일"}
                else {"다정하고 배려가 많은 편\n천천히 오래가는 스타일"},
                color=Gray400,
                style= CallTheme.typography.captionSmall
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFF8F4F5,
)
@Composable
private fun TempControlPreview() {
    CallFromAiTheme {
        TempControl(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 24.dp,
                ),
            onTemperatureChange = {},
        )
    }
}