import React, { Component } from 'react';
import { Button, Icon } from 'semantic-ui-react';

import MediaFormModal from './MediaFormModal';

class AddMediaButton extends Component {
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
          isOpen={this.state.isOpen}
          onClose={this.onClose}
          onCancel={this.onCancel}
          onSave={this.onSave}
          initialFields={{
            image: 'https://react.semantic-ui.com/assets/images/wireframe/white-image.png',
          }}
        />
      </div>
    );
  }
}

export default AddMediaButton;
