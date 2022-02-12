package com.ebenezer.gana.shoppyadmin.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.ebenezer.gana.shoppyadmin.R
import com.ebenezer.gana.shoppyadmin.databinding.ActivityLoginBinding
import com.ebenezer.gana.shoppyadmin.firestore.FirestoreClass
import com.ebenezer.gana.shoppyadmin.models.User
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        //Click event for the login button
        binding.btnLogin.setOnClickListener(this)


    }

    override fun onClick(view: View?) {
        view.let {
            when (it?.id) {
                R.id.btn_login -> {
                    logInRegisteredUser()

                }

                else -> {

                }
            }
        }

    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_email),
                    errorMessage = true
                )
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_password),
                    errorMessage = true
                )
                false
            }

            else -> {
                true
            }
        }


    }

    private fun logInRegisteredUser() {

        if (validateLoginDetails()) {
            // show the progress dialog
            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = binding.etEmail.text.toString().trim() { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim() { it <= ' ' }


            // Login using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    // If the registration is successfully done
                    if (task.isSuccessful) {
                        //FirestoreClass().getUserDetails(this@LoginActivity)

                        // Hide the progress dialog.
                        hideProgressDialog()

                        //Redirect the user to the Main Screen after log in
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {
                        hideProgressDialog()
                        // If the registration is not successful then show error message
                        showErrorSnackBar(
                            task.exception!!.message.toString(),
                            true
                        )
                    }
                }
        }

    }

}