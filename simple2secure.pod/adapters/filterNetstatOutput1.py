import sys
import re


def findPid():
    content = ""
    result = ""

    for arg in sys.argv[1:]:
        content += arg

    listen_expression = re.compile('.*HERGESTELLT.*')
    new_content = content.split("TCP")
    result_1_content = list(filter(listen_expression.match, new_content))

    result = re.findall(r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}', str(result_1_content))
    filteredLocalHostResults = list(filter(lambda x: x != '127.0.0.1', result))
    
    if result is None or len(result) == 0:
        result = ""
    else:
        result = result

    print(result)
    return result


findPid()
