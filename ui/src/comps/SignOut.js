import React from 'react';
import 'materialize-css/dist/css/materialize.min.css';
import { withCookies, Cookies } from 'react-cookie';
import { instanceOf } from 'prop-types';

class SignOut extends React.Component {

    static propTypes = {
        cookies: instanceOf(Cookies).isRequired
    };

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
        console.log("bye")
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

export default withCookies(SignOut);