import CookieHelper from "./CookieHelper";

export default class LoginHelper {
    static getLoginHeader() {
        let password = CookieHelper.getCookie("password")
        return LoginHelper.makeLoginHeader(password);
    }

    static makeLoginHeader(password) {
        return new Headers({
            'Authorization': 'Basic ' + btoa(":" + password),
        })
    }
}