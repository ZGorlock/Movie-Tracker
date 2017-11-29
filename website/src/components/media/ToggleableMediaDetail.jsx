import React, { Component } from 'react';
import { Button, Icon } from 'semantic-ui-react';
import PropTypes from 'prop-types';

import MediaFormModal from './MediaFormModal';
import MediaDetailModal from './MediaDetailModal';

class ToggleableMediaDetail extends Component {
  static propTypes = {
    image: PropTypes.string,
    title: PropTypes.string,
    type: PropTypes.string,
    description: PropTypes.string,
    genre: PropTypes.string,
    actors: PropTypes.string,
    showtimes: PropTypes.string,
    rating: PropTypes.string,
    year: PropTypes.string,
  };

  static defaultProps = {
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
  };

  onClose = () => this.setState({ isOpen: false })
  onEdit = () => this.setState({ isOpen: false, editing: true });
  show = () => () => this.setState({ isOpen: true });
  closeEdit = () => this.setState({ isOpen: true, editing: false })

  render() {
    const { isOpen, editing } = this.state;
    const fields = {
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
          fields={fields}
        />

        <MediaFormModal
          isOpen={editing}
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
