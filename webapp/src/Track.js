import React, { Component } from 'react';
import { ListItem } from 'material-ui/List';
import Avatar from 'material-ui/Avatar';
import Badge from 'material-ui/Badge';
import ActionFavorite from 'material-ui/svg-icons/action/favorite';
import ActionFavoriteBorder from 'material-ui/svg-icons/action/favorite-border';

export default class Track extends Component {
    constructor(props) {
        super(props);
        if (props.blockVote) {
            props.state.blockVote = true;
        } else {
            props.state.blockVote = false;
        }
        this.state = props.state;
    }

    vote(trackId) {
        if (!this.state.blockVote) {
            let command = "";
            if (this.state.userVote) {
                command = "remove";
            } else {
                command = "add";
            }
            fetch(window.apiUrl + 'queue/' + command + "/" + trackId)
                .then(response => response.json())
                .then(result => {
                    this.setState(result)
                });
        }
    }

    render() {
        let badge = null;
        if (!this.state.blockVote) {
            let icon = null;
            if (this.state.userVote) {
                icon = <ActionFavorite />;
            } else {
                icon = <ActionFavoriteBorder />;
            }
            badge = <Badge badgeContent={this.state.numVotes} secondary={true} >{icon}</Badge>;
        }
        return (
            <ListItem
                primaryText={this.state.song}
                secondaryText={this.state.artist}
                leftAvatar={<Avatar src={this.state.imageUrl} />}
                rightIcon={badge}
                onClick={(event) => this.vote.bind(this)(this.state.trackId)}
            />
        );
    }
}