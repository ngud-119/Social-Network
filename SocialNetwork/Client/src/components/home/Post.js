import React, { Fragment } from 'react';
import { userService } from '../../infrastructure'

const Post = (props) => {
    const imageClass = userService.getImageSize(props.imageUrl);
    const imageClassUserPick = userService.getImageSize(props.loggedInUserProfilePicUrl);
    const isRoot = userService.isRoot();
    const isTheCurrentLoggedInUser = (props.userId === userService.getUserId());

    const dayTime = props.time.hour <= 12 ?  'AM' : 'PM';
    const month = props.time.month.substring(0, 1) +  props.time.month.substring(1, 5).toLowerCase()
    const hour = props.time.hour < 10 ? '0'+props.time.hour: props.time.hour;

    const time = month + ' ' + props.time.dayOfMonth+ ' ' + hour + ':' + props.time.minute + ' '+ dayTime


    return (
        <Fragment>
            <div className="post-wrapper">
                <div className="post-content-article-header ">
                    <div className="post-content-article-image">
                        <img className={imageClassUserPick} src={props.loggedInUserProfilePicUrl} alt="bender" />
                    </div>
                    <div className="post-content-article-description">
                        <p className="post-user-info">{props.loggedInUserFirstName} {props.loggedInUserLastName} </p>
                        <p className="post-description"> {props.time.dayOfMonth} {month} {hour}:{props.time.minute} {dayTime}</p>
                    </div>
                </div>
                <div className="post-content">
                    <p className="">{props.content} </p>
                </div>

                {props.imageUrl && <div className="post-media">
                    <img className={imageClass} src={props.imageUrl} alt="Futurama" />
                </div>}

                <div className="post-footer">
                    <div className="post-left-side-icons-container">
                        <ul>
                            <li class="like-icon">
                                <div className="like-button" onClick={props.addLike.bind(this, props.postId)}> <i className="fas fa-thumbs-up"></i></div>
                            </li>
                            <li class="like-count">
                                <div >{props.likeCount}</div>
                            </li>
                            <li>
                                <i className="fas fa-share"></i>
                            </li>
                        </ul>
                    </div>
                    <div className="post-right-side-icons-container">
                        <div className="comment-icon">
                            <i className="fas fa-comments"></i>
                        </div>
                        <p>{props.commentsCount}</p>
                    </div>
                </div>
            </div>

        </Fragment>

    )
}

export default Post