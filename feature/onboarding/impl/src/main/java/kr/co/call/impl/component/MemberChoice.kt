package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.Black
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray600
import kr.co.call.designsystem.theme.Gray900
import kr.co.call.designsystem.theme.MainVariant3
import kr.co.call.designsystem.theme.White
import kr.co.call.onboarding.impl.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import kr.co.call.designsystem.theme.Gray100
import kr.co.call.designsystem.theme.SubRed

@Composable
fun MemberChoice(
    modifier: Modifier=Modifier,
    label: String,
    selectedOption: String, //드롭다운이 보여줄 값
    placeholder: String,
    options:List<String>, //선택 가능한 목록
    onOptionSelected: (String)->Unit,//선택됐을 때 실행할 동작
    required: Boolean=false,
    ){
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier=modifier,
    ){
        Row{
            Text(
                text=label,
                color= Gray600,
                style= CallTheme.typography.bodyMedium,
            )
            if (required){
                Spacer(modifier=Modifier.width(3.dp))

                Text(
                    text="*",
                    color= SubRed,
                    style= CallTheme.typography.bodyMedium,
                )
            }
        }
        Spacer(modifier=Modifier.height(7.dp))

        BoxWithConstraints(
            modifier=Modifier.fillMaxWidth(),
        ){
            val menuWidth=maxWidth

            Surface(
                modifier=Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clickable(
                        role= Role.Button,
                        onClick={
                        expanded= !expanded
                    },
            ),
                color=Gray100,
                shape=RoundedCornerShape(10.dp),
            ){
                Row(
                    modifier= Modifier
                        .fillMaxSize()
                        .padding(horizontal=17.dp),
                    verticalAlignment= Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ){
                    Text(
                        text=selectedOption.ifBlank {
                            placeholder
                        },
                        color=if(selectedOption.isBlank()){Gray600
                        } else{Gray900},
                        style= CallTheme.typography.bodyMediumMedium
                    )
                    Icon(
                        painter= painterResource(
                            id=if(expanded){
                                R.drawable.ic_up
                            }else{
                                R.drawable.ic_down
                            },
                        ),
                        contentDescription=null,
                        modifier=Modifier.size(
                            width=12.dp,
                            height=6.dp,
                        ),
                        tint = Color.Unspecified
                    )
                }
            }

            DropdownMenu(
                expanded=expanded,
                onDismissRequest = {
                    expanded=false
                },
                modifier= Modifier
                    .width(menuWidth)
                    .heightIn(max=420.dp),
                offset=DpOffset(
                    x=0.dp,
                    y=4.dp,
                ),
                shape= RoundedCornerShape(15.dp),
                containerColor = White,
                shadowElevation=4.dp,
            ) {
                options.forEach{ option ->
                    val isSelected= option == selectedOption

                    DropdownMenuItem(
                        modifier= Modifier
                            .fillMaxWidth()
                            .background(
                                color=if(isSelected){
                                    MainVariant3
                                }else {
                                    White
                                },
                            ),
                        text={
                            Text(
                                text=option,
                                color= Black,
                                style= CallTheme.typography.bodyMediumMedium,
                            )
                        },
                        contentPadding= PaddingValues(
                            horizontal=17.dp,
                            vertical=9.dp,
                        ),
                        onClick={
                            onOptionSelected(option)
                            expanded=false
                        },
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
private fun MemberChoicePreview() {
    var selectedJob by rememberSaveable {
        mutableStateOf("")
    }

    MemberChoice(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        label = "직업",
        selectedOption = selectedJob,
        placeholder = "직업을 선택해주세요",
        options = listOf(
            "대학생",
            "직장인",
            "무직",
        ),
        onOptionSelected = { option ->
            selectedJob = option
        },
        required = true,
    )
}