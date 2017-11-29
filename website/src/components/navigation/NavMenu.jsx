import React from 'react';
import { Container, Menu } from 'semantic-ui-react';
import { NavLink } from 'react-router-dom';
import PropTypes from 'prop-types';

const NavMenu = ({ onLogout }) => (
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
        <Menu.Item onClick={onLogout}>
          <NavLink
            className="ui red button"
            to="/login"
          >
          Logout
          </NavLink>
        </Menu.Item>
      </Menu.Menu>
    </Container>
  </Menu>
);

NavMenu.propTypes = {
  onLogout: PropTypes.func.isRequired,
};

export default NavMenu;
