package com.example.walkinghadang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.walkinghadang.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        if(MyApplication.checkAuth()){
            changeVisibility("login")
        }else{
            changeVisibility("logout")
        }
        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try{
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                MyApplication.auth.signInWithCredential(credential)
                    .addOnCompleteListener(this){task ->
                        if(task.isSuccessful && MyApplication.checkAuth()){
                            MyApplication.email = account.email
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            changeVisibility("logout")
                        }
                    }
            }catch (e: ApiException){
                changeVisibility("logout")
            }
        }
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }


        binding.goSignInBtn.setOnClickListener {
            //회원가입
            changeVisibility("signin")
        }
        binding.googleLoginBtn.setOnClickListener{
            //구글 로그인
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("807399885262-mgv6gff5taodv1lehv6gsdu3k0bbn0d8.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
            requestLauncher.launch(signInIntent)
        }

        binding.signBtn.setOnClickListener {
            //이메일 비밀번호 회원가입
            val email = binding.authEmailEditView.editText?.text.toString()
            val password = binding.authPasswordEditView.editText?.text.toString()
            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){task->
                    binding.authEmailEditView.editText?.text?.clear()
                    binding.authPasswordEditView.editText?.text?.clear()
                    if(task.isSuccessful){
                        MyApplication.auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener{sendTask->
                                if(sendTask.isSuccessful){
                                    Toast.makeText(baseContext, "회원가입에 성공하였습니다. 전송된 메일을 확인해 주세요", Toast.LENGTH_SHORT).show()
                                    changeVisibility("logout")
                                }else{
                                    Toast.makeText(baseContext, "메일 전송 실패", Toast.LENGTH_SHORT).show()
                                    changeVisibility("logout")
                                }
                            }
                    }else{
                        Toast.makeText(baseContext, "회원 가입 실패", Toast.LENGTH_SHORT).show()
                        changeVisibility("logout")
                    }
                }
        }

        binding.loginBtn.setOnClickListener {

            val email = binding.authEmailEditView.editText?.text.toString()
            val password = binding.authPasswordEditView.editText?.text.toString()
            MyApplication.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){task->
                    binding.authEmailEditView.editText?.text?.clear()
                    binding.authPasswordEditView.editText?.text?.clear()
                    if(task.isSuccessful){
                        if(MyApplication.checkAuth()){
                            MyApplication.email = email
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
//                            changeVisibility("login")
                        }else {
                            Toast.makeText(baseContext, "전송된 메일로 이메일 인증이 되지 않았습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        Toast.makeText(baseContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.kakaoLoginBtn.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    handleKakaoCallback(token, error)
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
                    handleKakaoCallback(token, error)
                }
            }
        }

    }

    private fun handleKakaoCallback(token: OAuthToken?, error: Throwable?) {
        val keyHash = Utility.getKeyHash(this)
        Log.d("KAKAO_KEY_HASH", keyHash)
        if (error != null) {
            Toast.makeText(this, "카카오 로그인 실패: ${error.message}", Toast.LENGTH_SHORT).show()
            Log.e("KAKAO_LOGIN_FAIL", "error: ${error}", error)
        } else if (token != null) {
            // 사용자 정보 요청
            UserApiClient.instance.me { user, userError ->
                if (userError != null) {
                    Toast.makeText(this, "사용자 정보 요청 실패: ${userError.message}", Toast.LENGTH_SHORT).show()
                } else if (user != null) {
                    MyApplication.nickname = "kakao : "+user.kakaoAccount?.profile?.nickname
                    Toast.makeText(this, "로그인 성공 ", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    fun changeVisibility(mode: String){
        if(mode === "login"){
            binding.run {
                goSignInBtn.visibility= View.GONE
                googleLoginBtn.visibility= View.GONE
                kakaoLoginBtn.visibility = View.GONE
                authEmailEditView.visibility= View.GONE
                authPasswordEditView.visibility= View.GONE
                signBtn.visibility= View.GONE
                loginBtn.visibility= View.GONE
            }

        }else if(mode === "logout"){
            binding.run {
                goSignInBtn.visibility = View.VISIBLE
                googleLoginBtn.visibility = View.VISIBLE
                kakaoLoginBtn.visibility = View.VISIBLE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.GONE
                loginBtn.visibility = View.VISIBLE
            }
        }else if(mode === "signin"){
            binding.run {
                goSignInBtn.visibility = View.GONE
                googleLoginBtn.visibility = View.GONE
                kakaoLoginBtn.visibility = View.GONE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.VISIBLE
                loginBtn.visibility = View.GONE
            }
        }
    }

}