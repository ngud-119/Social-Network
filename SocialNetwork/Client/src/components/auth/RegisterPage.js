import React, { Component } from 'react';
import '../../styles/FormPages.css'
import { requester, observer } from '../../infrastructure'
import Input from '../common/Input'
import { toast } from 'react-toastify';
import { ToastComponent } from '../common'

export default class RegisterPage extends Component {
    constructor(props) {
        super(props)

        this.state = {
            username: '',
            email: '',
            password: '',
            confirmPassword: '',
            firstName: '',
            lastName: '',
            address: '',
            city: '',
            touched: {
                username: false,
                email: false,
                password: false,
                confirmPassword: false,
                firstName: false,
                lastName: false,
                address: false,
                city: false,
            }
        };

        this.onChangeHandler = this.onChangeHandler.bind(this);
        this.onSubmitHandler = this.onSubmitHandler.bind(this);
        // this.buttonEnableFunc = this.buttonEnableFunc.bind(this);
    }

    onChangeHandler(event) {
        console.log('name: ', event.target.name)
        console.log('value: ', event.target.value)
        this.setState({
            [event.target.name]: event.target.value
        });
    }

    onSubmitHandler(event) {
        event.preventDefault();
        console.log('event: ', event);
        debugger;

        const {touched, ...otherProps} = this.state;

        requester.post('/users/register', { ...otherProps }, (response) => {

            console.log('response: ', response)
            debugger;


            if (response.success === true) {
                console.log('success message: ', response.message);
                debugger;
                toast.success(<ToastComponent.successToast text={response.message} />, {
                    position: toast.POSITION.TOP_RIGHT
                });
                // observer.trigger(observer.events.notification, { type: 'success', message: response.message });
                this.props.history.push('/login');
            } else {
                console.log('error message: ', response.message);
                debugger;
                // observer.trigger(observer.events.notification, { type: 'error', message: response.message });
                toast.error(<ToastComponent.errorToast text={response.message} />, {
                    position: toast.POSITION.TOP_RIGHT
                });
                // this.setState({
                //     username: '',
                //     email: '',
                //     password: '',
                //     confirmPassword: '',
                //     firstName: '',
                //     lastName: '',
                //     address: '',
                //     city: ''
                // })
            }

        })
    }

    handleBlur = (field) => (event) => {
        this.setState({
            touched: { ...this.state.touched, [field]: true }
        });

    }

    validate = (username, email, firstName, lastName, password, confirmPassword, address, city) => {
        debugger;
        const emailRegex = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;
        const firstLastNameRegex = /^[A-Z]([a-zA-Z]+)?$/;
        let testEmail = emailRegex.test(email)
        let testFirstName = firstLastNameRegex.test(firstName)
        let testLastName = firstLastNameRegex.test(lastName)
        console.log('testEmail : ', testEmail)
        debugger;
        return {
            username: username.length < 4 || username.length > 16,
            email: email.length === 0 || !testEmail,
            firstName: firstName.length === 0 || !testFirstName,
            lastName: lastName.length === 0 || !testLastName,
            password: password.length < 4 || password.length > 16,
            confirmPassword: confirmPassword.length === 0 || confirmPassword !== password,
            address: address.length === 0,
            city: city.length === 0,
        }
    }


