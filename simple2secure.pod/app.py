import sys

from flask import Response, json, request, render_template
from src import create_app
from src.db.database_schema import TestSchema, TestSequenceSchema
from src.util import file_utils, json_utils, task_utils
from src.db.database import TestResult, Test
from src.scheduler.scheduler_tasks import start_scheduler_tasks
import src.celery.celery_tasks as celery_tasks
import json
from urllib import parse

from src.util.test_utils import update_insert_tests_to_db

app = create_app(sys.argv)
start_scheduler_tasks(app, celery_tasks)


def to_pretty_json(value):
    return value


app.jinja_env.filters['tojson_pretty'] = to_pretty_json


@app.route("/services")
def parse_tests():
    tests_string = file_utils.read_json_testfile(app)
    resp = Response(tests_string, status=200, mimetype='application/json')
    return resp


@app.route("/results")
def show_test_results():
    with app.app_context():
        file_utils.update_services_file()
        test_results = TestResult.query.all()
        return render_template('testresults.html', len=len(test_results), test_results=test_results)


@app.route("/")
def index():
    return parse_tests()


@app.route("/services/run/sequence")
def run_sequence():
    with app.app_context():
        splitted_url = parse.urlsplit(request.url)
        url_query = parse.parse_qs(splitted_url.query)

        test_sequence_schema = TestSequenceSchema()

        test_sequence = task_utils.get_sequence_from_url(url_query, app)

        sequence_to_provide = test_sequence_schema.dump(test_sequence).data

        testRun = celery_tasks.schedule_sequence.delay(sequence_to_provide)

        return "Task sequence has been scheduled"


@app.route("/services/run")
def run_service():
    with app.app_context():

        test_schema = TestSchema()
        response = file_utils.read_json_testfile(app)
        # response_json_object = json.loads(response)
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
            resp = file_utils.schedule_test_on_the_portal(output, app, app.config['POD_ID'])

            if resp.status_code == 200:
                response_text = "Test " + test_name_response + " has been scheduled"
            else:
                response_text = "Problem occured while scheduling test!"

        return response_text


if __name__ == '__main__':
    app.run(ssl_context='adhoc', host='0.0.0.0', threaded=True, use_reloader=False)
