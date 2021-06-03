package com.example.localtrader.authentication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import com.example.localtrader.authentication.models.RegistrationUser
import com.example.localtrader.authentication.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class Authenticator(
    private val listener : AuthListener
) {

    private val auth by lazy { Firebase.auth }
    private val firestore by lazy { Firebase.firestore }
    private lateinit var user : RegistrationUser
    private var googleProfileUri : Uri? = null

    //must contain least 8 characters, 1 number, 1 upper and 1 lowercase [duplicate]
    private val passwordRegex by lazy { "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$".toRegex() }

    interface AuthListener{
        fun onError(msg : String)
    }

    interface NormalAuthListener : AuthListener{
        fun onNormalAuthCompletion(authUser: User)
    }

    interface GoogleAuthListener : AuthListener{
        fun onGoogleAuthActivityStart(intent : Intent)
        fun onGoogleAuthCompletion(authUser: User, googleProfileUri : Uri?)
    }

    fun startGoogleAuthActivity(activity : Activity){
        listener as GoogleAuthListener

        //creating client and getting intent
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("512088991736-jif4r4sog4jmlbahnkv1i61vtg0sirg1.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .build()

        val intent = GoogleSignIn.getClient(activity ,gso).signInIntent

        //starting google auth intent
        listener.onGoogleAuthActivityStart(intent)
    }

    fun registerWithLocalTrader(user : RegistrationUser){
        listener as NormalAuthListener

        this.user = user
        if (!credentialsAreValid()){
            return
        }

        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful)
                {
                    createUserInFirestore(auth.currentUser!!.uid)
                }
                else
                {
                    listener.onError(
                        if (task.exception?.message!! == "The email address is already in use by another account.") {
                            "Ez az e-mail cím már használatban van!"
                        } else{
                            "Ismeretlen hiba merült fel. Próbálja újra később!"
                        })
                }
            }
    }

    fun loginWithGoogle(account : GoogleSignInAccount){
        val tempUser = convertGoogleAccount(account)

        if (tempUser == null) listener.onError("Ismeretlen hiba merült fel. Próbálja újra később!")
        else{
            user = tempUser
            googleProfileUri = account.photoUrl
        }

        if (account.idToken == null) listener.onError("Ismeretlen hiba merült fel. Próbálja újra később!")
        logInWithGoogleToFirestore(account.idToken!!)
    }

    private fun convertGoogleAccount(account : GoogleSignInAccount) : RegistrationUser?{
        return if (account.familyName == null || account.givenName == null || account.email == null) null
        else {
            RegistrationUser(
                firstname = account.familyName!!,
                lastname = account.givenName!!,
                email = account.email!!
            )
        }
    }

    private fun logInWithGoogleToFirestore(idToken : String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        //save google user to firebase auth system
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                checkIfUserExists()
            }
            .addOnFailureListener{
                listener.onError("Ismeretlen hiba merült fel. Próbálja újra később!")
            }
    }

    private fun checkIfUserExists(){
        listener as GoogleAuthListener

        firestore.collection("users")
            .document(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()){
                    val authUser = snapshot.toObject<User>()
                    listener.onGoogleAuthCompletion(authUser!!, googleProfileUri)
                }
                else{
                    createUserInFirestore(auth.currentUser!!.uid)
                }
            }
    }

    private fun createUserInFirestore(uid : String){
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            val newUser = User(
                firstname = user.firstname,
                lastname =  user.lastname,
                email = user.email,
                messagingToken = token
            )

            //save to FireStore
            firestore.collection("users")
                .document(uid)
                .set(newUser)
                .addOnSuccessListener {
                    if (listener is GoogleAuthListener){
                        listener.onGoogleAuthCompletion(newUser, googleProfileUri)
                    }

                    if (listener is NormalAuthListener) {
                        listener.onNormalAuthCompletion(newUser)
                    }
                }
        }
    }

    private fun credentialsAreValid() : Boolean{

        if (user.password != user.passwordAgain){
            listener.onError("A jelszavak nem egyeznek!")
            return false
        }
        if (user.firstname.length < 2 || user.firstname.length > 40)
        {
            listener.onError( "Helytelen vezetéknév!")
            return false
        }
        if (user.lastname.length < 2 || user.lastname.length > 40)
        {
            listener.onError("Helytelen keresztnév!")
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(user.email).matches())
        {
            listener.onError( "Helytelen e-mail!")
            return false
        }
        if (!passwordRegex.matches(user.password))
        {
            listener.onError("A jelszónak legalább a következőket kell tartalmaznia: 1 szám, 1 nagybetű, 1 kisbetű!")
            return false
        }

        return true
    }

}