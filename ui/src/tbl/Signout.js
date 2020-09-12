import React from 'react';
import 'materialize-css/dist/css/materialize.min.css';
import $ from 'jquery';
import M from "materialize-css";
import { withCookies, Cookies } from 'react-cookie';
import { instanceOf } from 'prop-types';
import { Redirect } from "react-router-dom";


class Signout extends React.Component {

    static propTypes = {
        cookies: instanceOf(Cookies).isRequired
    };

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        const { cookies } = this.props;

        const params = {
            headers: {
                'Accept': "application/json, text/plain, */*",
                'Content-Type': 'application/json; charset=utf-8',
                "X-Auth-Token": cookies.get('tkn', { path: '/' })
            },
            method: "POST"
        };

        fetch('http://ebiznes.com:9000/auth/logout', params)
                cookies.remove('tkn', {path: '/'})
        console.log("signout render cookie1")
        console.log(cookies.get('tkn', { path: '/' }))
    }

    render() {
        if(!window.location.hash) {
            window.location = window.location + '#loaded';
            window.location.reload();
        }
            return (
                <div>
                    <h3> Logout successful... </h3>
                </div>

            );
        }
}

export default withCookies(Signout);