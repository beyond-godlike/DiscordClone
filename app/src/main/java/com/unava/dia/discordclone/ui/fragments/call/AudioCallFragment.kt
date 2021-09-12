package com.unava.dia.discordclone.ui.fragments.call

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.unava.dia.discordclone.R
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import kotlinx.android.synthetic.main.fragment_audio_call.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AudioCallFragment : Fragment() {

    var username = "enokentiy"
    var friendsUsername = "Diana"


    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    @Inject
    lateinit var firebaseDb: FirebaseDatabase

    var firebaseRef: DatabaseReference? = null

    private val APP_ID = "7e80f20eb83a45aab8354ac12cc"
    private val CHANNEL = "discordCloneChannel"
    private val TOKEN =
        "0067e80f20eb83a45aab8354ac12cc81829IADrCgR+uYbWJmYfj7TDAfUl+avwx6qs8dWWLwhm6lGs857BI3IAAAAAEAAQK9Jva0Y/YQEAAQBrRj9h"
    private var mRtcEngine: RtcEngine? = null

    private var mRtcEventHandler: IRtcEngineEventHandler? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_audio_call, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRtcEventHandler()

        if (checkSelfPermission(
                Manifest.permission.RECORD_AUDIO,
                PERMISSION_REQ_ID_RECORD_AUDIO
            ) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)
        ) {
            initializeAndJoinChannel()
        }
    }

    private fun initRtcEventHandler() {
        mRtcEventHandler = object : IRtcEngineEventHandler() {
            // Listen for the remote user joining the channel to get the uid of the user.
            override fun onUserJoined(uid: Int, elapsed: Int) {
                GlobalScope.launch(Dispatchers.Main) {
                    // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                    setupRemoteVideo(uid)
                }
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                //runOnUiThread { onRemoteUserLeft() }
            }
        }
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), permission) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permission),
                requestCode
            )
            return false
        }
        return true
    }

    private fun initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(requireContext(), APP_ID, mRtcEventHandler)
        } catch (e: Exception) {

        }
        mRtcEngine!!.enableVideo()

        val localContainer = local_video_view_container as FrameLayout

        val localFrame = RtcEngine.CreateRendererView(requireContext())
        localContainer.addView(localFrame)
        mRtcEngine!!.setupLocalVideo(VideoCanvas(localFrame, VideoCanvas.RENDER_MODE_FIT, 0))

        mRtcEngine!!.joinChannel(TOKEN, CHANNEL, "", 0)
    }

    private fun setupRemoteVideo(uid: Int) {
        val remoteContainer = remote_video_view_container as FrameLayout

        val remoteFrame = RtcEngine.CreateRendererView(requireContext())
        remoteFrame.setZOrderMediaOverlay(true)
        remoteContainer.addView(remoteFrame)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    override fun onDestroy() {
        super.onDestroy()

        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }
}