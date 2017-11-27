import React, { Component } from 'react';
import { Button, Form, Grid, Header, Loader, Message, Segment } from 'semantic-ui-react';
import { Redirect } from 'react-router-dom';
import isEmail from 'validator/lib/isEmail';

import { client } from '../../Client';

class Login extends Component {
  state = {
    fields: {
      email: '',
      password: '',
    },
    fieldErrors: {},
    loginInProgress: false,
    shouldRedirect: false,
  };

  onInputChange = (e, { name, validate }) => {
    const { fields, fieldErrors } = this.state;
    const { value } = e.target;
    fields[name] = value;
    fieldErrors[name] = validate(value);

    this.setState({ fields, fieldErrors });
  }

  isEmailValid = val => (isEmail(val) ? false : 'Invalid Email');

  isPasswordValid = val => (val ? false : 'Password required');

  performLogin = () => {
    this.setState({ loginInProgress: true });

    client.login().then(() => {
      this.setState({
        loginInProgress: false,
        shouldRedirect: true,
      });
    });
  }

  isValid = () => {
    const account = this.state.fields;
    const {
      email,
      password,
    } = account;
    const { fieldErrors } = this.state;
    const errMessages = Object.keys(fieldErrors).filter(key => fieldErrors[key]);

    return email && password && !errMessages.length;
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
            <Form error={hasErrors} size="large">
              <Segment stacked>
                <Form.Input
                  error={this.state.fieldErrors.email}
                  name="email"
                  onChange={this.onInputChange}
                  validate={this.isEmailValid}
                  disabled={this.state.loginInProgress}
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
                    disabled={!this.isValid()}
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
