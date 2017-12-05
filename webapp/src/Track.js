import React, { Component } from 'react';
import { ListItem } from 'material-ui/List';
import Avatar from 'material-ui/Avatar';
import Badge from 'material-ui/Badge';
import IconMenu from 'material-ui/IconMenu';
import MenuItem from 'material-ui/MenuItem';
import ActionThumbsUpDown from 'material-ui/svg-icons/action/thumbs-up-down';
import ActionThumbUp from 'material-ui/svg-icons/action/thumb-up';
import ActionThumbDown from 'material-ui/svg-icons/action/thumb-down';

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

    addVote(trackId, voteType) {
        fetch(window.apiUrl + 'queue/add/' + trackId + "?voteType=" + voteType)
            .then(response => response.json())
            .then(result => {
                this.setState(result)
            });
    }

    removeVote(trackId) {
        fetch(window.apiUrl + 'queue/remove/' + trackId)
            .then(response => response.json())
            .then(result => {
                this.setState(result)
            });
    }

    render() {
        let iconMenu = null;
        if (!this.state.blockVote) {
            let icon = null;
            if (this.state.voteType === "UPVOTE") {
                icon = <ActionThumbUp />;
            } else if (this.state.voteType === "DOWNVOTE") {
                icon = <ActionThumbDown />;
            } else {
                icon = <ActionThumbsUpDown />;
            }
            let badge = <Badge badgeContent={this.state.numVotes} secondary={true} onClick={(event) => event.stopPropagation()}>{icon}</Badge>;
            iconMenu = (
                <IconMenu iconButtonElement={badge}
                    anchorOrigin={{horizontal: 'left', vertical: 'center'}}
                    targetOrigin={{horizontal: 'left', vertical: 'center'}}>
                    <MenuItem primaryText="Thumb Up" leftIcon={<ActionThumbUp />} onClick={(event) => this.addVote.bind(this)(this.state.trackId, "UpVote")} />
                    <MenuItem primaryText="Reset" leftIcon={<ActionThumbsUpDown />} onClick={(event) => this.removeVote.bind(this)(this.state.trackId)} />
                    <MenuItem primaryText="Thumb Down" leftIcon={<ActionThumbDown />} onClick={(event) => this.addVote.bind(this)(this.state.trackId, "DownVote")} />
                </IconMenu>
            )
        }

        return (
            <ListItem
                primaryText={this.state.song}
                secondaryText={this.state.artist}
                leftAvatar={<Avatar src={this.state.imageUrl} />}
                rightIconButton={iconMenu}
                onClick={(event) => this.addVote.bind(this)(this.state.trackId, "UpVote")}
            />
        );
    }
}