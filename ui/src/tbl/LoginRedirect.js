import React from 'react';
import 'materialize-css/dist/css/materialize.min.css';
import $ from 'jquery';
import M from "materialize-css";
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
        // this.global contex = ${this.props.params.token}
        const { cookies } = this.props;
        let tkn = this.props.match.params.token;
        cookies.set('tkn', tkn, { path: '/' });
        this.setState({ tkn });
        // window.location.reload()
    }

    render() {
        if(!window.location.hash) {
            window.location = window.location + '#loaded';
            window.location.reload();
        }
        console.log(this.state.tkn);

        return (
            <div>
                <h3> Hey! Welcome. Login successful... </h3>
            </div>
        );
    }
}

export default withCookies(LoginRedirect);