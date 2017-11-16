import React, { Component } from 'react';
import Hero from './Hero';
import DescriptionList from './DescriptionList';

class Home extends Component { 
    render() {
        return (
            <div>
            <Hero />
            <DescriptionList />
            </div>
        );
    }
}

export default Home;
