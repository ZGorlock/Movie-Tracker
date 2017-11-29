import React, { Component } from 'react';
import { Button, Icon } from 'semantic-ui-react';
import PropTypes from 'prop-types';

import MediaFormModal from './MediaFormModal';

class AddMediaButton extends Component {
  static propTypes = {
    authToken: PropTypes.string.isRequired,
  }

  state = {
    isOpen: false,
  };

  onClose = () => {
    this.setState({ isOpen: false });
  };

  onCancel = () => {
    this.setState({ isOpen: false });
  }

  onSave = () => {
    this.setState({ isOpen: false });
  }

  onClick = () => {
    this.setState({ isOpen: true });
  }

  render() {
    return (
      <div>
        <Button
          color="red"
          fluid
          basic
          onClick={this.onClick}
        >
          <Icon name="plus" />
          Add Media
        </Button>

        <MediaFormModal
          authToken={this.props.authToken}
          isOpen={this.state.isOpen}
          onClose={this.onClose}
          onCancel={this.onCancel}
          onSave={this.onSave}
        />
      </div>
    );
  }
}

export default AddMediaButton;
