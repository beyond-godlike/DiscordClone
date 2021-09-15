package com.unava.dia.discordclone.ui.fragments.call

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.unava.dia.discordclone.data.User
import com.unava.dia.discordclone.other.Constants
import com.unava.dia.discordclone.other.Constants.APP_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class CallViewModel @Inject constructor(
    private val firebaseDb: FirebaseDatabase,
    @Named("main")
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    var username = "Andrew"
    var channelName = "enokentiy"

    private var DBFriend: List<String> = ArrayList()

    private var localState: String = Constants.USER_STATE_OPEN

    var mRtcEngine: RtcEngine? = null

    var info: MutableLiveData<String> = MutableLiveData()
    var remoteVideo: MutableLiveData<Int> = MutableLiveData()
    var remoteUserLeft: MutableLiveData<Boolean> = MutableLiveData()
    var remoteVideoMuted: MutableLiveData<RemoteMuted> = MutableLiveData()
    var joined: MutableLiveData<Boolean> = MutableLiveData()

    private var mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            GlobalScope.launch(dispatcher) {
                info.postValue("joined")
                firebaseDb
                    .getReference("users")
                    .child(username)
                    .setValue(User(username, uid, localState, DBFriend))
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            GlobalScope.launch(dispatcher) {
                remoteVideo.postValue(uid)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            GlobalScope.launch(dispatcher) {
                remoteUserLeft.postValue(true)
            }
        }

        override fun onUserMuteVideo(uid: Int, muted: Boolean) {
            GlobalScope.launch(dispatcher) {
                remoteVideoMuted.postValue(RemoteMuted(uid, muted))
            }
        }
    }

    fun connectToFirebase() {
        val dbUsers = firebaseDb.getReference("users")

        dbUsers.push()
        dbUsers.child(username).child("friend").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //DBFriend = snapshot.value as List<String>

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun initializeAndJoinChannel(context: Context) {
        try {
            mRtcEngine = RtcEngine.create(context, APP_ID, mRtcEventHandler)
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
        mRtcEngine = RtcEngine.create(context, APP_ID, mRtcEventHandler)
        mRtcEngine?.enableVideo()

        joined.postValue(true)
    }

}

data class RemoteMuted(val uid: Int, val muted: Boolean)
