package com.example.demoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demoapp.repository.MyRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query

class MyViewModel : ViewModel() {

    private var repository: MyRepository = MyRepository()
    init {

    }
    fun addUserToFireStore(user: FirebaseUser):Boolean{
        return repository.addUser(user)
    }
    fun checkUserLoggedIn():Boolean{
        return repository.checkUserLoggedIn()
    }

    fun sendMessageToFireStore(rid:String,time:Long,message:String){
        repository.sendMessage(rid,message,time)
    }

    fun makeGroup(){
        repository.makeGroup()
    }
    fun queryMessage(rid: String): Query {
        return repository.sortMessage(rid)
    }

    fun queryListOfUser():Query{
        return repository.getListOfUsers()
    }

    fun queryChats():Query{
        return repository.getChats()
    }
    fun signOutUser(){
        repository.signOut()
    }
}