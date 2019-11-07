import sys
import re

def findPid():
    content = ""
    result = ""
    '''f= open("netstat_output.txt","r")
    if f.mode == 'r':
        content = f.read()'''

    for arg in sys.argv[1:]:
        content += arg
        

    #pid = re.findall(r'(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}:[1-9]{5}\s*.*:.*\s*[1-9]{1,5})', content)
    listen_expression = re.compile('.*HERGESTELLT.*')
    two_ip_expression = re.compile('\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}:')
    new_content = content.split("TCP")
    result_1_content = list(filter(listen_expression.match, new_content))
    #result_2_content = list(filter(two_ip_expression.match, result_1_content))
    
    
    #pid = re.findall(r'[2]{5}.*:\w*\d{5}', new_content)
    result = re.findall(r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}', str(result_1_content))
    filteredLocalHostResults = list(filter(lambda x: x != '127.0.0.1', result))
    #result = result[1][1:]
    
    if result == None or len(result) == 0:
        result = ""
    else:
        result = result

    print(filteredLocalHostResults[0])
    return filteredLocalHostResults[0]
    
findPid()