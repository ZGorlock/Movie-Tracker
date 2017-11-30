import React from 'react';
import PropTypes from 'prop-types';
import { Card, Image } from 'semantic-ui-react';

import ToggleableMediaDetail from './ToggleableMediaDetail';

const MediaCard = ({ image, imageDump, title, ...other }) => (
  <Card>
    <Image fluid src={`data:image/jpeg;base64,${imageDump}`} alt={title} />
    <Card.Content>
      <Card.Header textAlign="center">
        {title}
      </Card.Header>
    </Card.Content>
    <ToggleableMediaDetail
      image={image}
      title={title}
      imageDump={imageDump}
      {...other}
    />
  </Card>
);

MediaCard.propTypes = {
  image: PropTypes.string,
  imageDump: PropTypes.string,
  title: PropTypes.string,
};

MediaCard.defaultProps = {
  image: '',
  imageDump: '',
  title: '',
};

export default MediaCard;
