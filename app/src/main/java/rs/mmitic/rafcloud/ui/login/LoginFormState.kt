package rs.mmitic.rafcloud.ui.login

import androidx.annotation.StringRes

/** Data validation state of the login form. */
data class LoginFormState(
    @StringRes val usernameError: Int? = null,
    @StringRes val passwordError: Int? = null
) {
    val isDataValid = usernameError == null && passwordError == null
}
