import React from 'react';
import { Container, Segment, Header } from 'semantic-ui-react';

const Footer = () => (
  <Segment inverted textAlign="left" style={{ padding: '2em 0em' }}>
    <Container>
      <Header as="h3" inverted>A UCF Student Project</Header>
    </Container>
  </Segment>
);

export default Footer;
