package kr.co.call.impl.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable

private data class AgreementState(
    val service: Boolean=false,
    val privacy: Boolean=false,
    val marketing: Boolean=false,
    val dataUsage: Boolean=false,
){
    val isAllChecked: Boolean
        get()=service && privacy && marketing && dataUsage
    val isRequiredChecked: Boolean
        get()=service && privacy
    fun updateAll(checked:Boolean): AgreementState{
        return copy(
            service=checked,
            privacy=checked,
            marketing=checked,
            dataUsage=checked,
        )
    }
}
@Composable
fun AgreementScreen(
    onServiceAgreementClick:()->Unit,
    onPrivacyAgreementClick:()->Unit,
    onMarketingAgreementClick:()->Unit,
    onDataAgreementClick:()->Unit,
    onNextClick:()->Unit,
    modifier: Modifier = Modifier,
) {
    var agreementState by remember{
        mutableStateOf(AgreementState)
    }
}