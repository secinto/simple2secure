import json
import logging
import sys

from flask import Response, json, request, render_template

import src.celery.celery_tasks as celery_tasks
from src import create_app
from src.db.database import TestResult, Test
from src.db.database_schema import TestSchema
from src.scheduler.scheduler_tasks import start_scheduler_tasks
from src.util import file_utils, json_utils
from src.util.file_utils import update_services_file, read_json_testfile
from src.util.rest_utils import schedule_test_on_the_portal
from src.util.test_utils import update_insert_tests_to_db

app = create_app(sys.argv)
start_scheduler_tasks(app, celery_tasks)


def to_pretty_json(value):
    return value


app.jinja_env.filters['tojson_pretty'] = to_pretty_json

log = logging.getLogger('pod.start_pod')


@app.route("/services")
def parse_tests():
    tests_string = read_json_testfile()
    resp = Response(tests_string, status=200, mimetype='application/json')
    return resp


@app.route("/results")
def show_test_results():
    with app.app_context():
        update_services_file()
        test_results = TestResult.query.all()
        return render_template('testresults.html', len=len(test_results), test_results=test_results)


@app.route("/")
def index():
    return parse_tests()


@app.route("/services/run")
def run_service():
    with app.app_context():

        test_schema = TestSchema()
        response = file_utils.read_json_testfile()
        update_insert_tests_to_db(response, app)

        response_text = "All available tests from services.json have been scheduled"

        if json_utils.is_blank(request.query_string) is True:
            response_text = "Test ID not specified! Please write the url in the following form " \
                            "{https://localhost:5000/services/run?test=test1}"

        else:
            test_name_response = request.args.get("test")

            db_test = Test.query.filter_by(name=test_name_response).first()

            step = request.args.get("step")
            precondition = request.args.get("precondition")
            postcondition = request.args.get("postcondition")

            if db_test is None:
                response_text = "Test with provided test name cannot be found"
            else:
                current_test = json.loads(db_test.test_content)

                if not json_utils.is_blank(step):
                    step_param_value = json_utils.parse_query_test(step, "step")
                    current_test["test_definition"]["step"]["command"]["parameter"]["value"] = step_param_value

                if not json_utils.is_blank(precondition):
                    precondition_param_value = json_utils.parse_query_test(precondition, "precondition")
                    current_test["test_definition"]["precondition"]["command"]["parameter"][
                        "value"] = precondition_param_value

                if not json_utils.is_blank(postcondition):
                    postcondition_param_value = json_utils.parse_query_test(postcondition, "postcondition")
                    current_test["test_definition"]["postcondition"]["command"]["parameter"][
                        "value"] = postcondition_param_value

            db_test.test_content = current_test
            print(db_test.test_content)
            output = test_schema.dump(db_test)
            resp = schedule_test_on_the_portal(output, app)

            if resp.status_code == 200:
                response_text = "Test " + test_name_response + " has been scheduled"
            else:
                response_text = "Problem occured while scheduling test!"

        return response_text


if __name__ == '__main__':
    app.run(ssl_context='adhoc', host='0.0.0.0', threaded=True, use_reloader=False)
