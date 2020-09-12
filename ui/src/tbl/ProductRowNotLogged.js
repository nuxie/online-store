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

        return (
            <tr>
                <td>{name}</td>
                <td>{price}</td>
                <td style={{width: '10%'}}><RedirectButton to={product_details_address}/></td>
            </tr>
        );
    }
}

export default withCookies(ProductRow);