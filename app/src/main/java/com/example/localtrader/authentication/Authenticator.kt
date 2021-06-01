package com.example.localtrader.authentication

import android.util.Patterns
import com.example.localtrader.authentication.models.RegistrationUser
import com.example.localtrader.authentication.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception

class Authenticator(
    private val authenticatorListener : AuthenticatorListener
) {

    class InvalidValueException(msg : String) : Exception(msg)

    private val auth by lazy { Firebase.auth }
    private val firestore by lazy { Firebase.firestore }
    private lateinit var user : RegistrationUser

    //must contain least 8 characters, 1 number, 1 upper and 1 lowercase [duplicate]
    private val passwordRegex by lazy { "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$".toRegex() }


    companion object{

        val AUTH_TYPE_GOOGLE by lazy{ 1 }
        val AUTH_TYPE_LOCAL_TRADER by lazy { 2 }
    }

    interface AuthenticatorListener{
        fun onError(msg : String)
        fun onCompletion(newUser : User)
    }


    fun register(type : Int, user : RegistrationUser){
        this.user = user

        when(type){
            AUTH_TYPE_GOOGLE -> registerWithGoogle()
            AUTH_TYPE_LOCAL_TRADER -> registerWithLocalTrader()
            else -> throw InvalidValueException("Invalid authentication type! Check the authenticator class!")
        }

    }

    private fun registerWithGoogle(){

    }

    private fun registerWithLocalTrader(){
        if (!credentialsAreValid()){
            return
        }

        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful)
                {
                    createUserInFirestore()
                }
                else
                {
                    authenticatorListener.onError(
                        if (task.exception?.message!! == "The email address is already in use by another account.") {
                            "Ez az e-mail cím már használatban van!"
                        } else{
                            "A regisztráció ismeretlen okból nem sikerült. Próbálja újra később"
                        })
                }
            }


    }

    private fun createUserInFirestore(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            val newUser = User(
                firstname = user.firstname,
                lastname =  user.lastname,
                email = user.email,
                messagingToken = token
            )

            //save to FireStore
            firestore.collection("users")
                .document(auth.currentUser!!.uid)
                .set(newUser)
                .addOnSuccessListener {
                    authenticatorListener.onCompletion(newUser)
                }
        }
    }

    private fun credentialsAreValid() : Boolean{

        if (user.password != user.passwordAgain){
            authenticatorListener.onError("A jelszavak nem egyeznek!")
            return false
        }
        if (user.firstname.length < 2 || user.firstname.length > 40)
        {
            authenticatorListener.onError( "Helytelen vezetéknév!")
            return false
        }
        if (user.lastname.length < 2 || user.lastname.length > 40)
        {
            authenticatorListener.onError("Helytelen keresztnév!")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(user.email).matches())
        {
            authenticatorListener.onError( "Helytelen e-mail!")
            return false
        }

        if (!passwordRegex.matches(user.password))
        {
            authenticatorListener.onError("A jelszónak legalább a következőket kell tartalmaznia: 1 szám, 1 nagybetű, 1 kisbetű!")
            return false
        }

        return true
    }

}