import React from "react";
import { withCookies, Cookies } from 'react-cookie';
import { instanceOf } from 'prop-types';

class Cart extends React.Component {
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

    handleBuyClick() {
        console.log("buying...")
    }

    componentDidMount() {
        const { cookies } = this.props;
        const params = {
            headers: {
                'Accept': "application/json, text/plain, */*",
                "X-Auth-Token": cookies.get('tkn', { path: '/' })
            },
            method: "GET"
        };

        fetch("http://localhost:9000/api/cart/details_extended", params)
            .then(res => res.json())
            .then(
                (result) => {
                    this.setState({
                        cart: result
                    });
                }
            )
    }

    render() {
        if(this.state.cart.length === 0) {
            return (
                    <h2> Nothing to see here... Let's go shopping! </h2>
            );
        } else {
            return (
                <div className="container col-12 col-lg-4 login-card mt-2 hv-center">
                    <h2>Cart</h2>
                    <table className="centered">
                        <thead>
                        <tr>
                            <th>Product</th>
                            <th>Quantity</th>
                            <th>Promotion</th>
                            <th>Price</th>
                        </tr>
                        </thead>
                        <tbody>
                        {this.state.cart.map(c =>
                            <tr key={c.productId}>
                                <td>{c.name}</td>
                                <td>{c.quantity}</td>
                                <td>{c.promotion}</td>
                                <td>{c.price - (c.price * c.promotion/100)}</td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                    <h4>Total price: {this.state.cart.reduce((prev, next) => prev + next.price - (next.price * next.promotion/100), 0)}</h4>
                    <a href="http://localhost:3000/payment">
                        <button
                            type="submit"
                            onSubmit={this.handleBuyClick()}
                            className="btn btn-primary z-depth-2 hoverable">
                            Buy
                        </button>
                    </a>
                </div>
            );
        }
    }
}

export default withCookies(Cart);