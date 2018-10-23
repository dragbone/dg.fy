import React, {Component} from 'react';
import MuteIcon from '@material-ui/icons/VolumeOff';
import Chip from '@material-ui/core/Chip';
import Avatar from "@material-ui/core/Avatar";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";

export default class MuteChip extends Component {
    constructor(props) {
        super(props);
        this.state = {
            mutedUntil: "",
            muteUser: "",
            muteDurationMinutes: 0
        };
    }

    componentDidMount() {
        fetch(window.apiUrl + 'mute')
            .then(response => response.json())
            .then(result => {
                this.loadContent(result);
            });
    }

    componentWillReceiveProps(nextProps, nextContent) {
        this.loadContent(nextProps.muteInfo)
    }

    mute() {
        fetch(window.apiUrl + 'mute', {method: 'POST'})
            .then(response => response.json())
            .then(result => {
                this.loadContent(result.muteInfo);
            });
    }

    loadContent(muteInfo) {
        this.setState(muteInfo);
    }

    render() {
        let muteText = null;
        if (this.state.mutedUntil) {
            muteText = "muted until " + this.state.mutedUntil
        } else {
            muteText = "mute for " + this.state.muteDurationMinutes + " min"
        }

        return <span>
            <Tooltip title={this.state.muteUser} placement="left">
                <Chip label={muteText} onClick={(event) => this.mute.bind(this)()} avatar={
                    <Avatar> <MuteIcon/> </Avatar>
                }/>
            </Tooltip>
            </span>
    }
}