import React, { Component } from 'react';
import { Button, Form, Grid, Header, Loader, Message, Segment } from 'semantic-ui-react';
import { Redirect } from 'react-router-dom';
import isEmail from 'validator/lib/isEmail';

import { client } from '../../Client';

class Signup extends Component {
  state = {
    fields: {
      email: '',
      password: '',
      confirmPassword: '',
    },
    fieldErrors: {},
    signupInProgress: false,
    shouldRedirect: false,
  };

  onInputChange = (e, { name, validate }) => {
    const { fields, fieldErrors } = this.state;
    const { value } = e.target;
    fields[name] = value;
    fieldErrors[name] = validate(value);
    const { password, confirmPassword } = fields;

    if (password && confirmPassword && password !== confirmPassword) {
      fieldErrors.confirmPassword = 'Passwords must match';
    } else {
      fieldErrors.confirmPassword = false;
    }

    this.setState({ fields, fieldErrors });
  }

  isEmailValid = val => (isEmail(val) ? false : 'Invalid Email');
  isPasswordValid = val => (val ? false : 'Password required');
  isConfirmPasswordValid = val => (val ? false : 'Password confirmation required');

  performSignup = () => {
    this.setState({ signupInProgress: true });

    client.login().then(() => {
      this.setState({
        signupInProgress: false,
        shouldRedirect: true,
      });
    });
  }

  isValid = () => {
    const account = this.state.fields;
    const {
      email,
      password,
      confirmPassword,
    } = account;
    const { fieldErrors } = this.state;
    const errMessages = Object.keys(fieldErrors).filter(key => fieldErrors[key]);

    return email && password && confirmPassword && !errMessages.length;
  }


  renderErrors() {
    const { fieldErrors } = this.state;
    const errMessages = Object.values(fieldErrors).filter(err => err);

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
            <Form error={hasErrors} size="large">
              <Segment stacked>
                <Form.Input
                  error={this.state.fieldErrors.email}
                  name="email"
                  onChange={this.onInputChange}
                  validate={this.isEmailValid}
                  disabled={this.state.signupInProgress}
                  fluid
                  icon="user"
                  iconPosition="left"
                  placeholder="E-mail address"
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
