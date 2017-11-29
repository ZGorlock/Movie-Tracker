/* eslint-disable no-undef */
import CryptoJS from 'crypto-js';

class Client {
  static LOGIN_SERVER_FAILURE = 'LOGIN_SERVER_FAILURE';
  static LOGIN_USER_NOT_FOUND = 'LOGIN_USER_NOT_FOUND';
  static LOGIN_INVALID_PASSWORD = 'LOGIN_INVALID_PASSWORD';

  static SIGNUP_SERVER_FAILURE = 'SIGNUP_SERVER_FAILURE';
  static SIGNUP_USERNAME_EXISTS = 'SIGNUP_USERNAME_EXISTS';

  static VALIDATE_SERVER_FAILURE = 'VALIDATE_SERVER_FAILURE';
  static VALIDATE_USER_NOT_PRODUCER = 'VALIDATE_USER_NOT_PRODUCER';

  login = (username, password) => {
    const passHash = CryptoJS.SHA512(password);

    const form = new FormData();
    form.append('user', username);
    form.append('passHash', passHash);

    return fetch('/authorizeUser', {
      method: 'POST',
      body: form,
    }).then(response => this.handleLogin(response, username));
  };

  handleLogin = (response, username) => {
    if (response.ok) {
      return response;
    }

    const message = response.headers.get('message');

    const error = new Error(`HTTP Error ${message}`);

    switch (message) {
      case `Failure: User: ${username} is not registered on the server`:
        error.type = Client.LOGIN_USER_NOT_FOUND;
        break;
      case `Failure: The user credentials are incorrect for the user: ${username}`:
        error.type = Client.LOGIN_INVALID_PASSWORD;
        break;
      default:
        error.type = Client.LOGIN_SERVER_FAILURE;
        break;
    }

    throw error;
  };

  validate = (username, password) => {
    const passHash = CryptoJS.SHA512(password);

    const form = new FormData();
    form.append('user', username);
    form.append('passHash', passHash);

    return fetch('/validateUser', {
      method: 'POST',
      body: form,
    }).then(response => this.handleValidate(response, username));
  };

  handleValidate = (response, username) => {
    if (!response.ok) {
      const message = response.headers.get('message');
      const error = new Error(`HTTP Error ${message}`);
      error.type = Client.VALIDATE_SERVER_FAILURE;

      throw error;
    }

    const userInfo = JSON.parse(response.headers.get('userInfo'));
    const isProducer = userInfo.producer === 'y';

    if (isProducer) {
      return response;
    }

    const error = new Error(`Validation Error User ${username} is not a producer`);
    error.type = Client.VALIDATE_USER_NOT_PRODUCER;

    throw error;
  };

  signup = (username, email, firstName, lastName, password) => {
    const passHash = CryptoJS.SHA512(password);

    const form = new FormData();
    form.append('user', username);
    form.append('email', email);
    form.append('firstName', firstName);
    form.append('lastName', lastName);
    form.append('passHash', passHash);
    form.append('producer', 'y');

    return fetch('/registerUser', {
      method: 'POST',
      body: form,
    }).then(response => this.handleSignup(response, username));
  };

  handleSignup = (response, username) => {
    if (response.ok) {
      return response;
    }

    const message = response.headers.get('message');

    const error = new Error(`HTTP Error ${message}`);

    switch (message) {
      case `Failure: User: ${username} is already registered on the server`:
        error.type = Client.SIGNUP_USERNAME_EXISTS;
        break;
      default:
        error.type = Client.SIGNUP_SERVER_FAILURE;
        break;
    }

    throw error;
  }
}

export default Client;
export const client = new Client();
