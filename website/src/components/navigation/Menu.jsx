import React from 'react';

import LandingMenu from './LandingMenu';
import NavMenu from './NavMenu';
import { client } from '../../Client';

const Menu = () => {
  if (client.isLoggedIn()) {
    return <NavMenu />;
  }

  return <LandingMenu />;
};

export default Menu;
