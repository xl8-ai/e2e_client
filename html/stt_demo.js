const msgerForm = get(".msger-inputarea");
const msgerInput = get(".msger-input");
const msgerChat = get(".msger-chat");

const PERSON_IMG = "https://image.flaticon.com/icons/svg/145/145867.svg";

var delay = 200;
var wasLastMessagePartial = false;

function appendMessage(name, img, side, text) {
  //   Simple solution for small apps
  const msgHTML = `
    <div class="msg ${side}-msg">
      <div class="msg-img" style="background-image: url(${img})"></div>

      <div class="msg-bubble">
        <div class="msg-info">
          <div class="msg-info-name">${name}</div>
          <div class="msg-info-time">${formatDate(new Date())}</div>
        </div>

        <div class="msg-text" dir="rtl">${text}</div>
      </div>
    </div>
  `;

  msgerChat.insertAdjacentHTML("beforeend", msgHTML);
  msgerChat.scrollTop += msgerChat.scrollHeight;
}

function findLastTextElem() {
  const textElems = document.getElementsByClassName("msg-text");
  if (textElems.length == 0) {
    return null;
  }
  return textElems[textElems.length - 1];
}

// Utils
function get(selector, root = document) {
  return root.querySelector(selector);
}

function formatDate(date) {
  const h = "0" + date.getHours();
  const m = "0" + date.getMinutes();

  return `${h.slice(-2)}:${m.slice(-2)}`;
}

function ticker() {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
       // Typical action to be performed when the document is ready:
      const responses = JSON.parse(xhttp.responseText);
      for (var i = 0; i < responses.length; i++) {
        const response = responses[i];
        const lastTextElem = findLastTextElem();
        if (wasLastMessagePartial && lastTextElem) {
          lastTextElem.innerText = response["response"];
          msgerChat.scrollTop += msgerChat.scrollHeight;
        } else {
          appendMessage("Speaker", PERSON_IMG, "left", response["response"]);
        }
        wasLastMessagePartial = response["is_partial"];
      }
      delay = 200;
    } else if (this.readyState == 4 && this.status == 0) {
      document.getElementsByClassName("msger-chat")[0].innerText=""
      delay = 2000;
    }
  };
  xhttp.open("GET", "http://localhost:5000", true);
  xhttp.send();

  setTimeout(() => {
    ticker();
  }, delay);
}

ticker();
