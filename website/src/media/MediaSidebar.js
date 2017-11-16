import React, { Component } from 'react'
import { Grid, Menu, Segment } from 'semantic-ui-react'

class MediaSidebar extends Component {
  state = { activeItem: 'bio' }

  handleItemClick = (e, { name }) => this.setState({ activeItem: name })

  render() {
    const { activeItem } = this.state

    return (
      <Grid>
        <Grid.Column width={4}>
          <Menu fluid vertical tabular>
            <Menu.Item name='Add' active={activeItem === 'Add'} onClick={this.handleItemClick} />
            <Menu.Item name='Edit' active={activeItem === 'Edit'} onClick={this.handleItemClick} />
            <Menu.Item name='Remove' active={activeItem === 'Remove'} onClick={this.handleItemClick} />
            <Menu.Item name='Help' active={activeItem === 'Help'} onClick={this.handleItemClick} />
          </Menu>
        </Grid.Column>

        <Grid.Column stretched width={12}>
          <Segment>
            Placeholder for add/edit/remove system
          </Segment>
        </Grid.Column>
      </Grid>
    )
  }
}

export default MediaSidebar;
