package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.impl.component.MainTab
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

) : ViewModel(), ContainerHost<MainState, MainSideEffect> {

    override val container: Container<MainState, MainSideEffect> = container(
        initialState = MainState()
    )

    fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.SelectTab -> changeTab(intent.tab)
        }
    }

    private fun changeTab(tab: MainTab) = intent {
        postSideEffect(MainSideEffect.NavigateTo(tab))
    }
}
