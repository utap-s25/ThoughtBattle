package com.example.thoughtbattle.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thoughtbattle.data.model.Debate
import com.example.thoughtbattle.data.model.User
import com.example.thoughtbattle.data.model.invalidUser
import com.example.thoughtbattle.data.repository.FirebaseRepository

import com.example.thoughtbattle.data.repository.SendBirdRepository


class MainViewModel : ViewModel() {
    private var currentAuthUser = invalidUser
    private val _currentDebate = MutableLiveData<Debate>()



    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }
    fun setCurrentDebate(debate:Debate){
        _currentDebate.value = debate

    }
    fun createnewDebate(title: String, sideA: String, sideB: String, onSuccess: Any, onError: Any):Debate{

        SendBirdRepository.createOpenChannel(title,{
             var debate =  Debate(title, sideA, sideB,  currentAuthUser.id,it)
            FirebaseRepository.createDebate(debate)
            setCurrentDebate(debate)


        }, currentAuthUser.id)
        return _currentDebate.value!!

    }
}