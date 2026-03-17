(function () {
    'use strict';

    const CHANNEL_ID = 104;
    const TAP_WINDOW_MS = 400;

    let saAgent = null;
    let saSocket = null;
    let tapCount = 0;
    let tapTimer = null;
    const feedbackEl = document.getElementById('feedback');
    const statusDot = document.getElementById('status-dot');

    // --- SAP Setup ---
    function initSAP() {
        webapis.sa.requestSAAgent(
            function (agents) {
                if (agents.length === 0) return;
                saAgent = agents[0];
                saAgent.setServiceConnectionListener({
                    onrequest: function (peerAgent) {
                        saAgent.acceptServiceConnectionRequest(peerAgent);
                    },
                    onconnect: function (socket) {
                        saSocket = socket;
                        statusDot.classList.add('connected');
                        socket.setSocketStatusListener(function (reason) {
                            saSocket = null;
                            statusDot.classList.remove('connected');
                        });
                    }
                });
                saAgent.findPeerAgents();
            },
            function (err) {
                console.error('SAP init error:', err);
            }
        );
    }

    // --- Send command to phone ---
    function sendAction(action) {
        if (!saSocket) { showFeedback(action, true); return; }
        const msg = JSON.stringify({ action: action });
        saSocket.sendData(CHANNEL_ID, msg);
        showFeedback(action, false);
    }

    // --- Visual feedback ---
    function showFeedback(action, offline) {
        feedbackEl.className = 'show';
        switch (action) {
            case 'point_me':
                feedbackEl.textContent = offline ? 'NOI ✓' : 'NOI';
                feedbackEl.classList.add('my-point');
                break;
            case 'point_opp':
                feedbackEl.textContent = offline ? 'LORO ✓' : 'LORO';
                feedbackEl.classList.add('opp-point');
                break;
            case 'undo':
                feedbackEl.textContent = '↩';
                feedbackEl.classList.add('undo');
                break;
        }
        setTimeout(function () {
            feedbackEl.className = '';
            feedbackEl.textContent = '';
        }, 800);
    }

    // --- Tap detection ---
    document.addEventListener('click', function () {
        tapCount++;
        clearTimeout(tapTimer);
        tapTimer = setTimeout(function () {
            const count = tapCount;
            tapCount = 0;
            if (count === 1) sendAction('point_me');
            else if (count === 2) sendAction('point_opp');
            else if (count >= 3) sendAction('undo');
        }, TAP_WINDOW_MS);
    });

    // --- Init ---
    document.addEventListener('DOMContentLoaded', function () {
        if (typeof webapis !== 'undefined' && webapis.sa) {
            initSAP();
        }
    });
}());
