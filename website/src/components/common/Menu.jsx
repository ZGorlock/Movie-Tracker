import React from 'react';
import PropTypes from 'prop-types';

const renderActiveTab = (activeTab, loggedIn) => {
  if (!loggedIn) {
    return ([
      <a className="active item">Home</a>,
    ]);
  }

  switch (activeTab) {
    case 'media':
      return ([
        <a className="item">Home</a>,
        <a className="active item">Media</a>,
        <a className="item">Help</a>,
      ]);
    case 'help':
      return ([
        <a className="item">Home</a>,
        <a className="item">Media</a>,
        <a className="active item">Help</a>,
      ]);
    default:
      return ([
          <a className="active item">Home</a>,
          <a className="item">Media</a>,
          <a className="item">Help</a>,
      ]);
  }
};

const renderLogin = loggedIn => (
  <div className="right menu">
    {loggedIn ? (
      <div className="item">
        <a className="ui red button">
          Logout
        </a>
      </div>
    ) : ([
      <div className="item">
        <a className="ui red basic button">
          Signup
        </a>
      </div>,
      <div className="item">
        <a className="ui red button">
          Login
        </a>
      </div>,
    ])}
  </div>
);

const Menu = ({ activeTab, loggedIn }) => (
  <div className="ui large menu">
    <div className="ui container">
      {renderActiveTab(activeTab, loggedIn)}
      {renderLogin(loggedIn)}
    </div>
  </div>
);

Menu.propTypes = {
  activeTab: PropTypes.string,
  loggedIn: PropTypes.bool,
};

Menu.defaultProps = {
  activeTab: 'home',
  loggedIn: false,
};

export default Menu;
