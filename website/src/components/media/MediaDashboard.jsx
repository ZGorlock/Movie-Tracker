import React, { Component } from 'react';
import { Dimmer, Loader, Segment } from 'semantic-ui-react';
import { Redirect } from 'react-router-dom';
import PropTypes from 'prop-types';

import MediaList from './MediaList';
import MediaClient, { mediaClient } from '../../MediaClient';

class MediaDashboard extends Component {
  static propTypes = {
    authToken: PropTypes.string.isRequired,
    producerId: PropTypes.string.isRequired,
  }

  state = {
    isLoading: false,
    media: [],
  };

  componentDidMount() {
    this.setState({ isLoading: true });
    const { producerId } = this.props;

    mediaClient.queryMedia(producerId).then((mediaIdList) => {
      const promises = mediaIdList.map(mediaId => (
        mediaClient.retrieveMedia(mediaId).then(mediaInfo => mediaInfo)
      ));

      Promise.all([...promises].map(p => p.catch(e => e)))
        .then((results) => {
          const media = results.filter(item => !item.toString().startsWith('Error:'));

          this.setState({ isLoading: false, media });
        })
        .catch(e => console.log(e));
    }).catch(this.handleRetrieveError);
  }

  handleRetrieveError = (error) => {
    switch (error.type) {
      case MediaClient.QUERY_MEDIA_SERVER_FAILURE:
        console.log(MediaClient.QUERY_MEDIA_SERVER_FAILURE);
        break;
      default:
        console.log(MediaClient.RETRIEVE_MEDIA_SERVER_FAILURE);
    }

    this.setState({ isLoading: false });
  }

  render() {
    if (this.props.authToken === '') {
      return <Redirect to="/login" />;
    }

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
