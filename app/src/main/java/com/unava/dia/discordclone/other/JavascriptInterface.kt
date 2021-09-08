package com.unava.dia.discordclone.other

import com.unava.dia.discordclone.ui.fragments.AudioCallFragment

class JavascriptInterface(val callFragment: AudioCallFragment) {

    @android.webkit.JavascriptInterface
    public fun onPeerConnected() {
        callFragment.onPeerConnected()
    }

}