package kr.co.call.impl.entry

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.CharacterManagementNavKey
import kr.co.call.api.CallTimeManagementNavKey
import kr.co.call.api.DisturbTimeNavKey
import kr.co.call.api.EditCharacterNavKey
import kr.co.call.api.EditProfileNavKey
import kr.co.call.api.FaqNavKey
import kr.co.call.api.MyPageNavKey
import kr.co.call.impl.screen.CharacterManagementScreen
import kr.co.call.api.ProfileNavKey
import kr.co.call.api.SubscriptionNavKey
import kr.co.call.api.TermNavKey
import kr.co.call.impl.screen.CallTimeManagementScreen
import kr.co.call.impl.screen.DisturbTimeScreen
import kr.co.call.impl.screen.EditCharacterScreen
import kr.co.call.impl.screen.EditProfileScreen
import kr.co.call.impl.screen.FaqScreen
import kr.co.call.impl.screen.MyPageScreen
import kr.co.call.impl.screen.ProfileScreen
import kr.co.call.impl.screen.SubscriptionScreen
import kr.co.call.impl.screen.TermScreen

fun EntryProviderScope<NavKey>.myPageEntry(
    navigateToCharacterManagement: () -> Unit,
    navigateToFaq: () -> Unit,
    navigateToTerms: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToSubscription: () -> Unit,
    navigateToDisturbTime: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToCallTimeManagement: () -> Unit,
    navigateToEditCharacter: (Long) -> Unit,
    onBack: () -> Unit = {},
) {
    entry<MyPageNavKey> {
        MyPageScreen(
            onNavigateToLogin = navigateToLogin,
            onNavigateToCharacterManagement = navigateToCharacterManagement,
            navigateToFaq = navigateToFaq,
            navigateToTerms = navigateToTerms,
            onNavigateToProfile = navigateToProfile,
        )
    }

    entry<CharacterManagementNavKey> {
        CharacterManagementScreen(
            onBackClick = onBack,
            navigateToAddCharacter = { /* TODO: 캐릭터 추가 화면 연결 */ },
            navigateToEditCharacter = navigateToEditCharacter,
        )
    }

    entry<EditCharacterNavKey> { key ->
        EditCharacterScreen(
            characterId = key.characterId,
            onBackClick = onBack,
        )
    }

    entry<FaqNavKey> {
        FaqScreen(onBackClick = onBack)
    }

    entry<TermNavKey> {
        TermScreen(onBackClick = onBack)
    }

    entry<ProfileNavKey> {
        ProfileScreen(
            onBackClick = onBack,
            navigateToEditProfile = navigateToEditProfile,
            navigateToSubscription = navigateToSubscription,
            navigateToDisturbTime = navigateToDisturbTime,
            navigateToCallTimeManagement = navigateToCallTimeManagement,
        )
    }

    entry<EditProfileNavKey> {
        EditProfileScreen(onBackClick = onBack)
    }

    entry<SubscriptionNavKey> {
        SubscriptionScreen(
            modifier = Modifier,
            onBackClick = onBack,
        )
    }

    entry<DisturbTimeNavKey> {
        DisturbTimeScreen(onBackClick = onBack)
    }

    entry<CallTimeManagementNavKey> {
        CallTimeManagementScreen(onBackClick = onBack)
    }
}