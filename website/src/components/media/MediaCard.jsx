import React from 'react';
import PropTypes from 'prop-types';
import { Card, Image } from 'semantic-ui-react';

import ToggleableMediaDetail from './ToggleableMediaDetail';

const MediaCard = ({ image, title, ...other }) => (
  <Card>
    <Image src={image} alt={title} />
    <Card.Content>
      <Card.Header textAlign="center">
        {title}
      </Card.Header>
    </Card.Content>
    <ToggleableMediaDetail
      image={image}
      title={title}
      {...other}
    />
  </Card>
);

MediaCard.propTypes = {
  image: PropTypes.string,
  title: PropTypes.string,
};

MediaCard.defaultProps = {
  image: '',
  title: '',
};

export default MediaCard;
