import React, { Component } from 'react';
import { List, ListItem } from 'material-ui/List';
import Avatar from 'material-ui/Avatar';
import TextField from 'material-ui/TextField';
import FlatButton from 'material-ui/FlatButton';

export default class SongSearch extends Component {
    state = { searchResult: [], timer: null, searchText: "" };

    inputChanged(event) {
        this.setState({ searchText: event.target.value });
        if (this.state.searchText === "") {
            this.setState({
                searchResult: []
            });
        } else {
            var load = this.loadSearch.bind(this);
            if (this.state.timer != null) {
                window.clearTimeout(this.state.timer);
            }
            var timer = window.setTimeout(function () {
                load();
            }, 500);
            this.setState({ timer: timer });
        }
    }

    loadSearch() {
        fetch(window.apiUrl + 'search/' + this.state.searchText)
            .then(response => response.json())
            .then(result => {
                this.setState({
                    searchResult: result.tracks.items
                });
            });
    }

    onListItemSelected(trackId) {
        fetch(window.apiUrl + 'queue/add/' + trackId + '?voteType=UpVote')
            .then(result => {
                window.songlistCallback();
            });

    }

    clear(event) {
        this.setState({ searchText: "", searchResult: [] });
    }

    render() {
        return (
            <div>
                <TextField floatingLabelText="Search Track" onChange={this.inputChanged.bind(this)} ref="searchText" value={this.state.searchText} />
                <FlatButton label="Clear" secondary={true} onClick={this.clear.bind(this)}/>
                <List>
                    {this.state.searchResult.slice(0, 5).map((item, index) => <ListItem key={index}
                        primaryText={item.name}
                        secondaryText={item.artists[0].name}
                        leftAvatar={<Avatar src={item.album.images[item.album.images.length - 1].url} />}
                        onClick={(event) => this.onListItemSelected.bind(this)(item.id)}
                    />)}
                </List>
            </div>
        );
    }
}