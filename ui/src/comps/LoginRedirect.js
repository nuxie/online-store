import React from 'react';
import 'materialize-css/dist/css/materialize.min.css';
import { withCookies, Cookies } from 'react-cookie';
import { instanceOf } from 'prop-types';


class LoginRedirect extends React.Component {

    static propTypes = {
        cookies: instanceOf(Cookies).isRequired
    };

    constructor(props) {
        super(props);
        const { cookies } = props;
        this.state = {
            tkn: cookies.get('tkn') || 'none'
        };
    }

    componentDidMount() {
        const { cookies } = this.props;
        let tkn = this.props.match.params.token;
        cookies.set('tkn', tkn, { path: '/' });
        this.setState({ tkn });
    }

    render() {
        if(!window.location.hash) {
            window.location = window.location + '#loaded';
            window.location.reload();
        }

        return (
            <div>
                <h3> Hey! Welcome. Login successful... </h3>
            </div>
        );
    }
}

export default withCookies(LoginRedirect);