    render() {
        debugger;

        const { username, email, firstName, lastName, password, confirmPassword, address, city } = this.state;
        // const isEnabled = this.buttonEnableFunc();
        const errors = this.validate(username, email, firstName, lastName, password, confirmPassword, address, city);
        const isEnabled = !Object.keys(errors).some(x => errors[x])

        const shouldMarkError = (field) => {
            const hasError = errors[field];
            const shouldShow = this.state.touched[field];

            return hasError ? shouldShow : false;
        }


        return (
            <div className="container">
                <h1 className="mt-5 mb-5 text-center font-weight-bold ">Register</h1>
                <form className="Register-form-container" onSubmit={this.onSubmitHandler}>

                    <div className="section-container">
                        <section className="left-section">
                            <div className="form-group">
                                {/* <label htmlFor="username" className={(shouldMarkError('username') ? "error-text-label" : "")}>Username</label> */}
                                <label htmlFor="username" >Username</label>
                                <input
                                    type="text"
                                    className={"form-control " + (shouldMarkError('username') ? "error" : "")}
                                    id="username"
                                    name="username"
                                    value={this.state.username}
                                    onChange={this.onChangeHandler}
                                    onBlur={this.handleBlur('username')}
                                    aria-describedby="usernameHelp"
                                    placeholder="Enter username"
                                />
                                {shouldMarkError('username') && <small id="usernameHelp" className="form-text error-text"> {(!this.state.username ? 'Username is required' : 'Username should be at least 4 and maximum 16 characters long.')}</small>}
                            </div>

                            <div className="form-group">
                                <label htmlFor="firstName" >First Name</label>
                                <input
                                    type="text"
                                    className={"form-control " + (shouldMarkError('firstName') ? "error" : "")}
                                    id="firstName"
                                    name="firstName"
                                    value={this.state.firstName}
                                    onChange={this.onChangeHandler}
                                    onBlur={this.handleBlur('firstName')}
                                    aria-describedby="firstNameHelp"
                                    placeholder="Enter first name"
                                />
                                {shouldMarkError('firstName') && <small id="firstNameHelp" className="form-text error-text">{(!this.state.firstName ? 'First Name is required' : 'First Name must start with a capital letter and must contain only letters..')}</small>}
                            </div>

                            <div className="form-group">
                                <label htmlFor="address" >Address</label>
                                <input
                                    type="text"
                                    className={"form-control " + (shouldMarkError('address') ? "error" : "")}
                                    id="address"
                                    name="address"
                                    value={this.state.address}
                                    onChange={this.onChangeHandler}
                                    onBlur={this.handleBlur('address')}
                                    aria-describedby="addressHelp"
                                    placeholder="Enter address"
                                />
                                {shouldMarkError('address') && <small id="addressHelp" className="form-text error-text">{(!this.state.address ? 'Address is required' : '')}</small>}
                            </div>

                            <div className="form-group">
                                <label htmlFor="password" >Password</label>
                                <input
                                    type="password"
                                    className={"form-control " + (shouldMarkError('password') ? "error" : "")}
                                    id="password"
                                    name="password"
                                    value={this.state.password}
                                    onChange={this.onChangeHandler}
                                    onBlur={this.handleBlur('password')}
                                    aria-describedby="passwordHelp"
                                    placeholder="Enter password"
                                />
                                {shouldMarkError('password') && <small id="passwordHelp" className="form-text error-text">{(!this.state.password ? 'Password is required!' : 'Password should be at least 4 and maximum 16 characters long!')}</small>}
                            </div>
                        </section>

                        <section className="right-section">
                            <div className="form-group">
                                <label htmlFor="email" >Email Address</label>
                                <input
                                    type="email"
                                    className={"form-control " + (shouldMarkError('email') ? "error" : "")}
                                    id="email"
                                    name="email"
                                    value={this.state.email}
                                    onChange={this.onChangeHandler}
                                    onBlur={this.handleBlur('email')}
                                    aria-describedby="emailHelp"
                                    placeholder="Enter email"

                                />
                                {shouldMarkError('email') && <small id="emailHelp" className="form-text error-text">{(!this.state.email ? 'Email is required!' : 'Invalid e-mail address!')}</small>}
                            </div>

                            <div className="form-group">
                                <label htmlFor="lastName" >Last Name</label>
                                <input
                                    type="text"
                                    className={"form-control " + (shouldMarkError('lastName') ? "error" : "")}
                                    id="lastName"
                                    name="lastName"
                                    value={this.state.lastName}
                                    onChange={this.onChangeHandler}
                                    onBlur={this.handleBlur('lastName')}
                                    aria-describedby="lastNameHelp"
                                    placeholder="Enter last name"
                                />
                                {shouldMarkError('lastName') && <small id="lastNameHelp" className="form-text error-text">{(!this.state.lastName ? 'Last Name is required!' : 'Last Name must start with a capital letter and must contain only letters..')}</small>}
                            </div>

                            <div className="form-group">
                                <label htmlFor="city" >City</label>
                                <input
                                    type="text"
                                    className={"form-control " + (shouldMarkError('city') ? "error" : "")}
                                    id="city"
                                    name="city"
                                    value={this.state.city}
                                    onChange={this.onChangeHandler}
                                    onBlur={this.handleBlur('city')}
                                    aria-describedby="cityHelp"
                                    placeholder="Enter city"
                                />
                                {shouldMarkError('city') && <small id="cityHelp" className="form-text error-text">{(!this.state.city ? 'City is required!' : '')}</small>}
                            </div>

                            <div className="form-group">
                                <label htmlFor="confirmPassword" >Confirm Password</label>
                                <input
                                    type="password"
                                    className={"form-control " + (shouldMarkError('confirmPassword') ? "error" : "")}
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    value={this.state.confirmPassword}
                                    onChange={this.onChangeHandler}
                                    onBlur={this.handleBlur('confirmPassword')}
                                    aria-describedby="confirmPasswordHelp"
                                    placeholder="Confirm your password"
                                />
                                {shouldMarkError('confirmPassword') && <small id="confirmPasswordHelp" className="form-text error-text">Passwords do not match.</small>}
                            </div>
                        </section>
                    </div>

                    <div className="text-center">
                        <button disabled={!isEnabled} type="submit" className="btn App-button-primary btn-lg m-3">Register</button>
                    </div>

                </form>
                <p>{JSON.stringify(this.state, null, 2)}</p>
            </div>
        )
    }
};
