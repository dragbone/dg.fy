import React, {Component} from 'react';
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem/ListItem";
import BoolConfigEntry from "./Config/BoolConfigEntry";
import InputConfigEntry from "./Config/InputConfigEntry";


export default class Config extends Component {
    state = {items: []};

    componentDidMount() {
        fetch(window.apiUrl + 'config')
            .then(response => response.json())
            .then(result => {
                this.setState({items: result});
            });
    }

    render() {
        return <span>
            <List>
                {this.state.items.map(item => <ListItem key={item.name}> {
                    item.type === "checkbox"
                        ? <BoolConfigEntry key={item.name} state={item}/>
                        : <InputConfigEntry key={item.name} state={item}/>
                }</ListItem>)}
            </List>
        </span>;
    }
}