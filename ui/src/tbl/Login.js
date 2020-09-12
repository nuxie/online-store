import React, {useState} from 'react';
import {Link} from "react-router-dom";
import { useHistory } from "react-router-dom";

export function Login(props) {
    let history = useHistory();
    const [state , setState] = useState({
        email : "",
        password : "",
        successMessage: null
    })
    const handleChange = (e) => {
        const {id , value} = e.target
        setState(prevState => ({
            ...prevState,
            [id] : value
        }))
    }

    const sendDetailsToServer = () => {
        if(state.email.length && state.password.length) {
            const params = {
                headers: {
                    'Accept': "application/json, text/plain, */*",
                    'Content-Type': 'application/json; charset=utf-8'
                },
                body: JSON.stringify({
                    "email": state.email,
                    "password": state.password
                }),
                method: "POST"
            };

            fetch('http://ebiznes.com:9000/auth/login', params)
                .then(result => result.json())
                .then(data => {
                    if(data.token !== undefined) {
                        console.log(data.token)
                        history.push('/auth/successful/' + data.token);
                    } else {
                        history.push('/failure');
                    }

                })
        }

    }

    const handleSubmitClick = (e) => {

        e.preventDefault();
        if(state.email !== '' && state.password !== '') {
            sendDetailsToServer()
        }
    }
    return(
        <div className="col-12 col-lg-4 login-card mt-2 hv-center">
            <form className="col s12">
                <div className="row">
                    <div className="input-field col s6 offset-s3">
                        <input id="email" type="email" className="validate" value={state.email} onChange={handleChange}/>
                        <label htmlFor="email">Email</label>
                    </div>
                </div>
                <div className="row">
                    <div className="input-field col s6 offset-s3">
                        <input id="password" type="password" className="validate" value={state.password} onChange={handleChange}/>
                        <label htmlFor="password">Password</label>
                    </div>
                </div>
                <button
                    type="submit"
                    className="btn btn-primary z-depth-2 hoverable"
                    onClick={handleSubmitClick}>
                    Login
                </button>
            </form>
            <div className="alert alert-success mt-2" style={{display: state.successMessage ? 'block' : 'none' }} role="alert">
                {state.successMessage}
            </div>
            <div className="mt-2">
                <span>No account? </span>
                <Link to="/register"><span>Register here</span></Link>
            </div>
            <br/> <br/>
            <a href="http://ebiznes.com:9000/auth/provider/google"><button className="btn btn-primary z-depth-2 hoverable">Google</button></a>
            <br/> <br/>
            <a href="http://ebiznes.com:9000/auth/provider/github"><button className="btn btn-primary z-depth-2 hoverable">GitHub</button></a>
        </div>
    )
}