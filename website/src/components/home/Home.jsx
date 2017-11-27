import React from 'react';
import { Container, Header, Segment } from 'semantic-ui-react';

import Landing from './Landing';
import Welcome from './Welcome';
import { client } from '../../Client';

const Home = () => {
  if (client.isLoggedIn()) {
    return <Welcome />;
  }

  return <Landing />;
};

export default Home;
