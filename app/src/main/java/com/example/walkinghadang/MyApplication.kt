package com.example.walkinghadang

import androidx.multidex.MultiDexApplication
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MyApplication : MultiDexApplication() {
    companion object{
        lateinit var auth : FirebaseAuth
        lateinit var db : FirebaseFirestore

        var email:String? = null
        var nickname: String? = null // 카카오 로그인 시 닉네임
        fun checkAuth() : Boolean{
            val currentUser = auth.currentUser

            return currentUser?.let{
                email = currentUser.email
                if(currentUser.isEmailVerified){
                    true
                }else{
                    false
                }
            }?:let {
                false
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
    }
}