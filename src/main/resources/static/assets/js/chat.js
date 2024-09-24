// chat.js

document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(window.location.search);
    const chatRoomId = urlParams.get('chatRoomId');

    if (chatRoomId) {
        openChatRoom(chatRoomId);
    }

    reloadChatRooms();
    reloadPartners();
});

function reloadChatRooms() {
    fetch('/talk/chat-rooms')
        .then(response => response.ok ? response.text() : Promise.reject('네트워크 응답 오류'))
        .then(text => text ? JSON.parse(text) : [])
        .then(chatRooms => updateChatRoomList(chatRooms))
        .catch(error => console.error('대화방 목록 로딩 중 오류:', error));
}

function reloadPartners() {
    fetch('/talk/partners')
        .then(response => response.json())
        .then(partners => updatePartnerList(partners))
        .catch(error => console.error('연락처 목록 로딩 중 오류:', error));
}

function updateChatRoomList(chatRooms) {
    const chatRoomListContainer = document.querySelector('#talk-list-nav');
    chatRoomListContainer.innerHTML = chatRooms.length === 0
        ? '<li class="nav-item"><span>대화방이 없습니다.</span></li>'
        : chatRooms.map(room => `
            <li class="nav-item">
                <a class="nav-link chatroom-link" href="javascript:void(0);" data-id="${room.idx}">
                    <span>${room.name}</span>
                    <span>${room.lastChatTime || ''}</span>
                    <span>${room.lastMessage ? room.lastMessage.substring(0, 12) : ''}</span>
                </a>
            </li>
        `).join('');

    document.querySelectorAll('.chatroom-link').forEach(link => {
        link.addEventListener('click', event => {
            event.preventDefault();
            openChatRoom(link.getAttribute('data-id'));
        });
    });
}

function updatePartnerList(partners) {
    const partnerListContainer = document.querySelector('#partner-list-nav');
    partnerListContainer.innerHTML = partners.length === 0
        ? '<li class="nav-item"><span>연락처가 없습니다.</span></li>'
        : partners.map(partner => `
            <li class="nav-item">
                <a class="nav-link contact-link" href="javascript:void(0);" data-id="${partner.id}">
                    ${partner.name}
                </a>
            </li>
        `).join('');

    document.querySelectorAll('.contact-link').forEach(link => {
        link.addEventListener('click', event => {
            event.preventDefault();
            loadPartnerDetails(link.getAttribute('data-id'));
        });
    });
}

function loadPartnerDetails(partnerId) {
    fetch(`/partners/${partnerId}`)
        .then(response => response.json())
        .then(partner => {
            const dynamicContent = document.getElementById('dynamic-content');
            dynamicContent.innerHTML = '';
            dynamicContent.appendChild(createProfileCard(partner));

            const chatButton = document.createElement('button');
            chatButton.innerText = "대화하기";
            chatButton.classList.add('btn', 'btn-primary', 'center-button');
            chatButton.addEventListener('click', () => startChat(partnerId));

            const initialMessage = document.getElementById('initial-message');
            if (initialMessage) initialMessage.style.display = 'none';

            dynamicContent.appendChild(chatButton);
        })
        .catch(error => console.error('연락처 정보 로딩 중 오류:', error));
}

function createProfileCard(partner) {
    const card = document.createElement('div');
    card.classList.add('initial-partner-profile', 'd-flex', 'flex-column', 'align-items-center');

    card.innerHTML = `
        <img src="../assets/img/profile-2398783_640.png" alt="Profile" class="rounded-circle">
        <div class="d-grid gap-2 mt-3">
            <h5>이름 : <span>${partner.name || 'ID'}</span></h5>
        </div>
        <div class="d-grid gap-2 mt-3">
            <h5>이메일 : <span>${partner.email || 'Email'}</span></h5>
        </div>
    `;
    return card;
}

function startChat(partnerId) {
    fetch(`/chat-room/partner/${partnerId}`)
        .then(response => response.json())
        .then(data => {
            const chatRoomId = data.chatRoomId;
            if (chatRoomId) {
                openChatRoom(chatRoomId);
                switchToTalkList();
                reloadChatRooms();
            } else {
                console.error('대화방 ID가 없습니다.');
            }
        })
        .catch(error => console.error('대화방을 찾거나 생성하는 중 오류 발생:', error));
}

