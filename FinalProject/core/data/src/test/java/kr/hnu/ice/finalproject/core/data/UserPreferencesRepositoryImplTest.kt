package kr.hnu.ice.finalproject.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kr.hnu.ice.finalproject.core.data.repository.UserPreferencesRepositoryImpl
import kr.hnu.ice.finalproject.core.model.FontSizeOption
import kr.hnu.ice.finalproject.core.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 설정 저장소(다크모드/글자 크기/고대비/로그인)의 저장→조회 왕복을 디바이스 없이 검증한다.
 * 실제 DataStore 대신 in-memory fake로 대체한다.
 */
class UserPreferencesRepositoryImplTest {

    /** DataStore<Preferences>를 MutableStateFlow로 흉내 내는 in-memory fake. */
    private class FakeDataStore : DataStore<Preferences> {
        private val state = MutableStateFlow(emptyPreferences())
        override val data: Flow<Preferences> = state
        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val next = transform(state.value)
            state.value = next
            return next
        }
    }

    private fun createRepository() = UserPreferencesRepositoryImpl(FakeDataStore())

    @Test
    fun `초기 상태는 비로그인에 기본 테마와 접근성 설정이다`() = runTest {
        val repo = createRepository()

        val data = repo.userData.first()

        assertFalse(data.isLoggedIn)
        assertNull(data.user)
        assertFalse(data.darkTheme)
        assertEquals(FontSizeOption.NORMAL, data.fontSize)
        assertFalse(data.highContrast)
    }

    @Test
    fun `다크모드와 접근성 설정이 저장되고 조회에 반영된다`() = runTest {
        val repo = createRepository()

        repo.setDarkTheme(true)
        repo.setFontSize(FontSizeOption.LARGE)
        repo.setHighContrast(true)

        val data = repo.userData.first()
        assertTrue(data.darkTheme)
        assertEquals(FontSizeOption.LARGE, data.fontSize)
        assertTrue(data.highContrast)
    }

    @Test
    fun `로그인 후 로그아웃하면 유저 정보만 지워지고 설정은 유지된다`() = runTest {
        val repo = createRepository()
        repo.setDarkTheme(true)
        repo.setUser(User(id = "u1", name = "홍길동", email = "hong@test.com"))

        // 로그인 반영 확인
        val loggedIn = repo.userData.first()
        assertTrue(loggedIn.isLoggedIn)
        assertEquals("홍길동", loggedIn.user?.name)

        repo.clearUser()

        // 로그아웃 후: 유저는 사라지지만 테마 설정은 남는다
        val loggedOut = repo.userData.first()
        assertFalse(loggedOut.isLoggedIn)
        assertNull(loggedOut.user)
        assertTrue(loggedOut.darkTheme)
    }
}