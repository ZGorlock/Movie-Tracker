import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Redirect } from 'react-router-dom';
import { Button, Message, Form, Icon, Image, Segment, Modal } from 'semantic-ui-react';

import MediaClient, { mediaClient } from '../../MediaClient';

const typeOptions = [
  {
    key: 'movie',
    value: 'Movie',
    text: 'Movie',
  },
  {
    key: 'show',
    value: 'Show',
    text: 'TV Show',
  },
];

const genreOptions = [
  {
    key: 'action',
    value: 'Action',
    text: 'Action',
  },
  {
    key: 'adventure',
    value: 'Adventure',
    text: 'Adventure',
  },
  {
    key: 'comedy',
    value: 'Comedy',
    text: 'Comedy',
  },
  {
    key: 'romance',
    value: 'Romance',
    text: 'Romance',
  },
  {
    key: 'sci-fi',
    value: 'Sci-fi',
    text: 'Sci-fi',
  },
  {
    key: 'thriller',
    value: 'Thriller',
    text: 'Thiller',
  },
  {
    key: 'horror',
    value: 'Horror',
    text: 'Horror',
  },
];

class MediaFormModal extends Component {
  static propTypes = {
    isEdit: PropTypes.bool,
    authToken: PropTypes.string.isRequired,
    isOpen: PropTypes.bool,
    onClose: PropTypes.func.isRequired,
    onCancel: PropTypes.func.isRequired,
    onSave: PropTypes.func.isRequired,
    initialFields: PropTypes.shape({
      mediaId: PropTypes.string,
      imageDump: PropTypes.string,
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
    isEdit: false,
    isOpen: false,
    initialFields: {
      mediaId: '',
      imageDump: '',
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
      mediaId: '',
      imageDump: '',
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
    fieldErrors: {},
    formErrors: {},
    isLoading: false,
    shouldRedirect: false,
  };

  componentDidMount() {
    this.setState({ fields: this.props.initialFields });
  }

  onSave = () => {
    const {
      mediaId,
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

    this.setState({ isLoading: true, formErrors: {} });

    if (this.props.isEdit) {
      console.log(image);

      mediaClient.editMedia(
        mediaId,
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
        this.setState({ isLoading: false, shouldRedirect: true });
        this.props.onSave();
      }).catch(this.handleSaveError);
    } else {
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
        this.setState({isLoading: false, shouldRedirect: true });
        this.props.onSave();
      }).catch(this.handleSaveError);
    }
  }

  onChange = (e, { name, value, validate }) => {
    const { fields, fieldErrors } = this.state;
    fields[name] = value;
    const invalid = validate(value);

    if (invalid) {
      fieldErrors[name] = invalid;
    } else {
      delete fieldErrors[name];
    }

    this.setState({ fields, fieldErrors });
  };

  handleSaveError = (error) => {
    const { formErrors } = this.state;

    switch (error.type) {
      case MediaClient.ADD_MEDIA_INVALID_AUTH:
        formErrors.server = 'Your auth token is expired. Please login again.';
        break;
      case MediaClient.ADD_MEDIA_TITLE_EXISTS:
        formErrors.server = 'This title already exists in the system';
        break;
      case MediaClient.EDIT_MEDIA_SERVER_FAILURE:
        formErrors.server = 'Failed to edit media';
        break;
      default:
        formErrors.server = 'Failed to connect to server';
    }

    this.setState({ formErrors, isLoading: false });
  };

  isImageValid = val => (val ? false: 'Image required');
  isTitleValid = val => (val ? false : 'Title required');
  isTypeValid = val => (val ? false : 'Type required');
  isDescriptionValid = val => (val ? false : 'Description required');
  isGenreValid = val => (val ? false : 'Genre required');
  isActorsValid = val => (val ? false : 'Actors required');
  isShowtimesValid = val => (val ? false : 'Showtimes required');
  isRatingValid = val => (val ? false : 'Rating required');
  isYearValid = (val) => {
    if (!val) {
      return 'Year required';
    }
    if (isNaN(val)) {
      return 'Year must be a number';
    }

    return false;
  }

  isValid = () => {
    const { fieldErrors } = this.state;
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

    const errMessages = Object.keys(fieldErrors).filter(key => fieldErrors[key]);

    return image && title && type && description &&
      genre && actors && showtimes && rating && year && !errMessages.length;
  }

  isFormValid = () => {
    const { formErrors } = this.state;
    const errMessages = Object.keys(formErrors).filter(key => formErrors[key]);

    return !errMessages.length;
  }

  renderErrors = () => {
    const { fieldErrors, formErrors } = this.state;
    const errorsCopy = Object.assign({}, formErrors);
    const errors = Object.assign(errorsCopy, fieldErrors);
    const errMessages = Object.values(errors).filter(err => err);

    const errComponents = [
      ...errMessages.map(errMessage => (
        <Message.Content>
          {errMessage}
        </Message.Content>
      )),
    ];

    return (
      <Message error>
        {errComponents}
      </Message>
    );
  }

  render() {
    if (this.state.shouldRedirect) {
      return <Redirect to="/media" />;
    }

    const {
      image,
      imageDump,
      title,
      type,
      description,
      genre,
      actors,
      showtimes,
      rating,
      year,
    } = this.state.fields;

    const { fieldErrors } = this.state;
    const hasErrors = !this.isValid();

    return (
      <Modal size="fullscreen" dimmer="blurring" open={this.props.isOpen} onClose={this.props.onClose}>
        <Modal.Header>Media Info</Modal.Header>
        <Modal.Content image scrolling>
          <Image
            wrapped
            size="large"
            src={`data:image/jpeg;base64,${imageDump}`}
          />
        </Modal.Content>
        <Segment>
          <Form error={hasErrors || !this.isFormValid()}>
            <Form.Input
              error={fieldErrors.image}
              label="Image Link"
              name="image"
              onChange={this.onChange}
              placeholder="Link of image..."
              validate={this.isImageValid}
            />
            <Form.Input
              error={fieldErrors.title}
              label="Title"
              name="title"
              onChange={this.onChange}
              placeholder="Title of media..."
              value={title}
              validate={this.isTitleValid}
            />
            <Form.Select
              label="Media Type"
              name="type"
              onChange={this.onChange}
              options={typeOptions}
              placeholder="Select media type..."
              value={type}
              validate={this.isTypeValid}
            />
            <Form.TextArea
              error={fieldErrors.description}
              label="Description"
              name="description"
              defaultValue={description}
              onChange={this.onChange}
              placeholder="Describe your media..."
              validate={this.isDescriptionValid}
            />
            <Form.Select
              label="Genre"
              name="genre"
              onChange={this.onChange}
              options={genreOptions}
              placeholder="Select media genre..."
              value={genre}
              validate={this.isGenreValid}
            />
            <Form.Input
              error={fieldErrors.actors}
              name="actors"
              value={actors}
              onChange={this.onChange}
              label="Actors"
              placeholder="List of actors..."
              validate={this.isActorsValid}
            />
            <Form.Input
              error={fieldErrors.showtimes}
              name="showtimes"
              value={showtimes}
              onChange={this.onChange}
              label="Showtimes"
              placeholder="List of showtimes..."
              validate={this.isShowtimesValid}
            />
            <Form.Input
              error={fieldErrors.rating}
              name="rating"
              value={rating}
              onChange={this.onChange}
              label="MPAA Rating"
              placeholder="Rating..."
              validate={this.isRatingValid}
            />
            <Form.Input
              error={fieldErrors.year}
              name="year"
              value={year}
              onChange={this.onChange}
              label="Year"
              placeholder="Release year..."
              validate={this.isYearValid}
            />
            {this.renderErrors()}
          </Form>
        </Segment>
        <Modal.Actions>
          <Button color="red" onClick={this.props.onCancel}>
            <Icon name="arrow left" />
            Cancel
          </Button>
          <Button
            disabled={hasErrors}
            loading={this.state.isLoading}
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
