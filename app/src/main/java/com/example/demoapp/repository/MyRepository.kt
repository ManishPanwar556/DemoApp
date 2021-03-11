package com.example.demoapp.repository


import android.util.Log
import com.example.demoapp.models.Message
import com.example.demoapp.models.MessageTime
import com.example.demoapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*
import kotlin.properties.Delegates

const val TAG = "Repository"

class MyRepository {
    private val db by lazy {
        FirebaseFirestore.getInstance()
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    var status=false
    fun addUser(user: FirebaseUser?): Boolean {
        val res=GlobalScope.async {
            db.collection("users").document(user!!.uid).get().addOnSuccessListener {document->
                if (document.exists()) {
                    status = true
                    Log.e(TAG,"Exist")
                } else {
                    user.let {
                        val user =
                            User(name = it.displayName, profile = it.photoUrl.toString(), id = it.uid)
                        db.collection("users").document(it.uid).set(user).addOnSuccessListener {
                            status = true
                            Log.e(TAG,"Saved")
                        }.addOnFailureListener {
                            Log.e(TAG,"FAILURE")
                            status = false
                        }
                    }
                }
            }.addOnFailureListener {
                status=false
            }
            status
        }


    }

    fun checkUserLoggedIn(): Boolean {
        return auth.currentUser.uid != null
    }

    fun sortMessage(rid: String): Query {
        return db.collection("message").document(rid).collection("messages").orderBy(
            "createdAt",
            Query.Direction.ASCENDING
        )
    }

    fun sendMessage(rid: String, message: String, time: Long) {
        db.collection("users").document(auth.currentUser.uid).get().addOnSuccessListener {
            val name = it.get("name").toString()
            uploadMessage(name, rid, message, time)
        }

    }

    private fun uploadMessage(name: String, rid: String, text: String, time: Long) {
        val message = Message(text, name, time)
        val messageTime = MessageTime(
            text, name, time, rid
        )
        db.collection("message").document(rid).set(messageTime).addOnSuccessListener {
            val messageId = UUID.randomUUID().toString()
            db.collection("message").document(rid).collection("messages").document(messageId)
                .set(message).addOnSuccessListener {
                    Log.e(TAG, "Message success")
                }.addOnFailureListener {
                    Log.e(TAG, "Message failure")
                }
        }

    }

    fun getListOfUsers(): Query {
        val uid = auth.currentUser.uid
        return db.collection("users").whereNotEqualTo("id", uid)
    }

    fun makeGroup() {
        db.collection("message").document("group").get().addOnSuccessListener {
            if (it.exists()) {
                Log.e("MainActivity", "Exist")
            } else {
                val message = MessageTime(id = "group")
                Log.e(TAG, "Not Exist")
                db.collection("message").document("group").set(message).addOnSuccessListener {
                    Log.e(TAG, "Complete")
                }
            }
        }
    }
    fun getChats():Query{
        return db.collection("message").orderBy("time",Query.Direction.DESCENDING)
    }
    fun signOut(){
        auth.signOut()
    }
}
