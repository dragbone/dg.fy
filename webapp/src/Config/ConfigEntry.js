import React, {Component} from 'react';
import LoginHelper from "../LoginHelper";

export default class ConfigEntry extends Component {
    constructor(props) {
        super(props);
        props.state.error = false;
        this.state = props.state;
    }

    updateConfig(value){
        this.setState({value: value});
        fetch(window.apiUrl + 'config/' + this.state.name + "/" + value, {
            method: 'POST',
            headers: LoginHelper.getLoginHeader()
        }).then(response => {
            this.setState({error: !response.ok});
        });
    }
}