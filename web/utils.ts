// Utils
export function formatDate(date: Date) {
  const h = "0" + date.getHours();
  const m = "0" + date.getMinutes();

  return `${h.slice(-2)}:${m.slice(-2)}`;
}

export function blobToBase64(blob: Blob) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(blob);
    reader.onloadend = function () {
      var base64data = reader.result;
      if (typeof base64data === "string") {
        base64data = base64data.replace("data:audio/wav;base64,", "");
        resolve(base64data);
      } else {
        reject();
      }
    };
    reader.onerror = function (e) {
      reject(e);
    };
  });
}
