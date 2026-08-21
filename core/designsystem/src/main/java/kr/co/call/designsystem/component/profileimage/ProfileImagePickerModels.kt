package kr.co.call.designsystem.component.profileimage

/**
 * 프로필 사진 후보 필터용 성별
 *
 * designsystem 전용 값입니다. domain/서버 성별 enum을 직접 넣지 말고,
 * feature 레이어에서 1:1 매핑해 전달하세요.
 *
 * ```kotlin
 * // feature 예시
 * fun DomainGender.toPicker(): ProfileImageGender = when (this) {
 *     DomainGender.MALE -> ProfileImageGender.MALE
 *     DomainGender.FEMALE -> ProfileImageGender.FEMALE
 * }
 * ```
 */
enum class ProfileImageGender {
    MALE,
    FEMALE,
}

/**
 * 프로필 사진 선택 목록의 한 항목입니다.
 *
 * 네트워크/DB 모델이 아니라 UI 전달용 DTO입니다.
 * Repository 응답을 feature에서 이 타입으로 변환해 넘깁니다.
 *
 * @property id 저장/식별에 쓰는 고유 키
 * @property imageUrl 표시 URL. 로딩 중에는 스켈레톤을 표시한다.
 */
data class ProfileImageOption(
    val id: String,
    val imageUrl: String,
)
