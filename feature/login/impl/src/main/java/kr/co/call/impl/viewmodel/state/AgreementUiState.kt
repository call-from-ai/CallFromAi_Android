package kr.co.call.impl.viewmodel.state

import kr.co.call.domain.model.login.AgreementTerm
import kr.co.call.domain.util.LoadStatus

data class AgreementUiState(
    //서버에서 조회한 약관 목록
    val terms: List<AgreementTerm> = emptyList(),

    //사용자가 동의한 약관의 id목록
    val checkedTermIds: Set<Long> = emptySet(),

    val status: LoadStatus=LoadStatus.Idle,
){
    //모든 약관이 선택됐는지 확인하여 '전체동의'체크박스 상태 결정
    val isAllChecked: Boolean
        get() = terms.isNotEmpty() &&
                terms.all { it.termId in checkedTermIds }

    //필수 약관이 모두 체크됐는지 확인하여 '다음'버튼의 활성화 여부 결정
    val isRequiredChecked: Boolean
        get() = terms.isNotEmpty() && terms
            .filter { it.isRequired }
            .all { it.termId in checkedTermIds }
}