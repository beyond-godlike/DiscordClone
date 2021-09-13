package com.unava.dia.discordclone.ui.fragments.call

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.unava.dia.discordclone.R
import com.unava.dia.discordclone.other.Constants.APP_ID
import com.unava.dia.discordclone.other.Constants.CHANNEL
import com.unava.dia.discordclone.other.Constants.PERMISSION_REQ_ID_CAMERA
import com.unava.dia.discordclone.other.Constants.PERMISSION_REQ_ID_RECORD_AUDIO
import com.unava.dia.discordclone.other.Constants.TOKEN
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import kotlinx.android.synthetic.main.fragment_audio_call.*
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class AudioCallFragment : Fragment() {

    var username = "enokentiy"
    var friendsUsername = "Diana"

    @Inject
    lateinit var firebaseDb: FirebaseDatabase

    @Inject
    @Named("main")
    lateinit var dispatcher:CoroutineDispatcher

    var firebaseRef: DatabaseReference? = null

    private var mRtcEngine: RtcEngine? = null

    private var mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            GlobalScope.launch(dispatcher) {
                setupRemoteVideo(uid)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            GlobalScope.launch(dispatcher) {
                onRemoteUserLeft()
            }
        }
        override fun onUserMuteVideo(uid: Int, muted: Boolean) {
            GlobalScope.launch(dispatcher) { onRemoteUserVideoMuted(uid, muted) }
        }
    }

    private fun onRemoteUserVideoMuted(uid: Int, muted: Boolean) {
        frameRemote.getChildAt(0) as SurfaceView

        val tag = frameRemote.tag
        if (tag != null && tag as Int == uid) {
            frameRemote.visibility = if (muted) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_audio_call, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()

        if (checkSelfPermission(
                Manifest.permission.RECORD_AUDIO,
                PERMISSION_REQ_ID_RECORD_AUDIO
            ) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)
        ) {
            initializeAndJoinChannel()
        }
    }

    private fun setupUi() {
        ivOnSwitchCameraClicked.setOnClickListener {
            mRtcEngine!!.switchCamera()
        }
        ivMute.setOnClickListener { iv->
        }
        ivOnLocalVideoMute.setOnClickListener { v ->

        }
        ivEndCall.setOnClickListener {
            activity?.onBackPressed()
        }
    }


    private fun onRemoteUserLeft() {
        frameRemote.removeAllViews()
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
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }
        mRtcEngine = RtcEngine.create(requireContext(), APP_ID, mRtcEventHandler)
        mRtcEngine?.enableVideo()

        val localContainer = frameRemote as FrameLayout

        val localFrame = RtcEngine.CreateRendererView(requireContext())
        localContainer.addView(localFrame)
        mRtcEngine?.setupLocalVideo(VideoCanvas(localFrame, VideoCanvas.RENDER_MODE_FIT, 0))

        mRtcEngine?.joinChannel(TOKEN, CHANNEL, "", 0)
    }

    private fun setupRemoteVideo(uid: Int) {
        val remoteContainer = frameLocal as FrameLayout

        val remoteFrame = RtcEngine.CreateRendererView(requireContext())
        remoteFrame.setZOrderMediaOverlay(true)
        remoteContainer.addView(remoteFrame)
        mRtcEngine?.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    override fun onDestroy() {
        super.onDestroy()

        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }
}