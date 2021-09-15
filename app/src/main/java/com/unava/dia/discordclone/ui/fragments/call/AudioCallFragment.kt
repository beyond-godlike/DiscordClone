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
import androidx.fragment.app.viewModels
import com.google.firebase.database.*
import com.unava.dia.discordclone.R
import com.unava.dia.discordclone.data.User
import com.unava.dia.discordclone.other.Constants
import com.unava.dia.discordclone.other.Constants.APP_ID
import com.unava.dia.discordclone.other.Constants.PERMISSION_REQ_ID_CAMERA
import com.unava.dia.discordclone.other.Constants.PERMISSION_REQ_ID_RECORD_AUDIO
import com.unava.dia.discordclone.other.Constants.TOKEN
import com.unava.dia.discordclone.ui.fragments.chat.ChatViewModel
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

    private val viewModel: CallViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_audio_call, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        getExtras()
        viewModel.connectToFirebase()

        if (checkSelfPermission(
                Manifest.permission.RECORD_AUDIO,
                PERMISSION_REQ_ID_RECORD_AUDIO
            ) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)
        ) {
            viewModel.initializeAndJoinChannel(requireContext())
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.info.observe(requireActivity(), {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        } )
        viewModel.remoteVideo.observe(requireActivity(), {
            setupRemoteVideo(it)
        })
        viewModel.remoteUserLeft.observe(requireActivity(), {
            onRemoteUserLeft()
        })
        viewModel.remoteVideoMuted.observe(requireActivity(), { remoteMuted->
            onRemoteUserVideoMuted(remoteMuted)
        })
        viewModel.joined.observe(requireActivity(), {
            onChannelJoined()
        })
    }

    private fun onChannelJoined() {
        val localContainer = frameRemote as FrameLayout

        val localFrame = RtcEngine.CreateRendererView(requireContext())
        localContainer.addView(localFrame)
        viewModel.mRtcEngine?.setupLocalVideo(VideoCanvas(localFrame, VideoCanvas.RENDER_MODE_FIT, 0))

        viewModel.mRtcEngine?.joinChannel(TOKEN, viewModel.channelName, "", 0)
    }

    private fun getExtras() {
        //channelName = username
    }

    private fun setupUi() {
        ivOnSwitchCameraClicked.setOnClickListener {
            // BAD
            viewModel.mRtcEngine!!.switchCamera()
        }
        ivMute.setOnClickListener { iv->
        }
        ivOnLocalVideoMute.setOnClickListener { v ->

        }
        ivEndCall.setOnClickListener {
            activity?.onBackPressed()
        }
    }


    private fun onRemoteUserVideoMuted(remoteMuted: RemoteMuted) {
        frameRemote.getChildAt(0) as SurfaceView

        val tag = frameRemote.tag
        if (tag != null && tag as Int == remoteMuted.uid) {
            frameRemote.visibility = if (remoteMuted.muted) View.GONE else View.VISIBLE
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

    private fun setupRemoteVideo(uid: Int) {
        val remoteContainer = frameLocal as FrameLayout

        val remoteFrame = RtcEngine.CreateRendererView(requireContext())
        remoteFrame.setZOrderMediaOverlay(true)
        remoteContainer.addView(remoteFrame)
        viewModel.mRtcEngine?.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }
}