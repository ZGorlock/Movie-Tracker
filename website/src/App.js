import React, { Component } from "react";
import "./App.css";
import LoginForm from './LoginForm';
import SignupForm from './SignupForm';
import Home from './home/Home';
import MediaSidebar from './media/MediaSidebar';
import MenuHeader from './common/MenuHeader';
import Footer from './common/Footer';

class App extends Component {
  render() {
    return (
      <div className="App">
        <MenuHeader />
        <Home />
        <LoginForm />
        <SignupForm />
        <MediaSidebar />
        <Footer />
      </div>
    )
  }
}


export default App;
