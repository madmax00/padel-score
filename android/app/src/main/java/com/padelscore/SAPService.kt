package com.padelscore

import android.content.Context
import android.util.Log
import com.samsung.android.sdk.SsdkUnsupportedException
import com.samsung.android.sdk.accessory.*
import org.json.JSONObject

class SAPService(context: Context) : SAAgentV2(TAG, context, PadelScoreSocket::class.java) {

    init {
        val accessory = SA()
        try {
            accessory.initialize(context)
        } catch (e: SsdkUnsupportedException) {
            Log.e(TAG, "SDK not supported", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize SDK", e)
            releaseAgent()
        }
    }

    companion object {
        const val TAG = "SAPService"
        const val CHANNEL_ID = 104
        var viewModel: ScoreViewModel? = null
    }

    override fun onFindPeerAgentsResponse(peerAgents: Array<out SAPeerAgent>?, result: Int) {
        Log.d(TAG, "onFindPeerAgentsResponse: result=$result")
    }

    override fun onServiceConnectionRequested(peerAgent: SAPeerAgent?) {
        peerAgent?.let { acceptServiceConnectionRequest(it) }
    }

    override fun onServiceConnectionResponse(peerAgent: SAPeerAgent?, socket: SASocket?, result: Int) {
        Log.d(TAG, "Connection response: result=$result")
    }

    override fun onError(peerAgent: SAPeerAgent?, errorMessage: String?, errorCode: Int) {
        Log.e(TAG, "Error: $errorMessage ($errorCode)")
    }

    class PadelScoreSocket : SASocket("PadelScoreSocket") {
        override fun onError(channelId: Int, errorMessage: String?, errorCode: Int) {
            Log.e(TAG, "Socket error on ch$channelId: $errorMessage")
        }

        override fun onReceive(channelId: Int, data: ByteArray?) {
            if (channelId != CHANNEL_ID || data == null) return
            try {
                val json = JSONObject(String(data))
                val action = json.getString("action")
                Log.d(TAG, "Received action: $action")
                viewModel?.handleWatchCommand(action)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse message", e)
            }
        }

        override fun onServiceConnectionLost(reason: Int) {
            Log.d(TAG, "Connection lost: reason=$reason")
        }
    }
}
