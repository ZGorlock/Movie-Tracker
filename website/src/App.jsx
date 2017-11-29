import React, { Component } from 'react';
import { Route, Switch } from 'react-router-dom';

import './App.css';
import { Home } from './components/home';
import { Help } from './components/help';
import { MediaDashboard } from './components/media';
import { Login } from './components/login';
import { Signup } from './components/signup';
import { Footer, Menu } from './components/navigation';

class App extends Component {
  state = {
    isLoggedIn: false,
    token: '',
    id: '',
  };

  onLogout = () => {
    this.setState({ isLoggedIn: false });
  };

  onLogin = (token, id) => {
    this.setState({ isLoggedIn: true, token, id });
    console.log(token);
    console.log(id);
  };

  render() {
    const {
      isLoggedIn,
      token,
      id,
    } = this.state;

    return (
      <div>
        <Menu
          isLoggedIn={isLoggedIn}
          onLogout={this.onLogout}
        />
        <Switch>
          <Route
            exact
            path="/"
            component={() =>
              <Home isLoggedIn={isLoggedIn} />
            }
          />
          <Route
            path="/media"
            component={() => <MediaDashboard authToken={token} id={id} />} />
          <Route
            path="/signup"
            component={() => <Signup onLogin={this.onLogin} />}
          />
          <Route
            path="/login"
            component={() => <Login onLogin={this.onLogin} />}
          />
          <Route path="/help" component={Help} />
        </Switch>
        <Footer />
      </div>
    );
  }
}

export default App;
