import React from 'react';
import PropTypes from 'prop-types';
import { Card } from 'semantic-ui-react';

import MediaCard from './MediaCard';
import AddMediaCard from './AddMediaCard';

const MediaList = ({ media }) => {
  const mediaCards = media.map(mediaItem => (
    <MediaCard {...mediaItem} />
  ));

  return (
    <Card.Group>
      {mediaCards}
      <AddMediaCard />
    </Card.Group>
  );
};

export default MediaList;
