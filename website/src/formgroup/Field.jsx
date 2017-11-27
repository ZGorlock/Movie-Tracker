import React, { Component } from 'react';
import PropTypes from 'prop-types';

class Field extends Component {
  static propTypes = {
    placeholder: PropTypes.string,
    name: PropTypes.string.isRequired,
    value: PropTypes.string,
    validate: PropTypes.func,
    onChange: PropTypes.func.isRequired,
  };

  static defaultProps = {
    placeholder: '',
    value: '',
    validate: undefined,
  }

  state = {
    value: '',
    error: false,
  };

  componentWillReceiveProps(update) {
    this.setState({ value: update.value });
  }

  onChange = (e) => {
    const { name } = this.props;
    const { value } = e.target;
    const error = this.props.validate ? this.props.validate(value) : false;

    this.setState({ value, error });

    this.props.onChange({ name, value, error });
  };

  render() {
    return (
      <div>
        <input
          className="ui input"
          onChange={this.onChange}
          placeholder={this.props.placeholder}
          value={this.state.value}
        />
        <span style={{ color: 'red' }}>{this.state.error}</span>
      </div>
    );
  }
}

export default Field;
