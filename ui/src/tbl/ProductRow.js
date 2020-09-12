import React from "react";
import {instanceOf} from "prop-types";
import {Cookies, withCookies} from "react-cookie";
import {RedirectButton} from "./Products";

class ProductRow extends React.Component {
    static propTypes = {
        cookies: instanceOf(Cookies).isRequired
    };

    constructor(props) {
        super(props);
        const { cookies } = props;
        this.state = {isToggleOn: true,
            cart: [],
            tkn: cookies.get('tkn') || 'none'
        };
        this.handleCartClick = this.handleCartClick.bind(this);
    }

    handleCartClick() {
        const { cookies } = this.props;
        console.log(cookies);
        fetch('http://localhost:9000/api/cart', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                "X-Auth-Token": cookies.get('tkn', { path: '/' })
            },
            body: JSON.stringify({
                id: 0, // it will get automatically incremented in the db
                user_id: cookies.get('tkn', { path: '/' }), //fill this out when implementing logging in = ?
                product_id: this.props.product.id,
                quantity: 1
            })
        })
            .then(res => res.json())
    }

    render() {
        const product = this.props.product;
        const product_details_address = '/product/' +  this.props.product.id;
        const name = product.stock > 0 ?
            product.name :
            <span style={{color: 'lightgray'}}>
                {product.name}
                </span>;
        const price = product.promotion > 0 ? <span style={{color: 'red'}}> {product.price - (product.price * product.promotion/100)} </span> : product.price;
        const cart_button = product.stock > 0 ?
            <button onClick={this.handleCartClick} class="btn-floating btn-primary z-depth-2 hoverable pink">+</button> : '';

        return (
            <tr>
                <td>{name}</td>
                <td>{price}</td>
                <td style={{width: '10%'}}><RedirectButton to={product_details_address}/></td>
                <td style={{width: '10%'}}>{cart_button}</td>
            </tr>
        );
    }
}

export default withCookies(ProductRow);