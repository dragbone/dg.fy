import React, {Component} from 'react';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import Avatar from '@material-ui/core/Avatar';
import Badge from '@material-ui/core/Badge';
import ActionThumbUp from '@material-ui/icons/ThumbUp';
import IconButton from "@material-ui/core/IconButton";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";


export default class Track extends Component {
    constructor(props) {
        super(props);
        props.state.blockVote = props.blockVote;
        this.state = props.state;
    }

    vote(voteType) {
        fetch(window.apiUrl + 'queue/' + this.state.trackId + "?voteType=" + voteType, {
            method: 'POST'
        })
            .then(response => response.json())
            .then(result => {
                this.setState(result)
            });
    }

    upVote() {
        if (this.state.blockVote) return;
        if (this.state.voteType === "UPVOTE") {
            this.removeVote();
        } else {
            this.vote("UpVote");
        }
    }

    downVote() {
        if (this.state.blockVote) return;
        this.vote("DownVote");
    }

    removeVote() {
        if (this.state.blockVote) return;
        fetch(window.apiUrl + 'queue/' + this.state.trackId, {
            method: 'DELETE'
        })
            .then(response => response.json())
            .then(result => {
                this.setState(result)
            });
    }

    renderAlbumArt() {
        if (this.state.blockVote) {
            return <Avatar src={this.state.imageUrl}/>;
        } else {
            return <Badge badgeContent={this.state.numVotes} color="secondary">
                <Avatar src={this.state.imageUrl}/>;
            </Badge>
        }
    }

    renderVoteMenu() {
        if (!this.state.blockVote) {
            return <div>
                <IconButton onClick={() => this.upVote()}
                            color={this.state.voteType === "UPVOTE" ? "secondary" : "default"}>
                    <ActionThumbUp/>
                </IconButton>
            </div>;
        } else {
            return <span/>;
        }
    }

    renderVoters() {
        let text = "";
        for (let user in this.state.userVotes) {
            text += user + ", ";
        }
        return text.substr(0, text.length-2);
    }

    render() {
        return (
            <ListItem onClick={() => this.upVote()} button={!this.state.blockVote}>
                {this.renderAlbumArt()}
                <ListItemText primary={this.state.song} secondary={this.state.artist}/>
                <ListItemSecondaryAction>
                    <Tooltip title={this.renderVoters()} placement="left">{this.renderVoteMenu()}</Tooltip>
                </ListItemSecondaryAction>
            </ListItem>
        );
    }
}