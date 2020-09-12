import React from "react";

export class Payment extends React.Component {

    handlePayClick() {
        console.log("paying...")
    }

    render() {
        return (
            <div>
                <h3>Please pay for the order</h3>
                <a href="http://localhost:3000/payment-successful"><button
                    type="submit"
                    onSubmit={this.handlePayClick()}
                    className="btn btn-primary z-depth-2 hoverable">
                    $$$
                </button></a>
            </div>
        );
    }
}