import React from "react";
import {instanceOf} from "prop-types";
import { withCookies, Cookies } from 'react-cookie';

class PaymentSuccessful extends React.Component {
    static propTypes = {
        cookies: instanceOf(Cookies).isRequired
    };

    constructor(props) {
        super(props);
        const { cookies } = props;
        this.state = {
            cart: [],
            tkn: cookies.get('tkn') || 'none'
        };
    }

    componentDidMount() {
        const { cookies } = this.props;
        const params = {
            headers: {
                'Accept': "application/json, text/plain, */*",
                'Content-Type': 'application/json; charset=utf-8',
                "X-Auth-Token": cookies.get('tkn', { path: '/' })
            },
            method: "DELETE"
        };
        fetch(`http://localhost:9000/api/cart/delete`, params)
            .then(res => res.json())
    }

    render() {
        return (
            <div>
                <h3> Thanks! Order has been placed. </h3>
            </div>
        );
    }
}

export default withCookies(PaymentSuccessful);