function switchToTalkList() {
    const partnerListNav = document.querySelector('#partner-list-nav');
    const talkListNav = document.querySelector('#talk-list-nav');

    if (partnerListNav && partnerListNav.classList.contains('show')) {
        partnerListNav.classList.remove('show');
    }
    if (talkListNav && !talkListNav.classList.contains('show')) {
        talkListNav.classList.add('show');
    }
}

let stompClient = null;
let currentChatRoomId = null;
let partnerId = null;
let reconnectAttempts = 0;
const maxReconnectAttempts = 5;
const reconnectInterval = 5000;

function connect(chatRoomId, callback) {
    if (stompClient && stompClient.connected) {
        if (callback) callback();
        return;
    }

    const socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, _ => {
        reconnectAttempts = 0;
        stompClient.subscribe('/topic/chat-room/' + chatRoomId, messageOutput => {
            const message = JSON.parse(messageOutput.body);
            showMessage(message);
        });
        if (callback) callback();
    }, error => {
        console.error('WebSocket 연결 실패: ', error);
    });
}

function handleConnectionError() {
    if (reconnectAttempts < maxReconnectAttempts) {
        reconnectAttempts++;
        setTimeout(() => {
            connect(currentChatRoomId, () => loadChatRoomMessages(currentChatRoomId));
        }, reconnectInterval);
    } else {
        console.error('최대 재연결 시도 횟수를 초과했습니다.');
    }
}

function openChatRoom(chatRoomId) {
    currentChatRoomId = chatRoomId;

    if (!stompClient || !stompClient.connected) {
        connect(chatRoomId, () => loadChatRoomMessages(chatRoomId));
    } else {
        loadChatRoomMessages(chatRoomId);
    }
}

