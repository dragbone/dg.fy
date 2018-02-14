import React, { Component } from 'react';
import { List, ListItem } from 'material-ui/List';
import Avatar from 'material-ui/Avatar';
import TextField from 'material-ui/TextField';
import FlatButton from 'material-ui/FlatButton';
import FloatingActionButton from 'material-ui/FloatingActionButton';
import ContentSkipNext from 'material-ui/svg-icons/av/skip-next';
import ContentPlay from 'material-ui/svg-icons/av/play-arrow';
import ContentPause from 'material-ui/svg-icons/av/pause';

export default class SongSearch extends Component {
    constructor(){
        super();

        fetch(window.apiUrl + 'isLoggedIn', {credentials: 'include'})
            .then(response => response.json())
            .then(result => {
                if(result === true){
                    this.setState({ admin: true });
                }
            }
        );
    }

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

    skip() {
        fetch(window.apiUrl + 'skip', {credentials: 'include'});
    }
    play() {
        fetch(window.apiUrl + 'play', {credentials: 'include'});
    }
    pause() {
        fetch(window.apiUrl + 'pause', {credentials: 'include'});
    }

    clear(event) {
        this.setState({ searchText: "", searchResult: [] });
    }

    render() {
        let adminTools = null
        if(this.state.admin){
            adminTools = <div>
                              <FloatingActionButton mini={true} onClick={(event) => this.skip.bind(this)()}>
                                  <ContentSkipNext />
                              </FloatingActionButton>
                              <FloatingActionButton mini={true} onClick={(event) => this.play.bind(this)()}>
                                  <ContentPlay />
                              </FloatingActionButton>
                              <FloatingActionButton mini={true} onClick={(event) => this.pause.bind(this)()}>
                                  <ContentPause />
                              </FloatingActionButton>
                          </div>
        } else {
            adminTools = <div />
        }

        return (
            <div>
                <TextField floatingLabelText="Search Track" onChange={this.inputChanged.bind(this)} ref="searchText" value={this.state.searchText} />
                <FlatButton label="Clear" secondary={true} onClick={this.clear.bind(this)}/>
                {adminTools}
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