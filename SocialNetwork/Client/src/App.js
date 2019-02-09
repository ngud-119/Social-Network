import React, { Component, Fragment } from 'react';
import {Route, Switch, withRouter} from 'react-router-dom';
import './styles/App.css';
import { Footer, Header } from './components/common';
import RegisterPage from './components/auth/RegisterPage';
import HomePage from './components/auth/HomePage';
import LoginPage from './components/auth/LoginPage';
import { withRootAuthorization, withAdminAuthorization } from './hocs/withAuthorization';
import ErrorPage from './components/common/ErrorPage';
import ProfilePage from './components/user/ProfilePage';


class App extends Component {
  constructor(props){
    super(props)

    this.onLogout = this.onLogout.bind(this);
  }

  onLogout(){
    localStorage.clear();
    this.props.history.push('/');
  }


  render() {
    const loggedIn = localStorage.getItem('token');
    console.log(localStorage.getItem('token'))
    console.log(localStorage.getItem('token') != null)
    debugger;
    return (
      <Fragment>
        <Header loggedIn={localStorage.getItem('token') != null} onLogout={this.onLogout} />
        <Switch>
          <Route exact path="/" component={HomePage} />
          {!loggedIn &&<Route exact path="/register" component={RegisterPage} />}
          {/* {<Route exact path="/register" component={RegisterPage} />} */}
          {!loggedIn && <Route exact path="/login" component={LoginPage} />}
          {loggedIn && <Route exact path="/profile/:id" component={ProfilePage} />}
           {/* <Route exact path="/profile" component={withAdminAuthorization(ProfilePage)} /> */}

          
          <Route exact path="/error" component={ErrorPage} />
          <Route component={ErrorPage} />
        </Switch>
        <Footer />
      </Fragment>
    )
  }
}

export default withRouter(App);
