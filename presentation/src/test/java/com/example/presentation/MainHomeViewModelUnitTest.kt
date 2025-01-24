package com.example.presentation

import com.example.domain.model.TotalInfoData
import com.example.domain.usecase.LoadTotalInfoUseCase
import com.example.presentation.state.MainHomeUiState
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


class MainHomeViewModelUnitTest {
    private lateinit var loadTotalInfoUseCase: LoadTotalInfoUseCase
    private lateinit var mainHomeViewModel: MainHomeViewModel

    private val initialState = MainHomeUiState(
        isUsernameClear = true,
        isBtnSaveVisible = true,
        isBtnSaveEnabled = false,
        isBtnBackVisible = false,
        isTextFortuneVisible = false,
        totalInfoData = null
    )

    @BeforeEach
    fun setUp() {
        loadTotalInfoUseCase = mock()
        mainHomeViewModel = MainHomeViewModel()
    }

    /*
    테스트: 이벤트 발생 시 처리해야 할 로직(usecase 호출 포함)
    - 이름 입력 이벤트 -> 확인 버튼 나타남    => 테스트 필요 없음
    - btnSave 클릭 이벤트 -> loadTotalInfoUseCase.invoke() 호출, 운세 텍스트 나타남, 확인 버튼 사라짐, 다시하기 버튼 나타남
    - btnBack 클릭 이벤트 -> 화면 초기화 -> inputName 초기화, 운세 텍스트 사라짐, 다시하기 버튼 사라짐, 확인 버튼 나타남

    => 각 상태 관리하는 변수 정의
    isUsernameClear: Boolean           // 이름 입력 상태
    isBtnSaveVisible: Boolean          // 확인 버튼 표시 여부
    isBtnSaveEnabled: Boolean          // 확인 버튼 활성화 여부
    isBtnBackVisible: Boolean          // 다시하기 버튼 표시 여부
    isTextFortuneVisible: Boolean      // 운세 텍스트 표시 여부
    totalInfoData: TotalInfoData       // 운세 정보

    => SharedFlow로 관리할 Data Class 정의
    : MainHomeUiState()
     */

    @Test
    @DisplayName("ui 상태를 초기화한다.")
    fun updatesMainUiStateToInitialStateWhenTriggered() =runTest {
        val result = MainHomeViewmModel.setInitUiState()

        // 결과 검증
        assertEquals(result, initialState)
    }

    @Test
    @DisplayName("사용자 이름이 입력되고, 확인 버튼이 활성화 된다.")
    fun setBtnSaveEnabledWhenTriggered() =runTest {
        val state = initialState.copy(isUsernameClear = false, isBtnSaveEnabled = true)

        val result = MainHomeViewmModel.setBtnSaveEnabled()

        // 결과 검증
        assertEquals(result, state)
    }

    @Test
    @DisplayName("loadTotalInfoUseCase 호출 시 totalInfoData.isNotEmpty()면 운세 텍스트가 업데이트 된 후 나타나고, 확인 버튼이 사라지고, 다시하기 버튼이 나타난다.")
    fun setBtnSaveGoneAndBtnBackVisibleAndTextFortuneVisibleWhenTotalInfoDataNotEmpty() =runTest {
        val username = "testUser"
        val mockData = TotalInfoData(username = username, date = "2025-01-22", content = "Sample Content", age = 28, personality = "긍정적")
        val state = initialState.copy(isBtnSaveVisible = false, isTextFortuneVisible = true, isBtnBackVisible = true, totalInfoData = mockData)

        // loadTotalInfoUseCase은 성공, totalInfoData.isNotEmpty()라고 가정
        whenever(loadTotalInfoUseCase.invoke(username)).thenReturn(mockData)

        val result = MainHomeViewModel.updateUiState()

        // 결과 검증
        assertEquals(result, state)

        // 호출 관련 검증
        verify(loadTotalInfoUseCase).invoke(username)
    }

    @Test
    @DisplayName("loadTotalInfoUseCase 호출 시 totalInfoData.isEmpty()면 운세 텍스트에 '현재 운세를 불러올 수 없습니다.'가 나타나고, 확인 버튼이 사라지고, 다시하기 버튼이 나타난다.")
    fun setBtnSaveGoneAndBtnBackVisibleAndTextFortuneVisibleWhenTotalInfoDataEmpty() =runTest {
        val username = "testUser"
        val mockData = TotalInfoData(username = "", date = "", content = "", age = 0, personality = "")
        val state = initialState.copy(isBtnSaveVisible = false, isTextFortuneVisible = true, isBtnBackVisible = true, totalInfoData = mockData)

        // loadTotalInfoUseCase은 성공, totalInfoData.isEmpty()라고 가정
        whenever(loadTotalInfoUseCase.invoke(username)).thenReturn(mockData)

        val result = MainHomeViewModel.updateUiState()

        // 결과 검증
        assertEquals(result, state)

        // 호출 관련 검증
        verify(loadTotalInfoUseCase).invoke(username)
    }
}