import requests
import json
import time


def create_headers(auth_token):
    created_headers = {'Accept-Language': 'en-EN',
                       'Authorization': "Bearer " + auth_token}

    return created_headers


def create_timestamp_with_file_ending():
    timestamp = time.time() * 1000
    return str(float(timestamp)) + ".zip"


localhost_portal = "https://localhost:8443"
hetzner_portal = "https://simple2secure.info:51001"

print("Please select the portal url:")
print("1. " + localhost_portal)
print("2. " + hetzner_portal)

portal_option = input("Select an option 1 or 2: ")

portal_url = localhost_portal
if portal_option == 2:
    portal_url = hetzner_portal

print("You have selected " + portal_url)

print("Please enter your credentials")
username = input("Username:")
password = input("Password:")

data = {'username': username, 'password': password}

response = requests.post(
    portal_url + '/api/login',
    data=json.dumps(data), verify=False
)

if response.status_code == 200:
    bearer_token = response.headers['Authorization']
    token = bearer_token[7:-1]
    if token:
        print("Authentication successful, downloading the license file")
        headers = create_headers(token)

        license_file = requests.post(
            portal_url + '/api/license/downloadLicenseForScript',
            data=token, headers=headers, verify=False
        )
        open('./simple2secure/simple2secure.pod/static/license/license' + create_timestamp_with_file_ending(),
             'wb').write(license_file.content)
        print('License downloaded successfully')
    else:
        print("Error occured during authorization")
else:
    print("Error occured during authorization")
