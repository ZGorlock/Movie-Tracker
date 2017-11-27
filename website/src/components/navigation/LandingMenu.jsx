import React from 'react';
import { Menu, Container } from 'semantic-ui-react';
import { NavLink } from 'react-router-dom';

const NavMenu = () => (
  <Menu size="large">
    <Container>
      <NavLink
        exact
        to="/"
        className="item"
        activeClassName="active"
      >
        Home
      </NavLink>
      <Menu.Menu position="right">
        <Menu.Item>
          <NavLink
            className="ui basic red button"
            to="/signup"
          >
            Signup
          </NavLink>
        </Menu.Item>
        <Menu.Item>
          <NavLink
            className="ui red button"
            to="/login"
          >
          Login
          </NavLink>
        </Menu.Item>
      </Menu.Menu>
    </Container>
  </Menu>
);

export default NavMenu;
