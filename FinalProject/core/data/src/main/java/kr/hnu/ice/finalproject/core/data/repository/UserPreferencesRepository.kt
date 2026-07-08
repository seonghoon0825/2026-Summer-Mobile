package kr.hnu.ice.finalproject.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kr.hnu.ice.finalproject.core.model.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore에 저장하는 사용자/설정 스냅샷.
 *
 * @param isLoggedIn 로그인 여부
 * @param user 로그인한 사용자 (비로그인 시 null)
 * @param darkTheme 다크모드 설정
 */
data class UserData(
    val isLoggedIn: Boolean,
    val user: User?,
    val darkTheme: Boolean,
)

/**
 * 로그인 상태/유저 정보/다크모드 설정을 저장하는 저장소.
 * 출처(DataStore)를 숨기고 Flow로 노출한다.
 */
interface UserPreferencesRepository {
    val userData: Flow<UserData>

    /** 로그인: 유저 정보를 저장하고 로그인 상태로 만든다. */
    suspend fun setUser(user: User)

    /** 로그아웃: 유저 정보를 지운다. */
    suspend fun clearUser()

    /** 다크모드 설정 변경. */
    suspend fun setDarkTheme(enabled: Boolean)
}

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    private object Keys {
        val LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

    override val userData: Flow<UserData> = dataStore.data.map { prefs ->
        val loggedIn = prefs[Keys.LOGGED_IN] ?: false
        val id = prefs[Keys.USER_ID]
        val user = if (loggedIn && id != null) {
            User(
                id = id,
                name = prefs[Keys.USER_NAME].orEmpty(),
                email = prefs[Keys.USER_EMAIL].orEmpty(),
            )
        } else {
            null
        }
        UserData(
            isLoggedIn = loggedIn,
            user = user,
            darkTheme = prefs[Keys.DARK_THEME] ?: false,
        )
    }

    override suspend fun setUser(user: User) {
        dataStore.edit { prefs ->
            prefs[Keys.LOGGED_IN] = true
            prefs[Keys.USER_ID] = user.id
            prefs[Keys.USER_NAME] = user.name
            prefs[Keys.USER_EMAIL] = user.email
        }
    }

    override suspend fun clearUser() {
        dataStore.edit { prefs ->
            prefs[Keys.LOGGED_IN] = false
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.USER_NAME)
            prefs.remove(Keys.USER_EMAIL)
        }
    }

    override suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.DARK_THEME] = enabled
        }
    }
}