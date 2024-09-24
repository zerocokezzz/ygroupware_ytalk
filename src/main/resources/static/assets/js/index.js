// index.js

function connectForNotifications() {

    const socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/notifications', function (message) {
            const notification = JSON.parse(message.body);
            addNotification(notification);
        });
    }, function (error) {
        console.error('WebSocket 연결 실패: ', error);
    });
}

function addNotification(notification) {
    const notificationList = document.querySelector('.dropdown-menu.messages');

    const chatRoomUrl = `/talk/talk-index?chatRoomId=${notification.chatRoomId}`;

    const newNotification = `
    <li class="message-item">
      <a href="${chatRoomUrl}">
        <div>
          <h4>${notification.sender}</h4>
          <p>${notification.message}</p>
        </div>
      </a>
    </li>
    <li><hr class="dropdown-divider"></li>
  `;

    notificationList.insertAdjacentHTML('afterbegin', newNotification);

    updateBadgeCount();
}

function updateBadgeCount() {
    let messageBadge = document.querySelector('.bi-chat-left-text .badge-number');
    const unreadMessagesCount = document.querySelectorAll('.dropdown-menu.messages .message-item').length;

    if (!messageBadge) {
        const badgeElement = document.createElement('span');
        badgeElement.classList.add('badge', 'bg-success', 'badge-number');
        badgeElement.textContent = unreadMessagesCount;
        const chatIcon = document.querySelector('.bi-chat-left-text');
        if (chatIcon) {
            chatIcon.parentElement.appendChild(badgeElement);
        }
    } else {
        messageBadge.textContent = unreadMessagesCount;
    }
}

function fetchUnreadMessages() {
    fetch('/chat-room/unread-messages')
        .then(response => response.json())
        .then(messages => {
            clearNotificationItems();

            messages.forEach(message => {
                const notification = {
                    chatRoomId: message.chatRoomId,
                    sender: message.sender,
                    message: message.content,
                    createdAt: message.createdAt
                };
                addNotification(notification);
            });

            updateBadgeCount();
        })
        .catch(error => console.error('Error fetching unread messages:', error));
}

document.addEventListener('DOMContentLoaded', function () {
    fetchUnreadMessages();
    connectForNotifications();
});

function clearNotificationItems() {
    const notificationItems = document.querySelectorAll('.dropdown-menu.messages .message-item, .dropdown-menu.messages .dropdown-divider');
    notificationItems.forEach(item => item.remove());
}