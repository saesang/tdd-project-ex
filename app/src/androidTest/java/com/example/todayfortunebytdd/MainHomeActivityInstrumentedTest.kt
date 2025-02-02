package com.example.todayfortunebytdd

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.presentation.MainHomeActivity
import com.example.presentation.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainHomeActivityInstrumentedTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject() // Hilt 의존성 주입
        ActivityScenario.launch(MainHomeActivity::class.java)
    }

    @Test
    // 유효 이름이 입력되면 btnSave가 활성화, 유효하지 않은 이름이 입력되면 btnSave가 비활성화된다.
    fun setBtnSaveStatusWhenInputNameEntered() {
        // 초기 상태
        onView(withId(R.id.btn_save)).check(matches(not(isEnabled())))

        // 유효 이름
        onView(withId(R.id.input_name))
            .perform(replaceText("seha"), closeSoftKeyboard())
        //버튼 활성화 확인
        onView(withId(R.id.btn_save)).check(matches(isEnabled()))

        // 무효 이름
        onView(withId(R.id.input_name))
            .perform(clearText(), replaceText("seha  "), closeSoftKeyboard())
        // 버튼 비활성화 확인
        onView(withId(R.id.btn_save)).check(matches(not(isEnabled())))

        // 유효 이름
        onView(withId(R.id.input_name))
            .perform(replaceText("세하"), closeSoftKeyboard())
        //버튼 활성화 확인
        onView(withId(R.id.btn_save)).check(matches(isEnabled()))

        // 이름 지웠을 때
        onView(withId(R.id.input_name))
            .perform(clearText())
        // 버튼 비활성화 확인
        onView(withId(R.id.btn_save)).check(matches(not(isEnabled())))
    }

    // btnSave가 클릭되면 ui가 변화된다.
    @Test
    fun updateUiStateWhenBtnSaveClicked() {
        // 초기 상태: 유효 이름 입력
        onView(withId(R.id.input_name))
            .perform(replaceText("세하"), closeSoftKeyboard())

        // btnSave 버튼 클릭
        onView(withId(R.id.btn_save)).perform(click())

        // UI 상태 변화 확인
        onView(withId(R.id.text_fortune)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.btn_back)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.btn_save)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.input_name)).check(matches(not(isEnabled())))    // 이름 수정 불가
    }

    // btnBack이 클릭되면 ui가 초기화된다.
    @Test
    fun resetUiStateWhenBtnBackClicked() {
        // 초기 상태: btnBack.visibility = VISIBLE
        onView(withId(R.id.btn_back)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        // btnBack 버튼 클릭
        onView(withId(R.id.btn_back)).perform(click())

        // UI 상태 변화 확인
        onView(withId(R.id.text_fortune)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.btn_back)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.btn_save)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.btn_save)).check(matches(not(isEnabled())))
        onView(withId(R.id.input_name)).check(matches(isEnabled()))    // 이름 수정 가능 상태
        onView(withId(R.id.input_name)).check(matches( withText("")))   // 이름 초기화
    }
}