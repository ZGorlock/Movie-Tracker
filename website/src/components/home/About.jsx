import React from 'react';
import { Container, Segment, Header } from 'semantic-ui-react';

const About = () => (
  <div>
    <Segment style={{ padding: '8em 0em' }} vertical textAlign="center">
      <Container text>
        <Header as="h3" color="red" style={{ fontSize: '2em' }}>We Help Producers and Consumers</Header>
        <p style={{ fontSize: '1.33em' }}>
          We provide resources for producers to upload data for their media content in order
            to further connect users to the content they love and drive engagement.
        </p>
      </Container>
    </Segment>
    <Segment style={{ padding: '8em 0em' }} vertical textAlign="center">
      <Container text>
        <Header as="h3" color="red" style={{ fontSize: '2em' }}>Easy to use</Header>
        <p style={{ fontSize: '1.33em' }}>
          We just want to get 100% for this project so we made this as simple to use as possible.
        </p>
      </Container>
    </Segment>
  </div>
);

export default About;
