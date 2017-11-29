import React, { Component } from 'react';
import { Button, Form, Grid, Header, Loader, Message, Segment } from 'semantic-ui-react';
import { Redirect } from 'react-router-dom';
import PropTypes from 'prop-types';
import isEmail from 'validator/lib/isEmail';

import Client, { client } from '../../Client';
import { isAlphanumeric } from '../../Validation';

class Signup extends Component {
  static propTypes = {
    onLogin: PropTypes.func.isRequired,
  };

  state = {
    fields: {
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      password: '',
      confirmPassword: '',
    },
    fieldErrors: {},
    formErrors: {},
    signupInProgress: false,
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

    const { password, confirmPassword } = fields;

    if (password && confirmPassword && password !== confirmPassword) {
      fieldErrors.confirmPassword = 'Passwords must match';
    } else {
      fieldErrors.confirmPassword = false;
    }

    this.setState({ fields, fieldErrors });
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

  isEmailValid = val => (isEmail(val) ? false : 'Invalid Email');
  isFirstNameValid = val => (val ? false : 'First name required');
  isLastNameValid = val => (val ? false : 'Last name required');
  isPasswordValid = val => (val ? false : 'Password required');
  isConfirmPasswordValid = val => (val ? false : 'Password confirmation required');

  performSignup = () => {
    const {
      username,
      email,
      firstName,
      lastName,
      password,
    } = this.state.fields;
    this.setState({ signupInProgress: true, formErrors: {} });
    client.signup(username, email, firstName, lastName, password).then((infoResp) => {
      client.login(username, password).then((tokenResp) => {
        const token = tokenResp.headers.get('authToken');
        const userInfo = JSON.parse(infoResp.headers.get('userInfo'));
        const id = userInfo.userId;

        this.setState({
          shouldRedirect: true,
        });

        this.props.onLogin(token, id);
      }).catch(this.handleSignupError);
    }).catch(this.handleSignupError);
  }

  handleSignupError = (error) => {
    const { fieldErrors, formErrors } = this.state;
    const { username } = this.state.fields;

    switch (error.type) {
      case Client.SIGNUP_USERNAME_EXISTS: {
        const message = `Username "${username}" is taken`;
        fieldErrors.username = message;
        formErrors.username = message;
        break;
      }
      default:
        formErrors.server = 'Failed to connect to server';
    }

    this.setState({ formErrors: {} });
    this.setState({ fieldErrors, formErrors, signupInProgress: false });
  }

  isValid = () => {
    const account = this.state.fields;
    const {
      username,
      email,
      firstName,
      lastName,
      password,
      confirmPassword,
    } = account;
    const { fieldErrors } = this.state;
    const errMessages = Object.keys(fieldErrors).filter(key => fieldErrors[key]);

    return username && email && firstName && lastName &&
      password && confirmPassword && !errMessages.length;
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
    const hasErrors = !this.isValid();

    if (this.state.shouldRedirect) {
      return <Redirect to="/" />;
    }

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
              Create a new account.
            </Header>
            <Form error={hasErrors || !this.isFormValid()} size="large">
              <Segment stacked>
                <Form.Input
                  error={this.state.fieldErrors.username}
                  onChange={this.onInputChange}
                  name="username"
                  validate={this.isUsernameValid}
                  disabled={this.state.signupInProgress}
                  fluid
                  icon="user"
                  iconPosition="left"
                  placeholder="Username"
                />
                <Form.Input
                  error={this.state.fieldErrors.email}
                  name="email"
                  onChange={this.onInputChange}
                  validate={this.isEmailValid}
                  disabled={this.state.signupInProgress}
                  fluid
                  icon="at"
                  iconPosition="left"
                  placeholder="E-mail address"
                />
                <Form.Input
                  error={this.state.fieldErrors.firstName}
                  onChange={this.onInputChange}
                  name="firstName"
                  validate={this.isFirstNameValid}
                  disabled={this.state.signupInProgress}
                  fluid
                  icon="id card"
                  iconPosition="left"
                  placeholder="First Name"
                />
                <Form.Input
                  error={this.state.fieldErrors.lastName}
                  onChange={this.onInputChange}
                  name="lastName"
                  validate={this.isLastNameValid}
                  disabled={this.state.signupInProgress}
                  fluid
                  icon="id card outline"
                  iconPosition="left"
                  placeholder="Last Name"
                />
                <Form.Input
                  error={this.state.fieldErrors.password}
                  name="password"
                  onChange={this.onInputChange}
                  validate={this.isPasswordValid}
                  disabled={this.state.signupInProgress}
                  fluid
                  icon="lock"
                  iconPosition="left"
                  placeholder="Password"
                  type="password"
                />
                <Form.Input
                  error={this.state.fieldErrors.confirmPassword}
                  onChange={this.onInputChange}
                  name="confirmPassword"
                  validate={this.isConfirmPasswordValid}
                  disabled={this.state.signupInProgress}
                  fluid
                  icon="lock"
                  iconPosition="left"
                  placeholder="Confirm password"
                  type="password"
                />
                {this.renderErrors()}
                {this.state.signupInProgress ? (
                  <Loader active inline />
                ) : (
                  <Button
                    disabled={hasErrors}
                    basic
                    color="red"
                    fluid
                    size="large"
                    onClick={this.performSignup}
                  >
                    Register
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

export default Signup;
