import React from 'react';
import TextField from "@material-ui/core/TextField";
import ConfigEntry from "./ConfigEntry";

export default class InputConfigEntry extends ConfigEntry {
    handleChange = event => {
        let value = event.target.value;
        if (value === "") {
            this.setState({value: value});
            this.setState({error: true});
            return;
        }
        this.updateConfig(value);
    };

    render() {
        return <TextField
            id={this.state.name}
            label={this.state.name}
            value={this.state.value}
            error={this.state.error}
            onChange={this.handleChange}
            type={this.state.type}
            InputLabelProps={{
                shrink: true,
            }}
            margin="normal"
        />;
    }
}