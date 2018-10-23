import React, {Component} from 'react';
import Track from './Track';
import List from '@material-ui/core/List';

export default class SongList extends Component {
    constructor(props) {
        super(props);
        this.state = {items: []};
    }

    componentWillReceiveProps(nextProps, nextContent) {
        this.setState({items: nextProps.tracks});
    }

    render() {
        return (
            <List>
                {this.state.items.map(item =>
                    <Track key={item.trackId + item.artist + item.numVotes} state={item}/>
                )}
            </List>
        );
    }
}