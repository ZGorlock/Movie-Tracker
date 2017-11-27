class Client {
  loggedIn = true;

  isLoggedIn = () => this.loggedIn;

  login = () => (
    new Promise((resolve) => {
      this.loggedIn = true;

      setTimeout(() => {
        resolve();
      }, 1000);
    })
  );

  logout = () => {
    this.loggedIn = false;
  }
}

export const client = new Client();
