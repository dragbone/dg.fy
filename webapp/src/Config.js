import React, {Component} from 'react';
import ConfigEntry from "./ConfigEntry";
import List from "@material-ui/core/List";
import AdminTools from "./AdminTools";
import AdminContainer from "./AdminContainer";
import ListItem from "@material-ui/core/ListItem/ListItem";


export default class Config extends Component {
    state = {items: []};

    componentDidMount() {
        fetch(window.apiUrl + 'config')
            .then(response => response.json())
            .then(result => {
                /*let items = [];
                for (let i in result) {
                    console.log(i + ":" + result[i]);
                    items.push({name: i, value: result[i]});
                }*/
                console.log(result);
                this.setState({items: result});
            });
    }

    render() {
        return <span>
            <span style={{float:"right"}}><AdminTools/></span>
            <List>
                {this.state.items.map(item => <ListItem><ConfigEntry key={item.name} state={item}/></ListItem>)}
            </List>
        </span>;
    }
}