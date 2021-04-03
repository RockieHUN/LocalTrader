package com.example.localtrader.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localtrader.chat.models.ChatInfo
import com.example.localtrader.chat.models.ChatMessage
import com.example.localtrader.chat.models.MessageInfo
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class MessagesViewModel(): ViewModel() {

    var chatInfo : ChatInfo? = null
    private val firestore = Firebase.firestore
    val chatItemMessages : MutableLiveData<List<ChatMessage>> = MutableLiveData()
    val messagesInfoList : MutableLiveData<List<MessageInfo>> = MutableLiveData()


    fun loadChat(){
        if (chatInfo == null) return

        firestore.collection("businesses")
            .document(chatInfo!!.businessId)
            .collection("chatCollection")
            .document(chatInfo!!.userId)
            .collection(chatInfo!!.userId)
            .orderBy("date", Query.Direction.ASCENDING)
            .limit(20)
            .addSnapshotListener{ snapshot, e ->

                if (e != null){
                    Firebase.crashlytics.log(e.toString())
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty){
                    chatItemMessages.value = snapshot.toObjects<ChatMessage>()
                }
            }
    }

    fun loadMessages(userId : String){
        firestore.collection("users")
            .document(userId)
            .collection("chatCollection")
            .orderBy("last_message_date", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { snapshot ->
                messagesInfoList.value = snapshot.toObjects<MessageInfo>()
            }
    }

    fun sendMessage(message : ChatMessage){
        addMessageToBusiness(message)
        addMessageToUser(message)
    }

    // PRIVATE FUNCTIONS

    private fun addMessageToBusiness(message : ChatMessage){
        if (chatInfo == null) return

        //add message
        firestore.collection("businesses")
            .document(chatInfo!!.businessId)
            .collection("chatCollection")
            .document(chatInfo!!.userId)
            .collection(chatInfo!!.userId)
            .add(message)

        //update the date of the last modification and friend? name
        val map = mapOf(
            "last_message_date" to  message.date,
            "last_message" to message.message,
            "client" to chatInfo!!.userName)

        firestore.collection("businesses")
            .document(chatInfo!!.businessId)
            .collection("chatCollection")
            .document(chatInfo!!.userId)
            .set(map)
    }

    private fun addMessageToUser(message : ChatMessage){
        if (chatInfo == null) return

        //add message
        firestore.collection("users")
            .document(chatInfo!!.userId)
            .collection("chatCollection")
            .document(chatInfo!!.businessId)
            .collection(chatInfo!!.businessId)
            .add(message)

        //update the date of the last modification and friend? name
        val map = mapOf(
            "last_message_date" to  message.date,
            "last_message" to  message.message,
            "client" to chatInfo!!.businessName
        )

        firestore.collection("users")
            .document(chatInfo!!.userId)
            .collection("chatCollection")
            .document(chatInfo!!.businessId)
            .set(map)
    }
}