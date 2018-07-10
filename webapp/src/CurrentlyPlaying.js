import React, { Component } from 'react';
import Track from './Track';
import List from '@material-ui/core/List';
import LinearProgress from '@material-ui/core/LinearProgress';

export default class CurrentlyPlaying extends Component {
    constructor() {
        super();
        this.state = { track: null, progress: 0 };
    }

    componentDidMount() {
        window.currentlyPlayingCallback = this.loadContent.bind(this);
    }

    loadContent(playing) {
        if(playing.track){
            this.setState({ track: playing.track, progress: playing.progress / playing.track.lengthS * 100 });
        }
    }

    render() {
        var track = null;
        if (this.state.track != null) {
            track = <Track key={this.state.track.trackId + this.state.track.artist} state={this.state.track} blockVote={true} />;
        }
        return (
            <List>
                {track}
                <LinearProgress variant="determinate" color="secondary" value={this.state.progress} />
            </List>
        );
    }
}