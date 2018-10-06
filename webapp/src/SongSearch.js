import React, {Component} from 'react';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Avatar from '@material-ui/core/Avatar';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';

export default class SongSearch extends Component {
    state = {searchResult: [], timer: null, searchText: ""};

    inputChanged(event) {
        this.setState({searchText: event.target.value});
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
            this.setState({timer: timer});
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
        fetch(window.apiUrl + 'queue/' + trackId + '?voteType=UpVote', {
            method: 'POST'
        })
            .then(result => {
                window.songlistCallback();
            });
    }

    clear(event) {
        this.setState({searchText: "", searchResult: []});
    }

    render() {
        return (
            <div>
                <TextField label="Search Track" onChange={this.inputChanged.bind(this)}
                           value={this.state.searchText}/>
                <Button color="secondary" onClick={this.clear.bind(this)}>Clear</Button>

                <List>
                    {this.state.searchResult.slice(0, 10).map((item, index) => <ListItem
                        button
                        key={index}
                        onClick={(event) => this.onListItemSelected.bind(this)(item.id)}
                    >
                        <Avatar src={item.album.images[item.album.images.length - 1].url}/>
                        <ListItemText
                            primary={<span>{item.name}<span style={{color: '#777777'}}> {item.artists[0].name} <span
                                style={{color: '#aaaaaa'}}>[{item.album.name}]</span></span></span>}
                            secondary={this.state.artist}/>
                    </ListItem>)}
                </List>
            </div>
        );
    }
}