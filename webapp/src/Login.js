import React, {Component} from 'react';
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button/Button";
import LoginHelper from "./LoginHelper";
import CookieHelper from "./CookieHelper";


export default class Login extends Component {
    constructor(props) {
        super(props);
        this.state = {value: ""};
    }

    handleLogin = event => {
        let value = this.state.value;
        fetch(window.apiUrl + 'login', {
            method: 'POST',
            headers: LoginHelper.makeLoginHeader(value)
        }).then(response => {
            if (response.ok) {
                CookieHelper.setCookie("password", value, 365);
                document.location.reload();
            }
        });
    };

    handleChange = event => {
        this.setState({value: event.target.value});
    };

    render() {
        return <span><TextField
            id="password"
            label="Password"
            value={this.state.value}
            onChange={this.handleChange}
            type="password"
            InputLabelProps={{
                shrink: true,
            }}
            margin="normal"
        />
            <Button onClick={this.handleLogin}>Login</Button>
        </span>;
    }
}