import React, {Component} from  'react';

export default class BackButton extends Component {

    static contextTypes = {
        router: () => true
    }

    render() {
        return (
            <button
            className={this.props.class}
            onClick={this.context.router.history.goBack}>
                {this.props.text}
            </button>
        )
    }
}