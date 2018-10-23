import React, {Component} from 'react';
import List from '@material-ui/core/List';
import LinearProgress from '@material-ui/core/LinearProgress';
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction/ListItemSecondaryAction";
import ListItem from "@material-ui/core/ListItem/ListItem";
import MuteChip from "./MuteChip";
import Avatar from "@material-ui/core/Avatar/Avatar";

export default class CurrentlyPlaying extends Component {
    constructor(props) {
        super(props);
        this.state = {
            track: null,
            progress: 0,
            muteInfo: null
        };
    }

    componentWillReceiveProps(nextProps, nextContent) {
        if (nextProps.playing.track != null) {
            this.setState({
                track: nextProps.playing.track,
                progress: nextProps.playing.progress / nextProps.playing.track.lengthS * 100,
                muteInfo: nextProps.muteInfo
            });
        }
    }

    render() {
        let track = null;
        if (this.state.track != null) {
            track = <ListItem>
                <Avatar src={this.state.track.imageUrl}/>
                <ListItemText primary={this.state.track.song} secondary={this.state.track.artist}/>
                <ListItemSecondaryAction>
                    <MuteChip muteInfo={this.state.muteInfo}/>
                </ListItemSecondaryAction>
            </ListItem>
        }
        return (
            <List>
                {track}
                <LinearProgress variant="determinate" color="secondary" value={this.state.progress}/>
            </List>
        );
    }
}