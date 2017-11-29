import React, { Component } from 'react';
import PropTypes from 'prop-types';

import LandingMenu from './LandingMenu';
import NavMenu from './NavMenu';
import { client } from '../../Client';

const Menu = ({ isLoggedIn, onLogout }) => {
  if (isLoggedIn) {
    return <NavMenu onLogout={onLogout} />;
  }

  return <LandingMenu />;
};

Menu.propTypes = {
  isLoggedIn: PropTypes.bool.isRequired,
  onLogout: PropTypes.func.isRequired,
};

export default Menu;
