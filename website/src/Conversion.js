/* eslint-disable import/prefer-default-export */
/* eslint-disable no-undef */
export const toDataUrl = url => fetch(url)
  .then(response => response.blob())
  .then(blob => new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onloadend = () => resolve(reader.result);
    reader.onerror = reject;
    reader.readAsDataURL(blob);
  }));

export const getFileExtension = filename => (
  filename.split('.').pop()
);
