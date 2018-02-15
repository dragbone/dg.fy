import React, { Component } from 'react';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import AppBar from 'material-ui/AppBar';
import './App.css'
import SongSearch from './SongSearch';
import SongList from './SongList';
import CurrentlyPlaying from './CurrentlyPlaying';
import { Card, CardHeader, CardText } from 'material-ui/Card';

import injectTapEventPlugin from 'react-tap-event-plugin';
injectTapEventPlugin();

window.apiUrl = 'http://' + window.location.hostname + '/api/';

export default class App extends Component {
    render() {
        return (
            <MuiThemeProvider>
                <div>
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
                </div>
            </MuiThemeProvider>
        );
    }
}