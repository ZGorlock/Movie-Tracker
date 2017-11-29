import React from 'react';
import PropTypes from 'prop-types';
import { Card } from 'semantic-ui-react';

import MediaCard from './MediaCard';
import AddMediaCard from './AddMediaCard';

const MediaList = ({ media, authToken }) => {
  const mediaCards = media.map(mediaItem => (
    <MediaCard authToken={authToken} {...mediaItem} />
  ));

  return (
    <Card.Group>
      {mediaCards}
      <AddMediaCard authToken={authToken} />
    </Card.Group>
  );
};

MediaList.propTypes = {
  authToken: PropTypes.string.isRequired,
};

export default MediaList;
