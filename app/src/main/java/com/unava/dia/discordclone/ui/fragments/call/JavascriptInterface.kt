package com.unava.dia.discordclone.ui.fragments.call

class JavascriptInterface(val callFragment: AudioCallFragment) {

    @android.webkit.JavascriptInterface
    public fun onPeerConnected() {
        callFragment.onPeerConnected()
    }

}