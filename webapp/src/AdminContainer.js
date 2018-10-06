import React, {Component} from 'react';
import LoginHelper from "./LoginHelper";

export default class AdminContainer extends Component {
    constructor(props) {
        super(props);
        this.state = {
            admin: false,
        };
    }

    componentDidMount() {
        fetch(window.apiUrl + 'login', {
            method: 'POST',
            headers: LoginHelper.getLoginHeader(),
        })
            .then(response => response.json())
            .then(result => {
                    if (result === true) {
                        this.setState({admin: true});
                    }
                }
            );
    }

    render() {
        return <span>{this.state.admin && this.props.children}</span>
    }
}