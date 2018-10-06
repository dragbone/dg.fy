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


    render() {
        const style = {
            marginRight: 12
        };

        return <span style={{marginLeft: 30}}>
                <Button variant="fab" style={style} mini={true} onClick={(event) => this.play.bind(this)()}>
                    <PlayIcon/>
                </Button>
                <Button variant="fab" style={style} mini={true} onClick={(event) => this.pause.bind(this)()}>
                    <PauseIcon/>
                </Button>
                <Button variant="fab" style={style} mini={true} onClick={(event) => this.skip.bind(this)()}>
                    <SkipIcon/>
                </Button>
            </span>
    }
}