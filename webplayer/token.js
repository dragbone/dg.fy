const redirect_uri = "http://localhost:9999/";

function login() {
    console.log("Redirecting to login...");
    const scope = encodeURIComponent("streaming user-read-private user-read-email");
    const state = encodeURIComponent("pizza"); // optional but recommended
    const encodedRedirect = encodeURIComponent(redirect_uri);
    window.location.href = `https://accounts.spotify.com/authorize?response_type=code&client_id=${clienId}&scope=${scope}&redirect_uri=${encodedRedirect}&state=${state}`;
}

function clearTokens() {
    window.localStorage.clear();
    console.log("Tokens cleared!");
}

function callback() {
    const refresh_token = window.localStorage.getItem('refresh_token');
    if (refresh_token) {
        refreshToken(refresh_token, access_token => window.initPlayer(access_token));
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');
    const state = urlParams.get('state');

    console.log(`callback:code=${code},state=${state}`);

    if (code && state) {
        requestToken(code, state);
    } else {
        login();
    }
}

function requestToken(code, state) {
    console.log("Requesting token...");

    fetch("https://accounts.spotify.com/api/token", {
        method: 'POST',
        body: toForm({
            code: code,
            redirect_uri: redirect_uri,
            grant_type: 'authorization_code'
        }),
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
            'Authorization': 'Basic ' + btoa(clienId + ':' + clientSecret)
        },
    }).then(response => response.json())
        .then(json => {
            console.log("Tokens received!");
            window.localStorage.setItem('access_token', json.access_token);
            window.localStorage.setItem('refresh_token', json.refresh_token);
            window.location.href = redirect_uri;
        });
}

function refreshToken(refresh_token, accessTokenCallback) {
    console.log(`Refreshing token ${refresh_token}...`);
    fetch("https://accounts.spotify.com/api/token", {
        method: 'POST',
        body: toForm({
            grant_type: "refresh_token",
            refresh_token: refresh_token
        }),
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
            'Authorization': 'Basic ' + btoa(clienId + ':' + clientSecret)
        },
    }).then(response => response.json()).then(json => {
        console.log("Token refreshed! Valid for: " + json.expires_in);
        window.localStorage.setItem('access_token', json.access_token);
        if (accessTokenCallback) accessTokenCallback(json.access_token);
    });
}

function toForm(bodyData) {
    return Object.keys(bodyData)
        .map(key => encodeURIComponent(key) + '=' + encodeURIComponent(bodyData[key]))
        .join('&')
}