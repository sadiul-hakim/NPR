const username = document.getElementById("username");

document.addEventListener("DOMContentLoaded", function () {
    let wsId = username.innerText;

    // Try to set up WebSocket connection with the handshake
    let socket = new SockJS('/npr-ws');

    // Create a new StompClient object with the WebSocket endpoint
    const stompClient = Stomp.over(socket);

    // Start the STOMP communications, provide a callback for when the CONNECT frame arrives.
    stompClient.connect(
        {'ws-id': wsId},
        function (frame) {
            stompClient.subscribe('/user/topic/notification', function (message) {
                showMessage(JSON.parse(message.body).message);
            });

            stompClient.subscribe("/topic/nprChannel", function (message) {
                showMessage(JSON.parse(message.body).message)
            });
        },
        function (err) {
            showInfo(err);
        }
    );
});

function showMessage(message) {
    console.log("Message = " + message)
}

function showInfo(message) {
    console.log(message)
}
