import React, { Component } from 'react';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import darkBaseTheme from 'material-ui/styles/baseThemes/darkBaseTheme';
import lightBaseTheme from 'material-ui/styles/baseThemes/lightBaseTheme';
import AppBar from 'material-ui/AppBar';
import './App.css'
import SongSearch from './SongSearch';
import SongList from './SongList';
import CurrentlyPlaying from './CurrentlyPlaying';
import { Card, CardHeader, CardText } from 'material-ui/Card';
import Toggle from 'material-ui/Toggle';

import injectTapEventPlugin from 'react-tap-event-plugin';
injectTapEventPlugin();

window.apiUrl = 'http://' + window.location.hostname + '/api/';

export default class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            light: true,
            theme: getMuiTheme(lightBaseTheme)
        };
    }

    switchTheme(isChecked) {
        if (isChecked) {
            this.setState({
                light: false,
                theme: getMuiTheme(darkBaseTheme)
            });
        } else {
            this.setState({
                light: true,
                theme: getMuiTheme(lightBaseTheme)
            });
        }
    }

    componentDidMount() {
        document.body.style.backgroundColor = '#777777';
    }

    render() {
        return (
            <MuiThemeProvider muiTheme={this.state.theme}>
                <div>
                    <Card>
                        <CardText>
                            <div>
                                <Toggle onToggle={(event, isInputChecked) => this.switchTheme.bind(this)(isInputChecked)} />
                            </div>
                            <div className="card">
                                <Card>
                                    <CardText>
                                        <SongSearch />
                                    </CardText>
                                </Card>
                            </div>
                            <div className="card">
                                <Card>
                                    <CardHeader title="Playing" />
                                    <CardText>
                                        <CurrentlyPlaying />
                                    </CardText>
                                </Card>
                            </div>
                            <div className="card">
                                <Card>
                                    <CardHeader title="Playlist" />
                                    <CardText>
                                        <SongList />
                                    </CardText>
                                </Card>
                            </div>

                        </CardText>
                    </Card>
                </div>
            </MuiThemeProvider>
        );
    }
}