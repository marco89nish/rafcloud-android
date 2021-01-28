package rs.mmitic.rafcloud.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import rs.mmitic.rafcloud.R
import rs.mmitic.rafcloud.data.LoginRepository
import rs.mmitic.rafcloud.data.model.LoggedInUser
import rs.mmitic.rafcloud.data.model.displayName
import rs.mmitic.rafcloud.ui.machines.MachinesActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (LoginRepository.isLoggedIn) {
            startActivity(Intent(this, MachinesActivity::class.java))
            finish()
        }

        setContentView(R.layout.activity_login)

        val usernameTextBox = findViewById<EditText>(R.id.username)
        val passwordTextBox = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login)
        val loadingProgressBar = findViewById<ProgressBar>(R.id.loading)

        loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory()).get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this) { loginFormState ->
            // disable login button unless both username / password is valid
            loginButton.isEnabled = loginFormState.isDataValid

            if (loginFormState.usernameError != null) {
                usernameTextBox.error = getString(loginFormState.usernameError)
            }
            if (loginFormState.passwordError != null) {
                passwordTextBox.error = getString(loginFormState.passwordError)
            }
        }

        loginViewModel.loginResult.observe(this) { loginResult ->
            loadingProgressBar.visibility = View.GONE
            when {
                loginResult.error != null -> showLoginFailed(loginResult.error)
                else -> {
                    updateUiWithUser(loginResult.success!!)
                    setResult(Activity.RESULT_OK)
                    startActivity(Intent(this, MachinesActivity::class.java))
                    // Close and destroy login activity once successful
                    finish()
                }
            }
        }

        usernameTextBox.afterTextChanged {
            loginViewModel.onLoginDataChanged(
                usernameTextBox.text.toString(),
                passwordTextBox.text.toString()
            )
        }

        passwordTextBox.apply {
            afterTextChanged {
                loginViewModel.onLoginDataChanged(
                    usernameTextBox.text.toString(),
                    passwordTextBox.text.toString()
                )
            }

            // Handle Enter/Done key from keyboard
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            usernameTextBox.text.toString(),
                            passwordTextBox.text.toString()
                        )
                }
                false
            }
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.login(usernameTextBox.text.toString(), passwordTextBox.text.toString())
        }
    }

    private fun updateUiWithUser(model: LoggedInUser) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName

        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
