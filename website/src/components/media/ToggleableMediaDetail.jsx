import React, { Component } from 'react';
import { Button, Icon } from 'semantic-ui-react';
import { Redirect } from 'react-router-dom';
import PropTypes from 'prop-types';

import MediaFormModal from './MediaFormModal';
import MediaDetailModal from './MediaDetailModal';
import { mediaClient } from '../../MediaClient';

class ToggleableMediaDetail extends Component {
  static propTypes = {
    authToken: PropTypes.string.isRequired,
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
    mediaId: PropTypes.string.isRequired,
  };

  static defaultProps = {
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
  };

  state = {
    isOpen: false,
    editing: false,
    shouldRedirect: false,
  };

  onClose = () => this.setState({ isOpen: false })
  onEdit = () => this.setState({ isOpen: false, editing: true });
  onDelete = () => {
    const { mediaId, authToken } = this.props;
    mediaClient.deleteMedia(mediaId, authToken).then(() => {
      this.setState({ isOpen: false, shouldRedirect: true });
    }).catch((error) => {
      console.log(error);
    });
  }
  show = () => () => this.setState({ isOpen: true });
  closeEdit = () => this.setState({ isOpen: true, editing: false })

  render() {
    if (this.state.shouldRedirect) {
      return <Redirect to="/media" />;
    }
    
    const { isOpen, editing } = this.state;
    const fields = {
      mediaId: this.props.mediaId,
      imageDump: this.props.imageDump,
      image: this.props.image,
      title: this.props.title,
      type: this.props.type,
      description: this.props.description,
      actors: this.props.actors,
      showtimes: this.props.showtimes,
      genre: this.props.genre,
      rating: this.props.rating,
      year: this.props.year,
    };

    return (
      <div>
        <Button
          onClick={this.show('blurring')}
          color="red"
          basic
          fluid
        >
          <Icon name="expand" />
          View Details
        </Button>

        <MediaDetailModal
          isOpen={isOpen}
          onEdit={this.onEdit}
          onClose={this.onClose}
          onDelete={this.onDelete}
          fields={fields}
        />

        <MediaFormModal
          authToken={this.props.authToken}
          isOpen={editing}
          isEdit
          onClose={this.closeEdit}
          onCancel={this.closeEdit}
          onSave={this.closeEdit}
          initialFields={fields}
        />
      </div>
    );
  }
}

export default ToggleableMediaDetail;
