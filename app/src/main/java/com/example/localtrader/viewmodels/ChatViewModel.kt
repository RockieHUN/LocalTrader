package com.example.localtrader.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localtrader.orders.models.ChatMessage
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class ChatViewModel(): ViewModel() {

    var businessId : String? = null
    var clientId : String? = null
    private val firestore = Firebase.firestore
    var messages : MutableLiveData<List<ChatMessage>> = MutableLiveData()


    fun loadChat(){

        firestore.collection("businesses")
            .document(businessId!!)
            .collection("chatCollection")
            .document("chatDocument")
            .collection(clientId!!)
            .orderBy("date", Query.Direction.ASCENDING)
            .limit(20)
            .addSnapshotListener{ snapshot, e ->

                if (e != null){
                    Firebase.crashlytics.log(e.toString())
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty){
                    messages.value = snapshot.toObjects<ChatMessage>()
                }
            }



    }

    fun sendMessage(message : ChatMessage){

        firestore.collection("businesses")
            .document(businessId!!)
            .collection("chatCollection")
            .document("chatDocument")
            .collection(clientId!!)
            .add(message)


    }
}