package rs.mmitic.rafcloud.ui.login

import androidx.annotation.StringRes
import rs.mmitic.rafcloud.data.model.LoggedInUser

/**
 * Authentication result : success (user details) or error message res.
 */
class LoginResult private constructor(
    val success: LoggedInUser? = null,
    @StringRes val error: Int? = null
){
    companion object {
        fun success(userModel: LoggedInUser) = LoginResult(success = userModel)
        fun error(@StringRes errorMsg: Int) = LoginResult(error = errorMsg)
    }
}
