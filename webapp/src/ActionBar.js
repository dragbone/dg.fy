import React, {Component} from 'react';
import MuteIcon from '@material-ui/icons/VolumeOff';
import Chip from '@material-ui/core/Chip';
import Avatar from "@material-ui/core/Avatar";
import AdminContainer from "./AdminContainer";
import AdminTools from "./AdminTools";

export default class ActionBar extends Component {
    constructor(props) {
        super(props);
        this.state = {
            muteInfo: {}
        };
    }

    componentDidMount(){
        fetch(window.apiUrl + 'mute')
            .then(response => response.json())
            .then(result => {
                this.loadContent(result);
            });
    }

    mute() {
        fetch(window.apiUrl + 'mute', {method: 'POST'})
            .then(response => response.json())
            .then(result => {
                this.loadContent(result.muteInfo);
            });
    }

    loadContent(muteInfo) {
        this.setState({muteInfo: muteInfo});
    }

    render() {
        let muteText = null;
        if(this.state.muteInfo.mutedUntil){
            muteText = "muted until " + this.state.muteInfo.mutedUntil
        }else{
            muteText = "mute for " + this.state.muteInfo.muteDurationMinutes + " min"
        }

        return <span>
            <Chip label={muteText} onClick={(event) => this.mute.bind(this)()} avatar={
                <Avatar> <MuteIcon/> </Avatar>
            }/>
            </span>
    }
}