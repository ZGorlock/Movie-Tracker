import React from 'react';
import { Container, Menu, Button } from 'semantic-ui-react';

const MenuHeader = () => (
  <Menu fixed='top' size='large'>
    <Container>
      <Menu.Item as='a' active>Home</Menu.Item>
      <Menu.Item as='a'>Profile</Menu.Item>
      <Menu.Item as='a'>Media</Menu.Item>
      <Menu.Item as='a'>Settings</Menu.Item>
      <Menu.Menu position='right'>
        <Menu.Item className='item'>
          <Button as='a' color='red' basic>Log in</Button>
        </Menu.Item>
        <Menu.Item>
          <Button as='a' color='red'>Sign Up</Button>
        </Menu.Item>
      </Menu.Menu>
    </Container>
  </Menu>
);

export default MenuHeader;