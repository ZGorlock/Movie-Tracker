import React from 'react';
import { Route, Switch } from 'react-router-dom';

import './App.css';
import { Home } from './components/home';
import { MediaDashboard } from './components/media';
import { Login } from './components/login';
import { Logout } from './components/logout';
import { Signup } from './components/signup';
import { Footer, Menu } from './components/navigation';

const App = () => (
  <div>
    <Menu />
    <Switch>
      <Route exact path="/" component={Home} />
      <Route path="/media" component={MediaDashboard} />
      <Route path="/signup" component={Signup} />
      <Route path="/login" component={Login} />
      <Route path="/logout" component={Logout} />
    </Switch>
    <Footer />
  </div>
);

export default App;
