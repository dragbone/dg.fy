import React, {Component} from 'react';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import Avatar from '@material-ui/core/Avatar';
import Badge from '@material-ui/core/Badge';
import MenuItem from '@material-ui/core/MenuItem';
import ActionThumbsUpDown from '@material-ui/icons/ThumbsUpDown';
import ActionThumbUp from '@material-ui/icons/ThumbUp';
import ActionThumbDown from '@material-ui/icons/ThumbDown';
import Menu from "@material-ui/core/Menu";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import IconButton from "@material-ui/core/IconButton";

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

    vote(voteType) {
        fetch(window.apiUrl + 'queue/add/' + this.state.trackId + "?voteType=" + voteType)
            .then(response => response.json())
            .then(result => {
                this.setState(result)
            });
    }

    upVote(){
        this.vote("UpVote");
        this.setState({vote: 1});
    }

    downVote(){
        this.vote("DownVote");
        this.setState({vote: -1});
    }

    removeVote() {
        this.setState({vote: 0});
        fetch(window.apiUrl + 'queue/remove/' + this.state.trackId)
            .then(response => response.json())
            .then(result => {
                this.setState(result)
            });
    }

    render() {
        return (
            <ListItem onClick={() => this.upVote()} button>
                <Badge badgeContent={this.state.numVotes} color="secondary">
                    <Avatar src={this.state.imageUrl}/>
                </Badge>
                <ListItemText primary={this.state.song} secondary={this.state.artist}/>
                <ListItemSecondaryAction>
                    <IconButton onClick={() => this.downVote()} color={this.state.voteType === "DOWNVOTE" ? "secondary" : "default"} >
                        <ActionThumbDown/>
                    </IconButton>
                    <IconButton onClick={() => this.removeVote()} color={this.state.voteType === "NONE" ? "secondary" : "default"}>
                        <ActionThumbsUpDown/>
                    </IconButton>
                    <IconButton onClick={() => this.upVote()} color={this.state.voteType === "UPVOTE" ? "secondary" : "default"}>
                        <ActionThumbUp/>
                    </IconButton>
                </ListItemSecondaryAction>
            </ListItem>
        );
    }
}