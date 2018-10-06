import React, {Component} from 'react';
import TextField from "@material-ui/core/TextField";
import LoginHelper from "./LoginHelper";
import Checkbox from "@material-ui/core/Checkbox/Checkbox";
import ListItem from "@material-ui/core/ListItem/ListItem";
import FormControlLabel from "@material-ui/core/FormControlLabel/FormControlLabel";


export default class ConfigEntry extends Component {
    constructor(props) {
        super(props);
        this.state = props.state;
    }

    handleChange = event => {
        let value = event.target.value;
        switch (this.state.type) {
            case "checkbox":
                value = event.target.checked;
                break;
        }

        fetch(window.apiUrl + 'config/' + this.state.name + "/" + value, {
            method: 'POST',
            headers: LoginHelper.getLoginHeader()
        }).then(response => {
            if (response.ok) {
                this.setState({value: value});
            }
        });
    };

    render() {
        switch (this.state.type) {
            case "checkbox":
                return <FormControlLabel
                    label={this.state.name}
                    control={<Checkbox
                        checked={this.state.value}
                        onChange={this.handleChange}
                        margin="normal"
                    />}
                />;
        }
        return <TextField
            id={this.state.name}
            label={this.state.name}
            value={this.state.value}
            onChange={this.handleChange}
            type={this.state.type}
            InputLabelProps={{
                shrink: true,
            }}
            margin="normal"
        />;
    }
}