import React, {Component} from 'react';
import Button from '@material-ui/core/Button';
import Play from '@material-ui/icons/PlayArrow';
import Pause from '@material-ui/icons/Pause';
import Skip from '@material-ui/icons/SkipNext';
import Report from '@material-ui/icons/Report';
import DeleteSweep from '@material-ui/icons/DeleteSweep';
import LoginHelper from "./LoginHelper";

export default class AdminTools extends Component {
    skip() {
        fetch(window.apiUrl + 'skip', {
            headers: LoginHelper.getLoginHeader()
        });
    }

    play() {
        fetch(window.apiUrl + 'play', {
            headers: LoginHelper.getLoginHeader()
        });
    }

    pause() {
        fetch(window.apiUrl + 'pause', {
            headers: LoginHelper.getLoginHeader()
        });
    }

    purge() {
        fetch(window.apiUrl + 'queue', {
            method: "delete",
            headers: LoginHelper.getLoginHeader()
        });
    }

    report() {
        fetch(window.apiUrl + 'report', {
            headers: LoginHelper.getLoginHeader()
        });
    }

    render() {
        const style = {
            marginRight: 12
        };

        return <span style={{marginLeft: 30}}>
                <Button variant="fab" style={style} mini={true} onClick={() => this.play.bind(this)()}>
                    <Play/>
                </Button>
                <Button variant="fab" style={style} mini={true} onClick={() => this.pause.bind(this)()}>
                    <Pause/>
                </Button>
                <Button variant="fab" style={style} mini={true} onClick={() => this.skip.bind(this)()}>
                    <Skip/>
                </Button>
                <Button variant="fab" style={style} mini={true} onClick={() => this.purge.bind(this)()}>
                    <DeleteSweep/>
                </Button>
                <Button variant="fab" style={style} mini={true} onClick={() => this.report.bind(this)()}>
                    <Report/>
                </Button>
            </span>
    }
}