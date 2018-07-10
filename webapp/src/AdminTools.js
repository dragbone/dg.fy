import React, {Component} from 'react';
import Button from '@material-ui/core/Button';
import MuteIcon from '@material-ui/icons/VolumeOff';
import PlayIcon from '@material-ui/icons/PlayArrow';
import PauseIcon from '@material-ui/icons/Pause';
import SkipIcon from '@material-ui/icons/SkipNext';
import Chip from '@material-ui/core/Chip';
import Avatar from "@material-ui/core/Avatar";

export default class AdminTools extends Component {
    constructor(props) {
        super(props);
        this.state = {
            admin: false,
            muteInfo: {}
        };
    }

    componentDidMount() {
        window.adminToolsCallback = this.loadContent.bind(this);
        fetch(window.apiUrl + 'isLoggedIn', {credentials: 'same-origin'})
            .then(response => response.json())
            .then(result => {
                    if (result === true) {
                        this.setState({admin: true});
                    }
                }
            );
    }

    skip() {
        fetch(window.apiUrl + 'skip', {credentials: 'same-origin'});
    }

    play() {
        fetch(window.apiUrl + 'play', {credentials: 'same-origin'});
    }

    pause() {
        fetch(window.apiUrl + 'pause', {credentials: 'same-origin'});
    }

    mute() {
        fetch(window.apiUrl + 'mute')
            .then(response => response.json())
            .then(result => {
                this.loadContent(result.muteInfo);
            });
    }

    loadContent(muteInfo) {
        this.setState({admin: this.state.admin, muteInfo: muteInfo});
    }

    render() {
        const style = {
            marginRight: 12
        };
        let actions = null;
        if (this.state.admin) {
            actions = <span>
                <Button variant="fab" style={style} mini={true} onClick={(event) => this.play.bind(this)()}>
                    <PlayIcon />
                </Button>
                <Button variant="fab" style={style} mini={true} onClick={(event) => this.pause.bind(this)()}>
                    <PauseIcon />
                </Button>
                <Button variant="fab" style={style} mini={true} onClick={(event) => this.skip.bind(this)()}>
                    <SkipIcon />
                </Button>
            </span>;
        }

        let muteText = null;
        if(this.state.muteInfo.mutedUntil){
            muteText = "muted until " + this.state.muteInfo.mutedUntil
        }else{
            muteText = "mute for " + this.state.muteInfo.muteDurationMinutes + " min"
        }

        return <span style={{marginLeft: 30}}>
                {actions}
            <Chip label={muteText} onClick={(event) => this.mute.bind(this)()} avatar={
                <Avatar> <MuteIcon/> </Avatar>
            }/>
            </span>
    }
}