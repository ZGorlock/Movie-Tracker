import React from 'react';
import PropTypes from 'prop-types';
import { Button, Form, Icon, Image, Rating, Segment, Modal } from 'semantic-ui-react';

const typeOptions = [
  {
    key: 'movie',
    value: 'movie',
    text: 'Movie',
  },
  {
    key: 'movie',
    value: 'show',
    text: 'TV Show',
  },
];

const genreOptions = [
  {
    key: 'action',
    value: 'action',
    text: 'Action',
  },
  {
    key: 'horror',
    value: 'horror',
    text: 'Horror',
  },
];

const MediaFormModal = ({
  isOpen,
  onClose,
  onCancel,
  onSave,
  initialFields,
}) => (
  <Modal size="fullscreen" dimmer="blurring" open={isOpen} onClose={onClose}>
    <Modal.Header>Media Info</Modal.Header>
    <Modal.Content image scrolling>
      <Image
        wrapped
        size="large"
        src={initialFields.image}
      />
    </Modal.Content>
    <Segment>
      <Form>
        <Form.Input label="Title" placeholder="Title of media..." />
        <Form.Select
          label="Media Type"
          options={typeOptions}
          placeholder="Select media type..."
        />
        <Form.TextArea
          label="Description"
          placeholder="Describe your media..."
        />
        <Form.Select
          label="Genre"
          options={genreOptions}
          placeholder="Select media genre..."
        />
        <Form.Field
          size="huge"
          label="Rating"
          control={Rating}
          maxRating={5}
          clearable
        />
        <Form.Input label="Year" placeholder="Release year..." />
      </Form>
    </Segment>
    <Modal.Actions>
      <Button color="red" onClick={onCancel}>
        <Icon name="arrow left" />
        Cancel
      </Button>
      <Button
        positive
        icon="save"
        labelPosition="right"
        content="Save"
        onClick={onSave}
      />
    </Modal.Actions>
  </Modal>
);

MediaFormModal.propTypes = {
  isOpen: PropTypes.bool,
  onClose: PropTypes.func.isRequired,
  onCancel: PropTypes.func.isRequired,
  onSave: PropTypes.func.isRequired,
};

MediaFormModal.defaultProps = {
  isOpen: false,
};

export default MediaFormModal;
