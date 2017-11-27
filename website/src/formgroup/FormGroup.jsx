import React, { Component } from 'react';
import isEmail from 'validator/lib/isEmail';
import Field from './Field';
import CourseSelect from './CourseSelect';

class FormGroup extends Component {
  // Initial state
  state = {
    fields: {
      name: '',
      email: '',
      course: '',
      department: '',
    },
    fieldErrors: {},
    people: [],
  };

  // onInputchange sets the state of field matching the given name
  // to the given value.
  onInputChange = ({ name, value, error }) => {
    const { fields, fieldErrors } = this.state;
    fields[name] = value;
    fieldErrors[name] = error;
    this.setState({ fields, fieldErrors });
  };

  // onFormSubmit submits the entire form and checks for form-level
  // validation errors. If valid, the state of the form is reset
  // and the data is added to the list.
  onFormSubmit = (e) => {
    e.preventDefault();
    const people = [...this.state.people];
    const person = this.state.fields;

    if (!this.isValid()) {
      return;
    }

    this.setState({
      fields: {
        name: '',
        email: '',
        department: '',
        course: '',
      },
      people: people.concat(person),
    });
  };

  // isValid checks if the form state is a valid submission. Name,
  // email, department, and course must be selected for a valid submission.
  isValid = () => {
    const person = this.state.fields;
    const {
      name,
      email,
      department,
      course,
    } = person;
    const { fieldErrors } = this.state;
    const errMessages = Object.keys(fieldErrors).filter(key => fieldErrors[key]);

    return name && email && department && course && !errMessages.length;
  }

  render() {
    return (
      <div>
        <h1>Signup Sheet</h1>
        <form
          className="ui form"
          onSubmit={this.onFormSubmit}
        >
          <Field
            name="name"
            onChange={this.onInputChange}
            placeholder="Name"
            value={this.state.fields.name}
            validate={val => (val ? false : 'Name Required')}
          />
          <Field
            name="email"
            onChange={this.onInputChange}
            placeholder="Email"
            value={this.state.fields.email}
            validate={val => (isEmail(val) ? false : 'Invalid Email')}
          />
          <CourseSelect
            department={this.state.fields.department}
            course={this.state.fields.course}
            onChange={this.onInputChange}
          />

          <br />

          <input
            className="ui button"
            type="submit"
            disabled={!this.isValid()}
          />
        </form>
        <div>
          <h3>People</h3>
          <ul className="ui list">
            {this.state.people.map(({
              name,
              email,
              department,
              course,
            }, i) => (
              <li key={i}>
                {[name, email, department, course].join('-')}
              </li>
            ))}
          </ul>
        </div>
      </div>
    );
  }
}

export default FormGroup;
