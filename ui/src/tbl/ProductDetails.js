import React from 'react';
import 'materialize-css/dist/css/materialize.min.css';
import $ from 'jquery';
import M from "materialize-css";
import {
    useParams
} from "react-router-dom";

class ProductDetails extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            product: {},
            reviews: []
        };
    }

    componentDidMount() {
        // this.global contex = ${this.props.params.token}
        fetch(`http://localhost:9000/api/products/${this.props.params.id}`)
            .then(res => res.json())
            .then(
                (result) => {
                    this.setState({
                        product: result,
                    });
                }
            )
        fetch(`http://localhost:9000/api/product_reviews/${this.props.params.id}`)
            .then(res => res.json())
            .then(
                (result) => {
                    this.setState({
                        reviews: result,
                    });
                }
            )
    }

    render() {
        const rev = [];
        console.log(this.state.product);
        console.log(this.props.product);
        console.log(this.state.reviews);
        console.log(this.props.reviews);
        this.state.reviews.forEach((review) => {
            rev.push(<div>{review.description}</div>);
        });

        return (
            <div>
                <h3>{this.state.product.name}</h3>
                <div>Price: {this.state.product.price}</div>
                <div>Description: <br/> {this.state.product.description}</div>
                <br/> <br/>
                <h3>Reviews</h3>
                <div>{rev}</div>
            </div>
        );
    }
}


export default (props) => (
    <ProductDetails
        {...props}
        params={useParams()}
    />
);