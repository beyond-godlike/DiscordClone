package com.unava.dia.discordclone.ui.fragments.call

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.unava.dia.discordclone.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_audio_call.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AudioCallFragment : Fragment() {

    var username = "enokentiy"
    var friendsUsername = "Diana"

    var isPeerConnected = false

    @Inject
    lateinit var firebaseDb: FirebaseDatabase

    var firebaseRef: DatabaseReference? = null

    var isAudio = true
    var isVideo = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_audio_call, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseRef = firebaseDb.reference.child("")
        username = "enokentiy"

        callBtn.setOnClickListener {
            //friendsUsername = friendNameEdit.text.toString()
            sendCallRequest()
        }


        toggleAudioBtn.setOnClickListener {
            isAudio = !isAudio
            callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")")
            toggleAudioBtn.setImageResource(if (isAudio) R.drawable.ic_action_call else R.drawable.ic_action_add)
        }

        toggleVideoBtn.setOnClickListener {
            isVideo = !isVideo
            callJavascriptFunction("javascript:toggleVideo(\"${isVideo}\")")
            toggleVideoBtn.setImageResource(if (isVideo) R.drawable.ic_action_video else R.drawable.ic_action_video)
        }

        setupWebView()

    }


    private fun sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(
                requireContext(),
                "You're not connected. Check your internet",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        firebaseRef!!.child(friendsUsername).child("incoming").setValue(username)
        firebaseRef!!.child(friendsUsername).child("isAvailable")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.value.toString() == "true") {
                        listenForConnId()
                    }

                }

            })

    }

    private fun listenForConnId() {
        firebaseRef!!.child(friendsUsername).child("connId")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null)
                        return
                    switchToControls()
                    callJavascriptFunction("javascript:startCall(\"${snapshot.value}\")")
                }

            })
    }

    private fun loadVideoCall() {
        val filePath = "file:android_asset/call.html"
        webView.loadUrl(filePath)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                initializePeer()
            }
        }
    }

    var uniqueId = ""
    private fun initializePeer() {

        uniqueId = getUniqueID()

        callJavascriptFunction("javascript:init(\"${uniqueId}\")")
        firebaseRef!!.child(username).child("incoming")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    onCallRequest(snapshot.value as? String)
                }

            })

    }

    private fun setupWebView() {

        webView.webChromeClient = object : WebChromeClient() {
            @SuppressLint("NewApi")
            override fun onPermissionRequest(request: PermissionRequest?) {
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                request?.grant(request.resources)
                //}
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.addJavascriptInterface(JavascriptInterface(this), "Android")

        loadVideoCall()
    }

    private fun onCallRequest(caller: String?) {
        if (caller == null) return

        callLayout.visibility = View.VISIBLE
        incomingCallTxt.text = "$caller is calling..."

        acceptBtn.setOnClickListener {
            firebaseRef!!.child(username).child("connId").setValue(uniqueId)
            firebaseRef!!.child(username).child("isAvailable").setValue(true)

            callLayout.visibility = View.GONE
            switchToControls()
        }

        rejectBtn.setOnClickListener {
            firebaseRef!!.child(username).child("incoming").setValue(null)
            callLayout.visibility = View.GONE
        }

    }


    private fun switchToControls() {
        inputLayout.visibility = View.GONE
        callControlLayout.visibility = View.VISIBLE
    }


    private fun getUniqueID(): String {
        return UUID.randomUUID().toString()
    }

    private fun callJavascriptFunction(functionString: String) {
        webView.post { webView.evaluateJavascript(functionString, null) }
    }


    fun onPeerConnected() {
        Toast.makeText(requireContext(), "connecting", Toast.LENGTH_LONG).show()
        isPeerConnected = true
    }

    override fun onDestroy() {
        //firebaseRef!!.child(username).setValue(null)
        webView.loadUrl("about:blank")
        super.onDestroy()
    }


}