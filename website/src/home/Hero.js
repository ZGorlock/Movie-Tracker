import React from 'react';
import { Container, Header, Segment } from 'semantic-ui-react';

const Hero = () => {
  return (
    <Segment
      inverted
      textAlign='center'
      style={{ minHeight: 600, padding: '1em 0em' }}
      vertical
    >
      <Container text>
        <Header
          as='h1'
          content='A place for media enthusiasts to connect.'
          inverted
          style={{ fontSize: '4em', fontWeight: 'normal', marginBottom: 0, marginTop: '3em' }}
        />
        <Header
          color='red'
          as='h2'
          content='Pretentious description.'
          inverted
          style={{ fontSize: '1.7em', fontWeight: 'normal' }}
        />
      </Container>
    </Segment>
  );
};

export default Hero;
