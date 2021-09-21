package com.mtjin.free_room.views.login

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mtjin.free_room.R
import com.mtjin.free_room.base.BaseFragment
import com.mtjin.free_room.databinding.FragmentLoginBinding
import com.mtjin.free_room.utils.RC_GOOGLE_LOGIN
import com.mtjin.free_room.utils.uuid
import com.mtjin.free_room.views.main.MainActivity
import timber.log.Timber
import javax.inject.Inject


class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
    }

    private fun initViewModelCallback() {
        with(viewModel) {
            googleLoginResult.observe(this@LoginFragment, Observer { result ->
                if (result) {
                    initNavigation()
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToBottomNav1())
                } else {
                    showToast(getString(R.string.login_fail_text))
                }
            })
        }
    }

    private fun initNavigation() {
        findNavController().graph.startDestination = R.id.bottom_nav_1
        (activity as MainActivity).initNavigation()
    }

    private fun initClickListener() {
        binding.btnGoogleLogin.setOnClickListener {//구글로그인은 onCLick 안됨
            googleLogin()
        }
    }

    private fun initGoogleLogin() {
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.let {
            uuid = it.uid
            showToast(getString(R.string.auto_login_text))
            initNavigation()
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToBottomNav1())
        }
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(thisContext, gso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_LOGIN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                account?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                Timber.d(e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uuid = auth.currentUser?.uid.toString()
                    viewModel.googleLogin()
                } else {
                    showToast(getString(R.string.login_fail_text))
                }
            }
    }

    private fun googleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN)
    }

    override fun init() {
        (activity as MainActivity).mainComponent.inject(this)
        binding.vm = viewModel
        initGoogleLogin()
        initClickListener()
        initViewModelCallback()
    }
}