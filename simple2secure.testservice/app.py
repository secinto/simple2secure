from flask import Response, json, request, render_template
from src import create_app
from src.util import file_utils
from src.util import json_utils
from src.db.database import TestResult, Test
from src.scheduler.scheduler_tasks import start_scheduler_tasks
import src.celery.celery_tasks as celery_tasks

app = create_app()
start_scheduler_tasks(app, celery_tasks)


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
    response = file_utils.read_json_testfile(app)
    return response


@app.route("/services/run")
def run_service():
    with app.app_context():
        response = file_utils.read_json_testfile(app)
        # response_json_object = json.loads(response)
        file_utils.update_insert_tests_to_db(response, app)

        response_text = "All available tests from services.json have been scheduled"

        if json_utils.is_blank(request.query_string) is True:
            tests = Test.query.all()

            for test in tests:
                current_test = json.loads(test.test_content)
                celery_tasks.schedule_test.delay(current_test["test_definition"], test.id)

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
                    current_test["test_definition"]["precondition"]["command"]["parameter"]["value"] = precondition_param_value

                if not json_utils.is_blank(postcondition):
                    postcondition_param_value = json_utils.parse_query_test(postcondition, "postcondition")
                    current_test["test_definition"]["postcondition"]["command"]["parameter"][
                        "value"] = postcondition_param_value

                celery_tasks.schedule_test.delay(current_test["test_definition"], db_test.id)
                response_text = "Test " + test_name_response + " has been scheduled"

        return response_text


if __name__ == '__main__':
    app.run(ssl_context='adhoc', host='0.0.0.0', threaded=True)
