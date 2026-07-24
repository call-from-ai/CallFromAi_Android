package kr.co.call.domain.exception

/**
 * 서버 정책상 메인 캐릭터를 아직 교체할 수 없을 때 발생하는 예외
 */
class CharacterChangeUnavailableException :
    IllegalStateException("아직 메인 연인을 변경할 수 없습니다.")
