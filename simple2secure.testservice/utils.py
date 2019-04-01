import platform


def read_json_testfile():
    # Read test file and return it
    tests_file = open('services.json', 'r')
    return tests_file.read()


def is_blank(mystring):
    # Check if provided string is null or empty
    if mystring and mystring.strip():
        return False
    return True


def get_tool(argument):
    # Check the provided argument and return the real
    # command according to the current OS
    if argument == 'SAVE_RESULT':
        if platform.system().lower() == 'windows':
            return 'type nul >'
        else:
            return 'mkdir'
    elif argument == 'DELETE_RESULT':
        if platform.system().lower() == 'windows':
            return 'del'
        else:
            return 'rm -rf'
    else:
        return argument


def construct_command(tool, argument):
    # Construct command from the tool name and argument
    return tool + ' ' + argument


def write_to_result_log(content):
    # Write the result to the log file
    log_file = open("result/result.json", "w")
    log_file.write(content)
    log_file.close()


def get_json_test_object(data, test_id, key, attribute):
    # Iterate over data object
    for test in data:
        # Check if provided test id is equal to the test_id of the current object
        if test["id"] == test_id:
            # Check what is the provided attribute name and get value of that attribute
            if attribute == "command":
                return test[key][attribute]["executable"]
            elif attribute == "parameter":
                return test[key]["command"][attribute]["value"]


def parse_query_test(query_string, item_type):
    # Split query params by "%"
    mylist = query_string.split("%")
    print(enumerate(mylist))
    for index, item in enumerate(mylist):
        # Split sub params by "="
        mysublist = item.split("=")
        # Check if current attribute equals the provided item_type
        if mysublist[0] == item_type:
            correct_item = mylist[index+1].split("=")
            return correct_item[1]


def get_test_item_value(data, query_string, test_id, test_type, attribute):
    # if query_string is empty get the object from the file
    if is_blank(query_string):
        value = get_json_test_object(data, test_id, test_type, attribute)
    else:
        # If query string of the searched attribute is empty get it from the file
        if is_blank(parse_query_test(query_string, attribute)):
            value = get_json_test_object(data, test_id, test_type, attribute)
        # Read the value from the query_string
        else:
            value = parse_query_test(query_string, attribute)

    return value












