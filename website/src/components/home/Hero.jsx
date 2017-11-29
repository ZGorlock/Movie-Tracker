import React from 'react';
import { Container, Header, Segment } from 'semantic-ui-react';

const Hero = () => (
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
        A place for media enthusiasts to connect.
      </Header>
      <Header
        color="red"
        as="h2"
        inverted
        style={{ fontSize: '1.7em', fontWeight: 'normal' }}
      >
        Pretentious description.
      </Header>
    </Container>
  </Segment>
);

export default Hero;
