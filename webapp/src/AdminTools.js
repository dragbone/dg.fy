import React, { Component } from 'react';
import FloatingActionButton from 'material-ui/FloatingActionButton';
import ContentSkipNext from 'material-ui/svg-icons/av/skip-next';
import ContentPlay from 'material-ui/svg-icons/av/play-arrow';
import ContentPause from 'material-ui/svg-icons/av/pause';

export default class AdminTools extends Component {
    constructor(props) {
        super(props);
        this.state = {
            admin: false
        };
    }

    componentDidMount() {
        fetch(window.apiUrl + 'isLoggedIn', { credentials: 'same-origin' })
            .then(response => response.json())
            .then(result => {
                if (result === true) {
                    this.setState({ admin: true });
                }
            }
            );
    }

    skip() {
        fetch(window.apiUrl + 'skip', { credentials: 'same-origin' });
    }

    play() {
        fetch(window.apiUrl + 'play', { credentials: 'same-origin' });
    }

    pause() {
        fetch(window.apiUrl + 'pause', { credentials: 'same-origin' });
    }

    render() {
        const style = {
            marginRight: 12
        };
        if (this.state.admin) {
            return <span style={{marginLeft: 30}}>
                <FloatingActionButton style={style} mini={true} onClick={(event) => this.play.bind(this)()}>
                    <ContentPlay />
                </FloatingActionButton>
                <FloatingActionButton style={style} mini={true} onClick={(event) => this.pause.bind(this)()}>
                    <ContentPause />
                </FloatingActionButton>
                <FloatingActionButton style={style} mini={true} onClick={(event) => this.skip.bind(this)()}>
                    <ContentSkipNext />
                </FloatingActionButton>
            </span>;
        } else {
            return <div />;
        }
    }
}