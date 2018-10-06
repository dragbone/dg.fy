import React from 'react';
import Checkbox from "@material-ui/core/Checkbox/Checkbox";
import FormControlLabel from "@material-ui/core/FormControlLabel/FormControlLabel";
import ConfigEntry from "./ConfigEntry";

export default class BoolConfigEntry extends ConfigEntry {
    constructor(props) {
        props.state.value = props.state.value == 'true';
        super(props);
    }

    handleChange = event => {
        this.updateConfig(event.target.checked);
    };

    render() {
        return <FormControlLabel
            label={this.state.name}
            control={<Checkbox
                checked={this.state.value}
                onChange={this.handleChange}
                margin="normal"
            />}
        />;
    }
}