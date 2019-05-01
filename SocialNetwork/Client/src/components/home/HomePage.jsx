import React, { Component, Fragment, Suspense, lazy } from 'react';
import { Route, Switch, Redirect } from 'react-router-dom';
import { toast } from 'react-toastify';
import { ToastComponent } from '../common';
import { requester, userService } from '../../infrastructure/';
import placeholder_user_image from '../../assets/images/placeholder.png';
import default_background_image from '../../assets/images/default-background-image.jpg';

import MessageBox from './MessageBox';
import UserSearchResultsPage from '../../components/user/UserSearchResultsPage';
import UserProfilePage from '../../components/user/UserProfilePage';
import UserFriendsAllPage from '../../components/user/UserFriendsAllPage';
import UserFindFriendsPage from '../../components/user/UserFindFriendsPage';
import UserFriendRequestsPage from '../../components/user/UserFriendRequestsPage';
import UserAllPage from '../../components/user/UserAllPage';
import UserEditPage from '../../components/user/UserEditPage';
import UserDeletePage from '../../components/user/UserDeletePage';
import UserGalleryPage from '../../components/user/UserGalleryPage';
import UserLogsPage from '../../components/user/UserLogsPage';
import ErrorPage from '../../components/common/ErrorPage';

import TimeLine from './TimeLine';
import HeaderSection from './HeaderSection';
import MainSharedContent from './MainSharedContent';
import Intro from './Intro';
import PhotoGallery from './PhotosGallery';
import FriendsGallery from './FriendsGallery';

import { connect } from 'react-redux';
import { fetchPicturesAction } from '../../store/actions/pictureActions';
import { fetchLoggedInUserAction, fetchTimeLineUserAction, fetchAllFriendsAction } from '../../store/actions/userActions';


// const UserSearchResultsPage = lazy(() => import('../user/UserSearchResultsPage'));
// const UserProfilePage = lazy(() => import('../user/UserProfilePage'));
// const UserFriendsAllPage = lazy(() => import('../user/UserFriendsAllPage'));
// const UserFindFriendsPage = lazy(() => import('../user/UserFindFriendsPage'));
// const UserAllPage = lazy(() => import('../user/UserAllPage'));
// const UserEditPage = lazy(() => import('../user/UserE../user/UserEditPage.oldt UserDeletePage = lazy(() => import('../user/UserDeletePage'));
// const UserGalleryPage = lazy(() => import('../user/UserGalleryPage'));
// const UserLogsPage = lazy(() => import('../user/UserLogsPage'));
// const MessageBox = lazy(() => import('./MessageBox'));

// const ErrorPage = lazy(() => import('../common/ErrorPage'));

class HomePage extends Component {
    constructor(props) {
        super(props)

        this.state = {
            authorities: [],
            picturesArr: [],
            friendsArr: [],
            friendsArrSearch: [],
            friendsCandidatesArr: [],
            userWaitingForAcceptingRequest: [],
            usersReceivedRequestFromCurrentUser: [],
            friendsChatArr: [],
            ready: false
        }

        this.loadTimeLineUserData = this.loadTimeLineUserData.bind(this);
        this.loadAllPictures = this.loadAllPictures.bind(this);
        this.loadAllFriends = this.loadAllFriends.bind(this);
        // this.searchResults = this.searchResults.bind(this);
        // this.findFriends = this.findFriends.bind(this);
    }

    componentDidMount() {
        console.log("Home componentDidMount")
        const userId = userService.getUserId();
        const timeLineUserId = userService.getUserId();

        this.props.loadLoggedInUserData(userId);
        this.loadTimeLineUserData(timeLineUserId);
        this.loadAllPictures(timeLineUserId);
        this.loadAllFriends(timeLineUserId);

        this.setState({ ready: true });
    }

    componentDidUpdate(prevProps, prevState) {
        const errorMessage = this.getErrorMessage(prevProps);
        const successMessage = this.getSuccessMessage(prevProps)

        if (errorMessage) {
            toast.error(<ToastComponent.errorToast text={errorMessage} />, {
                position: toast.POSITION.TOP_RIGHT
            });
        } else if (successMessage) {
            toast.success(<ToastComponent.successToast text={successMessage} />, {
                position: toast.POSITION.TOP_RIGHT
            });
        }
    }

    getSuccessMessage(prevProps) {
        if (!this.props.fetchPictures.hasError && this.props.fetchPictures.message && this.props.fetchPictures !== prevProps.fetchPictures) {
            return this.props.fetchPictures.message;
        }
        else if (!this.props.timeLineUserData.hasError && this.props.timeLineUserData.message && this.props.timeLineUserData !== prevProps.timeLineUserData) {
            return this.props.timeLineUserData.message;

        } else if (!this.props.loggedInUserData.hasError && this.props.loggedInUserData.message && this.props.loggedInUserData !== prevProps.loggedInUserData) {
            return this.props.loggedInUserData.message;
        }
        else if (!this.props.fetchAllFriends.hasError && this.props.fetchAllFriends.message && this.props.fetchAllFriends !== prevProps.fetchAllFriends) {
            return this.props.fetchAllFriends.message;
        }
        return null;
    }

