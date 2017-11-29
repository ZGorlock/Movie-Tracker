/* eslint-disable no-undef */
class MediaClient {
  static ADD_MEDIA_SERVER_FAILURE = 'ADD_MEDIA_SERVER_FAILURE';

  static QUERY_MEDIA_SERVER_FAILURE = 'QUERY_MEDIA_SERVER_FAILURE';

  static RETRIEVE_MEDIA_SERVER_FAILURE = 'RETRIEVE_MEDIA_SERVER_FAILURE';

  static DELETE_MEDIA_SERVER_FAILURE = 'DELETE_MEDIA_SERVER_FAILURE';
  static DELETE_MEDIA_DOES_NOT_EXIST = 'DELETE_MEDIA_DOES_NOT_EXIST';

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
  ) => {
    const form = new FormData();
    form.append('image', '');
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
  };

  handleAddMedia = (response) => {
    console.log(response);
    const message = response.headers.get('message');
    const error = new Error(`HTTP Error ${message}`);
    console.log(error);

    switch (message) {
      default:
        error.type = MediaClient.ADD_MEDIA_SERVER_FAILURE;
    }

    throw error;
  };

  queryMedia = (producerId) => {
    const form = new FormData();
    form.append('producerId', producerId);

    return fetch('/queryMedia', {
      method: 'POST',
      body: form,
    }).then(response => this.handleQueryMedia(response));
  };

  handleQueryMedia = (response) => {
    if (repsonse.ok) {
      const mediaIdList = JSON.parse(response.headers.get('mediaInfo'));
      return mediaIdList;
    }

    console.log(response);
    const message = response.headers.get('message');
    const error = new Error(`HTTP Error ${message}`);
    console.log(error);

    switch (message) {
      default:
        error.type = MediaClient.ADD_MEDIA_SERVER_FAILURE;
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
      const mediaInfo = JSON.parse(response.headers.get('mediaInfo'));
      const fields = {
        fields: mediaInfo,
      };

      return fields;
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

    switch (message) {
      case `Failure: Media: ${username} does not exists on the server`:
        error.type = MediaClient.DELETE_MEDIA_DOES_NOT_EXIST;
        break;
      default:
        error.type = MediaClient.DELETE_MEDIA_SERVER_FAILURE;
        break;
    }

    throw error;
  }
}


export default MediaClient;
export const mediaClient = new MediaClient();
