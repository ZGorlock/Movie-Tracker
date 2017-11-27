import React from 'react';
import { Container, Menu } from 'semantic-ui-react';
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
      <NavLink
        to="/media"
        className="item"
        activeClassName="active"
      >
      Media
      </NavLink>
      <NavLink
        to="/help"
        className="item"
        activeClassName="active"
      >
      Help
      </NavLink>
      <Menu.Menu position="right">
        <Menu.Item>
          <NavLink
            className="ui red button"
            to="/logout"
          >
          Logout
          </NavLink>
        </Menu.Item>
      </Menu.Menu>
    </Container>
  </Menu>
);

export default NavMenu;
