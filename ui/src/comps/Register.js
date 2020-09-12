import React, {useState} from 'react';
import {Link} from "react-router-dom";
import { useHistory } from "react-router-dom";

export function Register(props) {
    let history = useHistory();
    const [state , setState] = useState({
        email : "",
        password : "",
        confirmPassword : "",
        firstName: "",
        lastName: "",
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
        if(state.email.length && state.password.length && state.firstName.length && state.lastName.length) {
            const params = {
                headers: {
                    'Accept': "application/json, text/plain, */*",
                    'Content-Type': 'application/json; charset=utf-8'
                },
                body: JSON.stringify({
                    "email": state.email,
                    "firstName": state.firstName,
                    "lastName": state.lastName,
                    "password": state.password}),
                method: "POST"
            };

            fetch('http://ebiznes.com:9000/auth/register', params)
                .then(function (response) {
                    if(response.ok){
                        state.successMessage = 'Registration successful. Redirecting to home page...'
                        history.push('/');
                    } else{
                        console.log("Some error ocurred");
                    }
                })
                .catch(function (error) {
                    console.log(error);
                });
        }

    }

    const handleSubmitClick = (e) => {
        e.preventDefault();
        if(state.password === state.confirmPassword && state.password !== '' && state.email !== '' && state.firstName !== '' && state.lastName !== '') {
            sendDetailsToServer()
        } else {
            history.push('/failure');
            console.log("error occured when trying to register");
        }
    }
    return(
        <div className="col-12 col-lg-4 login-card mt-2 hv-center">
            <form className="col s12">
                <div className="row">
                    <div className="input-field col s3 offset-s3">
                        <input id="firstName" type="text" className="validate" value={state.firstName} onChange={handleChange}/>
                        <label htmlFor="firstName">First Name</label>
                    </div>
                    <div className="input-field col s3">
                        <input id="lastName" type="text" className="validate" value={state.lastName} onChange={handleChange}/>
                        <label htmlFor="lastName">Last Name</label>
                    </div>
                </div>
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
                <div className="row">
                    <div className="input-field col s6 offset-s3">
                        <input id="confirmPassword" type="password" className="validate" value={state.confirmPassword} onChange={handleChange}/>
                        <label htmlFor="password">Confirm Password</label>
                    </div>
                </div>
                <button
                    type="submit"
                    className="btn btn-primary z-depth-2 hoverable"
                    onClick={handleSubmitClick}>
                    Register
                </button>
            </form>
            <div className="alert alert-success mt-2" style={{display: state.successMessage ? 'block' : 'none' }} role="alert">
                {state.successMessage}
            </div>
            <div className="mt-2">
                <span>Already have an account? </span>
                <Link to="/login"><span>Login here</span></Link>
            </div>
            <br/> <br/>
            <a href="http://ebiznes.com:9000/auth/provider/google"><button className="btn btn-primary z-depth-2 hoverable">Google</button></a>
            <br/> <br/>
            <a href="http://ebiznes.com:9000/auth/provider/github"><button className="btn btn-primary z-depth-2 hoverable">GitHub</button></a>
        </div>
    )
}