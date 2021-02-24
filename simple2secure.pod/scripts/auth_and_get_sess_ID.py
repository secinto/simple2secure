import sys

import requests


def auth_and_get_session_id(username, password, uri_for_get, uri_for_post):
    ses = requests.session()
    res = ses.get(uri_for_get)
    # First I send a get request to get the cookies
    sessionId = ses.cookies['JSESSIONID']
    payload = {
        'username': username,
        'password': password,
        'JSESSIONID': sessionId
    }

    # With the post request you are able to log in to the site
    res = ses.post(uri_for_post, data=payload)

    print(ses.cookies['JSESSIONID'])
    return ses.cookies['JSESSIONID']


if __name__ == "__main__":
    if len(sys.argv) != 5:
        raise SyntaxError("The script expects username, password, uri_get (for the GET request, to initially obtain "
                          "the session cookie) and uri_post (for the POST request) as parameter.")
    elif len(sys.argv) == 5:
        auth_and_get_session_id(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4])
