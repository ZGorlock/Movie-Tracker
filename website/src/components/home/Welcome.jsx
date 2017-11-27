import React from 'react';
import { Container, Header, Segment } from 'semantic-ui-react';

const Home = () => (
  <Segment
    inverted
    textAlign="center"
    style={{ minHeight: 600, padding: '1em 0em' }}
    vertical
  >
    <Container text>
      <Header
        as="h1"
        inverted
        style={{
          fontSize: '4em',
          fontWeight: 'normal',
          marginBottom: 0,
          marginTop: '3em',
        }}
      >
        Welcome Producer!
      </Header>
      <Header
        color="red"
        as="h2"
        inverted
        style={{ fontSize: '1.7em', fontWeight: 'normal' }}
      >
        Use the media tab to add, edit, or remove content from our system.
      </Header>
    </Container>
  </Segment>
);

export default Home;
