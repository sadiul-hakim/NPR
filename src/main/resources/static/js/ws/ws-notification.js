const username = document.getElementById("username");
const bodyTag = document.getElementsByTagName("body")[0]; // Get the first body element

// Create notification container
const notificationContainer = document.createElement("div");
notificationContainer.classList.add("notification-container");
bodyTag.appendChild(notificationContainer);

document.addEventListener("DOMContentLoaded", function () {
    let wsId = username.innerText;

    // Set up WebSocket connection
    let socket = new SockJS('/npr-ws');
    const stompClient = Stomp.over(socket);

    // Start the STOMP communications
    stompClient.connect(
        {'ws-id': wsId},
        function (frame) {
            stompClient.subscribe('/user/queue/notification', function (message) {
                showNotification(JSON.parse(message.body).title, JSON.parse(message.body).message, "bg-info");
            });

            stompClient.subscribe("/topic/nprChannel", function (message) {
                showNotification(JSON.parse(message.body).title, JSON.parse(message.body).message, "bg-success");
            });
        },
        function (err) {
            showInfo(err);
        }
    );
});

function showNotification(title, msg, type = "bg-info") {
    let notification = createTag("div", "", ["notification", type]);
    let deleteBtn = createTag("button", "x", ["delete"]);

    let messageDiv = createTag("div", "", []);
    let messageTag = createTag("span", msg, ["text-light", "fs-6"]);
    let titleTag = createTag("h4", title, ["fw-bold", "text-warning"]);

    messageDiv.append(titleTag);
    messageDiv.append(messageTag);

    notification.append(deleteBtn);
    notification.append(messageDiv);

    // Append to floating notification container
    notificationContainer.appendChild(notification);

    // Remove notification on "X" click
    deleteBtn.addEventListener("click", () => {
        hideNotification(notification);
    });

    // Auto-dismiss after 3 seconds
    setTimeout(() => hideNotification(notification), 30000);
}

function hideNotification(notification) {
    notification.classList.add("fade-out");
    setTimeout(() => notification.remove(), 800);
}

function showMessage(message) {
    console.log("Message = " + message);
}

function showInfo(message) {
    console.log(message);
}

function createTag(name, innerText, classList) {
    let tag = document.createElement(name);
    classList.forEach(c => tag.classList.add(c));
    tag.innerText = innerText;
    return tag;
}
