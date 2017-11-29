import React from 'react';
import PropTypes from 'prop-types';
import { Button, Card, Icon, Image, Modal } from 'semantic-ui-react';

const MediaDetailModal = ({
  isOpen,
  onEdit,
  onClose,
  fields,
}) => {
  const {
    image,
    title,
    type,
    description,
    genre,
    actors,
    showtimes,
    rating,
    year,
  } = fields;

  return (
    <div>
      <Modal size="fullscreen" dimmer="blurring" open={isOpen} onClose={onClose}>
        <Modal.Header>Media Info</Modal.Header>
        <Modal.Content image>
          <Image wrapped size="large" src={image} />
          <Modal.Description>
            <Card.Group>
              <Card>
                <Card.Content>
                  <Card.Header>Title</Card.Header>
                  <Card.Description>{title}</Card.Description>
                </Card.Content>
              </Card>

              <Card>
                <Card.Content>
                  <Card.Header>Type</Card.Header>
                  <Card.Description>{type}</Card.Description>
                </Card.Content>
              </Card>

              <Card>
                <Card.Content>
                  <Card.Header>Genre</Card.Header>
                  <Card.Description>{genre}</Card.Description>
                </Card.Content>
              </Card>

              <Card>
                <Card.Content>
                  <Card.Header>Description</Card.Header>
                  <Card.Description>{description}</Card.Description>
                </Card.Content>
              </Card>

              <Card>
                <Card.Content>
                  <Card.Header>Actors</Card.Header>
                  <Card.Description>{actors}</Card.Description>
                </Card.Content>
              </Card>

              <Card>
                <Card.Content>
                  <Card.Header>Showtimes</Card.Header>
                  <Card.Description>{showtimes}</Card.Description>
                </Card.Content>
              </Card>

              <Card>
                <Card.Content>
                  <Card.Header>Rating</Card.Header>
                  <Card.Description>{rating}</Card.Description>
                </Card.Content>
              </Card>

              <Card>
                <Card.Content>
                  <Card.Header>Year</Card.Header>
                  <Card.Description>{year}</Card.Description>
                </Card.Content>
              </Card>
            </Card.Group>
          </Modal.Description>
        </Modal.Content>
        <Modal.Actions>
          <Button color="red" onClick={onClose}>
            <Icon name="remove" />
            Delete
          </Button>
          <Button
            positive
            icon="edit"
            labelPosition="right"
            content="Edit"
            onClick={onEdit}
          />
        </Modal.Actions>
      </Modal>
    </div>
  );
};

MediaDetailModal.propTypes = {
  fields: PropTypes.shape({
    image: PropTypes.string,
    title: PropTypes.string,
    type: PropTypes.string,
    description: PropTypes.string,
    genre: PropTypes.string,
    actors: PropTypes.string,
    showtimes: PropTypes.string,
    rating: PropTypes.string,
    year: PropTypes.string,
  }),
  isOpen: PropTypes.bool,
  onClose: PropTypes.func.isRequired,
  onEdit: PropTypes.func.isRequired,
};

MediaDetailModal.defaultProps = {
  isOpen: false,
  fields: {
    image: '',
    title: '',
    type: '',
    description: '',
    genre: '',
    actors: '',
    showtimes: '',
    rating: '',
    year: '',
  },
};

export default MediaDetailModal;
