package rs.mmitic.rafcloud.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rs.mmitic.rafcloud.R
import rs.mmitic.rafcloud.data.LoginRepository
import rs.mmitic.rafcloud.data.Result

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginFormState = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginFormState

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val result = loginRepository.login(username, password)

            _loginResult.value = when (result) {
                is Result.Success -> LoginResult.success(result.data)
                else -> LoginResult.error(R.string.login_failed)
            }
        }
    }

    fun onLoginDataChanged(username: String, password: String) {
        _loginFormState.value = when {
            !isUserNameValid(username) ->
                LoginFormState(usernameError = R.string.invalid_username)
            !isPasswordValid(password) ->
                LoginFormState(passwordError = R.string.invalid_password_length)
            else -> LoginFormState() // no errors
        }
    }

    private fun isUserNameValid(username: String) =
        Patterns.EMAIL_ADDRESS.matcher(username).matches()

    private fun isPasswordValid(password: String): Boolean = password.length > 3
}
