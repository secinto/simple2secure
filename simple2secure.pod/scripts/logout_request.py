import requests
import sys
import re


def logout_with_valid_session_id(valid_session_id):
    ses = requests.session()
    ses.cookies['JSESSIONID'] = valid_session_id
    reex = '(<p class=\"info-msg\">)(.*)(</p>)'
    res = ses.get("https://pentest.moxis.cloud/logout")
    mess = re.search(reex, res.text)
    if mess:
        result = mess.group(2)
        print(result)


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise SyntaxError("The script expects a valid session ID as parameter.")
    elif len(sys.argv) == 2:
        logout_with_valid_session_id(sys.argv[1])
