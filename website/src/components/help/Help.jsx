import React from 'react';
import { Container, Header, Segment } from 'semantic-ui-react';

const Help = () => (
  <Segment
    inverted
    style={{ minHeight: 600, padding: '1em 0em' }}
    vertical
  >
    <Container text>
      <Header
        as="h1"
        textAlign="center"
        inverted
        style={{
          fontSize: '4em',
          fontWeight: 'normal',
          marginBottom: '1em',
          marginTop: '0',
        }}
      >
        Have Questions?
      </Header>
      <Header
        color="red"
        as="h2"
        textAlign="left"
        inverted
        style={{ fontSize: '1.7em', fontWeight: 'normal' }}
      >
        How do I upload Media? <br />

        <Container text>
          <Header
            as="h3"
            textAlign="left"
            inverted
            style={{ fontSize: '1em', fontWeight: 'normal', padding: '1em' }}
          >
            Welcome to the adventure! To start adding your work, click on the Media tab and
            you'll see an option toadd media, click it, and enter all the information about
            your movie or show and upload a image to show to
            potential viewer and click save! That's it! <br />
          </Header>
        </Container>

        How do I change information about a movie/TV show I already uploaded? <br />

        <Container text>
          <Header
            as="h3"
            textAlign="left"
            inverted
            style={{ fontSize: '1em', fontWeight: 'normal', padding: '1em' }}
          >
            We understand that things change, or maybe you didn't get the wording quite right
            when you first uploaded, so all you have to do is click the media tab, find the
            entry you want to edit, click on the 'View Details' button below it. This opens a
            page detailing everything about your entry, now all you need to do is click
            the green edit button and enter whatever information you wanted to change! <br />

          </Header>
        </Container>

        How do I logout? <br />

        <Container text>
          <Header
            as="h3"
            textAlign="left"
            inverted
            style={{ fontSize: '1em', fontWeight: 'normal', padding: '1em' }}
          >
            The Logout button is always at the top right of your browser, click it once 
            and you're done! <br />

          </Header>
        </Container>

        How do I change my password?<br />

        <Container text>
          <Header
            as="h3"
            textAlign="left"
            inverted
            style={{ fontSize: '1em', fontWeight: 'normal', padding: '1em' }}
          >
            Here at Movie Tracker we value your security above all and want to protect your 
            information against unauthorized changes so we require users to create a new 
            account if they forget their password<br />
          </Header>
        </Container>

      </Header>
    </Container>
  </Segment>
);

export default Help;
