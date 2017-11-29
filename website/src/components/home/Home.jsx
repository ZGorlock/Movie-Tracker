import React from 'react';
import PropTypes from 'prop-types';

import Landing from './Landing';
import Welcome from './Welcome';

const Home = ({ isLoggedIn }) => {
  if (isLoggedIn) {
    return <Welcome />;
  }

  return <Landing />;
};

Home.propTypes = {
  isLoggedIn: PropTypes.bool.isRequired,
};

export default Home;
