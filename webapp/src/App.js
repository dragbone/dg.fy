import React, {Component} from 'react';
import {MuiThemeProvider, createMuiTheme} from '@material-ui/core/styles';
import './App.css'
import SongSearch from './SongSearch';
import SongList from './SongList';
import CurrentlyPlaying from './CurrentlyPlaying';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Switch from '@material-ui/core/Switch';
import Typography from "@material-ui/core/Typography";

window.apiUrl = 'http://' + window.location.hostname + '/api/';

export default class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            light: true,
            theme: createMuiTheme({
                palette: {
                    type: 'light',
                },
            })
        };
    }

    switchTheme(isChecked) {
        if (isChecked) {
            this.setState({
                light: false,
                theme: createMuiTheme({
                    palette: {
                        type: 'dark',
                    },
                })
            });
        } else {
            this.setState({
                light: true,
                theme: createMuiTheme({
                    palette: {
                        type: 'light',
                    },
                })
            });
        }
    }

    componentDidMount() {
        document.body.style.backgroundColor = '#777777';
    }

    render() {
        return (
            <MuiThemeProvider theme={this.state.theme}>
                <Card>
                    <CardContent>
                        <Typography gutterBottom variant="headline" component="h2">
                            Playing
                        </Typography>
                        <br />
                        <CurrentlyPlaying/>
                        <br />
                        <SongSearch/>
                    </CardContent>
                </Card>
                <br />
                <Card>
                    <CardContent>
                        <Typography gutterBottom variant="headline" component="h2">
                            Playlist
                        </Typography>
                        <SongList/>
                    </CardContent>
                </Card>
                <div>
                    <Switch onChange={(event) => this.switchTheme.bind(this)(event.target.checked)}/>
                </div>
            </MuiThemeProvider>
        );
    }
}