import React, { Component } from 'react';
import Track from './Track';
import List from '@material-ui/core/List';

export default class SongList extends Component {
    constructor() {
        super();
        this.state = { items: [] };
    }

    componentDidMount() {
        this.loadContent();
        window.songlistCallback = this.loadContent.bind(this);
        setInterval(this.loadContent.bind(this), 2000);
    }

    loadContent() {
        fetch(window.apiUrl + 'queue')
            .then(response => response.json())
            .then(result => {
                this.setState({ items: result.playlist.tracks });
                window.currentlyPlayingCallback(result.playlist.playing);
                window.adminToolsCallback(result.muteInfo);
            });
    }

    render() {
        return (
            <List>
                {this.state.items.map(item => <Track key={item.trackId + item.artist + item.numVotes} state={item} />)}
            </List>
        );
    }
}