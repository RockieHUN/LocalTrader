package com.example.localtrader.viewmodels

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localtrader.authentication.models.User
import com.example.localtrader.business.models.Business
import com.example.localtrader.chat.models.ChatLoadInformation
import com.example.localtrader.chat.models.ChatMessage
import com.example.localtrader.chat.models.MessageInfo
import com.example.localtrader.utils.comparators.FirebaseDateComparator
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class MessagesViewModel(): ViewModel() {

    var chatLoadInformation : ChatLoadInformation? = null

    private val firestore = Firebase.firestore
    val chatItemMessages : MutableLiveData<List<ChatMessage>> = MutableLiveData()
    val messagesInfoList : MutableLiveData<List<MessageInfo>> = MutableLiveData()


    fun loadChat(header : TextView){
        if (chatLoadInformation == null) return
        val businessId = chatLoadInformation!!.businessId
        val userId = chatLoadInformation!!.userId
        val whoIsTheOther = chatLoadInformation!!.whoIsTheOther

        if (whoIsTheOther == 1){
            firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val user : User? = snapshot.toObject()
                    if (user != null){
                        header.text = "${user.firstname} ${user.lastname}"
                    }
                }
        }
        else{
            firestore.collection("businesses")
                .document(businessId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val business : Business? = snapshot.toObject()
                    if (business != null){
                        header.text = business.name
                    }
                }
        }

        firestore.collection("businesses")
            .document(businessId)
            .collection("chatCollection")
            .document(userId)
            .collection(userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener{ snapshot, e ->

                if (e != null){
                    Firebase.crashlytics.log(e.toString())
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty){
                    chatItemMessages.value = snapshot.toObjects<ChatMessage>().reversed()
                }
            }
    }

    fun loadMessages(userId : String, businessId : String?){
        firestore.collection("users")
            .document(userId)
            .collection("chatCollection")
            .orderBy("last_message_date", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { snapshot ->
                var messages = snapshot.toObjects<MessageInfo>().toMutableList()

                if (businessId != null && businessId != ""){
                    firestore.collection("businesses")
                        .document(businessId)
                        .collection("chatCollection")
                        .orderBy("last_message_date", Query.Direction.DESCENDING)
                        .limit(20)
                        .get()
                        .addOnSuccessListener { snapshot2 ->
                            messages.addAll(snapshot2.toObjects())
                            messages = messages.sortedWith(FirebaseDateComparator).toMutableList()
                            messagesInfoList.value = messages
                        }
                        .addOnFailureListener{
                            messagesInfoList.value = messages
                        }
                }
                else{
                    messagesInfoList.value = messages
                }
            }
    }

    fun sendMessage(message : ChatMessage){
        addMessageToBusiness(message)
        addMessageToUser(message)
    }

    // PRIVATE FUNCTIONS

    private fun addMessageToBusiness(message : ChatMessage){
        if (chatLoadInformation == null) return
        val businessId = chatLoadInformation!!.businessId
        val userId = chatLoadInformation!!.userId

        //add message
        firestore.collection("businesses")
            .document(businessId)
            .collection("chatCollection")
            .document(userId)
            .collection(userId)
            .add(message)

        //update the date of the last modification and friend? name
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val user : User? = snapshot.toObject()
                if (user != null){
                    val info = MessageInfo(
                        senderName = "${user.firstname} ${user.lastname}",
                        last_message = message.message,
                        last_message_date = message.date,
                        senderId = userId,
                        senderType = 1
                    )
                    firestore.collection("businesses")
                        .document(businessId)
                        .collection("chatCollection")
                        .document(userId)
                        .set(info)
                }
            }


    }

    private fun addMessageToUser(message : ChatMessage){
        if (chatLoadInformation== null) return
        val businessId = chatLoadInformation!!.businessId
        val userId = chatLoadInformation!!.userId

        //add message
        firestore.collection("users")
            .document(userId)
            .collection("chatCollection")
            .document(businessId)
            .collection(businessId)
            .add(message)

        //update the date of the last modification and friend? name
        firestore.collection("businesses")
            .document(businessId)
            .get()
            .addOnSuccessListener { snapshot ->
                val business : Business? = snapshot.toObject()
                if (business!= null){
                    val info = MessageInfo(
                        senderName = business.name,
                        last_message = message.message,
                        last_message_date = message.date,
                        senderId = businessId,
                        senderType = 2)

                    firestore.collection("users")
                        .document(userId)
                        .collection("chatCollection")
                        .document(businessId)
                        .set(info)
                }
            }
    }
}