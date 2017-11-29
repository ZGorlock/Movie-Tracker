import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Button, Form, Icon, Image, Rating, Segment, Modal } from 'semantic-ui-react';

import MediaClient, { mediaClient } from '../../MediaClient';

const typeOptions = [
  {
    key: 'movie',
    value: 'movie',
    text: 'Movie',
  },
  {
    key: 'show',
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
    key: 'adventure',
    value: 'adventure',
    text: 'Adventure',
  },
  {
    key: 'comedy',
    value: 'comedy',
    text: 'Comedy',
  },
  {
    key: 'romance',
    value: 'romance',
    text: 'Romance',
  },
  {
    key: 'sci-fi',
    value: 'sci-fi',
    text: 'Sci-fi',
  },
  {
    key: 'thriller',
    value: 'thriller',
    text: 'Thiller',
  },
  {
    key: 'adventure',
    value: 'adventure',
    text: 'Adventure',
  },
  {
    key: 'comedy',
    value: 'comedy',
    text: 'Comedy',
  },
  {
    key: 'romance',
    value: 'romance',
    text: 'Romance',
  },
  {
    key: 'sci-fi',
    value: 'sci-fi',
    text: 'Sci-fi',
  },
  {
    key: 'thriller',
    value: 'thriller',
    text: 'Thiller',
  },
  {
    key: 'horror',
    value: 'horror',
    text: 'Horror',
  },
];

class MediaFormModal extends Component {
  static propTypes = {
    authToken: PropTypes.string.isRequired,
    isOpen: PropTypes.bool,
    onClose: PropTypes.func.isRequired,
    onCancel: PropTypes.func.isRequired,
    onSave: PropTypes.func.isRequired,
    initialFields: PropTypes.shape({
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
  };

  static defaultProps = {
    isOpen: false,
    initialFields: {
      image: 'https://react.semantic-ui.com/assets/images/wireframe/white-image.png',
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

  state = {
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

  componentDidMount() {
    this.setState({ fields: this.props.initialFields });
  }

  onSave = () => {
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
    } = this.state.fields;

    mediaClient.addMedia(
      image,
      title,
      type,
      description,
      genre,
      actors,
      showtimes,
      rating,
      year,
      this.props.authToken,
    ).then(() => {
      this.props.onSave();
    }).catch((error) => {
      console.log(error);
    });
  }

  onChange = (e, { name, value }) => {
    const { fields } = this.state;
    fields[name] = value;

    this.setState({ fields });
  };

  render() {
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
    } = this.state.fields;

    return (
      <Modal size="fullscreen" dimmer="blurring" open={this.props.isOpen} onClose={this.props.onClose}>
        <Modal.Header>Media Info</Modal.Header>
        <Modal.Content image scrolling>
          <Image
            wrapped
            size="large"
            src={image}
          />
        </Modal.Content>
        <Segment>
          <Form>
            <Form.Input
              label="Title"
              name="title"
              onChange={this.onChange}
              placeholder="Title of media..."
              defaultValue={title}
            />
            <Form.Select
              label="Media Type"
              defaultSelectedLabel={type}
              name="type"
              onChange={this.onChange}
              options={typeOptions}
              placeholder="Select media type..."
              value={type}
            />
            <Form.TextArea
              label="Description"
              name="description"
              defaultValue={description}
              onChange={this.onChange}
              placeholder="Describe your media..."
            />
            <Form.Select
              label="Genre"
              defaultSelectedLabel={genre}
              name="genre"
              onChange={this.onChange}
              options={genreOptions}
              placeholder="Select media genre..."
              value={genre}
            />
            <Form.Input
              name="actors"
              defaultValue={actors}
              onChange={this.onChange}
              label="Actors"
              placeholder="List of actors..."
            />
            <Form.Input
              name="showtimes"
              defaultValue={showtimes}
              onChange={this.onChange}
              label="Showtimes"
              placeholder="List of showtimes..."
            />
            <Form.Input
              name="rating"
              defaultValue={rating}
              onChange={this.onChange}
              label="MPAA Rating"
              placeholder="Rating..."
            />
            <Form.Input
              name="year"
              defaultValue={year}
              onChange={this.onChange}
              label="Year"
              placeholder="Release year..."
            />
          </Form>
        </Segment>
        <Modal.Actions>
          <Button color="red" onClick={this.props.onCancel}>
            <Icon name="arrow left" />
            Cancel
          </Button>
          <Button
            positive
            icon="save"
            labelPosition="right"
            content="Save"
            onClick={this.onSave}
          />
        </Modal.Actions>
      </Modal>
    );
  }
}

export default MediaFormModal;
