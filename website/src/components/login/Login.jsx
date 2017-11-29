import React, { Component } from 'react';
import { Button, Form, Grid, Header, Loader, Message, Segment } from 'semantic-ui-react';
import { Redirect } from 'react-router-dom';
import PropTypes from 'prop-types';

import Client, { client } from '../../Client';
import { isAlphanumeric } from '../../Validation';

class Login extends Component {
  static propTypes = {
    onLogin: PropTypes.func.isRequired,
  };

  state = {
    fields: {
      username: '',
      password: '',
    },
    fieldErrors: {},
    formErrors: {},
    loginInProgress: false,
    shouldRedirect: false,
  };

  onInputChange = (e, { name, validate }) => {
    const { fields, fieldErrors } = this.state;
    const { value } = e.target;
    fields[name] = value;
    const invalid = validate(value);

    if (invalid) {
      fieldErrors[name] = invalid;
    } else {
      delete fieldErrors[name];
    }

    this.setState({ fields, fieldErrors });
  }

  setServerError = (message, name) => {
    const { fieldErrors, formErrors } = this.state;
    formErrors[name] = message;

    if (name !== 'server') {
      fieldErrors[name] = message;
    }

    this.setState({ formErrors: {} });
    this.setState({ fieldErrors, formErrors, loginInProgress: false });
  }

  performLogin = () => {
    const { username, password } = this.state.fields;
    this.setState({ loginInProgress: true, formErrors: {} });

    client.login(username, password).then((tokenResp) => {
      client.validate(username, password).then((infoResp) => {
        const token = tokenResp.headers.get('authToken');
        const userInfo = JSON.parse(infoResp.headers.get('userInfo'));
        const id = userInfo.userId;

        this.setState({
          shouldRedirect: true,
        });
        this.props.onLogin(token, id);
      }).catch(this.handleLoginError);
    }).catch(this.handleLoginError);
  }

  handleLoginError = (error) => {
    const { username } = this.state.fields;

    switch (error.type) {
      case Client.LOGIN_USER_NOT_FOUND: {
        this.setServerError(`User "${username}" does not exist`, 'username');
        break;
      }
      case Client.LOGIN_INVALID_PASSWORD: {
        this.setServerError('Incorrect password', 'password');
        break;
      }
      case Client.VALIDATE_USER_NOT_PRODUCER: {
        this.setServerError(`User "${username}" is not registered as a producer`, 'username');
        break;
      }
      default:
        this.setServerError('Failed to connect to server', 'server');
    }
  }

  isUsernameValid = (val) => {
    if (!val) {
      return 'Username required';
    }
    if (val.length > 32) {
      return 'Username must be less than 32 characters';
    }
    if (!isAlphanumeric(val)) {
      return 'Username contains invalid characters';
    }

    return false;
  };

  isPasswordValid = val => (val ? false : 'Password required');

  isValid = () => {
    const account = this.state.fields;
    const {
      username,
      password,
    } = account;
    const { fieldErrors } = this.state;
    const errMessages = Object.keys(fieldErrors).filter(key => fieldErrors[key]);
    return username && password && !errMessages.length;
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
      return <Redirect to="/" />;
    }

    const hasErrors = !this.isValid();

    return (
      <div className="login-form">
        <style>{`
        body > div,
        body > div > div,
        body > div > div > div.login-form {
          height: 100%;
        }
      `}</style>
        <Grid
          textAlign="center"
          style={{ height: '100%' }}
          verticalAlign="middle"
        >
          <Grid.Column style={{ maxWidth: 450 }}>
            <Header as="h2" textAlign="center">
              Sign in to an existing account.
            </Header>
            <Form error={hasErrors || !this.isFormValid()} size="large">
              <Segment stacked>
                <Form.Input
                  error={this.state.fieldErrors.username}
                  name="username"
                  onChange={this.onInputChange}
                  validate={this.isUsernameValid}
                  disabled={this.state.loginInProgress}
                  fluid
                  icon="user"
                  iconPosition="left"
                  placeholder="Username"
                />
                <Form.Input
                  error={this.state.fieldErrors.password}
                  name="password"
                  onChange={this.onInputChange}
                  validate={this.isPasswordValid}
                  disabled={this.state.loginInProgress}
                  fluid
                  icon="lock"
                  iconPosition="left"
                  placeholder="Password"
                  type="password"
                />
                {this.renderErrors()}
                {this.state.loginInProgress ? (
                  <Loader active inline />
                ) : (
                  <Button
                    disabled={hasErrors}
                    basic
                    color="red"
                    fluid
                    size="large"
                    onClick={this.performLogin}
                  >
                      Sign in
                  </Button>
                )}
              </Segment>
            </Form>
          </Grid.Column>
        </Grid>
      </div>
    );
  }
}

export default Login;
