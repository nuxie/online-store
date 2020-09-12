import React, {useState} from 'react';
import './App.css';
import 'materialize-css'; // It installs the JS asset only
import 'materialize-css/dist/css/materialize.min.css';
import * as products_all from "./tbl/Products";
import * as products_all_not_logged from "./tbl/ProductsNotLogged";
import ProductDetails from "./tbl/ProductDetails";
import Cart from "./tbl/Cart";
import {Register} from "./tbl/Register";
import {Login} from "./tbl/Login";
import {Fail} from "./tbl/Fail";
import LoginRedirect from "./tbl/LoginRedirect";
import Signout from "./tbl/Signout";
import { withCookies, Cookies } from 'react-cookie';
import { instanceOf } from 'prop-types';

import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link
} from "react-router-dom";
import {Payment} from "./tbl/Payment";
import PaymentSuccessful from "./tbl/PaymentSuccessful";


class App extends React.Component {

    static propTypes = {
        cookies: instanceOf(Cookies).isRequired
    };

    constructor(props) {
        super(props);
        const { cookies } = props;
        this.state = {
            tkn: cookies.get('tkn')
        };
    }

    render(){
        console.log("App render")
        console.log(this.state.tkn)
        if(this.state.tkn !== undefined) {
            console.log("NOT UNDEFINED")
            console.log(this.state.tkn)
            return (
                <Router>
                    <div className="App">
                        <nav>
                            <div className="container">
                                <ul id="nav-mobile" className="left hide-on-med-and-down">
                                    <li><Link to="/cart">Cart</Link></li>
                                    <li><Link to="/products">Products</Link></li>
                                </ul>
                                <ul id="nav-mobile" className="right hide-on-med-and-down">
                                    <li><Link to="/signout">Sign Out</Link></li>
                                </ul>
                            </div>
                        </nav>

                        {/* A <Switch> looks through its children <Route>s and
            renders the first one that matches the current URL. */}
                        <Switch>
                            <Route path="/cart">
                                <Cart />
                            </Route>
                            <Route path="/payment">
                                <Payment />
                            </Route>
                            <Route path="/payment-successful">
                                <PaymentSuccessful />
                            </Route>
                            <Route path="/products">
                                <products_all.Products />,
                            </Route>
                            <Route path="/product/:id">
                                <ProductDetails />,
                            </Route>
                            <Route path="/login">
                                <Login />,
                            </Route>
                            <Route path="/register">
                                <Register />,
                            </Route>
                            <Route path="/failure">
                                <Fail />,
                            </Route>
                            <Route exact path="/auth/successful/:token" component={LoginRedirect} />
                            <Route path="/signout" component={Signout} />,
                        </Switch>
                    </div>
                </Router>
            )
        } else {
            console.log("UNDEFINED")
            console.log(this.state.tkn)
            return (
                <Router>
                    <div className="App">
                        <nav>
                            <div className="container">
                                <ul id="nav-mobile" className="left hide-on-med-and-down">
                                    <li><Link to="/products">Products</Link></li>
                                </ul>
                                <ul id="nav-mobile" className="right hide-on-med-and-down">
                                    <li><Link to="/login">Login</Link></li>
                                    <li><Link to="/register">Register</Link></li>
                                </ul>
                            </div>
                        </nav>

                        {/* A <Switch> looks through its children <Route>s and
            renders the first one that matches the current URL. */}
                        <Switch>
                            <Route path="/cart">
                                <Cart />
                            </Route>
                            <Route path="/products">
                                <products_all_not_logged.Products />,
                            </Route>
                            <Route path="/product/:id">
                                <ProductDetails />,
                            </Route>
                            <Route path="/login">
                                <Login />,
                            </Route>
                            <Route path="/register">
                                <Register />,
                            </Route>
                            <Route path="/failure">
                                <Fail />,
                            </Route>
                            <Route exact path="/auth/successful/:token" component={LoginRedirect} />
                            <Route path="/signout">
                                <Signout />,
                            </Route>
                        </Switch>
                    </div>
                </Router>
            )
        }
    }}

export default withCookies(App);