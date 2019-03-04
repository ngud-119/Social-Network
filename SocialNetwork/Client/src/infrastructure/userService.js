
export default {
    getPayload: () => {
        const token = localStorage.getItem('token')

        if (token !== null && token !== undefined) {
            const payload = JSON.parse(atob(token.split('.')[1]));
            if (payload) {
                return payload;
            }
        }
    },

    getUsername: () => {
        const token = localStorage.getItem('token')

        if (token !== null && token !== undefined) {
            const payload = JSON.parse(atob(token.split('.')[1]));

            if (payload) {
                return payload['sub'];
            }
        }
    },

    getUserId: () => {

        const token = localStorage.getItem('token')
        if (token !== null && token !== undefined) {
            const payload = JSON.parse(atob(token.split('.')[1]));

            if (payload) {
                return payload['id'];
            }
        }
    },

    getRole: () => {
        const token = localStorage.getItem('token')
        if (token !== null && token !== undefined) {
            const payload = JSON.parse(atob(token.split('.')[1]));
            if (payload) {
                return payload['role'];
            }
        }
    },

    isRoot: () => {
        const token = localStorage.getItem('token')
        if (token !== null && token !== undefined) {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const role = payload['role'];

            if (payload) {
                if ((role !== null || role !== undefined) && role === 'ROOT') {
                    return true;
                }
            }
        }

        return false;
    },

    isAdmin: () => {
        const token = localStorage.getItem('token')
        if (token !== null && token !== undefined) {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const role = payload['role'];
            debugger;
            if (payload) {
                if ((role !== null || role !== undefined) && (role === 'ADMIN' || role === 'ROOT')) {
                    return true;
                }
            }
        }

        return false;
    },


    isLoggedInUser(username) {
        const token = localStorage.getItem('token')
        if (token !== null && token !== undefined) {
            const payload = JSON.parse(atob(token.split('.')[1]));

            if (payload) {
                const loggedInUserName = payload['sub'];
                if (username === loggedInUserName) {
                    return true;
                }
                return false;
            }
        }
    },

    checkIfIsRoot(role) {
        if (role === 'ROOT') {
          return true;
        }
    
        return false;
      }
}