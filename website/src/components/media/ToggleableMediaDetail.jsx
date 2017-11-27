import React, { Component } from 'react';
import { Button, Card, Header, Icon, Image, Modal, Rating, Statistic } from 'semantic-ui-react';

class ToggleableMediaDetail extends Component {
  state = {
    open: false,
  };

  show = () => () => this.setState({ open: true });
  close = () => this.setState({ open: false })

  render() {
    const { open } = this.state;

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

        <Modal size="fullscreen" dimmer="blurring" open={open} onClose={this.close}>
          <Modal.Header>Media Info</Modal.Header>
          <Modal.Content image>
            <Image wrapped size="large" src={this.props.image} />
            <Modal.Description>
              <Card.Group>
                <Card>
                  <Card.Content>
                    <Card.Header>Title</Card.Header>
                    <Card.Description>{this.props.title}</Card.Description>
                  </Card.Content>
                </Card>

                <Card>
                  <Card.Content>
                    <Card.Header>Genre</Card.Header>
                    <Card.Description>Placeholder Genre</Card.Description>
                  </Card.Content>
                </Card>
            
                <Card>
                  <Card.Content>
                    <Card.Header>Description</Card.Header>
                    <Card.Description>Placeholder Description</Card.Description>
                  </Card.Content>
                </Card>

                <Card>
                  <Card.Content>
                    <Card.Header>Actors</Card.Header>
                    <Card.Description>Placeholder Actors</Card.Description>
                  </Card.Content>
                </Card>

                <Card>
                  <Card.Content>
                    <Card.Header>Placeholder Rating</Card.Header>
                    <Rating rating={4} maxRating={5} size="huge" disabled />
                  </Card.Content>
                </Card>

                <Card>
                  <Card.Content>
                    <Card.Header>Year</Card.Header>
                    <Card.Description>2017</Card.Description>
                  </Card.Content>
                </Card>
              </Card.Group>
            </Modal.Description>
          </Modal.Content>
          <Modal.Actions>
            <Button color="red" onClick={this.close}>
              <Icon name="remove" />
              Delete
            </Button>
            <Button
              positive
              icon="edit"
              labelPosition="right"
              content="Edit"
              onClick={this.close}
            />
          </Modal.Actions>
        </Modal>
      </div>
    );
  }
}

export default ToggleableMediaDetail;
