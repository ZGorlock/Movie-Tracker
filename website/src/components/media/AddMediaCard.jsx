import React from 'react';
import { Card, Image } from 'semantic-ui-react';

import AddMediaButton from './AddMediaButton';

const AddMediaCard = () => (
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
    <AddMediaButton />
  </Card>
);

export default AddMediaCard;