function loadChatRoomMessages(chatRoomId) {
    const dynamicContent = document.getElementById('dynamic-content');
    dynamicContent.innerHTML = `
        <div class="mesgs">
            <h5 id="chatRoomName" class="text-center"></h5>
            <button id="exitRoomBtn" class="btn btn-danger">대화방 나가기</button>
            <div class="msg_history"></div>
            <div class="type_msg">
                <div class="input_msg_write" id="inputContainer"></div>
            </div>
        </div>
    `;

    const exitButton = document.getElementById('exitRoomBtn');
    exitButton.addEventListener('click', exitChatRoom);

    fetch(`/chat-room/messages/${chatRoomId}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('chatRoomName').textContent = data.roomName;
            const messageContainer = document.querySelector('.msg_history');
            if (data.messages.length === 0) {
                messageContainer.innerHTML = '<p id="noMessages" class="no-messages">대화가 없습니다.</p>';
            } else {
                data.messages.forEach(message => showMessage(message));
            }

            partnerId = data.partnerId;

            const unreadMessageIds = data.messages
                .filter(message => !message.read)
                .map(message => message.messageId);

            if (unreadMessageIds.length > 0) {
                markMessagesAsRead(unreadMessageIds);
            }

            addInputFieldAndSendButton();
        })
        .catch(error => console.error('채팅방 메시지를 불러오는 중 오류 발생:', error));
}

function addInputFieldAndSendButton() {
    const inputContainer = document.getElementById('inputContainer');

    if (!document.getElementById('messageInput')) {
        const inputElement = document.createElement('textarea');
        inputElement.id = "messageInput";
        inputElement.placeholder = "메시지를 입력하세요";
        inputElement.className = 'write_msg';
        inputElement.rows = 1;

        const sendButton = document.createElement('button');
        sendButton.id = "msg_send_btn";
        sendButton.className = 'msg_send_btn';
        sendButton.type = "button";
        sendButton.innerHTML = '<i class="bi bi-symmetry-vertical" aria-hidden="true"></i>';

        inputContainer.append(inputElement, sendButton);

        sendButton.addEventListener('click', sendMessage);

        inputElement.addEventListener('keydown', event => {
            if (event.key === 'Enter' && !event.shiftKey) {
                event.preventDefault();
                sendMessage();
            }
        });

        inputElement.addEventListener('input', () => {
            inputElement.style.height = 'auto';
            inputElement.style.height = `${inputElement.scrollHeight}px`;
        });
    }
}

function sendMessage() {
    const message = document.getElementById("messageInput").value.trim();

    if (!message || !currentChatRoomId || !partnerId || !stompClient || !stompClient.connected) {
        console.error('메시지 전송을 위한 데이터가 부족합니다.');
        return;
    }

    const messageId = Date.now();
    const currentTime = new Date().toISOString();
    const senderId = document.getElementById("currentUserId").value;

    const messagePayload = {
        'messageId': messageId,
        'sender': senderId,
        'receiver': partnerId,
        'content': message,
        'createdAt': currentTime,
        'isRead': false
    };

    stompClient.send("/app/chat.sendMessage", { 'receiverId': partnerId }, JSON.stringify(messagePayload));

    document.getElementById("messageInput").value = '';

    const noMessageElement = document.getElementById('noMessages');
    if (noMessageElement) {
        noMessageElement.remove();
    }

    document.querySelector('.msg_history').scrollTop = document.querySelector('.msg_history').scrollHeight;
}

function showMessage(message) {
    const messageContainer = document.querySelector('.msg_history');
    const currentUserId = document.getElementById('currentUserId').value;
    const isCurrentUser = message.sender === currentUserId;

    // 서버에서 받은 UTC 시간을 자바스크립트 Date 객체로 변환
    const utcDate = new Date(message.createdAt);

    // 클라이언트에서 9시간을 빼서 KST로 변환 (잘못된 추가 변환을 보정)
    const correctedDate = new Date(utcDate.getTime() - (9 * 60 * 60 * 1000)); // UTC에서 9시간 빼기

    // 시간과 날짜 포맷
    const formattedTime = correctedDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
    const formattedDate = correctedDate.toLocaleDateString();
    const timeDate = `${formattedDate} ${formattedTime}`;

    const formattedContent = message.content.replace(/\n/g, '<br>').replace(/ {2}/g, '&nbsp;&nbsp;');

    const messageElement = isCurrentUser
        ? `<div class="outgoing_msg">
                <div class="sent_msg">
                    <p>${formattedContent}</p>
                    <span class="time_date">${timeDate}</span>
                </div>
            </div>`
        : `<div class="incoming_msg">
                <div class="incoming_img">
                    <img src="../assets/img/profile-2398783_640.png" alt="user">
                </div>
                <div class="received_msg">
                    <div class="received_withd_msg">
                        <p>${formattedContent}</p>
                        <span class="time_date">${timeDate}</span>
                    </div>
                </div>
            </div>`;

    const noMessageElement = document.getElementById('noMessages');
    if (noMessageElement) {
        noMessageElement.remove();
    }

    messageContainer.insertAdjacentHTML('beforeend', messageElement);
    messageContainer.scrollTop = messageContainer.scrollHeight;
}

function exitChatRoom() {
    if (!currentChatRoomId) {
        console.error('현재 열린 대화방이 없습니다.');
        return;
    }

    if (!confirm('정말 대화방을 나가시겠습니까? 이 작업은 취소할 수 없습니다.')) {
        return;
    }

    const currentUserId = document.getElementById('currentUserId').value;

    fetch(`/chat-room/exit/${currentChatRoomId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: currentUserId })
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            document.getElementById('dynamic-content').innerHTML = '';
            reloadChatRooms();

            fetch('/talk/chat-rooms')
                .then(response => response.json())
                .then(chatRooms => {
                    if (chatRooms.length === 0) {
                        window.location.href = "/talk/talk-index";
                    } else {
                        showInitialMessage();
                    }
                })
                .catch(error => console.error('대화방 로드 중 오류:', error));
        })
        .catch(error => console.error('대화방 나가기 중 오류 발생:', error));
}


function showInitialMessage() {
    const dynamicContent = document.getElementById('dynamic-content');
    dynamicContent.innerHTML = `
        <div id="initial-message" class="initial-message text-center">
            <img th:src="@{../assets/img/sms-5320930_1280.jpg}" alt="Chat Placeholder" class="placeholder-img">
        </div>
    `;
}

function markMessagesAsRead(messageIds) {
    fetch('/chat-room/messages/mark-as-read', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(messageIds)
    });
}
