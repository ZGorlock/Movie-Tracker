import React, { Component } from 'react';
import PropTypes from 'prop-types';

const apiClient = department => (
  new Promise((resolve) => {
    let courses = [];

    switch (department) {
      case 'core':
        courses = [
          'Core Course I',
          'Core Course II',
        ];
        break;
      case 'electives':
        courses = [
          'Elective Course I',
          'Elective Course II',
        ];
        break;
      default:
    }

    setTimeout(() => {
      resolve(courses);
    }, 1000);
  })
);

class CourseSelect extends Component {
  static propTypes = {
    department: PropTypes.string,
    course: PropTypes.string,
    onChange: PropTypes.func.isRequired,
  };

  static defaultProps = {
    department: '',
    course: '',
  };

  state = {
    department: '',
    course: '',
    courses: [],
    isLoading: false,
  };

  componentWillReceiveProps(update) {
    this.setState({
      department: update.department,
      course: update.course,
    });
  }

  onSelectDepartment = (e) => {
    const department = e.target.value;
    const course = '';
    this.setState({ department, course });
    this.props.onChange({ name: 'department', value: department });
    this.props.onChange({ name: 'course', value: course });

    if (department) {
      this.fetch(department);
    }
  }

  onSelectCourse = (e) => {
    const course = e.target.value;
    this.setState({ course });
    this.props.onChange({ name: 'course', value: course });
  }

  fetch = (department) => {
    this.setState({ isLoading: true, courses: [] });

    apiClient(department).then((courses) => {
      this.setState({ isLoading: false, courses });
    });
  }

  renderDepartmentSelect = () => (
    <select value={this.state.department || ''} onChange={this.onSelectDepartment}>
      <option value="">Which department?</option>
      <option value="core">NodeSchool: Core</option>
      <option value="electives">NodeSchool: Electives</option>
    </select>
  );

  renderCourseSelect = () => {
    if (this.state.isLoading) {
      return <div className="ui loader" />;
    }
    if (!this.state.department || !this.state.courses.length) {
      return <span />;
    }

    return (
      <select value={this.state.course || ''} onChange={this.onSelectCourse}>
        {[
          <option value="" key="course-none">
            Which course?
          </option>,
          ...this.state.courses.map((course, i) => (
            <option key={i} value={course}>
              {course}
            </option>
          )),
        ]}
      </select>
    );
  }

  render() {
    return (
      <div>
        {this.renderDepartmentSelect()}
        <br />
        {this.renderCourseSelect()}
      </div>
    );
  }
}

export default CourseSelect;