import { formatDate } from "./utils";

const msgerChat = document.querySelector(".msger-chat")!;

export function appendMessage(
  name: string,
  img: string,
  side: string,
  text: string
) {
  const msgHTML = `
    <div class="msg ${side}-msg">
      <div class="msg-img" style="background-image: url(${img})"></div>

      <div class="msg-bubble">
        <div class="msg-info">
          <div class="msg-info-name">${name}</div>
          <div class="msg-info-time">${formatDate(new Date())}</div>
        </div>

        <div class="msg-text">${text}</div>
      </div>
    </div>
  `;

  msgerChat.insertAdjacentHTML("beforeend", msgHTML);
  msgerChat.scrollTop += msgerChat.scrollHeight;
}

export function findLastMessageElement() {
  const textElems = document.getElementsByClassName("msg-text");
  if (textElems.length == 0) {
    return null;
  }
  return textElems[textElems.length - 1];
}

export function scrollMessages() {
  msgerChat.scrollTop += msgerChat.scrollHeight;
}
