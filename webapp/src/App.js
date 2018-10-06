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
import Config from './Config'
import Login from "./Login";
import AdminContainer from "./AdminContainer";
import CookieHelper from "./CookieHelper";
import ActionBar from "./ActionBar";
import AdminTools from "./AdminTools";

window.apiUrl = 'http://' + window.location.hostname + '/api/';

export default class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            darkTheme: CookieHelper.getCookie("darkTheme"),
            showConfig: false
        };
    }

    themes = {
        false: createMuiTheme({
            palette: {
                type: 'light',
            },
        }),
        true: createMuiTheme({
            palette: {
                type: 'dark',
            },
        })
    };

    switchConfig(isChecked) {
        this.setState({
            showConfig: isChecked
        });
    }

    switchTheme(isChecked) {
        this.setState({
            darkTheme: isChecked,
        });
        CookieHelper.setCookie("darkTheme", isChecked, 365);
    }

    componentDidMount() {
        document.body.style.backgroundColor = '#777777';
    }

    render() {
        return (
            <MuiThemeProvider theme={this.themes[this.state.darkTheme]}>
                <Card>
                    <CardContent>
                        <Typography gutterBottom variant="headline" component="h2">
                            Playing
                            <span style={{float: "right"}}>
                                <Switch onChange={(event) => this.switchTheme.bind(this)(event.target.checked)}
                                        checked={this.state.darkTheme}/>
                            </span>
                        </Typography>
                        <br/>
                        <CurrentlyPlaying/>
                        <br/>
                        <SongSearch/>
                    </CardContent>
                </Card>
                <br/>
                <Card>
                    <CardContent>
                        <Typography gutterBottom variant="headline" component="h2">
                            Playlist
                        </Typography>
                        <SongList/>
                    </CardContent>
                </Card>
                <AdminContainer>
                    <br/>
                    <Card>
                        <CardContent>
                            <Typography gutterBottom variant="headline" component="h2">
                                Admin
                            </Typography>
                            <AdminTools/>
                            <Config/>
                        </CardContent>
                    </Card>
                </AdminContainer>
                <br/>
                <Card>
                    <CardContent>
                        <Login/>
                    </CardContent>
                </Card>
            </MuiThemeProvider>
        );
    }
}