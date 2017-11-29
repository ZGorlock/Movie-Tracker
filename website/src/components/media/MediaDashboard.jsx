import React, { Component } from 'react';
import { Dimmer, Loader, Segment } from 'semantic-ui-react';
import PropTypes from 'prop-types';

import MediaList from './MediaList';

const mediaData = [
  {
    image: 'https://i0.wp.com/media2.slashfilm.com/slashfilm/wp/wp-content/images/Last-Jedi-Poster.jpeg',
    title: 'Star Wars: The Last Jedi',
    description: 'Directed by Rhian Johnson',
    id: 0,
  },
  {
    image: 'http://starwarsblog.starwars.com/wp-content/uploads/2015/10/star-wars-force-awakens-official-poster.jpg',
    title: 'Star Wars: The Force Awakens',
    description: 'Directed by J.J. Abrams',
    id: 1,
  },
  {
    image: 'https://cdn.vox-cdn.com/uploads/chorus_image/image/57474957/thor_ragnarok_poster.0.jpg',
    title: 'Thor: Ragnarok',
    description: 'Directed by Taki Watiti',
    id: 2,
  },
  {
    image: 'http://cromeyellow.com/wp-content/uploads/2011/08/driveposterdomestic.jpeg',
    title: 'Drive',
    description: 'Directed by Nicholas Winding Refn',
    id: 3,
  },
  {
    image: 'https://i.imgur.com/tWlimWX.jpg',
    title: 'Blade Runner 2049',
    description: 'Directed by Dennis Villanueve',
    id: 4,
  },
];

const apiClient = () => (
  new Promise((resolve) => {
    setTimeout(() => {
      resolve(mediaData);
    }, 1000);
  })
);

class MediaDashboard extends Component {
  static propTypes = {
    authToken: PropTypes.string.isRequired,
  }

  state = {
    isLoading: false,
    media: [],
  };

  componentDidMount() {
    this.setState({ isLoading: true });

    apiClient().then((media) => {
      this.setState({ isLoading: false, media });
    });
  }

  render() {
    const { isLoading, media } = this.state;
    const { authToken } = this.props;

    return (
      <div>
        {isLoading ? (
          <Dimmer active>
            <Loader>
              Loading
            </Loader>
          </Dimmer>
        ) : (
          <Segment>
            <MediaList authToken={authToken} media={media} />
          </Segment>
        )}
      </div>
    );
  }
}

export default MediaDashboard;
