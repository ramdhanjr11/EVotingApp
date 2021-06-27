package com.muramsyah.evotingapp.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.muramsyah.evotingapp.model.CalonKahim
import com.muramsyah.evotingapp.model.Mahasiswa
import com.muramsyah.evotingapp.model.SetupSystem
import com.muramsyah.evotingapp.utils.FireBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var dataCalonKahim = MutableLiveData<ArrayList<CalonKahim>>()
    var dataUser = MutableLiveData<Mahasiswa>()
    var dataSystem = MutableLiveData<SetupSystem>()

    private val context = application

    init {
        getDataKahim()
        getDataUser()
        getSystemsFromDatabase()
    }

    private fun getDataKahim() {
        viewModelScope.launch(Dispatchers.IO) {
            FireBaseUtils.ref.getReference("CalonKahim")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val result = ArrayList<CalonKahim>()
                        for (i in snapshot.children) {
                            i.getValue(CalonKahim::class.java)?.let { result.add(it) }
                        }
                        dataCalonKahim.postValue(result)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    private fun getDataUser() {
        viewModelScope.launch(Dispatchers.IO) {
            FireBaseUtils.auth.currentUser?.let {
                FireBaseUtils.ref.getReference("Users").child(it.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        @SuppressLint("NullSafeMutableLiveData")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(Mahasiswa::class.java)
                            if (user != null) {
                                dataUser.postValue(user)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
            }
        }
    }

    fun voteCalonKahim1() {
        dataUser.value?.let {
            FireBaseUtils.ref.getReference("Users").child(it.id).child("voteId").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == "1" || snapshot.value == "2") {
                        Toast.makeText(context, "Kamu hanya bisa voting 1x", Toast.LENGTH_SHORT).show()
                    } else {
                        FireBaseUtils.ref.getReference("Users").child(it.id).child("voteId").setValue("1")
                        FireBaseUtils.ref.getReference("CalonKahim").child("1830511049").child("voteCount").setValue(ServerValue.increment(1))
                        FireBaseUtils.ref.getReference("Users").child(it.id).child("vote").setValue(true).addOnCompleteListener {
                            Toast.makeText(context, "Kamu telah memvoting Ramdhan!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    fun voteCalonKahim2() {
        dataUser.value?.let {
            FireBaseUtils.ref.getReference("Users").child(it.id).child("voteId").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == "1" || snapshot.value == "2") {
                        Toast.makeText(context, "Kamu hanya bisa voting 1x", Toast.LENGTH_SHORT).show()
                    } else {
                        FireBaseUtils.ref.getReference("Users").child(it.id).child("voteId").setValue("2")
                        FireBaseUtils.ref.getReference("CalonKahim").child("1830511048").child("voteCount").setValue(ServerValue.increment(1))
                        FireBaseUtils.ref.getReference("Users").child(it.id).child("vote").setValue(true).addOnCompleteListener {
                            Toast.makeText(context, "Kamu telah memvoting Dais!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    fun getSystemsFromDatabase() {
        FireBaseUtils.ref.getReference("SetupSystem").child("voteSystem")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = ArrayList<Any>()
                    for (i in snapshot.children) {
                        result.add(i.value as Any)
                    }
                    dataSystem.value = SetupSystem(result[0].toString(), result[1] as Boolean)
                    Log.d("result", result.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}