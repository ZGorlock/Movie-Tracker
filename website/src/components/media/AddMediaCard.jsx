import React from 'react';
import { Card, Image } from 'semantic-ui-react';
import PropTypes from 'prop-types';

import AddMediaButton from './AddMediaButton';

const AddMediaCard = ({ authToken }) => (
  <Card>
    <Image
      src="https://react.semantic-ui.com/assets/images/wireframe/white-image.png"
    />
    <Card.Content>
      <Card.Header textAlign="center">
        Is your media not here?
      </Card.Header>
      <Card.Description textAlign="center">
        Use the button below to add new content to our database.
      </Card.Description>
    </Card.Content>
    <AddMediaButton authToken={authToken} />
  </Card>
);

AddMediaCard.propTypes = {
  authToken: PropTypes.string.isRequired,
};

export default AddMediaCard;
