import re
import sys


def find_ip():
    content = ""
    result = ""

    for arg in sys.argv[1:]:
        content += arg

    listen_expression = re.compile('.*ESTABLISHED.*')
    new_content = content.split("TCP")
    result_1_content = list(filter(listen_expression.match, new_content))

    result = re.findall(r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}', str(result_1_content))
    filtered_local_host_results = list(filter(lambda x: x != '127.0.0.1', result))
    
    if filtered_local_host_results is None or len(filtered_local_host_results) == 0:
        filtered_local_host_results = ""
    else:
        result = filtered_local_host_results[0]

    print(result)
    return result


if __name__ == "__main__":
    find_ip()