    getErrorMessage(prevProps) {
        if (this.props.fetchPictures.hasError && prevProps.fetchPictures.error !== this.props.fetchPictures.error) {
            return this.props.fetchPictures.message || 'Server Error';
        }
        else if (this.props.timeLineUserData.hasError && prevProps.timeLineUserData.error !== this.props.timeLineUserData.error) {
            return this.props.timeLineUserData.message || 'Server Error';

        } else if (this.props.loggedInUserData.hasError && prevProps.loggedInUserData.error !== this.props.loggedInUserData.error) {
            return this.props.loggedInUserData.message || 'Server Error';
        }
        else if (this.props.fetchAllFriends.hasError && prevProps.fetchAllFriends.error !== this.props.fetchAllFriends.error) {
            return this.props.fetchAllFriends.message || 'Server Error';
        }

        return null;
    }

    loadTimeLineUserData(userId) {
        this.props.loadTimelineUserData(userId);
    }

    loadAllPictures = (userId) => {
        this.props.loadAllPictures(userId);
    }

    loadAllFriends = (userId) => {
        this.props.loadAllFriends(userId);
    }

    // searchResults = (userId, search) => {
    //     this.setState({
    //         search
    //     })

    //     const requestBody = { loggedInUserId: userId, search: search }

    //     requester.post('/relationship/search', requestBody, (response) => {
    //         this.setState({
    //             friendsArrSearch: response.filter(user => user.status === 1),
    //             friendsCandidatesArr: response.filter(user => user.status !== 0 && user.status !== 1),
    //             userWaitingForAcceptingRequest: response.filter(user => user.status === 0 && user.starterOfAction === true),
    //             usersReceivedRequestFromCurrentUser: response.filter(user => user.status === 0 && user.starterOfAction === false),
    //             ready: true
    //         })
    //     }).catch(err => {
    //         toast.error(<ToastComponent.errorToast text={`Internal Server Error: ${err.message}`} />, {
    //             position: toast.POSITION.TOP_RIGHT
    //         });

    //         if (err.status === 403 && err.message === 'Your JWT token is expired. Please log in!') {
    //             localStorage.clear();
    //             this.props.history.push('/login');
    //         }
    //     })
    // }

    render() {
        const loading = this.props.fetchPictures.loading || this.props.timeLineUserData.loading || this.props.loggedInUserData.loading || this.props.fetchAllFriends.loading;
        if (!this.state.ready || loading) {
            // return null;
            return <h1 className="text-center pt-5 mt-5">Loading HomePage...</h1>
        }

        const isRoot = userService.isRoot();
        const isAdmin = userService.isAdmin();
        const isTheCurrentLoggedInUser = this.props.loggedInUserData.id === this.props.timeLineUserData.id;
        let loggedIn = userService.isTheUserLoggedIn();

        return (
            <Fragment>
                <HeaderSection  {...this.props.timeLineUserData} />
                <main className="site-content">
                    <section className="main-section">
                        <TimeLine {...this.props.timeLineUserData} />
                        <Suspense fallback={<h1 className="text-center pt-5 mt-5">Loading Falback HomePage...</h1>}>
                            <Switch>
                                {loggedIn && <Route exact path="/home/comments/:id" component={MainSharedContent} />}
                                {loggedIn && <Route exact path="/home/profile/:id" component={UserProfilePage} />}
                                {loggedIn && (isRoot || isAdmin || isTheCurrentLoggedInUser) && <Route exact path="/home/users/edit/:id" component={UserEditPage} />}
                                {(loggedIn && (isRoot || isAdmin)) && <Route exact path="/home/users/all/:id" component={UserAllPage} />}
                                {(loggedIn && isRoot) && <Route exact path="/home/users/delete/:id" component={UserDeletePage} />}
                                {loggedIn && <Route exact path="/home/gallery/:id" component={UserGalleryPage} />} />}
                                {(loggedIn && (isRoot || isAdmin)) && <Route exact path="/home/logs/:id" component={UserLogsPage} />}
                                {loggedIn && <Route exact path="/home/friends/:id" component={UserFriendsAllPage} />}
                                {loggedIn && <Route exact path="/home/findFriends/:id" component={UserFindFriendsPage} />}
                                {loggedIn && <Route exact path="/home/friendRequests/:id" component={UserFriendRequestsPage} />}
                                {loggedIn && <Route exact path="/home/users/search/" component={UserSearchResultsPage}  />}
                                {/* 
                                {/* <Route exact path="/error" component={ErrorPage} />
                                <Route render={(props) => <Redirect to="/" {...props} />} /> */}
                            </Switch>
                        </Suspense>
                    </section>
                    <Fragment>
                        <section className="aside-section">
                            <Intro {...this.props.timeLineUserData} />
                            <PhotoGallery picturesArr={this.props.picturesArr} timeLineUserId={this.props.timeLineUserData.id} />
                            <FriendsGallery friendsArr={this.props.friendsArr} timeLineUserId={this.props.timeLineUserData.id} />
                            <MessageBox />
                        </section>
                    </Fragment>
                </main>
            </Fragment>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        picturesArr: state.fetchPictures.picturesArr,
        fetchPictures: state.fetchPictures,
        timeLineUserData: state.timeLineUserData,
        loggedInUserData: state.loggedInUserData,
        friendsArr: state.fetchAllFriends.friendsArr,
        fetchAllFriends: state.fetchAllFriends,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        loadAllPictures: (userId) => { dispatch(fetchPicturesAction(userId)) },
        loadLoggedInUserData: (userId) => { dispatch(fetchLoggedInUserAction(userId)) },
        loadTimelineUserData: (userId) => { dispatch(fetchTimeLineUserAction(userId)) },
        loadAllFriends: (userId) => { dispatch(fetchAllFriendsAction(userId)) },
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(HomePage);