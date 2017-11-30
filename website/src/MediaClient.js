/* eslint-disable no-undef */
import { toDataUrl, getFileExtension } from './Conversion';

class MediaClient {
  static ADD_MEDIA_SERVER_FAILURE = 'ADD_MEDIA_SERVER_FAILURE';
  static ADD_MEDIA_INVALID_AUTH = 'ADD_MEDIA_INVALID_AUTH';
  static ADD_MEDIA_TITLE_EXISTS = 'ADD_MEDIA_TITLE_EXISTS';

  static QUERY_MEDIA_SERVER_FAILURE = 'QUERY_MEDIA_SERVER_FAILURE';

  static RETRIEVE_MEDIA_SERVER_FAILURE = 'RETRIEVE_MEDIA_SERVER_FAILURE';

  static DELETE_MEDIA_SERVER_FAILURE = 'DELETE_MEDIA_SERVER_FAILURE';
  static DELETE_MEDIA_DOES_NOT_EXIST = 'DELETE_MEDIA_DOES_NOT_EXIST';

  static EDIT_MEDIA_SERVER_FAILURE = 'EDIT_MEDIA_SERVER_FAILURE';

  addMedia = (
    image,
    title,
    type,
    description,
    genre,
    actors,
    showtimes,
    rating,
    year,
    authToken,
  ) => (
    toDataUrl(image).then((imageData) => {
      const data = imageData.replace(/^data:image\/(png|jpg|jpeg);base64,/, '');
      const form = new FormData();
      form.append('imageDump', data);
      form.append('imageType', getFileExtension(image));
      form.append('title', title);
      form.append('type', type);
      form.append('description', description);
      form.append('genre', genre);
      form.append('actors', actors);
      form.append('showtimes', showtimes);
      form.append('rating', rating);
      form.append('year', year);
      form.append('authToken', authToken);

      return fetch('/addMedia', {
        method: 'POST',
        body: form,
      }).then(response => this.handleAddMedia(response));
    }));

  handleAddMedia = (response) => {
    if (response.ok) {
      return;
    }

    const message = response.headers.get('message');
    const error = new Error(`HTTP Error ${message}`);

    if (message.endsWith('already exists on the server')) {
      error.type = MediaClient.ADD_MEDIA_TITLE_EXISTS;
      throw error;
    }

    switch (message) {
      case 'Failure: The specified communication channel could not decrypt the auth token':
        error.type = MediaClient.ADD_MEDIA_INVALID_AUTH;
        break;
      default:
        error.type = MediaClient.ADD_MEDIA_SERVER_FAILURE;
    }

    throw error;
  };

  editMedia = (
    mediaId,
    image,
    title,
    type,
    description,
    genre,
    actors,
    showtimes,
    rating,
    year,
    authToken,
  ) => (
    toDataUrl(image).then((imageData) => {
      const data = imageData.replace(/^data:image\/(png|jpg|jpeg);base64,/, '');
      const form = new FormData();
      form.append('imageDump', data);
      form.append('imageName', image);
      form.append('imageType', getFileExtension(image));
      form.append('mediaId', mediaId);
      form.append('title', title);
      form.append('type', type);
      form.append('description', description);
      form.append('genre', genre);
      form.append('actors', actors);
      form.append('showtimes', showtimes);
      form.append('rating', rating);
      form.append('year', year);
      form.append('authToken', authToken);

      return fetch('/editMedia', {
        method: 'POST',
        body: form,
      }).then(response => this.handleEditMedia(response));
    }));

  handleEditMedia = (response) => {
    if (response.ok) {
      return;
    }

    const message = response.headers.get('message');
    const error = new Error(`HTTP Error ${message}`);
    error.type = MediaClient.ADD_MEDIA_SERVER_FAILURE;

    throw error;
  };


  queryMedia = (producerId) => {
    const form = new FormData();
    form.append('producerId', producerId);
    form.append('title', '');
    form.append('type', '');
    form.append('description', '');
    form.append('genre', '');
    form.append('actors', '');
    form.append('showtimes', '');
    form.append('rating', '');

    return fetch('/queryMedia', {
      method: 'POST',
      body: form,
    }).then(response => this.handleQueryMedia(response));
  };

  handleQueryMedia = (response) => {
    if (response.ok) {
      const results = JSON.parse(response.headers.get('results'));
      const mediaIdList = results.results;

      return mediaIdList;
    }

    const message = response.headers.get('message');
    const error = new Error(`HTTP Error ${message}`);

    switch (message) {
      default:
        error.type = MediaClient.QUERY_MEDIA_SERVER_FAILURE;
    }

    throw error;
  };

  retrieveMedia = (mediaId) => {
    const form = new FormData();
    form.append('mediaId', mediaId);

    return fetch('/retrieveMedia', {
      method: 'POST',
      body: form,
    }).then(response => this.handleRetrieveMedia(response));
  }

  handleRetrieveMedia = (response) => {
    if (response.ok) {
      const imageDump = response.headers.get('imageDump');
      const mediaInfo = JSON.parse(response.headers.get('mediaInfo'));
      mediaInfo.imageDump = imageDump;

      return mediaInfo;
    }

    const message = response.headers.get('message');
    const error = new Error(`HTTP Error ${message}`);

    switch (message) {
      default:
        error.type = MediaClient.RETRIEVE_MEDIA_SERVER_FAILURE;
        break;
    }

    throw error;
  }

  deleteMedia = (mediaId, authToken) => {
    const form = new FormData();
    form.append('mediaId', mediaId);
    form.append('authToken', authToken);

    return fetch('/deleteMedia', {
      method: 'POST',
      body: form,
    }).then(response => this.handleDeleteMedia(response));
  }

  handleDeleteMedia = (response) => {
    if (response.ok) {
      return;
    }

    const message = response.headers.get('message');
    const error = new Error(`HTTP Error ${message}`);

    if (message.endsWith('does not exists on the server')) {
      error.type = MediaClient.DELETE_MEDIA_DOES_NOT_EXIST;
    } else {
      error.type = MediaClient.DELETE_MEDIA_SERVER_FAILURE;
    }

    throw error;
  }
}


export default MediaClient;
export const mediaClient = new MediaClient();
