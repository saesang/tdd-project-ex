package com.example.presentation

import com.example.presentation.util.TextValidator
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TextValidatorUnitTest {
    private lateinit var textValidator : TextValidator

    @Test
    @DisplayName("빈 문자열에 대해서는 false를 반환한다.")
    fun returnFalseWithNoText() {
        val input = ""
        val result = TextValidator.isValidText(input)
        assertFalse(result)
    }

    @Test
    @DisplayName("공백만 있는 문자열에 대해서는 false를 반환한다.")
    fun returnFalseWithBlank() {
        val input = "   "
        val result = TextValidator.isValidText(input)
        assertFalse(result)
    }

    @Test
    @DisplayName("특수문자가 포함된 문자열에 대해서는 false를 반환한다.")
    fun returnFalseWithSpecialCharacters() {
        val input = "!@#$%"
        val result = TextValidator.isValidText(input)
        assertFalse(result)
    }

    @Test
    @DisplayName("숫자가 포함된 문자열에 대해서는 false를 반환한다.")
    fun returnFalseWithNumbers() {
        val input = "12345"
        val result = TextValidator.isValidText(input)
        assertFalse(result)
    }

    @Test
    @DisplayName("알파벳과 여러 공백으로 이루어진 문자열에 대해서는 false를 반환한다.")
    fun returnFalseWithAlphabetsWithSeveralBlanks() {
        val input = "seha  tak"
        val result = TextValidator.isValidText(input)
        assertFalse(result)
    }

    @Test
    @DisplayName("알파벳과 1개의 공백으로만 이루어진 문자열에 대해서는 true를 반환한다.")
    fun returnTrueWithAlphabets() {
        val input = "seha tak"
        val result = TextValidator.isValidText(input)
        assertTrue(result)
    }

    @Test
    @DisplayName("한글로만 이루어진 문자열에 대해서는 true를 반환한다.")
    fun returnTrueWithKorean() {
        val input = "탁세하"
        val result = TextValidator.isValidText(input)
        assertTrue(result)
    }
}