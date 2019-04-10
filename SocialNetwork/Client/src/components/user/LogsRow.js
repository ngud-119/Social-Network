import React, { Component } from 'react';
import { NavLink } from 'react-router-dom';
import { userService, requester } from '../../infrastructure'
import { toast } from 'react-toastify';
import { ToastComponent } from '../common'


export default class UserRow extends Component {
    constructor(props) {
        super(props)

        this.state = {
            id: '',
            username: '',
            method: '',
            tableName: '',
            action: '',
            time: ''
        }

        this.promote = this.promote.bind(this);
        this.demote = this.demote.bind(this);
    }

    componentDidMount(){
        this.setState({...this.props})
    }

    promote = (event) => {
        event.preventDefault();
        const id = this.state.id;
        requester.post('/users/promote?id=' + id, id, (response) => {
            if (response.success) {
                this.setState({role: 'ADMIN'})
                toast.success(<ToastComponent.successToast text={response.message} />, {
                    position: toast.POSITION.TOP_RIGHT
                });
            } else {
                toast.error(<ToastComponent.errorToast text={response.message} />, {
                    position: toast.POSITION.TOP_RIGHT
                });
            }
        }).catch(err => {
            toast.error(<ToastComponent.errorToast text={`Internal Server Error: ${err.message}`} />, {
                position: toast.POSITION.TOP_RIGHT
            });

            if(err.status === 403 && err.message === 'Your JWT token is expired. Please log in!'){
                localStorage.clear();
                this.props.history.push('/login');
            }
        })
    }

    demote = (event) => {
        event.preventDefault();
        const id = this.state.id;
        requester.post('/users/demote?id=' + id, id, (response) => {
            if (response.success) {
                this.setState({role: 'USER'})
                toast.success(<ToastComponent.successToast text={response.message} />, {
                    position: toast.POSITION.TOP_RIGHT
                });
            } else {
                toast.error(<ToastComponent.errorToast text={response.message} />, {
                    position: toast.POSITION.TOP_RIGHT
                });
            }

        }).catch(err => {
            toast.error(<ToastComponent.errorToast text={`Internal Server Error: ${err.message}`} />, {
                position: toast.POSITION.TOP_RIGHT
            });

            if(err.status === 403 && err.message === 'Your JWT token is expired. Please log in!'){
                localStorage.clear();
                this.props.history.push('/login');
            }
        })
    }

    render = () => {
        return (
            <tr className="row" >
                <td className="col-md-1 font-weight-bold" >
                    {/* <h5>{this.state.index}</h5> */}
                   {this.state.index}
                </td>
                <td className="col-md-2" style={{'color':'#40a3f4'}}>
                   {this.state.username}
                </td>

                <td className="col-md-2 py-auto" >
                    {this.state.method}
                </td>

                <td className="col-md-2" >
                   {this.state.action}
                </td>

                <td className="col-md-2" >
                    {this.state.tableName}
                </td>

                <td className="col-md-3" >
                    {this.state.time}
                </td>

                {/* <td className="col-md-5 d-flex justify-content-center" >
                    {(!userService.checkIfIsRoot(this.state.role) && !userService.isLoggedInUser(this.state.username)) &&
                        <h5>
                            <button className="btn App-button-primary btn-lg m-1" onClick={this.promote} >Promote</button>
                        </h5>}
                    {(!userService.checkIfIsRoot(this.state.role) && !userService.isLoggedInUser(this.state.username)) &&
                        <h5>
                            <button className="btn App-button-primary btn-lg m-1" onClick={this.demote} >Demote</button>
                        </h5>}
                    <h5>
                        <NavLink className="btn App-button-primary btn-lg m-1" to={`/home/profile/${this.state.id}`} role="button">Profile</NavLink>
                    </h5>
                </td> */}
            </tr>
        )
    }
}
