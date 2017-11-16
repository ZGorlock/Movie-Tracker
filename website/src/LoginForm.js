import React, { Component } from 'react';
import { Button, Form, Grid, Header, Loader, Segment } from 'semantic-ui-react';

class LoginForm extends Component {
  state = {
    loginInProgress: false,
    shouldRedirect: false
  };

  performLogin = () => {
    this.setState({ loginInProgress: true });
    this.setState({ shouldRedirect: true });
  }

  render() {
    return (
      <div className='login-form'>
        <style>{`
        body > div,
        body > div > div,
        body > div > div > div.login-form {
          height: 100%;
        }
      `}</style>
        <Grid
          textAlign='center'
          style={{ height: '100%' }}
          verticalAlign='middle'
        >
          <Grid.Column style={{ maxWidth: 450 }}>
            <Header as='h2' textAlign='center'>
              Sign in to an existing account.
            </Header>
            <Form size='large'>
              <Segment stacked>
                <Form.Input
                  fluid
                  icon='user'
                  iconPosition='left'
                  placeholder='E-mail address'
                />
                <Form.Input
                  fluid
                  icon='lock'
                  iconPosition='left'
                  placeholder='Password'
                  type='password'
                />
                {this.state.loginInProgress ? (
                  <Loader active={true} inline={true} />
                ) : (
                    <Button basic color='red' fluid size='large' onClick={this.performLogin}>
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

export default LoginForm
